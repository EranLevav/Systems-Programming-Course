package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

/**
 * Sent to the student's actor. Removes the course from the student's grades HashMap.
 *
 */
public class removeCourseFromStudent extends Action<Boolean> {
	
	private String courseName;

	public removeCourseFromStudent(String course) {
		super();
		setActionName("removeCourseFromStudent");
		this.courseName = course;

	}
	@Override
	protected void start() {
		StudentPrivateState studentState = (StudentPrivateState)actorState;
		boolean deleted=false;
		if (studentState.getGrades().containsKey(courseName)) {
			studentState.getGrades().remove(courseName);
			deleted = true;
		}
		complete(deleted);
				
	}
}

