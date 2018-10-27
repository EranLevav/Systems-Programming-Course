package bgu.spl.a2.sim;

import java.util.List;
import java.util.Map;

public class Computer {

	String computerType;
	long failSig;
	long successSig;
	

	public Computer(String computerType) {
		this.computerType = computerType;
	}
	
	/**
	 * @param failSig
	 * 				signature to be assigned to students that don't match administrative obligations.
	 */
	public void setFailSig(long failSig) {
		this.failSig=failSig;
	}
	/**
	 * @param successSig
	 * 				signature to be assigned to students that match administrative obligations.
	 */
	public void setSuccessSig(long successSig) {
		this.successSig=successSig;
	}
	/**
	 * this method checks if the courses' grades fulfill the conditions
	 * @param courses
	 * 							courses that should be passed
	 * @param coursesGrades
	 * 							courses' grade
	 * @return a signature if couersesGrades grades meet the conditions
	 */
	public long checkAndSign(List<String> courses, Map<String, Integer> coursesGrades){
		boolean passedAllCourses = true;
		for(String course: courses)	{
			Integer grade = coursesGrades.get(course);
			if (grade == null || grade < 56) {
				passedAllCourses = false;
				break;
			}
		}
		if (passedAllCourses)
			return successSig;
		return failSig;
	}

	/**
	 * @return the computer's name/type
	 */
	public String getType() {
		return computerType;
	}
	
	@Override
	public String toString() {
		return "Computer [computerType=" + computerType + ", failSig=" + failSig + ", successSig=" + successSig + "]"+"\n";
	}
}
