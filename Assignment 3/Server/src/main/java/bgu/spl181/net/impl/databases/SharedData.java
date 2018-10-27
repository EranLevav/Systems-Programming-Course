package bgu.spl181.net.impl.databases;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Represents a shared data object.
 * enables access to all USTBprotocol users
 *
 */
public interface SharedData {
	ConcurrentHashMap<String, Integer> getLoggedInUsers();
	ConcurrentHashMap<String, User> getRegisteredInUsers();
	User register(String username, String password);
	User login(String username, String password, int connectionId);
	void signout(String username);
	void updateUsersDataFile();
	ReentrantReadWriteLock getUsersDBLock();
}
