package bgu.spl.a2.sim.actions;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

/**
 * Attempts to register student to course
 *
 */
public class ParticipateInCourse extends Action<Boolean>{
	
	private String studentId;
	private String courseName;
	private int grade;
	
public ParticipateInCourse(String studentId, String course, int grade){ 
		super();
		setActionName("Participate In Course");
		this.studentId=studentId;
		this.courseName=course;
		this.grade = grade;
}
	@Override
	protected void start() {
		CoursePrivateState courseState= ((CoursePrivateState)actorState);
		//if the course has not been opened, its actor was already created with zero spaces. 
		//course has no space for student or the course was closed.
		if (courseState.getAvailableSpots() <= 0) {
			complete(false); 
			return;
		}
		if (!studentHasAllPrequisites()) {
			complete(false);
			return;
		}
		// student will be registered. Decreasing the available spots in advance prevents over-registration
		courseState.decAvailableSpots(); 
		Action<Boolean> addtoStudentAction=new AddCourseToStudent(courseName , grade , courseState.getPrequisites());
		addRequiredAction(addtoStudentAction);
		sendMessage(addtoStudentAction, studentId, new StudentPrivateState());
		//assign continuation callback
		then(requiredActions, ()-> {			
			Boolean result = (Boolean)requiredActions.get(0).getResult().get();
			courseState.getRegStudents().add(studentId);
			courseState.incRegistered();
			complete(result);
		});
	}
	/**
	 * @return true iff student's grades list contains all of the course's prequisites 
	 */
	private boolean studentHasAllPrequisites() {
		CoursePrivateState courseState= ((CoursePrivateState)actorState);
		sendMessage(null, studentId, new StudentPrivateState()); //create student if doesn't exist
		StudentPrivateState studentState = (StudentPrivateState)pool.getPrivateState(studentId);
		List<String> prequisites= courseState.getPrequisites();
		HashMap<String, Integer> studentGradesSnapshot= new HashMap<String, Integer>();
		boolean copied=false;
		while(!copied) { //copy the current grades of the student to the new HashMap
			try {
				studentGradesSnapshot.putAll(studentState.getGrades());
				copied= true;
			}
			catch (ConcurrentModificationException tryAgain) {}
		}
		boolean hasAllPrequisites = true;
		//check if the student has all prerequisites.
		for(String course: prequisites)	{
			hasAllPrequisites = studentGradesSnapshot.containsKey(course); 
			if (!hasAllPrequisites)
				break;
		}
		return hasAllPrequisites;
	}
}
