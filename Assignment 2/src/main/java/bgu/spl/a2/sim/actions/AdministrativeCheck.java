package bgu.spl.a2.sim.actions;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.Computer;
import bgu.spl.a2.sim.Warehouse;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

/**
 * Sent to the department's actor.
 * Attempts to acquire a computer from warehouse. 
 * When acquired, checks the students' administrative obligation and produces a signature accordingly.
 *
 */
public class AdministrativeCheck extends Action<Boolean>{
	private List<String> students;
	private List<String> conditions;
	private String computerName;
	private Warehouse warehouse;
	/**
	 * @param students
	 * @param conditions
	 * @param computer
	 * @param warehouse
	 */
	public AdministrativeCheck(List<String> students, List<String> conditions, String computerName, Warehouse warehouse){
		super();
		setActionName("Administrative Check");
		this.students = students;
		this.conditions = conditions;
		this.computerName= computerName;
		this.warehouse=warehouse;
	}
	@Override
	protected void start() {
		Promise<Computer> computerPromise= warehouse.acquire(computerName); //attempt to acquire mutex
		// set continuation callback:
		continuation=()->{
			Computer computer= computerPromise.get();
			List<String> depStudentList=((DepartmentPrivateState)actorState).getStudentList(); 
			for(String student: students) {
				if(depStudentList.contains(student)) {
					StudentPrivateState studentState=(StudentPrivateState)pool.getPrivateState(student);
					boolean gradesCopied= false;
					HashMap<String,Integer> studentGrades=new HashMap<String,Integer>();
					while(!gradesCopied) { 
						// if another thread tries to access grades while iterating over grades an exception will be thrown. 
						// Will try again until map is copied
						try {
							studentGrades.putAll(studentState.getGrades()); //copy grades map
							gradesCopied=true;
						}
						catch(ConcurrentModificationException tryAgain) {}
					}
					long signature=computer.checkAndSign(conditions,studentGrades);
					SetStudentSignature setSigAction= new SetStudentSignature(signature);
					addRequiredAction(setSigAction);
					sendMessage(setSigAction, student, new StudentPrivateState());
				}	
			}
			then(requiredActions, ()->{
				//when finished, release the mutex.
				warehouse.release(computer.getType());
				complete(true);
			});	
		}; //end of continuation
		
		
		if(computerPromise.isResolved())
			continuation.call();
		else
			computerPromise.subscribe(()->{ pool.submit(this, actorId, actorState); }); 
			//push "this" AdminCheck action back to queue when resolved (computer is available)
		
		
			
		
	}
}


