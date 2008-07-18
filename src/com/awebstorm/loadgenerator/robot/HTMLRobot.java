package com.awebstorm.loadgenerator.robot;

import java.io.InputStream;
import org.apache.log4j.Logger;


/**
 * Robot to handle HTML calls.
 * @author Cromano
 * @version 1.0
 *
 */
public class HTMLRobot extends Robot {
	
	private Logger consoleLog = Logger.getLogger(this.getClass());
	private BrowserState currentState;
	
	/**
	 * Constructor defines the InputStream to be read.
	 * @param script script to parse
	 * @param proxyPort Port on which to contact the local proxy
	 */
	public HTMLRobot(final InputStream script, final int proxyPort) {
		super(script);
		currentState = new BrowserState(prefs, proxyPort);
		if (consoleLog.isDebugEnabled()) {
			consoleLog.debug("Built an HTMLRobot");
		}
	}

	/**
	 * Configures the robot, then executes the list of Steps.
	 */
	public final void run() {
		int stepNum = 0;
		while (!stepQueue.isEmpty()) {
			if (stopExecuting) {
				return;
			}
			Step tempStep = stepQueue.poll();
				if (consoleLog.isDebugEnabled()) {
					consoleLog.debug("Begin Executing Step: " 
							+ robotID + ',' 
							+ tempStep.getName() 
							+ ',' + stepNum);
				}
				this.currentStep = tempStep;
				tempStep.execute(robotID, currentState);
				stepNum++;
		}
		if (consoleLog.isDebugEnabled()) {
			consoleLog.debug("Robot is closing: " + this.robotID);
		}		
	}
	
}
