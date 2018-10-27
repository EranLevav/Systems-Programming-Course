package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

/**
 * Sent to the department's actor. Closes the course.
 *
 */
public class CloseCourse extends Action<Boolean>{
	
	private String course;

/**
 * constructor
 * @param courseName the name of the course to close.
 */	
	
public CloseCourse (String courseName) {
	super();
	this.course = courseName;
	setActionName("Close Course");
}
	
	@Override
	protected void start() {
		// Check if course exists before we try to close it.
		if (!((DepartmentPrivateState)actorState).getCourseList().contains(course)) {
			complete(false);
			return;
		}
		//Check if the course is closed already.
		CoursePrivateState courseState = (CoursePrivateState) pool.getPrivateState(course);
		if (!courseState.isOpen()) {
			complete(false);
			return;
		} 
		
		Action<Boolean> unregisterAll=new UnregisterAll(course);
		addRequiredAction(unregisterAll);
		sendMessage(unregisterAll, course, new CoursePrivateState());
		then(requiredActions, ()-> {	
			Boolean result = (Boolean)requiredActions.get(0).getResult().get();
			complete(result);
		});
	}

}
