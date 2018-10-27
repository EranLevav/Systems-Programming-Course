package bgu.spl.a2.sim;

import java.util.HashMap;
import bgu.spl.a2.Promise;

/**
 * represents a warehouse that holds a finite amount of computers
 * and their suspended mutexes.
 * releasing and acquiring should be blocking free.
 */
public class Warehouse {
	@SuppressWarnings("unused") // Computers are used via the mutexes. There's no direct access from the warehouse.
	private HashMap<String, Computer> computers;
	private HashMap<String, SuspendingMutex> mutexes;
	
	/**
	 * @param computers
	 * 				list of computers to be added to warehouse
	 */
	public Warehouse(HashMap<String, Computer> computers) {
		this.computers = computers;
		//Adds a mutex to each computer, from which it will be acquired
		this.mutexes = new HashMap<String, SuspendingMutex>();
		computers.forEach((name,computer)->{
			mutexes.put(name, new SuspendingMutex(computer));
		});
	}

	/**
	 * Attempts to acquire the mutex of the computer
	 * @param computerName
	 * @return a promise that when resolved will contain the computer that corresponds the type.
	 */
	public Promise<Computer> acquire(String computerName) {
		return mutexes.get(computerName).down();
	}

	/**
	 * @param computerName
	 * Releases the computerName's mutex
	 */
	public void release(String computerName) {
		mutexes.get(computerName).up();
	}




	
	
	
}
