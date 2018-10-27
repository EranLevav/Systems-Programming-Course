package bgu.spl.a2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * an abstract class that represents an action that may be executed using the
 * {@link ActorThreadPool}
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add to this class can
 * only be private!!!
 *
 * @param <R> the action result type
 */
public abstract class Action<R> {

	/**
	 * callback to be called upon second invocation of handle()- set by then() method. 
	 */
	protected boolean isStarted; 					//indicates if the action is executed for the first time.
	protected ArrayList<Action<?>> requiredActions; //List of actions that must be resolved prior to resolving this action.
	protected Promise<R> promise; 					//Holds the action's promise object
	protected callback continuation;				//Holds the rest of the action to be executed when executed again.
	protected PrivateState actorState;				
	protected ActorThreadPool pool;
	protected String actionName;
	protected String actorId;
	
	public Action(){
		isStarted=false;
		requiredActions=new ArrayList<Action<?>>();
		promise=new Promise<R>();
		continuation=null;
		//The rest of the fields are set upon calling handle.
	}
	/**
     * start handling the action - note that this method is protected, a thread
     * cannot call it directly.
     */
    protected abstract void start();
    
    /**
    *
    * start/continue handling the action
    *
    * this method should be called in order to start this action
    * or continue its execution in the case where it has been already started.
    *
    * IMPORTANT: this method is package protected, i.e., only classes inside
    * the same package can access it - you should *not* change it to
    * public/private/protected
    *
    */
   /*package*/ final void handle(ActorThreadPool pool, String actorId, PrivateState actorState) {
	   
	   this.pool=pool;
	   this.actorId=actorId;
	   this.actorState=actorState;
	   this.actorState.addRecord(actionName);
	   if(!isStarted){
		   isStarted=true;
		   start();
	   }
	   else
		   continuation.call();
	   pool.unlockQueue(actorId);
	   //if the actor's queue isn't empty, notify other threads that there's an action available.
	   if(!pool.actorIsEmpty(actorId))
		   pool.getVM().inc();
   }
    
    
    /**
     * add a callback to be executed once *all* the given actions results are
     * resolved
     * 
     * Implementors note: make sure that the callback is running only once when
     * all the given actions completed.
     *
     * @param actions
     * @param callback the callback to execute once all the results are resolved
     */
    protected final void then(Collection<? extends Action<?>> actions, callback callback) {
    	this.continuation=callback;
    	AtomicInteger actionCounter=new AtomicInteger(actions.size()); 
        callback promiseCallback=()->{
        	//decrement counter atomically, then check if counter reached zero.
        	//upon reaching zero, all actions it depends on have been resolved- push back to queue
    		if(actionCounter.decrementAndGet()==0)
    			pool.submit(this, actorId, actorState);
    	};
        for(Action<?> action: actions)
        	action.getResult().subscribe(promiseCallback);
    }

    /**
     * resolve the internal result - should be called by the action derivative
     * once it is done.
     *
     * @param result - the action calculated result
     */
    protected final void complete(R result) {
       getResult().resolve(result);
    }
    
    /**
     * @return action's promise (result)
     */
    public final Promise<R> getResult() {
    	return promise;
    }
    
    /**
     * send an action to an other actor
     * 
     * @param action
     * 				the action
     * @param actorId
     * 				actor's id
     * @param actorState
	 * 				actor's private state (actor's information)
	 *    
     * @return promise that will hold the result of the sent action
     */
	public Promise<?> sendMessage(Action<?> action, String actorId, PrivateState actorState){
        pool.submit(action, actorId, actorState);
        Promise<?> p = (action==null) ? null : action.getResult();
        return p;
	}
	
	/**
	 * set action's name
	 * @param actionName
	 */
	public void setActionName(String actionName){
        this.actionName=actionName;
	}
	
	/**
	 * @return action's name
	 */
	public String getActionName(){
        return actionName;
	}
	
	/**
	 * adds an action to the list of actions "this" action depends on to continue.
	 * @param actionToAdd
	 * 				action to be added to list of dependencies.
	 */
	protected void addRequiredAction (Action<?> actionToAdd) {
		requiredActions.add(actionToAdd);
	}
	
}
