package bgu.spl.a2.sim.privateStates;

import java.util.ArrayList;
import java.util.List;

import bgu.spl.a2.PrivateState;

/**
 * this class describe course's private state
 */
@SuppressWarnings("serial")
public class CoursePrivateState extends PrivateState{

	private Integer availableSpots;
	private Integer registered;
	private List<String> regStudents;
	private List<String> prequisites;
	
	/**
 	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 */
	public CoursePrivateState() {
		super();
		regStudents=new ArrayList<String>();
		prequisites=new ArrayList<String>();
		registered=0;
		availableSpots=0;
	}

	/**
	 * @return available spots
	 */
	public Integer getAvailableSpots() {
		return availableSpots;
	}
	
	/**
	 * @param availableSpots
	 * 				new number of available spots
	 */
	public void setAvailableSpots(int availableSpots) {
		this.availableSpots=new Integer(availableSpots);
	}
	/**
	 * Increases number of available spots by one.
	 */
	public void incAvailableSpots() {
		this.availableSpots=new Integer(availableSpots.intValue()+1);
	}
	/**
	 * Decreases number of available spots by one.
	 */
	public void decAvailableSpots() {
		this.availableSpots=new Integer(availableSpots.intValue()-1);
	}

	
	/**
	 * @return the number of students registered to the course.
	 */
	public Integer getRegistered() {
		return registered;
	}
	
	/**
	 * @param the new number of students registered to the course.
	 */
	public void setRegistered(int registered) {
		this.registered=new Integer(registered);
	}
	
	/**
	 * Increases the new number of students registered to the course by one.
	 */
	public void incRegistered() {
		this.registered=new Integer(registered.intValue()+1);
	}
	/**
	 * Decreases the new number of students registered to the course by one.
	 */
	public void decRegistered() {
		this.registered=new Integer(registered.intValue()-1);
	}

	/**
	 * @return the list of students registered to the course.
	 */
	public List<String> getRegStudents() {
		return regStudents;
	}

	/**
	 * @return the list of prequisites of the course.
	 */
	public List<String> getPrequisites() {
		return prequisites;
	}
	
	/**
	 * @param prequisites
	 * 				the list of prequisites of the course.
	 */
	public void setPrequisites(List<String> prequisites) {
		this.prequisites=new ArrayList<String>(prequisites);
	}

	/**
	 * @return true iff the course is open.
	 */
	public boolean isOpen() {
		return availableSpots.intValue()>=0;
	}
}
