package bgu.spl181.net.impl.databases;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.LongSerializationPolicy;

public class DataBase implements SharedData{
	private Gson gson;
	private MoviesDataBase moviesDB;
	private UsersDataBase usersDB;
	private String moviesJsonPath;
	private String usersJsonPath;
	private ReentrantReadWriteLock moviesDBLock;
	private ReentrantReadWriteLock usersDBLock;
	
	/**
	 * @param moviesDB
	 * @param usersDB
	 */
	public DataBase(String moviesJsonPath, String usersJsonPath) {
		gson= new GsonBuilder()
				.setPrettyPrinting()
				.setLongSerializationPolicy(LongSerializationPolicy.STRING)
				.disableHtmlEscaping()
				.create();
		this.moviesJsonPath=moviesJsonPath;
		this.usersJsonPath=usersJsonPath;
		readJson(moviesJsonPath);
		readJson(usersJsonPath);
		usersDB.init();
		moviesDB.init();
		moviesDBLock= new ReentrantReadWriteLock(true); //Fair
		usersDBLock= new ReentrantReadWriteLock(true); //Fair
	}

	public ReentrantReadWriteLock getMoviesDBLock() {
		return moviesDBLock;
	}

	public ReentrantReadWriteLock getUsersDBLock() {
		return usersDBLock;
	}

	/**
	 * @return the moviesDB
	 */
	public MoviesDataBase getMoviesDataBase() {
		return moviesDB;
	}


	/**
	 * @return the usersDB
	 */
	public UsersDataBase getUsersDataBase() {
		return usersDB;
	}


	private void readJson(String fileName){
		File filePath = new File(fileName);
		//try with resource
		try (BufferedReader reader= new BufferedReader(new FileReader(filePath));){
			String file= reader.lines().collect(Collectors.joining());
			//Fix minor syntax issues in json file
			//file= file.replaceAll("( : |: | :)", ":");
			if(fileName.equals(moviesJsonPath))
				this.moviesDB= gson.fromJson(file, MoviesDataBase.class);
			else
				this.usersDB= gson.fromJson(file, UsersDataBase.class);
		}
		catch(JsonSyntaxException ex) {
			System.out.println("JSON syntax error.");
		}
		catch(IOException ex) {
			System.out.println("Error reading file.");
			ex.printStackTrace();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * @param fileName
	 * Updates the JSON file specified (Movies.json or Users.json)
	 */
	public void writeToJson(String fileName){
		try(FileWriter fw = new FileWriter(fileName);){
			String toFile= gson.toJson(fileName.equals(moviesJsonPath) ? moviesDB : usersDB);
	        fw.write(toFile);
		}

		catch(IOException ex) {
			System.out.println("Error writing file.");
			ex.printStackTrace();
		}
	}

	@Override
	public ConcurrentHashMap<String, Integer> getLoggedInUsers(){
		return usersDB.getLoggedInUsers();
	}

	@Override
	public void signout(String username) {
		usersDB.signout(username);
	}

	@Override
	public BBUser login(String username, String password, int connectionId) {
		return usersDB.login(username,password, connectionId);
	}

	@Override
	public BBUser register(String username, String password) {
		return usersDB.register(username,password);
	}

	@Override
	public void updateUsersDataFile() {
			writeToJson(usersJsonPath);
	}
	public void updateMoviesDataFile() {
			writeToJson(moviesJsonPath);
	}

	@Override
	public ConcurrentHashMap<String, User> getRegisteredInUsers() {
		return usersDB.getRegisteredUsers();
	}
}
