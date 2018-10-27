package bgu.spl.a2;

import static org.junit.Assert.*;

import org.junit.Test;

import junit.framework.Assert;

public class VersionMonitorTest {

	/**
	 * test method with JUnit
	 */
	@Test
	public void testGetVersion(){
		try { 
			VersionMonitor vm= new VersionMonitor();
    		Assert.assertEquals("Initial version not set to 0",0, vm.getVersion());
    	}
		catch(Exception badEx){
			Assert.fail("getVersion() failed. Exception: "+badEx.getMessage());
		}
	}
	
	/**
	 * test inc() method with JUnit
	 */
	@Test
	public void testInc() {
		try { 
			VersionMonitor vm= new VersionMonitor();
			vm.inc();
			Assert.assertEquals("Version not incremented properly",1, vm.getVersion());
    	}
		catch(Exception badEx){
			Assert.fail("inc() failed. Exception: "+badEx.getMessage());
		}
	}
	
	
	/**
	 * test await() method with JUnit
	 */
	@Test
	public void TestAwait(){
		VersionMonitor vm=new VersionMonitor();
		boolean[] result={false};
    	Thread toAwait = new Thread() {
    	@Override
    	public void run() {
	    	try {
	    		vm.await(1); //(2)
	    	}
	    	catch (Exception badEx){
	    		Assert.fail("await() should return. Throws ex: " + badEx.getMessage());
	    	}
	    	try{
	    		Thread.sleep(2000); //(3)
	    	}
	    	catch (InterruptedException ex)	{}
	    	try{
		    		vm.await(vm.getVersion());	//(5)
	    	}
	    	catch(InterruptedException ex){
	    		result[0] = true;
	    	}
	    	catch (Exception badEx){
	    		Assert.fail("await() should return. Throws ex: " + badEx.getMessage());
	    	}
    	}
    };
    	
		toAwait.start();  //(1)
		try{
			Thread.sleep(500);
		}
		catch(InterruptedException ex){}
		Assert.assertFalse("Should be TIMED_WAITING state (sleeping thread)", toAwait.getState()==Thread.State.WAITING);
		try{
			Thread.sleep(3000);
		}
		catch(InterruptedException ex){}
		Assert.assertEquals("Should be waiting state",toAwait.getState(), Thread.State.WAITING);//(6)
		vm.inc(); //(4)
		try {
			Thread.sleep(3000);
		}
		catch (InterruptedException ex){}
		if (!result[0]==true){
			Assert.fail("Thread still in waiting status after version change");
		}
	}
}


