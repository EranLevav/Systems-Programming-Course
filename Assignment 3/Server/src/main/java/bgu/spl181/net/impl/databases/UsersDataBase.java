package bgu.spl181.net.impl.databases;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;


public class UsersDataBase {
	
	private ArrayList <BBUser> users;
	private transient ConcurrentHashMap<String, User> registeredUsers; // eran - concurrent ?
	private transient ConcurrentHashMap<String, Integer> loggedInUsers; // eran - concurrent ?

	/**
	 * @param users
	 * List of users to be initialized from Json
	 */
	public UsersDataBase(ArrayList<BBUser> users) {
		this.users = users;
	}
	/**
	 * invoked when database is constructed.
	 * adds the users from the JSON file to the registeredUsers HashMap
	 */
	public void init(){
		loggedInUsers= new ConcurrentHashMap<String, Integer>();
		registeredUsers= new ConcurrentHashMap<String, User>();
		for(BBUser user: users) 
			registeredUsers.put(user.getUsername(), user);
	}
	/**
	 * @return the loggedInUsers
	 */
	public ConcurrentHashMap<String, Integer> getLoggedInUsers() {
		return loggedInUsers;
	}

	/**
	 * @return the users
	 */
	public ArrayList<BBUser> getUsers() {
		return users;
	}

	/**
	 * @param userName
	 * @return
	 */
	public boolean remove(String userName) {
		return users.removeIf((user)-> user.getUsername().equals(userName));
	}
	
	 
	/**
	 * @param newUser user to be added
	 * @return true if user was added
	 */
	public boolean add(BBUser newUser) {
		registeredUsers.put(newUser.getUsername(), newUser);
		return users.add(newUser);
	}

	public BBUser login(String username, String password, int connectionId) {
		if(!registeredUsers.containsKey(username)) //if user is not registered- return false;
			return null;
		if(loggedInUsers.containsKey(username)) //if user is logged in- return false
			return null;
		//user registered & not logged in.
		BBUser user=(BBUser)registeredUsers.get(username);
		if(!user.getPassword().equals(password))	//passwords don't match
			return null;
		//username and password match- commit login
		loggedInUsers.put(username, new Integer(connectionId));
		return user;	
	}
	public void signout(String username) {
		loggedInUsers.remove(username);
	}
	public BBUser register(String username, String password) {
		if(registeredUsers.containsKey(username))
			return null;
		BBUser newUser= new BBUser(username, "normal", password, null, new ArrayList<>(), 0);
		add(newUser);
		return newUser;
		
	}
	public ConcurrentHashMap<String, User> getRegisteredUsers() {
		return registeredUsers;
	}
}
