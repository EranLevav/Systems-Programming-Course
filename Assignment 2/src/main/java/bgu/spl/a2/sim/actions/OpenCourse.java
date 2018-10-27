package bgu.spl.a2.sim.actions;

import java.util.List;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

/**
 * Sent to the department's actor.
 * Opens a new course. 
 *
 */
public class OpenCourse extends Action<Boolean>{
	private String courseName;
	private int spaces;
	private List<String> prerequisites;
	
/**
 * constructor
 * @param course
 * @param space
 * @param List<String> prerequisites
 */		
	public OpenCourse(String course, int space, List<String> prerequisites){
		super();
		setActionName("Open Course");
		this.courseName=course;
		this.spaces=space;	
		this.prerequisites= prerequisites;
	}
	@Override
	protected void start() {
		CoursePrivateState courseState= new CoursePrivateState();
		sendMessage(null, courseName, courseState); //create course actor in ThreadPool
		courseState= (CoursePrivateState) pool.getPrivateState(courseName);
		//Initializes the course, in the case that spaces where added etc. beforehand.
		courseState.setAvailableSpots(spaces);
		courseState.setPrequisites(prerequisites);
		courseState.setRegistered(0);
		((DepartmentPrivateState)actorState).getCourseList().add(courseName);
		complete(true);
	}

}
