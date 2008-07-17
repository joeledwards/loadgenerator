package com.awebstorm.loadgenerator.robot;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;

import com.awebstorm.loadgenerator.Proxy;

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
	public Proxy[] loadGenProxyArray = new Proxy[100];
	public boolean[] loadGenProxyBooleanArray = new boolean[100];
	private int localPort;
	private String remotehost;
	private int remoteport;
	
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
/*		newRobot = new HTMLRobot(newStream);
		newRobot.run();*/
		for (int i = 0; i < loadGenProxyBooleanArray.length; i++) {
			if (loadGenProxyArray[i].getMyRobotOwner() == null) {
				loadGenProxyBooleanArray[i] = true;
			}
			if (loadGenProxyBooleanArray[i]) {
				newRobot = new HTMLRobot(newStream,localPort+i);
				loadGenProxyArray[i].setMyRobotOwner(newRobot);
				loadGenProxyBooleanArray[i] = false;
				break;
			}
		}
		//loadGenProxyArray.put(proxy9000, newRobot.robotID);
		//proxy9000.init();
		newRobot.init();
		while (newRobot.t.isAlive()) {
			
		}
	}
	
	/**
	 * Setup general operations before each test.
	 */
	public final void setUp() {
		
		PropertyConfigurator.configureAndWatch(LOAD_GEN_LOG_PROPS_LOC);
		URL scheduler = null;
		
		loadGeneratorProperties = 
			(PropertyResourceBundle) ResourceBundle.getBundle(LOAD_GEN_PROPS_LOC);

		localPort = Integer.parseInt(loadGeneratorProperties.getString("proxyLocalPortRange"));
		remotehost = loadGeneratorProperties.getString("proxyDefaultRemoteTarget");
		remoteport = Integer.parseInt(loadGeneratorProperties.getString("proxyDefaultRemotePort"));
		for ( int i = 0; i < loadGenProxyArray.length; i++ ) {
			loadGenProxyArray[i] = new Proxy(localPort+i,remotehost,remoteport);
			loadGenProxyArray[i].init();
			loadGenProxyBooleanArray[i] = true;
		}
		
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

