package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

/**
 * Sent to the department's actor. Adds a student to the department.
 *
 */
public class AddStudent extends Action<Boolean>{
	private String studentID;

/**
* constructor
* @param studentID
*/		
	public AddStudent( String studentID){
		super();
		setActionName("Add Student");
		this.studentID=studentID;
	}

	@Override
	protected void start() {
		//create a new actor
		sendMessage(null, studentID, new StudentPrivateState());	
		((DepartmentPrivateState)actorState).getStudentList().add(studentID);
		complete(true);
	}

}	
	
	

