package com.awebstorm.loadgenerator.robot;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;




/**
 * Test the behaviour of the HTMLRobot class.
 * @author Cromano
 *
 */
public class HTMLRobotBehaviour {
	
	private HTMLRobot newRobot;
	private String scriptLocation;
	private PropertyResourceBundle loadGeneratorProperties;
	private static final String LOAD_GEN_PROPS_LOC = "LoadGenerator";
	private static final String LOAD_GEN_LOG_PROPS_LOC = "log4j.properties";
	
	/**
	 * Behaviour to test the general use of an HTML robot in a semi long script.
	 */
	public final void shouldGenerateGoodResults() {
		scriptLocation = "Script.xml";
		InputStream newStream = null;
		try {
			newStream = new FileInputStream(scriptLocation);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		newRobot = new HTMLRobot(newStream);
		newRobot.run();
	}

/*	public void shouldAppendGoodResultsLog() {
		scriptLocation = "Script2.xml";
		InputStream newStream = null;
		try {
			newStream = new FileInputStream(scriptLocation);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		newRobot = new HTMLRobot(newStream);
		newRobot.run();
	}
	
	public void shouldTimeoutSuccessfully() {
		scriptLocation = "Script3.xml";
		InputStream newStream = null;
		try {
			newStream = new FileInputStream(scriptLocation);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		newRobot = new HTMLRobot(newStream);
		newRobot.run();
		
	}
	
	public void shouldFailButNotThrowException() {
		scriptLocation = "Script4.xml";
		InputStream newStream = null;
		try {
			newStream = new FileInputStream(scriptLocation);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		newRobot = new HTMLRobot(newStream);
		newRobot.run();
	}*/
	
	/**
	 * Setup general operations before each test.
	 */
	public final void setUp() {
		
		PropertyConfigurator.configureAndWatch(LOAD_GEN_LOG_PROPS_LOC);
		URL scheduler = null;
		
		loadGeneratorProperties = 
			(PropertyResourceBundle) ResourceBundle.getBundle(LOAD_GEN_PROPS_LOC);

		try {
			scheduler = new URL (
					loadGeneratorProperties.getString("schedulerProtocol"),
					loadGeneratorProperties.getString("schedulerHost"),
					Integer.parseInt(loadGeneratorProperties.getString("schedulerPort")),
					loadGeneratorProperties.getString("schedulerFile")
			);
		} catch (NumberFormatException e) {
			System.exit(3);
		} catch (MalformedURLException e) {
			System.exit(3);
		}
		
	}
	
	/**
	 * Take down everything after tests.
	 */
	public final void tearDown() {
		LogManager.shutdown();
		newRobot = null;
	}

}

