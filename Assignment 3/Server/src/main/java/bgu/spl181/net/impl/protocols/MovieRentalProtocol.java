package bgu.spl181.net.impl.protocols;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import bgu.spl181.net.impl.databases.BBUser;
import bgu.spl181.net.impl.databases.DataBase;
import bgu.spl181.net.impl.databases.Movie;
import bgu.spl181.net.impl.databases.MoviesDataBase;
import bgu.spl181.net.impl.databases.User;

public class MovieRentalProtocol extends USTBProtocol{
	private MoviesDataBase moviesDB;
	private ReentrantReadWriteLock moviesLock;

	
	public MovieRentalProtocol(DataBase sharedData) {
		super(sharedData);
		this.moviesDB=sharedData.getMoviesDataBase(); 
		this.moviesLock=sharedData.getMoviesDBLock();
		
	}
	
	@Override
	protected void registerCommand(List<String> message) {
		if(message.size()<3){		//missing country argument
			errorCommand("registration failed");
			return;
		}
		super.registerCommand(message); //handles lock as well
	}
		
	
	@Override
	public void handleRequest(List<String> message) {
		if(message.isEmpty()) {
			errorCommand("request failed");
			return;
		}
		String commandName= message.remove(0);
		if(!isLoggedIn) {
			errorCommand("request "+ commandName +" failed");
			return;
		}
		switch(commandName){
			case "balance":
				balanceCommand(message);	 			
				break;
			case "info":
				infoCommand(message);		
				break;
			case "rent":
				rentCommand(message);		 
				break;
			case "return":
				returnCommand(message);		
				break;
				
			//admin only requests
			case "addmovie":
				addMovieCommand(message);
				break;
			case "remmovie":
				remMovieCommand(message);
				break;
			case "changeprice":
				changePriceCommand(message);
				break;
			default:
				errorCommand("unknown command");
				break;
		}

	}
	
	private void returnCommand(List<String> message) {
		String movieName=message.remove(0);
		usersLock.writeLock().lock();
		moviesLock.writeLock().lock();
		try {
			Movie currMovie = moviesDB.get(movieName);
			//checks if the movie exists and the if user is renting the movie
			if (currMovie ==null || !((BBUser)user).containsMovieByName(movieName)) {
				errorCommand("request return failed");
			 	return;
			}
			
			((BBUser)user).removeMovie(movieName); 	//remove the movie from user movie rented list
			currMovie.incAvailableAmount();			//increase total amount by 1
			acknowledgeCommand("return "+ "\""+movieName+"\" "+ "success");
			broadcastCommand("movie "+"\""+ movieName + "\" "+ currMovie.getAvailableAmount()+ " "+ currMovie.getPrice());
			((DataBase)sharedData).updateMoviesDataFile();
			((DataBase)sharedData).updateUsersDataFile();
		}
		finally {
			usersLock.writeLock().unlock();
			moviesLock.writeLock().unlock();
		}
	}

	private void rentCommand(List<String> message) {
		String movieName=message.remove(0);
		usersLock.writeLock().lock();
		moviesLock.writeLock().lock();
		try {
			if (!preconditionsForRent(movieName)) {
				errorCommand("request rent failed");
			 	return;
			}
			Movie currMovie = moviesDB.get(movieName);
			((BBUser)user).addMovie(currMovie); 					//remove the movie from user movie rented list
			((BBUser)user).reduceFromBalance(currMovie.getPrice()); //decrease balance by price
			currMovie.decAvailableAmount();							//decrease total amount by 1
			acknowledgeCommand("rent "+ "\""+ movieName+ "\" "+ "success");
			broadcastCommand("movie "+ "\""+movieName +"\" "+ currMovie.getAvailableAmount()+ " "+ currMovie.getPrice());
			((DataBase)sharedData).updateMoviesDataFile();
			((DataBase)sharedData).updateUsersDataFile();
		}
		finally {
			usersLock.writeLock().unlock();
			moviesLock.writeLock().unlock();
		}
	}

	private void infoCommand(List<String> message) {
		moviesLock.readLock().lock();
		try {
			//No movie name was given
			if (message.isEmpty()) {
				acknowledgeCommand("info "+ moviesDB.getMoviesName());
				return;
			}			
			String movieName=message.remove(0);
			Movie currMovie = moviesDB.get(movieName);
			
			if (currMovie==null) {
				errorCommand("request info failed");
				return;
			}
			acknowledgeCommand("info "+ currMovie.movieInfo());
		}		
		finally {
			moviesLock.readLock().unlock();
		}
	}

	private void balanceCommand(List<String> message) {
		String typeOfBalance=message.remove(0);
		boolean isInfoRequest = typeOfBalance.equals("info");
		Lock lock= isInfoRequest ? usersLock.readLock() :  usersLock.writeLock() ;
		lock.lock();
		try {
			long currentAmount = ((BBUser)user).getBalance();
			if (isInfoRequest) { 
				acknowledgeCommand("balance "+ currentAmount);
				return;
			}
			// TypeOfBalance is "add", increase the user's balance
			int amountToAdd = Integer.parseInt(message.remove(0));
			((BBUser)user).addToBalance(amountToAdd);
			acknowledgeCommand("balance "+(currentAmount+amountToAdd)+" added "+amountToAdd);
			((DataBase)sharedData).updateUsersDataFile();	
		}
		finally {
			lock.unlock();
		}
	}

	private void changePriceCommand(List<String> message) {
		if(!isAdmin()){							//access denied- non admin user
			errorCommand("request changeprice failed");
			return;
		}
		
		String movieName=message.remove(0);
		int price= Integer.parseInt(message.remove(0));
		if(price < 1){							//price is less than permitted
			errorCommand("request changeprice failed");
			return;
		}
		
		moviesLock.writeLock().lock();
		try {
			Movie movieToUpdate= moviesDB.get(movieName);
			if(movieToUpdate==null){		//movie doesn't exist
				errorCommand("request changeprice failed");
				return;
			}
			movieToUpdate.setPrice(price);
			acknowledgeCommand("changeprice "+"\""+ movieName+ "\" "+"success");
			broadcastCommand("movie "+ "\""+movieName +"\" "+ movieToUpdate.getAvailableAmount() + " "+ price);
			((DataBase)sharedData).updateMoviesDataFile();
		}
		finally {
			moviesLock.writeLock().unlock();
		}
	}

	private void remMovieCommand(List<String> message) {
		if(!isAdmin()){							//access denied- non admin user
			errorCommand("request remmovie failed");
			return;
		}
		String movieName=message.remove(0);
		moviesLock.writeLock().lock();
		try {
			Movie movieToRemove= moviesDB.get(movieName);
			if(movieToRemove==null){		//movie doesn't exist
				errorCommand("request remmovie failed");
				return;
			}
			if(movieToRemove.getAvailableAmount()!=movieToRemove.getTotalAmount()){ //there is currently a rented copy
				errorCommand("request remmovie failed");
				return;
			}
			moviesDB.remove(movieName);
			acknowledgeCommand("remmovie "+"\""+ movieName+"\" "+ "success");
			broadcastCommand("movie "+ "\""+movieName+"\" "+"removed");
			((DataBase)sharedData).updateMoviesDataFile();
		}
		finally {
			moviesLock.writeLock().unlock();		
		}
	}

	private void addMovieCommand(List<String> message) {
		if(!isAdmin()){							//access denied- non admin user
			errorCommand("request addmovie failed");
			return;
		}
		String movieName=message.remove(0);
		int amount=Integer.parseInt(message.remove(0));
		int price= Integer.parseInt(message.remove(0));
		if(amount < 1 || price < 1){			//amount or price less than permitted
			errorCommand("request addmovie failed");
			return;
		}
		moviesLock.writeLock().lock();
		try { 
			if(moviesDB.contains(movieName)){		//movie already exists
				errorCommand("request addmovie failed");
				return;
			}
			ArrayList<String> bannedCountries= new ArrayList<String>(message); //Constructs an ArrayList of banned countries from the remaining arguments.
			Movie movieToAdd= new Movie(moviesDB.nextMovieId(), movieName, price, bannedCountries, amount, amount); 
			moviesDB.add(movieToAdd);
			acknowledgeCommand("addmovie "+"\""+ movieName+"\" "+ "success");
			broadcastCommand("movie "+ "\""+movieName+"\" "+ amount+ " " + price);
			((DataBase)sharedData).updateMoviesDataFile();
		}
		finally {
			moviesLock.writeLock().unlock();		
		}
	}

	private boolean isAdmin(){
		return ((BBUser)user).isAdmin();
	}
	
	//Eran	
	private boolean preconditionsForRent(String movieName) {
		Movie currMovie = moviesDB.get(movieName);
		//The movie does not exist in the system
		if (currMovie==null) 
			return false;
		//The user is already renting the movie
		if (((BBUser)user).containsMovieByName(movieName)) {
			return false;
		}
		//There are no more copies of the movie that are available for rental
		if (currMovie.getAvailableAmount() == 0) 
			return false;
		//The movie is banned in the user’s country
		if (currMovie.getBannedCountries().contains(((BBUser)user).getCountry()))
			return false;
		//The user does not have enough money in their balance
		if (((BBUser)user).getBalance() < currMovie.getPrice())
			return false;

		return true;
	}

	@Override
	public void handleRegisterData(List<String> message, User user) {
		String country= message.get(0);
		((BBUser)user).setCountry(country);
	}

} 