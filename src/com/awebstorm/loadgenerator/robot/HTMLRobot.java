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
	private BrowserState _currentState = new BrowserState(prefs);
	
	/**
	 * Constructor defines the InputStream to be read.
	 */
	public HTMLRobot(InputStream scriptLocation){	
		super(scriptLocation);
		if ( consoleLog.isDebugEnabled()) {
			consoleLog.debug("Built an HTMLRobot");
		}
	}

	/**
	 * Configures the robot, then executes the list of Steps.
	 */
	public void run() {

		int stepNum = 0;
		while(!stepQueue.isEmpty()) {
			Step tempStep = stepQueue.poll();
				if ( consoleLog.isDebugEnabled()) {
					consoleLog.debug("Begin Executing Step: " + robotID + ',' + tempStep.getName() + ',' + stepNum );
				}
				tempStep.execute(robotID, _currentState);
				tempStep = null;
				stepNum++;
		}
		if ( consoleLog.isDebugEnabled()) {
			consoleLog.debug("Robot is closing: " + this.robotID);
		}
		
	}
	
}
