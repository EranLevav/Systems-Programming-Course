package bgu.spl.a2.sim.actions;

import java.util.List;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

/**
 * Sent to the course's actor. Closes the course and Unregister()s all students from it.
 *
 */
public class UnregisterAll extends Action<Boolean> {

	private String course;
	
	public UnregisterAll (String courseName) {
		super();
		this.course = courseName;
		setActionName("UnregisterAll");
	}
	@Override
	protected void start() {
		//First we close the course (by setting AvailableSpots to -1) to prevent adding students to the course.
		CoursePrivateState courseState = (CoursePrivateState)actorState;
		courseState.setAvailableSpots(-1);
		List <String> studentList= courseState.getRegStudents();
		if(studentList.isEmpty()) {
			complete(true);
			return;
		}
		for(String student: studentList){
			Action<Boolean> unregisterPerStudent=new Unregister(student, course);
			addRequiredAction(unregisterPerStudent);
			sendMessage(unregisterPerStudent, course, actorState);
		}
		then(requiredActions, () -> {
			complete(courseState.getRegistered()== 0);
		});
			
	}
}