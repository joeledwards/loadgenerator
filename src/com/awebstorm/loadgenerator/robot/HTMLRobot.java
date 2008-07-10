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
	private BrowserState currentState = new BrowserState(prefs);
	
	/**
	 * Constructor defines the InputStream to be read.
	 * @param script script to parse
	 */
	public HTMLRobot(final InputStream script) {	
		super(script);
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
				tempStep.execute(robotID, currentState);
				tempStep = null;
				stepNum++;
		}
		if (consoleLog.isDebugEnabled()) {
			consoleLog.debug("Robot is closing: " + this.robotID);
		}
		
	}
	
}
