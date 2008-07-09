package com.awebstorm.loadgenerator.robot;
import java.io.InputStream;
import java.util.HashMap;
import java.util.PriorityQueue;
import org.apache.log4j.Logger;


/**
 * Generic Robot operations include Script parsing, general preferences, and stop boolean.
 * @author Cromano
 * @version 1.0
 *
 */
public abstract class Robot implements Runnable {

	protected String robotID;
	protected HashMap<String,String> prefs = new HashMap<String,String>();
	protected PriorityQueue<Step> stepQueue = new PriorityQueue<Step>();
	private boolean stopExecuting;
	protected int httpRequestTimeout;
	private int defaultWaitStep;
	
	/**
	 * Default robot Constructor
	 * Child classes should call this to setup the Robot fields and parse the Script
	 * 
	 * @param script Location of the Robot Script
	 * @param consoleLog Logger to be used for general debugging and info
	 * @param resultLog Logger to be used for results
	 * @param errorLog Logger to be used for all errors in or out of debugging
	 */
	protected Robot( InputStream script ){
		this.stopExecuting = false;
		new ScriptReader().run(script,stepQueue,prefs);
		this.setDefaultRobotPreferences();
	}

	/**
	 * Get the value of the stop thread boolean.
	 * @return stop thread value
	 */
	public synchronized boolean getStopExecuting() {
		return stopExecuting;
	}
	
	/**
	 * Method must be implemented by extending classes, it provides the functionality of
	 * parsing the input file for steps.
	 */
	public abstract void run();

	/**
	 * Load the generic robot preferences.
	 * @param prefsLocation Location of the preferences file
	 */
	private void setDefaultRobotPreferences( ) {
		httpRequestTimeout = Integer.parseInt(prefs.get("timeout"));
		defaultWaitStep = Integer.parseInt(prefs.get("waitstep"));
		robotID = prefs.get("jobID");
	}
	
	/**
	 * Set the value of the stop thread boolean.
	 * @param value New value
	 */
	public synchronized void setStopExecuting(boolean value) {
		this.stopExecuting=value;
	}

}
