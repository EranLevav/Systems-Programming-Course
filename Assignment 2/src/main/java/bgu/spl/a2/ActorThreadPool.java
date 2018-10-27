package bgu.spl.a2;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.CountDownLatch;


/**
 * represents an actor thread pool - to understand what this class does please
 * refer to your assignment.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class ActorThreadPool {
	protected VersionMonitor vm;
	protected ConcurrentHashMap<String,AtomicBoolean> lockMap; //A HashMap that holds the state of the actor's queues (available/ locked)
	protected ConcurrentHashMap<String,PrivateState> stateMap;
	protected ConcurrentHashMap<String,Queue<Action<?>>> actorQueueMap;
	protected ArrayList<Thread> threads;
	protected CountDownLatch shutdownCounter; //Used to wait on threads to finish execution before shutting downb the pool.
	/**
	 * creates a {@link ActorThreadPool} which has nthreads. Note, threads
	 * should not get started until calling to the {@link #start()} method.
	 *
	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 *
	 * @param nthreads
	 *            the number of threads that should be started by this thread
	 *            pool
	 */
	public ActorThreadPool(int nthreads) {
		vm=new VersionMonitor();
		lockMap= new ConcurrentHashMap<>();
		stateMap= new ConcurrentHashMap<>();
		actorQueueMap=new ConcurrentHashMap<>();
		threads= new ArrayList<>();
		initThreadsList(nthreads);
		shutdownCounter=new CountDownLatch(nthreads);
	}
	
	/**
	 * Creates n threads with an event loop runnable, then adds them to the thread list.
	 * @param nthreads number of threads to add to threads list
	 */
	private void initThreadsList(int nthreads) {
		for (int i = 0; i < nthreads; i++) {
			Runnable eventLoop= ()->{ //Thread's run()
				while(!Thread.currentThread().isInterrupted()){
					try {
						int version=vm.getVersion();
						boolean aquiredActor=false;
						for(Entry<String,Queue<Action<?>>> e: actorQueueMap.entrySet()){
							aquiredActor=tryLockActor(e.getKey());
							if(Thread.currentThread().isInterrupted())
								break;
						}
						if(Thread.currentThread().isInterrupted())
							break;
						if(!aquiredActor)
							vm.await(version);
					} 
					catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
				//when interrupted, loop breaks. Decrease terminated threads counter
				shutdownCounter.countDown();
			};
			Thread thread= new Thread(eventLoop);
			threads.add(thread);
		}		
	}
/**
  *If no other thread occupies the actor, "lock" its action queue and execute the first action in it.
 * @param actorId 
 * 				the ID of the actor from whom we are trying to fetch an action.
 */
private boolean tryLockActor(String actorId){
		boolean aquiredLock=false;
		Queue<Action<?>> actorQueue = actorQueueMap.get(actorId);
		if(actorQueue.isEmpty())
			return false;
		AtomicBoolean isLocked=lockMap.get(actorId);
		if(isLocked.compareAndSet(false, true)){
			Action<?> actionToExecute= actorQueue.poll();
			aquiredLock=(actionToExecute!=null);
			if(!aquiredLock) //queue is empty
				isLocked.set(false);
			if(aquiredLock && !Thread.currentThread().isInterrupted()) {
				actionToExecute.handle(this, actorId, stateMap.get(actorId));
			}
		}
		return aquiredLock;
 }
	/**
	 * getter for actors
	 * @return actors
	 */
	public Map<String, PrivateState> getActors(){
		return stateMap;
	}
	
	/**
	 * getter for actor's private state
	 * @param actorId actor's id
	 * @return actor's private state
	 */
	public PrivateState getPrivateState(String actorId){
		return stateMap.get(actorId);
	}

	/**
	 * submits an action into an actor to be executed by a thread belongs to
	 * this thread pool
	 *
	 * @param action
	 *            the action to execute
	 * @param actorId
	 *            corresponding actor's id
	 * @param actorState
	 *            actor's private state (actor's information)
	 */
	public void submit(Action<?> action, String actorId, PrivateState actorState) {
		if(actorState==null)
			return;
		if(stateMap.get(actorId)==null){ //create actor if non-existent
			Queue<Action<?>> actorQueue = new ConcurrentLinkedQueue<Action<?>>();
			actorQueueMap.put(actorId, actorQueue);
			stateMap.put(actorId, actorState);
			lockMap.put(actorId,new AtomicBoolean(false));
		}
		if(action==null) //message sent in order to create a new actor
			return;
		Queue<Action<?>> actorQ= actorQueueMap.get(actorId);
		actorQ.add(action);
		vm.inc();
	}

	/**
	 * closes the thread pool - this method interrupts all the threads and waits
	 * for them to stop - it is returns *only* when there are no live threads in
	 * the queue.
	 *
	 * after calling this method - one should not use the queue anymore.
	 *
	 * @throws InterruptedException
	 *             if the thread that shut down the threads is interrupted
	 */
	public void shutdown() throws InterruptedException {
		for(Thread t: threads)
			t.interrupt();
		shutdownCounter.await(); //wait on all threads to terminate before returning.
		return;
			
	}

	/**
	 * start the threads belongs to this thread pool
	 */
	public void start() {
		for (Thread t: threads) 
			t.start();
	}
	
	/**
	 * @return the pool's VersiomMonitor
	 */
	protected VersionMonitor getVM(){
		return vm;
	}
	
	/**
	 * @param actorId
	 * @return true if the actor's queue is empty
	 */
	protected boolean actorIsEmpty(String actorId){
		return actorQueueMap.get(actorId).isEmpty();
	}
	/**
	 * @param actorId
	 * unlocks the actor's action queue
	 */
	protected void unlockQueue(String actorId){
		lockMap.get(actorId).set(false);
	}
}
