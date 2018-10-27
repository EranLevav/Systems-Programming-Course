package bgu.spl.a2;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;
import java.util.Arrays;

public class PromiseTest {

	/**
	 * tests get() method with JUnit
	 */
	@Test
	public void testGet() {
		Promise<Integer> promise= new Promise<Integer>();
		try{
			promise.get();
			Assert.fail("Expected: [IllegalStateException] after get() before resolve");
		}
		catch(IllegalStateException goodEx){
			int expectedVal=1;
			promise.resolve(expectedVal);
			try{
				int result= promise.get();
				Assert.assertEquals("get(): Promise value doesn't match expected value", expectedVal, result);
			}
			catch(Exception badEx){
				Assert.fail("get() failed. Exception: "+badEx.getMessage());
			}
		}
		catch(Exception badEx){
			Assert.fail("get() failed. Exception: "+badEx.getMessage());
		}
		

	}
	
	/**
	 * tests isResolved() method with JUnit
	 */
	@Test
	public void testIsResolved() {
		try{
			Promise<Integer> promise= new Promise<>();
			boolean isResolved= promise.isResolved();
			if(isResolved){
				Assert.fail("isResolved=true for unresolved promise.");
			}
			try{
				int resolveValue=0;
				promise.resolve(resolveValue);
				if(!isResolved){
					Assert.fail("isResolved=false for resolved promise.");
				}
			}
			catch(Exception badEx){
				Assert.fail("resolve() failed. Exception: "+badEx.getMessage());
			}
		}
		catch(Exception badEx){	
			Assert.fail("isResolved() failed. Exception: "+badEx.getMessage());
		}
	}
	
	/**
	 * tests resolve() method with JUnit
	 */
	@Test
	public void testResolve() {
		try{
			Promise<Integer> promise= new Promise<>();
			int expectedVal=1;
			int numOfCallbacks= 10;
			
			int[] arr= addCallbacks(promise, numOfCallbacks); //array of 0's
			promise.resolve(expectedVal);
			testCallbacks(arr);
			try{
				promise.resolve(expectedVal+1);
				Assert.fail("Resolved with two different values.");
			}
			catch (IllegalStateException goodEx) {
				int getValue= promise.get();
				assertEquals("resolve(): Promise value doesn't match expected value", getValue, expectedVal);
			}
			
			catch(Exception wrongEx){
				Assert.fail("Expected: [IllegalStateException]"+'\n'+"Actual: "+wrongEx.getClass().getCanonicalName());
			}
		}
		catch(Exception badEx){	
			Assert.fail("resolve() failed. Exception: "+badEx.getMessage());
		}
	}

	/**
	 * tests subscribe() method with JUnit
	 */
	@Test
	public void testSubscribe() {
		try{
			int numOfCallbacks= 10;
			Promise<Integer> promise=new Promise<>();
			addCallbacks(promise, numOfCallbacks);
			promise.resolve(1);
			
			boolean a[]={false};
			promise.subscribe(()-> {a[0]=true;});
			Assert.assertEquals("Callback not invoked immidiately when resolved", a[0], true);
		}
		catch(Exception badEx){
			Assert.fail("resolve() failed. Exception: "+badEx.getMessage());
		}
	}
	
	/**
	 * @param actual array received after calling resolved, expected to be array of 1's
	 */
	private void testCallbacks(int[] actual) {
		int[] expected= new int[actual.length];
		Arrays.fill(expected,1);
		Assert.assertArrayEquals("Callbacks not invoked properly.", expected, actual);
	}

	/**
	 * @param <T>
	 * @param p Promise
	 * @param numOfCallbacks number of callbacks to be added
	 * @return reference to array of numOfCallbacks 0's, to be changed to 1's once resolved 
	 */
	private <T> int[] addCallbacks(Promise<T> p, int numOfCallbacks){
		int arrLength=numOfCallbacks;
		int[]arr= new int[arrLength];
		for(int element: arr){
			element=0;
		}
		for(int i=0; i<arr.length; i++){
			int index=i;
			callback b= ()->{arr[index]=1;};
			try{ p.subscribe(b); }
			catch(Exception badEx){ Assert.fail("subscribe() failed");}
		}
		return arr;
	}
}