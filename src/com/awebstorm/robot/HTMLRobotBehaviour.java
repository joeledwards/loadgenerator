package com.awebstorm.robot;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.PropertyResourceBundle;
import java.util.Queue;
import java.util.ResourceBundle;

import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;

import com.awebstorm.Proxy;

/**
 * Test the behaviour of the HTMLRobot class.
 * @author Cromano
 *
 */
public class HTMLRobotBehaviour {
	
	private PropertyResourceBundle loadGeneratorProperties;
	private static final String LOAD_GEN_PROPS_LOC = "LoadGenerator";
	private static final String LOAD_GEN_LOG_PROPS_LOC = "log4j.properties";
	private static int numberOfProxies = 5;
	private static int numberOfRobots = 1;
	private Proxy[] loadGenProxyArray = new Proxy[numberOfProxies];
	private int localPort;
	private String remotehost;
	private int remoteport;
	
	/**
	 * Test 1 Threaded Robot on 1 Proxy making a bad request
	 */
/*	public final void shouldGenARobotForBadRequest() {
		
		Queue<InputStream> newStreams = new LinkedList<InputStream>();
		boolean robotsAlive = true;
		
		try {
			newStreams.add(new FileInputStream("BadStepScript.xml"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < loadGenProxyArray.length && !newStreams.isEmpty(); i++) {
				HTMLRobot newRobot = new HTMLRobot(newStreams.poll(), localPort + i);
				loadGenProxyArray[i].setMyRobotOwner(newRobot);
				loadGenProxyArray[i].setRemotehost(newRobot.getTargetDomain());
				newRobot.init();
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
	}*/
	
	/**
	 * Test 1 Threaded Robots on 1 Threaded proxy making a good request.
	 */
	public final void shouldGenARobotOnProxy() {

		numberOfRobots = 1;
		Queue<InputStream> newStreams = new LinkedList<InputStream>();
		boolean robotsAlive = true;
		
		try {
			while (newStreams.size() < numberOfRobots) {
				newStreams.add(new FileInputStream("ScriptThread" + (newStreams.size() + 1) + ".xml"));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < loadGenProxyArray.length && !newStreams.isEmpty(); i++) {
				HTMLRobot newRobot = new HTMLRobot(newStreams.poll(), localPort + i);
				loadGenProxyArray[i].setMyRobotOwner(newRobot);
				newRobot.init();
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
					}
				}
			}
		}
	}
	
	/**
	 * Test 5 Threaded Robots on 5 Threaded proxies on 1 location call.
	 *//*
	public final void shouldGen5RobotsOnProxy() {

		numberOfRobots = 5;
		Queue<InputStream> newStreams = new LinkedList<InputStream>();
		boolean robotsAlive = true;
		
		try {
			while (newStreams.size() < numberOfRobots) {
				newStreams.add(new FileInputStream("ScriptThread" + (newStreams.size() + 1) + ".xml"));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < loadGenProxyArray.length && !newStreams.isEmpty(); i++) {
				InputStream temp = newStreams.poll();
				HTMLRobot newRobot = new HTMLRobot(temp, localPort + i);
				loadGenProxyArray[i].setMyRobotOwner(newRobot);
				newRobot.init();
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

	}*/
	
	/**
	 * Test 5 Threaded Robots on 5 Threaded proxies with only 1 GET or 1 POST request on a page per robot.
	 *//*
	public final void shouldGen5RobotsOn5Proxies() {

		numberOfRobots = 5;
		Queue<InputStream> newStreams = new LinkedList<InputStream>();
		boolean robotsAlive = true;
		
		try {
			while (newStreams.size() != numberOfRobots) {
				newStreams.add(new FileInputStream("ScriptThread" + (newStreams.size() + 1) + ".xml"));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < loadGenProxyArray.length && !newStreams.isEmpty(); i++) {
				HTMLRobot newRobot = new HTMLRobot(newStreams.poll(),localPort+i);
				loadGenProxyArray[i].setMyRobotOwner(newRobot);
				newRobot.init();
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

		numberOfRobots = 5;
		Queue<InputStream> newStreams = new LinkedList<InputStream>();
		boolean robotsAlive = true;
		
		try {
			while (newStreams.size() != numberOfRobots) {
				newStreams.add(new FileInputStream("Script" + (newStreams.size() + 1) + ".xml"));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < loadGenProxyArray.length && !newStreams.isEmpty(); i++) {
				HTMLRobot newRobot = new HTMLRobot(newStreams.poll(),localPort+i);
				loadGenProxyArray[i].setMyRobotOwner(newRobot);
				newRobot.init();
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
		
		System.out.println("Starting new test.");
		
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
		for (int i = 0; i < loadGenProxyArray.length; i++) {
			System.out.println("Killing Proxy at: " + (localPort+i));
			loadGenProxyArray[i].setShouldStopRunning(true);
		}
		System.out.println("Killed all Proxies");
		LogManager.shutdown();
	}
	
	/**
	 * Retrieves the number of proxies created.
	 * @return The number of proxies created
	 */
	public final int getNumberOfProxies() {
		return numberOfProxies;
	}

	public int getLocalPort() {
		return localPort;
	}

}