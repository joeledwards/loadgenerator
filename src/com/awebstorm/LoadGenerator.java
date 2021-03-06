package com.awebstorm;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.concurrent.PriorityBlockingQueue;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.awebstorm.robot.HtmlRobot;
import com.awebstorm.robot.Robot;


/**
 * Main class for the loadgenerator slaves. Performs system scan for optimization and 
 * then queries the Scheduler for Robot Scripts. Each Robot Script is handed to a 
 * Robot thread of the necessary subclass. Also, capable of returning results to 
 * the Scheduler.
 * 
 * @author Cromano
 * @version 1.0
 *
 */
public class LoadGenerator {

	private PropertyResourceBundle loadGeneratorProperties;
	private static final String LOAD_GEN_PROPS_LOC = "LoadGenerator";
	private static final String LOAD_GEN_LOG_PROPS_LOC = "log4j.properties";
	private Logger consoleLog = Logger.getLogger(this.getClass());
	private URL scheduler;
	private static boolean stop = false;
	private static PriorityBlockingQueue<Robot> aliveRobotList = new PriorityBlockingQueue<Robot>();
	private static long robotThreadUID;
	private static int numberOfProxies;
	
	/**
	 * Main method constructs a new LoadGeneratorImpl and calls run() on it 
	 * @param args Console parsing not functional
	 */
	public static void main(String[] args) {
		PropertyConfigurator.configureAndWatch(LOAD_GEN_LOG_PROPS_LOC);
		new LoadGenerator().run();
	}
	
	/**
	 * Configures the properties of this loadGenerator
	 */
	public void loadProperties( ) {
		
		if( consoleLog.isDebugEnabled()) {
			consoleLog.debug("Logs configured.");
		}
		
		//Load Properties
		loadGeneratorProperties = (PropertyResourceBundle) ResourceBundle.getBundle(LOAD_GEN_PROPS_LOC);
		try {
			scheduler = new URL (
					loadGeneratorProperties.getString("schedulerProtocol"),
					loadGeneratorProperties.getString("schedulerHost"),
					Integer.parseInt(loadGeneratorProperties.getString("schedulerPort")),
					loadGeneratorProperties.getString("schedulerFile")
			);
		} catch (NumberFormatException e) {
			consoleLog.fatal("Bad Port Number receieved from properties.", e);
			System.exit(3);
		} catch (MalformedURLException e) {
			consoleLog.fatal("Bad URL parameters received from properties.", e);
			System.exit(3);
		}
		if( consoleLog.isDebugEnabled()) {
			consoleLog.debug("Properties configured.");
		}
	}

	/**
	 * Retrieve the URL of the Scheduler
	 * @return The URI of the Scheduler
	 */
	public URL getSchedulerURL() {
		return scheduler;
	}

	/**
	 * Create a robot
	 * @param ScriptLocation Location of the Robot's Script
	 * @param prefsLocation Location of the Robot's preferences
	 */
	public void createRobot(InputStream script, Proxy newProxy) {
		if( consoleLog.isDebugEnabled()) {
			consoleLog.debug("Creating new Robot.");
		}
		Thread t = new Thread(selectRobotType(script,newProxy), String.valueOf(robotThreadUID));
		t.start();
	}

	/**
	 * Run a LoadGenerator 
	 */
	public void run() {
		loadGeneratorProperties = 
			(PropertyResourceBundle) ResourceBundle.getBundle(LOAD_GEN_PROPS_LOC);
		
		if ( consoleLog.isDebugEnabled() ) {
			consoleLog.debug("Starting Load Generator");
		}

		this.loadProperties();
		schedulerSync();
		
		while (!stop) {
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				consoleLog.error("LoadGenerator interrupted during a wait cycle",e);
			}
			try {
				retrieveScripts();
			} catch (IOException e) {
				consoleLog.fatal("The LoadGenerator failed to retrieve scripts from the scheduler.",e);
				System.exit(3);
			}
			
		}
		
		if ( consoleLog.isDebugEnabled() ) {
			consoleLog.debug("Closing Load Generator.");
		}
		
	}
	
	/**
	 * Synchronize LoadGenerator system variables with those of the scheduler.
	 */
	private void schedulerSync() {
		//TODO -Sync with the scheduler for time and any other requirements
	}

	/**
	 * Retrieve scripts from the scheduler when available
	 * @throws IOException
	 */
	private void retrieveScripts() throws IOException {
		InputStream scriptStream = null;
		//TODO -Open Stream...
	}

	/**
	 * Chooses a robotType to start based on undetermined factors.
	 * @param scriptLocation
	 * @return
	 */
	private Robot selectRobotType(InputStream script, Proxy newProxy) {
			return new HtmlRobot(script,10000,newProxy);
	}
	
	/**
	 * Kill the robot using the specified name
	 * @param jobID The name of the robot's thread
	 * @return True if the robot was asked to die, false if the robot was not found or otherwise unable to be stopped
	 */
	private boolean killRobot (String jobID) {
		
		if (consoleLog.isDebugEnabled()) {
			consoleLog.debug("Attempting to kill: " );
		}

		
		return false;
	}
	
	/**
	 * Kill all robots
	 */
	private void killAllRobots () {
		
		if (consoleLog.isDebugEnabled()) {
			consoleLog.debug("Attempting to kill all robots.");
		}
		
		while( !aliveRobotList.isEmpty() ) {
			if (consoleLog.isDebugEnabled()) {
				consoleLog.debug("Robot found, trying to kill it...");
			}
			aliveRobotList.poll().setEnd(true);
		}
		
	}

	public static int getNumberOfProxies() {
		return numberOfProxies;
	}
}
