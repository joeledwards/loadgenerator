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

	protected HashMap<String,String> prefs = new HashMap<String, String>();
	protected PriorityQueue<Step> stepQueue = new PriorityQueue<Step>();
	protected int httpRequestTimeout;
	private int defaultWaitStep;
	//Robot ID vars
	protected String robotID;
	protected int currentStep;
	protected long timeLastStepFinished;
	protected boolean stopExecuting;
	
	/**
	 * Default robot Constructor.
	 * Child classes should call this to setup the Robot fields 
	 * and parse the Script
	 * 
	 * @param script Location of the Robot Script
	 */
	protected Robot(final InputStream script) {
		this.stopExecuting = false;
		new ScriptReader().run(script,stepQueue,prefs);
		this.setDefaultRobotPreferences();
	}

	/**
	 * Get the value of the stop thread boolean.
	 * @return stop thread value
	 */
	public final synchronized boolean getStopExecuting() {
		return stopExecuting;
	}
	
	/**
	 * Method must be implemented by extending classes, 
	 * it provides the functionality of parsing the 
	 * input file for steps.
	 */
	public abstract void run();

	/**
	 * Load the generic robot preferences.
	 */
	private void setDefaultRobotPreferences() {
		httpRequestTimeout = Integer.parseInt(prefs.get("timeout"));
		defaultWaitStep = Integer.parseInt(prefs.get("waitstep"));
		robotID = prefs.get("jobID");
	}
	
	/**
	 * Set the value of the stop thread boolean.
	 * @param value New value
	 */
	public final synchronized void setStopExecuting(final boolean value) {
		this.stopExecuting = value;
	}

	/**
	 * Get the current Step of the Robot.
	 * @return The current Step of the Robot
	 */
	public final int getCurrentStep() {
		return currentStep;
	}

	/**
	 * Set the current Step of the Robot.
	 * @param newCurrentStep The new current Step of the Robot
	 */
	public final void setCurrentStep(final int newCurrentStep) {
		this.currentStep = newCurrentStep;
	}

	/**
	 * Get the time the last step finished in long format.
	 * @return The time the last step finished
	 */
	public final long getTimeLastStepFinished() {
		return timeLastStepFinished;
	}

	/**
	 * Set the time the last step finished in long format.
	 * @param newTimeLastStepFinished time the last step finished
	 */
	public final void setTimeLastStepFinished(final long newTimeLastStepFinished) {
		this.timeLastStepFinished = newTimeLastStepFinished;
	}

}
