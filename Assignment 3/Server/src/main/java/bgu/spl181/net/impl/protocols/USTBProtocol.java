
package bgu.spl181.net.impl.protocols;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.impl.databases.SharedData;
import bgu.spl181.net.impl.databases.User;

public abstract class USTBProtocol implements BidiMessagingProtocol<List<String>> {
	protected SharedData sharedData;
	protected Connections<List<String>> connections;
	protected int connectionId;
	protected boolean shouldTerminate;
	protected boolean isLoggedIn;
	protected User user;
	protected ReentrantReadWriteLock usersLock;
	
    public USTBProtocol(SharedData sharedData) {
        this.sharedData = sharedData;
        this.usersLock=sharedData.getUsersDBLock();
    }
	public void start(int connectionId, Connections<List<String>> connections){
		this.connectionId=connectionId;
		this.connections=connections;
		this.shouldTerminate=false;
		this.isLoggedIn=false;
	}
	public void process(List<String> message){
		String commandName=message.remove(0);
		switch(commandName){
			case "REGISTER":
				registerCommand(message);
				break;
			case "LOGIN":
				loginCommand(message);
				break;
			case "SIGNOUT":
				signoutCommand(message);
				break;
			case "REQUEST":
				handleRequest(message); //abstract: depends on service implementation.
				break;
			default:
				errorCommand("unknown command");
				break;
		}
	}
	protected void broadcastCommand(String message) {
			List<String> msg=new LinkedList<>();
			msg.add("BRODCAST "+ message);
			for(Integer userId: sharedData.getLoggedInUsers().values())
				connections.send(userId, msg);
	}
	/**
	 * REGISTER
	 */
	protected void registerCommand(List<String> message) {
		if(isLoggedIn || message.size()<2){		//user logged in or missing argument
			errorCommand("registration failed");
			return;
		}
		String username= message.remove(0);
		String password= message.remove(0);
		usersLock.writeLock().lock();
		User registeredUser= sharedData.register(username, password);
		if(registeredUser==null){
			usersLock.writeLock().unlock();
			errorCommand("registration failed");
			return;
		}
		if(!message.isEmpty())
			handleRegisterData(message, registeredUser);
		sharedData.updateUsersDataFile(); //UPDATE JSON FILE
		usersLock.writeLock().unlock();
		acknowledgeCommand("registration succeeded");
	}
	/**
	 * SIGNOUT
	 * without first argument
	 */
	protected void signoutCommand(List<String> message) {
		if(!isLoggedIn || !message.isEmpty()){
			errorCommand("signout failed");
			return;
		}
		
		usersLock.writeLock().lock();
		isLoggedIn=false;
		sharedData.signout(user.getUsername());
		usersLock.writeLock().unlock();

		acknowledgeCommand("signout succeeded");
		connections.disconnect(connectionId);
		shouldTerminate=true;
	}

	/**
	 * ERROR
	 */
	protected void errorCommand(String errorMessage) {
		List<String> msg=new LinkedList<>();
		msg.add("ERROR "+ errorMessage);
		connections.send(connectionId, msg);
	}
	
	/**
	 * ACKNOWLEDGE
	 * @param ackMessage
	 * 			message to be sent to client
	 */
	protected void acknowledgeCommand(String ackMessage) {
		List<String> msg=new LinkedList<>();
		msg.add("ACK "+ ackMessage);
		connections.send(connectionId, msg);
	}

	/**
	 * LOGIN
	 */
	protected void loginCommand(List<String> message) {
		if (isLoggedIn || message.size()<2){
			errorCommand("login failed");
			return;
		}
		String username=message.remove(0);
		String password=message.remove(0);
		
		usersLock.writeLock().lock();
		User loginUser= sharedData.login(username, password, connectionId);
		if(loginUser!=null){
			isLoggedIn=true;
			this.user=loginUser;
			acknowledgeCommand("login succeeded");
			usersLock.writeLock().unlock();
		}
		else {
			usersLock.writeLock().unlock();
			errorCommand("login failed");
		}
	}

	public boolean shouldTerminate(){
		return shouldTerminate;
	}
	
	protected abstract void handleRequest(List<String> message);
	protected abstract void handleRegisterData(List<String> message, User toCompleteRegister);
	
}
