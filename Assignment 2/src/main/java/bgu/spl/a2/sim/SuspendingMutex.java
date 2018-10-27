package bgu.spl.a2.sim;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import bgu.spl.a2.Promise;

/**
 * 
 * this class is related to {@link Computer}
 * it indicates if a computer is free or not
 * 
 * Note: this class can be implemented without any synchronization. 
 * However, using synchronization will be accepted as long as the implementation is blocking free.
 *
 */
public class SuspendingMutex {
	
	private AtomicBoolean isFree;
	private Computer computer;
	private ConcurrentLinkedQueue <Promise<Computer>> promiseQueue; 
	// Concurrent nature required to support synchronized add() method (multi-thread addition to queue)
	
	/**
	 * Constructor
	 * @param computer
	 */
	public SuspendingMutex(Computer computer){
		this.computer = computer;
		isFree = new AtomicBoolean(true);
		promiseQueue = new ConcurrentLinkedQueue <Promise<Computer>>();
	}
	/**
	 * Computer acquisition procedure
	 * Note that this procedure is non-blocking and should return immediately
	 * 
	 * @return a promise for the requested computer
	 */
	
	public Promise<Computer> down(){
		Promise<Computer> computerPromise= new Promise <Computer>();
		promiseQueue.add(computerPromise);
		resolveNext();
		return computerPromise;
	}
	
	/**
	 * resolves the next promise in the mutex's queue (the next consumer)
	 */
	private void resolveNext() {
		Promise<Computer> nextPromise;
		if (isFree.compareAndSet(true, false)) {
			nextPromise= promiseQueue.poll();
			if(nextPromise!=null)
				nextPromise.resolve(computer);
			else
				isFree.set(true);
		}

	}
	/**
	 * Computer return procedure
	 * releases a computer which becomes available in the warehouse upon completion
	 */
	public void up(){
		isFree.set(true);
		if (!promiseQueue.isEmpty())
			resolveNext();
	}
}
