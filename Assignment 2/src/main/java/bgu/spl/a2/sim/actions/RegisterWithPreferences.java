package bgu.spl.a2.sim.actions;

import java.util.List;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
	
/**
 * Recursively attempts to register the student to the first course in the list, 
 * until it has registered to one, or couldn't register to any.
 *
 */
public class RegisterWithPreferences extends Action<Boolean>{
	private String studentId;
	private List<String> preferences;
	private List<Integer> grades;
	
	public RegisterWithPreferences(String studentId, List<String> preferences, List<Integer> grades){ 
		super();
		setActionName("Register With Preferences");
		this.studentId=studentId;
		this.preferences=preferences;
		this.grades = grades;
}
	@Override
	protected void start() {
		recursiveTryRegister();
	}
	
	/**
	 * A recursive call to "this" action. 
	 * Executes the recursive continuation of the registration attempt to the next course.
	 * complete() if list of preferences is empty or student has registered to a course. 
	 */
	protected void recursiveTryRegister() {
		if(preferences.isEmpty() || preferences.size()!=grades.size()){
			complete(false);
			return;
		}
		String nextPreferredCourse= preferences.remove(0);
		Integer nextGrade=grades.remove(0);
		Action<Boolean> participateAction=new ParticipateInCourse(studentId, nextPreferredCourse, nextGrade);
		addRequiredAction(participateAction);
		sendMessage(participateAction, nextPreferredCourse, new CoursePrivateState());
		then(requiredActions, ()->{
			Boolean isRegisteredToCourse = (Boolean)(requiredActions.get(0).getResult().get());
			if (isRegisteredToCourse) {
				complete(true);
				return;
			}
			requiredActions.clear();	//The required actions for the next registration attempt should be empty.
			recursiveTryRegister(); 	//recursive call

		});
	}
}