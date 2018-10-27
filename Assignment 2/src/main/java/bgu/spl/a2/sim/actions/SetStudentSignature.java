package bgu.spl.a2.sim.actions;
import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

/**
 * Sent to the student's actor. Sets its signature.
 *
 */
public class SetStudentSignature extends Action<Boolean>{
	private long signature;
	
	public SetStudentSignature(long signature) {
		super();
		setActionName("SetStudentSignature");
		this.signature=signature;
	}

	@Override
	protected void start() {
		((StudentPrivateState)actorState).setSignature(signature);
		complete(true);
	}
}