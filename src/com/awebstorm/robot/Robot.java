package com.awebstorm.robot;
import java.io.InputStream;
import java.util.HashMap;
import java.util.PriorityQueue;

import com.awebstorm.Proxy;

/**
 * Generic Robot operations include Script parsing, general preferences, and stop boolean.
 * @author Cromano
 * @version 1.0
 *
 */
public abstract class Robot implements Runnable {

	protected HashMap<String, String> prefs = new HashMap<String, String>();
	protected PriorityQueue<Step> stepQueue = new PriorityQueue<Step>();
	private String targetDomain;
	private int targetPort;
	private static int defaultWaitStep = 1000;
	private InputStream scriptStream;
	//Robot ID vars
	protected String robotID;
	protected Step currentStep;
	protected long timeLastStepFinished = 0;
	protected boolean stopExecuting = false;
	protected Thread myRobotThread;
	protected Proxy currentProxy;
	
	/**
	 * Initialize a Robot in its own Thread.
	 */
	public final void init() {	
		myRobotThread = new Thread(this);
		myRobotThread.setDaemon(true);
		myRobotThread.start();
	}
	
	/**
	 * Default robot Constructor.
	 * Child classes should call this to setup the Robot fields 
	 * and parse the Script
	 * 
	 * @param script Script to be parsed into steps
	 * @param newProxy Proxy recording this Robots actions
	 */
	protected Robot(final InputStream script, final Proxy newProxy) {
		scriptStream = script;
		currentProxy = newProxy;
		new ScriptReader().run(scriptStream, stepQueue, prefs, this);
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
	 * Method must be called by extending classes, 
	 * it provides the functionality of parsing the 
	 * input file for steps.
	 */
	public void run() {

	}

	/**
	 * Load the generic robot preferences.
	 */
	private void setDefaultRobotPreferences() {
		defaultWaitStep = Integer.parseInt(prefs.get("waitstep"));
		robotID = prefs.get("jobID");
		targetDomain = prefs.get("domain");
		targetPort = Integer.parseInt(prefs.get("remoteport"));
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
	 * Retrieve the value of the defaultWaitStep.
	 * @return Length of the default WAIT step
	 */
	public static int getDefaultWaitStep() {
		return defaultWaitStep;
	}
	public String getTargetDomain() {
		return targetDomain;
	}
	public Proxy getCurrentProxy() {
		return currentProxy;
	}
	public Thread.State getThreadState() {
		return myRobotThread.getState();
	}
	public int getTargetPort() {
		return targetPort;
	}
}
