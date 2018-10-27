package bgu.spl.a2.sim.actions;

import java.util.List;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

/**
 * Sent to the student's actor.
 * Adds a course to the student's grades HashMap.
 *
 */
public class AddCourseToStudent extends Action<Boolean>{
	
	private String courseName;
	private int grade;
	@SuppressWarnings("unused")
	private List<String> prequisites;
	
	/**
	 * @param course
	 * @param grade
	 * @param prequisites
	 * 				list of the course's prequisites 
	 */
	public AddCourseToStudent(String course, int grade,List<String> prequisites){ 
		super();
		setActionName("AddCourseToStudent");
		this.courseName=course;
		this.grade = grade;
		this.prequisites=prequisites;
	}

	@Override
	protected void start() {
		StudentPrivateState studentState = (StudentPrivateState)actorState;
		studentState.addCourse(courseName, grade);
		complete(true);
	}

}
