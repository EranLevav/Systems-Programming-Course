
package bgu.spl.a2.sim;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import bgu.spl.a2.Action;
import bgu.spl.a2.ActorThreadPool;
import bgu.spl.a2.PrivateState;
import bgu.spl.a2.json.AbstractAction;
import bgu.spl.a2.json.JsonInput;
import bgu.spl.a2.json.JsonInput.JsonComputer;
import bgu.spl.a2.sim.actions.AddSpaces;
import bgu.spl.a2.sim.actions.AddStudent;
import bgu.spl.a2.sim.actions.AdministrativeCheck;
import bgu.spl.a2.sim.actions.CloseCourse;
import bgu.spl.a2.sim.actions.OpenCourse;
import bgu.spl.a2.sim.actions.ParticipateInCourse;
import bgu.spl.a2.sim.actions.RegisterWithPreferences;
import bgu.spl.a2.sim.actions.Unregister;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator {	
	public static ActorThreadPool actorThreadPool;
	public static Warehouse warehouse;				//warehouse of computers
	public static JsonInput jsonInput;				//The parsed object corresponding the JSON input 
	/**
	* Begin the simulation Should not be called before attachActorThreadPool()
	*/
    public static void start(){
    	actorThreadPool.start();
    	//each phase blocks the next one until complete.
    	try {
	    	executePhase(jsonInput.getPhase1());
	    	executePhase(jsonInput.getPhase2());
	    	executePhase(jsonInput.getPhase3());
    	}
    	catch(InterruptedException ignore) {}
    }
	
	/**
	* attach an ActorThreadPool to the Simulator, this ActorThreadPool will be used to run the simulation
	* 
	* @param myActorThreadPool - the ActorThreadPool which will be used by the simulator
	*/
	public static void attachActorThreadPool(ActorThreadPool myActorThreadPool){
		actorThreadPool=myActorThreadPool;
	}
	
	/**
	* shut down the simulation
	 * @return a Hashmap of private states
	 */
	public static HashMap<String,PrivateState> end(){
		try {
			actorThreadPool.shutdown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		HashMap<String,PrivateState> output= new HashMap<String,PrivateState>();
		output.putAll(actorThreadPool.getActors());
		return output;
	}
	
	
	/**
	 * @param args
	 * 			args[0] is the relative path to the JSON input file
	 * The main method simulates the thread pool framework functionality in our university model.
	 */
	public static void main(String [] args){
		jsonParser(args[0]); //parses the JSON input file into jsonInput
		int nThreads=jsonInput.getThreads().intValue();
		attachActorThreadPool(new ActorThreadPool(nThreads));
		addComputers();
		start();
		HashMap<String , PrivateState> SimulationResult = end();
		//try with resources. Closes IO after operation.
		try(FileOutputStream   fout = new FileOutputStream("result.ser");
			ObjectOutputStream oos  = new ObjectOutputStream(fout);    ) {
			oos.writeObject(SimulationResult);
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
		}
		catch (IOException e) {
			System.out.println("Error with OutputStream.");
		}
		
	}
		
	/**
	 * Initializes the warehouse.
	 * Iterates over the computers in the JSON input and adds corresponding computers to the warehouse.
	 */
	private static void addComputers() {
		HashMap<String, Computer> computers= new HashMap<String, Computer>();
		for(JsonComputer jsonComp: jsonInput.getComputers()) {
			String type= jsonComp.getType();
			Computer computerToAdd= new Computer(type);
			computerToAdd.setFailSig(jsonComp.getFailSig());
			computerToAdd.setSuccessSig(jsonComp.getSuccessSig());
			computers.put(type, computerToAdd);
		}
		warehouse= new Warehouse(computers);
	}

	/**
	 * @param fileName
	 * 				the relative path to the JSON input file
	 * Parses the JSON input file into a JsonInput object
	 */
	private static void jsonParser(String fileName){
		File filePath = new File(fileName);
		//try with resource
		try (BufferedReader reader= new BufferedReader(new FileReader(filePath));){
			String file= reader.lines().collect(Collectors.joining());
			//Fix minor syntax issues and parse ["-"] grades to -1.
			file= file.replaceAll("( : |: | :)", ":").replaceAll("\"-\"", "\"-1\"");
			jsonInput=new Gson().fromJson(file, JsonInput.class);
		}
		catch(JsonSyntaxException ex) {
			System.out.println("JSON syntax error.");
		}
		catch(IOException ex) {
			System.out.println("Error reading file.");
			ex.printStackTrace();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Executes all the actions in the phase, and waits for them to complete before returning. 
	 * @param phase
	 * 			the array of actions in the phase, to be executed. 
	 * @throws InterruptedException
	 */
	private static void executePhase(AbstractAction[] phase) throws InterruptedException{
		int numberOfActions=phase.length;
		// CountdownLatch assures that all actions will be complete before moving on to the next phase.
		CountDownLatch phaseCounter= new CountDownLatch(numberOfActions);
		for(AbstractAction action: phase) {
			//for each action in the JSON object:
			//sends the relevant parameters according to the action's name,
			//creates a corresponding action and submits the action to the actor thread pool- to the corresponding actor.
			//subscribes a callback to the promise to decrement the count of actions resolved in the current phase.
			//when count reaches zero, the execution will move on to the next phase
			Action<?> actionToSubmit;
			switch(action.getActionName()) {
				case "Open Course":
					actionToSubmit=new OpenCourse(action.getCourse(), action.getSpace(), action.getPrerequisites());
					actionToSubmit.getResult().subscribe(()->phaseCounter.countDown()); 
					actorThreadPool.submit(actionToSubmit, action.getDepartment(), new DepartmentPrivateState());
					break;
				case "Add Student":
					actionToSubmit=new AddStudent(action.getStudent());
					actionToSubmit.getResult().subscribe(()->phaseCounter.countDown()); 
					actorThreadPool.submit(actionToSubmit, action.getDepartment(), new DepartmentPrivateState());
					break;
				case "Add Spaces":
					actionToSubmit=new AddSpaces(action.getNumber());
					actionToSubmit.getResult().subscribe(()->phaseCounter.countDown()); 
					actorThreadPool.submit(actionToSubmit, action.getCourse(), new CoursePrivateState());
					break;
				case "Participate In Course":
					int grade=action.getGrade().get(0).intValue();
					actionToSubmit=new ParticipateInCourse(action.getStudent(), action.getCourse(), grade);
					actionToSubmit.getResult().subscribe(()->phaseCounter.countDown()); 
					actorThreadPool.submit(actionToSubmit, action.getCourse(), new CoursePrivateState());
					break;
				case "Register With Preferences":
					actionToSubmit=new RegisterWithPreferences(action.getStudent(), action.getPreferences(),action.getGrade());
					actionToSubmit.getResult().subscribe(()->phaseCounter.countDown()); 
					actorThreadPool.submit(actionToSubmit, action.getStudent(), new StudentPrivateState());
					break;
				case "Unregister":
					actionToSubmit=new Unregister(action.getStudent(), action.getCourse());
					actionToSubmit.getResult().subscribe(()->phaseCounter.countDown()); 
					actorThreadPool.submit(actionToSubmit, action.getCourse(), new CoursePrivateState());
					break;
				case "Close Course":
					actionToSubmit=new CloseCourse(action.getCourse());
					actionToSubmit.getResult().subscribe(()->phaseCounter.countDown()); 
					actorThreadPool.submit(actionToSubmit, action.getDepartment(), new DepartmentPrivateState());
					break;
				case "Administrative Check":
					actionToSubmit=new AdministrativeCheck(action.getStudents(), action.getConditions(),  action.getComputer(), warehouse);
					actionToSubmit.getResult().subscribe(()->phaseCounter.countDown()); 
					actorThreadPool.submit(actionToSubmit, action.getDepartment(), new DepartmentPrivateState());
					break;
			}
		}
		phaseCounter.await(); 
		//Blocking method. will continue only when all the actions in the phase have been resolved.
	}
}
