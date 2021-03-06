package com.awebstorm.robot;

import java.io.InputStream;
import org.apache.log4j.Logger;

import com.awebstorm.Proxy;


/**
 * Robot to handle HTML calls.
 * @author Cromano
 * @version 1.0
 *
 */
public class HtmlRobot extends Robot {
	
	private Logger consoleLog = Logger.getLogger(this.getClass());
	private BrowserState currentState;
	
	/**
	 * Constructor defines the InputStream to be read.
	 * @param script script to parse
	 * @param proxyPort Port on which to contact the local proxy
	 * @param newProxy Proxy that will be used to record throughput information
	 */
	public HtmlRobot(final InputStream script, final int proxyPort, final Proxy newProxy) {
		super(script, newProxy);
		currentState = new BrowserState(prefs, proxyPort);
		if (consoleLog.isDebugEnabled()) {
			consoleLog.debug("Built an HtmlRobot on" + this.robotID);
		}
	}
	
	/**
	 * Get the port on which this Robot speaks.
	 * @return Port on which this Robot speaks to its proxy
	 */
	public final int getProxyPort() {
		return currentState.getProxyPort();
	}

	/**
	 * Configures the robot, then executes the list of Steps.
	 */
	public final void run() {
		int stepNum = 0;
		while (!stepQueue.isEmpty()) {
			if (this.isEnd()) {
				if (consoleLog.isDebugEnabled())
					consoleLog.debug("HtmlRobot exiting prematurely.");
				
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
				Thread.yield();
				tempStep.execute(robotID, currentState);
				if (!tempStep.getName().equals("pause")) {
					try {
						Thread.sleep(this.getRobotSpeed());
					} catch (InterruptedException e) {
						consoleLog.error("HtmlRobot interrupted during sleep.", e);
					}
				}
				stepNum++;
		}
		if (consoleLog.isDebugEnabled()) {
			consoleLog.debug("Robot is closing: " + this.robotID);
		}		
	}
	
}
