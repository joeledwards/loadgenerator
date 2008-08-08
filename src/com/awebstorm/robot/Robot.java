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
	private int robotSpeed = 1000;
	private InputStream scriptStream;
	//Robot ID vars
	protected String robotID;
	protected Step currentStep;
	protected long timeLastStepFinished = 0;
	protected Thread myRobotThread;
	protected Proxy currentProxy;
	private boolean end;
	
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
		robotSpeed = Integer.parseInt(prefs.get("waitstep"));
		robotID = prefs.get("jobID");
		targetDomain = prefs.get("domain");
		targetPort = Integer.parseInt(prefs.get("remoteport"));
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
	 * Get the domain of the target of this Robot.
	 * @return The domain of the target of this robot
	 */
	public String getTargetDomain() {
		return targetDomain;
	}
	/**
	 * Get the proxy that will be used by all operations performed by this Robot.
	 * @return The proxy to use
	 */
	public Proxy getCurrentProxy() {
		return currentProxy;
	}
	/**
	 * Get the current state of this Robot's thread.
	 * @return The state of this Robot's thread
	 */
	public Thread.State getThreadState() {
		return myRobotThread.getState();
	}
	/**
	 * Get the port of the target of this Robot. The proxy will use this port to communicate with the target.
	 * @return The port on which to communicate with the target.
	 */
	public int getTargetPort() {
		return targetPort;
	}
	/**
	 * Allows the Robot to know when to exit its sequence of Steps prematurely.
	 * @param b True if the Robot should end asap, else continue executing
	 */
	public void setEnd(boolean b) {
		this.end = b;
	}
	/**
	 * Classes extending the Robot class will be able to tell if they should exit prematurely.
	 * @return True if the Robot should exit prematurely, else false
	 */
	protected boolean isEnd() {
		return this.end;
	}
	/**
	 * Set the robot speed. This delay will not be used if the last step was a pause.
	 * @param robotSpeed New time between robot steps
	 */
	public void setRobotSpeed(int robotSpeed) {
		this.robotSpeed = robotSpeed;
	}
	/**
	 * Get the value of the robot speed. This delay will not be used if the last step was a pause.
	 * @return Time between Robot steps
	 */
	public int getRobotSpeed() {
		return robotSpeed;
	}
}
