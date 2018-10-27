package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

/**
 * Action description: attempts to add spaces to the student.
 *
 */
public class AddSpaces extends Action<Boolean>{
		private int spaces;
		
/**
 * @param amount of spaces to add
 */		
		public AddSpaces(int spaces){
			super();
			setActionName("Add Spaces");
			this.spaces=spaces;
		}
		@Override
		protected void start() {
			CoursePrivateState courseState= ((CoursePrivateState)actorState);
			int currSpaces=courseState.getAvailableSpots().intValue();
			if(currSpaces==-1) { // verify the course is open before adding spaces
				complete(false);
				return;
			}
			courseState.setAvailableSpots(currSpaces+spaces);
			complete(true);
		}
}

