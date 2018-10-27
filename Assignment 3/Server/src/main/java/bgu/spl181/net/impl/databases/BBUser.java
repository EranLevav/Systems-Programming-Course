package bgu.spl181.net.impl.databases;

import java.util.ArrayList;

public class BBUser extends User{
	private String type;
	private String country;
	private ArrayList<Movie> movies;
	private long balance;
	/**
	 * @param username
	 * @param type
	 * @param password
	 * @param country
	 * @param movies
	 * @param balance
	 */
	public BBUser(String username, String type, String password, String country, ArrayList<Movie> movies,
			long balance) {
		this.username = username;
		this.type = type;
		this.password = password;
		this.country = country;
		this.movies = movies;
		this.balance = balance;
	}
	
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @return true if the type is "admin"
	 */
	public boolean isAdmin() {
		return type.equals("admin");
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the balance
	 */
	public long getBalance() {
		return balance;
	}

	/**
	 * @param balance the balance to set
	 */
	public void setBalance(long balance) {
		this.balance = balance;
	}
	
	//Eran
	public void addToBalance(long balanceToAdd) {
		this.balance = balance + balanceToAdd;
	}
	
	//Eran
	public void reduceFromBalance(long balanceToReduce) {
		this.balance = balance - balanceToReduce;
	}
	
	/**
	 * @return the movies
	 */
	public ArrayList<Movie> getMovies() {
		return movies;
	}

	/**
	 * @param movie
	 * @return
	 * @see java.util.ArrayList#add(java.lang.Object)
	 */
	public boolean addMovie(Movie movie) {
		return movies.add(movie);
	}

	/**
	 * @param movie
	 * @return
	 * @see java.util.ArrayList#remove(java.lang.Object)
	 */
	public boolean removeMovie(String movieName) {
		return movies.removeIf((movie)-> movie.getName().equals(movieName));
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "User [username=" + username + ", type=" + type + ", password=" + password + ", country=" + country
				+ '\n'+ ", movies=" + movies + ", balance=" + balance + "]"  +'\n'+ "Movie Count:"+movies.size()+ '\n';
	}
	
	public boolean containsMovieByName(String movieName) {
		for (Movie m : movies) {
			if (m.getName().equals(movieName))
				return true;
		}
		return false;		
	}	
}
