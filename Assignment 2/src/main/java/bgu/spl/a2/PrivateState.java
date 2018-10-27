package bgu.spl.a2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * an abstract class that represents private states of an actor
 * it holds actions that the actor has executed so far 
 * IMPORTANT: You can not add any field to this class.
 */
@SuppressWarnings("serial")
public abstract class PrivateState implements Serializable{
	
	// holds the actions that were executed by the corresponding actor
	private List<String> history;
	
	public PrivateState(){
		history=new ArrayList<String>();
	}
	/**
	 * @return the history list of recorded actions
	 */
	public List<String> getLogger(){
		return history;
	}
	
	/**
	 * add an action to the records
	 *  
	 * @param actionName
	 * 				action to be added
	 */
	public void addRecord(String actionName){
		history.add(actionName);
	}
	
	
}
