package com.awebstorm.loadgenerator.robot;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.PropertyResourceBundle;
import java.util.Queue;
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
	
	private PropertyResourceBundle loadGeneratorProperties;
	private static final String LOAD_GEN_PROPS_LOC = "LoadGenerator";
	private static final String LOAD_GEN_LOG_PROPS_LOC = "log4j.properties";
	private Proxy[] loadGenProxyArray = new Proxy[100];
	private int localPort;
	private String remotehost;
	private int remoteport;
	
	/**
	 * Test 1 Threaded Robots on 1 Threaded proxy.
	 */
	public final void shouldGenARobotOnProxy() {

		int size = 1;
		Queue<InputStream> newStreams = new LinkedList<InputStream>();
		boolean robotsAlive = true;
		
		try {
			while (newStreams.size() < size) {
				newStreams.add(new FileInputStream("ScriptThread" + (newStreams.size() + 1) + ".xml"));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < loadGenProxyArray.length && !newStreams.isEmpty(); i++) {
			if (loadGenProxyArray[i].getMyRobotOwner() == null) {
				HTMLRobot newRobot = new HTMLRobot(newStreams.poll(), localPort + i);
				loadGenProxyArray[i].setMyRobotOwner(newRobot);
				newRobot.init();
			}
		}
		
		while (robotsAlive) {
			robotsAlive = false;
			for (int i = 0; i < loadGenProxyArray.length; i++) {
				if (loadGenProxyArray[i].getMyRobotOwner() != null) {
					if (loadGenProxyArray[i].getMyRobotOwner().t.isAlive()) {
						robotsAlive = true;
						break;
					} else {
						loadGenProxyArray[i].setMyRobotOwner(null);
						break;
					}
				}
			}
		}
	}
	
	/*
	*//**
	 * Test 5 Threaded Robots on 5 Threaded proxies with only 1 GET or 1 POST step per robot.
	 *//*
	public final void shouldGen5RobotsOn5Proxies() {

		int size = 5;
		Queue<InputStream> newStreams = new LinkedList<InputStream>();
		boolean robotsAlive = true;
		
		try {
			while (newStreams.size() != size) {
				newStreams.add(new FileInputStream("ScriptThread" + (newStreams.size() + 1) + ".xml"));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < loadGenProxyArray.length && !newStreams.isEmpty(); i++) {
			if (loadGenProxyArray[i].getMyRobotOwner() == null) {
				HTMLRobot newRobot = new HTMLRobot(newStreams.poll(),localPort+i);
				loadGenProxyArray[i].setMyRobotOwner(newRobot);
				newRobot.init();
			}
		}
		
		while (robotsAlive) {
			robotsAlive = false;
			for(int i = 0; i < loadGenProxyArray.length; i++) {
				if (loadGenProxyArray[i].getMyRobotOwner() != null) {
					if (loadGenProxyArray[i].getMyRobotOwner().t.isAlive()) {
						robotsAlive = true;
						break;
					} else {
						loadGenProxyArray[i].setMyRobotOwner(null);
						break;
					}
				}
			}
		}
	}
	
	*//**
	 * Test 5 Threaded Robots on 5 Threaded proxies with multiple steps per Robot.
	 *//*
	public final void shouldGen5RobotsOn5ProxiesMultiStep() {

		int size = 5;
		Queue<InputStream> newStreams = new LinkedList<InputStream>();
		boolean robotsAlive = true;
		
		try {
			while (newStreams.size() != size) {
				newStreams.add(new FileInputStream("Script2Thread" + (newStreams.size() + 1) + ".xml"));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < loadGenProxyArray.length && !newStreams.isEmpty(); i++) {
			if (loadGenProxyArray[i].getMyRobotOwner() == null) {
				HTMLRobot newRobot = new HTMLRobot(newStreams.poll(),localPort+i);
				loadGenProxyArray[i].setMyRobotOwner(newRobot);
				newRobot.init();
			}
		}
		
		while (robotsAlive) {
			robotsAlive = false;
			for(int i = 0; i < loadGenProxyArray.length; i++) {
				if (loadGenProxyArray[i].getMyRobotOwner() != null) {
					if (loadGenProxyArray[i].getMyRobotOwner().t.isAlive()) {
						robotsAlive = true;
						break;
					} else {
						loadGenProxyArray[i].setMyRobotOwner(null);
						break;
					}
				}
			}
		}
	}*/

	/**
	 * Setup general operations before each test.
	 */
	public final void setUp() {
		
		PropertyConfigurator.configureAndWatch(LOAD_GEN_LOG_PROPS_LOC);
		loadGeneratorProperties = 
			(PropertyResourceBundle) ResourceBundle.getBundle(LOAD_GEN_PROPS_LOC);

		localPort = Integer.parseInt(loadGeneratorProperties.getString("proxyLocalPortRange"));
		remotehost = loadGeneratorProperties.getString("proxyDefaultRemoteTarget");
		remoteport = Integer.parseInt(loadGeneratorProperties.getString("proxyDefaultRemotePort"));
		for (int i = 0; i < loadGenProxyArray.length; i++) {
			loadGenProxyArray[i] = new Proxy(localPort + i, remotehost, remoteport);
			loadGenProxyArray[i].init();
		}
		
/*		URL scheduler = null;
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
		}*/
	}
	
	/**
	 * Take down everything after tests.
	 */
	public final void tearDown() {
		LogManager.shutdown();
	}

}

