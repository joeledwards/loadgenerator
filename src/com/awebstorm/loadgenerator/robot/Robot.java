package com.awebstorm.loadgenerator.robot;
import java.io.InputStream;
import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * Generic Robot operations include Script parsing, general preferences, and stop boolean.
 * @author Cromano
 * @version 1.0
 *
 */
public abstract class Robot implements Runnable {

	protected HashMap<String, String> prefs = new HashMap<String, String>();
	protected PriorityQueue<Step> stepQueue = new PriorityQueue<Step>();
	protected int httpRequestTimeout;
	private static int defaultWaitStep = 1000;
	//Robot ID vars
	protected String robotID;
	protected Step currentStep;
	protected long timeLastStepFinished;
	protected boolean stopExecuting;
	protected Thread t;
	
	/**
	 * Initialize a Robot in its own Thread.
	 */
	public final void init() {	
		t = new Thread(this);
		t.start();
	}
	
	/**
	 * Default robot Constructor.
	 * Child classes should call this to setup the Robot fields 
	 * and parse the Script
	 * 
	 * @param script Location of the Robot Script
	 */
	protected Robot(final InputStream script) {
		this.stopExecuting = false;
		new ScriptReader().run(script, stepQueue, prefs);
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
	public final Step getCurrentStep() {
		return currentStep;
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
	/**
	 * Retrieve my Thread.
	 * @return My Thread
	 */
	public final Thread getMyThread() {
		return t;
	}
	
	/**
	 * Retrieve the value of the defaultWaitStep.
	 * @return Length of the default WAIT step
	 */
	public static int getDefaultWaitStep() {
		return defaultWaitStep;
	}

}
