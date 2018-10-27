package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

/**
 * Sent to the courses actor. Unregisters a student from the course.
 *
 */
public class Unregister extends Action<Boolean>{

	private String studentId;
	private String courseName;
	
	public Unregister(String studentId, String course) {
		setActionName("Unregister");
		this.studentId=studentId;
		this.courseName=course;
	}
	@Override
	protected void start() {
		CoursePrivateState courseState= ((CoursePrivateState)actorState);
		removeCourseFromStudent removeAction = new removeCourseFromStudent(courseName);
		addRequiredAction(removeAction);
		sendMessage(removeAction,studentId, new StudentPrivateState());
		then(requiredActions, ()-> {
			Boolean result = (Boolean)requiredActions.get(0).getResult().get();
			if (result) {
				courseState.decRegistered();
				courseState.getRegStudents().remove(studentId);
				if (courseState.isOpen()) {
					courseState.incAvailableSpots();
				}
			}
			complete(result);
		});		
	}
}
