package bgu.spl181.net.srv;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.srv.bidi.ConnectionHandler;

public class ConnectionsImpl<T> implements Connections<T> {

	private ConcurrentHashMap<Integer, ConnectionHandler<T>> connections;
	private ReentrantLock lock;
	
	/**
	 * @param connections
	 */
	public ConnectionsImpl() {
		this.connections = new ConcurrentHashMap<Integer, ConnectionHandler<T>>();
		this.lock=new ReentrantLock();
	}
	public void addConnection(int connectionId, ConnectionHandler<T> handler){
		connections.put(connectionId, handler);
	}
	public boolean send(int connectionId, T msg) {
		ConnectionHandler<T> connection=connections.get(connectionId);
		if (connection!=null){
			connection.send(msg);
			return true;
		}
		return false;
	}

	public void broadcast(T msg) {
		//send the message via each connection handler in the map
		lock.lock();
		connections.values().forEach((connectionHandler)->connectionHandler.send(msg));
		lock.unlock();
	}

	public void disconnect(int connectionId) {
		connections.remove(connectionId);
	}
	
	/**
	 * Print all the connectionIds
	 * Used for testing
	 */
	public void printMap() {
		for(Integer connId: connections.keySet())
			System.out.print("["+ connId+ "] ");
		System.out.println();
		
	}
}
