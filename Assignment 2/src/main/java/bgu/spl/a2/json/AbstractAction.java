package bgu.spl.a2.json;
import java.util.List;

import com.google.gson.annotations.SerializedName;
// This is a generic POJO to hold a JSON parsed action object. 
// There's also the option of using RuntimeTypeAdapterFactory for more elegant and action-specific implementation.
// In this one-size-fits-all approach, non-exist fields will be parsed to null.
public class AbstractAction {
	//All possible fields in JSON input, serialized by name.
	@SerializedName("Action")
	private String actionName;
	@SerializedName("Department")
	private String department;
	@SerializedName("Course")
	private String course;
	@SerializedName("Space")
	private int space;
	@SerializedName("Prerequisites")
	private List<String> prerequisites;
	@SerializedName("Conditions")
	private List<String> conditions;
	@SerializedName("Computer")
	private String computer;
	@SerializedName("Students")
	private List<String> students;
	@SerializedName("Student")
	private String student;
	@SerializedName("Preferences")
	private List<String> preferences;
	@SerializedName("Grade")
	private List<Integer> grade;
	@SerializedName("Number")
	private int number;

	/**
	 * constructor
	 * @param actionName
	 * @param department
	 * @param course
	 * @param space
	 * @param prerequisites
	 * @param conditions
	 * @param computer
	 * @param students
	 * @param student
	 * @param preferences
	 * @param grade
	 * @param number
	 */
	public AbstractAction(String actionName, String department, String course, int space, List<String> prerequisites,
			List<String> conditions, String computer, List<String> students, String student, List<String> preferences,
			List<Integer> grade, int number) {
		this.actionName = actionName;
		this.department = department;
		this.course = course;
		this.space = space;
		this.prerequisites = prerequisites;
		this.conditions = conditions;
		this.computer = computer;
		this.students = students;
		this.student = student;
		this.preferences = preferences;
		this.grade = grade;
		this.number = number;
	}

	public String getActionName() {
		return actionName;
	}

	public String getDepartment() {
		return department;
	}

	public String getCourse() {
		return course;
	}

	public int getSpace() {
		return space;
	}

	public List<String> getPrerequisites() {
		return prerequisites;
	}

	public List<String> getConditions() {
		return conditions;
	}

	public String getComputer() {
		return computer;
	}

	public List<String> getStudents() {
		return students;
	}

	public String getStudent() {
		return student;
	}

	public List<String> getPreferences() {
		return preferences;
	}

	public List<Integer> getGrade() {
		return grade;
	}

	public int getNumber() {
		return number;
	}

}
