package com.awebstorm.robot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PropertyResourceBundle;
import java.util.Queue;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jbehave.core.mock.UsingMatchers;

import com.awebstorm.Proxy;
import com.awebstorm.robot.LogDataExtractor.ALineData;

/**
 * Test the behaviour of the HTMLRobot class.
 * @author Cromano
 *
 */
public class HTMLRobotBehaviour  extends UsingMatchers {
	
	private PropertyResourceBundle loadGeneratorProperties;
	private static final String LOAD_GEN_PROPS_LOC = "LoadGenerator";
	private static final String LOAD_GEN_LOG_PROPS_LOC = "log4j.properties";
	private static int maxNumberOfProxies = 100;
	private static int numberOfRobots = 1;
	private Proxy[] loadGenProxyArray;
	private String remotehost;
	private int remoteport;
	private Logger consoleLog = Logger.getLogger(this.getClass());
	private long fileStart;
	
	/**
	 * Test 1 Threaded Robot on 1 Threaded Proxy making a bad request.
	 */
	public final void shouldGenARobotForBadRequest() {
		
		numberOfRobots = 1;
		Queue<InputStream> newStreams = new LinkedList<InputStream>();
		LinkedList<Robot> robots = new LinkedList<Robot>();
		for (int i = 0; i < numberOfRobots; i++) {
			loadGenProxyArray[i] = new Proxy(remotehost, remoteport);
		}
		try {
			newStreams.add(new FileInputStream("BadStepScript.xml"));
		} catch (FileNotFoundException e) {
			consoleLog.error("Script File not found.", e);
		}
		for (int i = 0;!newStreams.isEmpty();i++) {
			HTMLRobot newRobot = new HTMLRobot(newStreams.poll(), loadGenProxyArray[i].getLocalport(), loadGenProxyArray[i]);
			robots.add(newRobot);
			newRobot.init();
		}
		
		Iterator<Robot> robotIterator = robots.iterator();
		while (robotIterator.hasNext()) {
			Robot nextRobot = robotIterator.next();
			while (nextRobot.getThreadState().compareTo(Thread.State.TERMINATED) != 0) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					consoleLog.error("Interrupted Sleep.",e);
				}
			}
		}
		robotIterator = robots.iterator();
		while (robotIterator.hasNext()) {
			Proxy testProxy = robotIterator.next().getCurrentProxy();
			try {
				testProxy.shutdown();
			} catch (IOException e) {
				consoleLog.error("Could not shutdown a proxy.", e);
			}
			while(testProxy.getThreadState().compareTo(Thread.State.TERMINATED) != 0) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					consoleLog.error("Interrupted Sleep.", e);
				}
			}
		}
		LogDataExtractor reader = new LogDataExtractor("console.log", fileStart);
		ensureThat(reader.isConsoleLogHasNoErrors());
		HashMap<String,ALineData> results = reader.getMyLines();
		ensureThat(results.get("0010-2").getSentBytes().equals("222"));
		ensureThat(results.get("0010-2").getReceiveBytes().equals("6024"));
	}
	
	/**
	 * Test 1 Threaded Robot on 1 Threaded proxy making a good request.
	 */
	public final void shouldGenARobotOnProxy() {

		numberOfRobots = 1;
		Queue<InputStream> newStreams = new LinkedList<InputStream>();
		LinkedList<Robot> robots = new LinkedList<Robot>();
		
		for (int i = 0; i < numberOfRobots; i++) {
			loadGenProxyArray[i] = new Proxy(remotehost, remoteport);
		}
		try {
			while (newStreams.size() < numberOfRobots) {
				newStreams.add(new FileInputStream("ScriptThread" + (newStreams.size() + 1) + ".xml"));
			}
		} catch (FileNotFoundException e) {
			consoleLog.error("Could not find the script file.", e);
		}
		for (int i = 0;!newStreams.isEmpty();i++) {
			HTMLRobot newRobot = new HTMLRobot(newStreams.poll(), loadGenProxyArray[i].getLocalport(), loadGenProxyArray[i]);
			robots.add(newRobot);
			newRobot.init();
		}
		Iterator<Robot> robotIterator = robots.iterator();
		while (robotIterator.hasNext()) {
			Robot nextRobot = robotIterator.next();
			while (nextRobot.getThreadState().compareTo(Thread.State.TERMINATED) != 0) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					consoleLog.error("Interrupted sleep.", e);
				}
			}
		}
		robotIterator = robots.iterator();
		while (robotIterator.hasNext()) {
			Proxy testProxy = robotIterator.next().getCurrentProxy();
			try {
				testProxy.shutdown();
			} catch (IOException e) {
				consoleLog.error("Could not shutdown a proxy.", e);
			}
			while(testProxy.getThreadState().compareTo(Thread.State.TERMINATED) != 0) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					consoleLog.error("Interrupted sleep.", e);
				}
			}
		}
		LogDataExtractor reader = new LogDataExtractor("console.log", fileStart);
		ensureThat(reader.isConsoleLogHasNoErrors());
		HashMap<String,ALineData> results = reader.getMyLines();
		ensureThat(results.get("0001-2").getSentBytes().equals("222"));
		ensureThat(results.get("0001-2").getReceiveBytes().equals("692"));
	}
	
	/**
	 * Test 5 Threaded Robots on 5 Threaded Proxies making requests on a single known target
	 */
	public final void shouldGen5RobotsOnProxy() {

		numberOfRobots = 5;
		LinkedList<Robot> robots = new LinkedList<Robot>();
		
		for (int i = 0; i < numberOfRobots; i++) {
			loadGenProxyArray[i] = new Proxy(remotehost, remoteport);
		}
		try {
			for (int i = 0; i < numberOfRobots; i++) {
				HTMLRobot newRobot = new HTMLRobot(
						new FileInputStream("ScriptThread" + (i + 1) + ".xml"), 
						loadGenProxyArray[i].getLocalport(), 
						loadGenProxyArray[i]);
				robots.add(newRobot);
				newRobot.init();
				
			}
		} catch (FileNotFoundException e) {
			consoleLog.error("Could not find a script file.", e);
		}
		
		Iterator<Robot> robotIterator = robots.iterator();
		while (robotIterator.hasNext()) {
			Robot nextRobot = robotIterator.next();
			while (nextRobot.getThreadState().compareTo(Thread.State.TERMINATED) != 0) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					consoleLog.error("Interrupted Sleep", e);
				}
			}
		}
		robotIterator = robots.iterator();
		while (robotIterator.hasNext()) {
			Proxy testProxy = robotIterator.next().getCurrentProxy();
			try {
				testProxy.shutdown();
			} catch (IOException e) {
				consoleLog.error("Could not shutdown a proxy.", e);
			}
			while(testProxy.getThreadState().compareTo(Thread.State.TERMINATED) != 0) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					consoleLog.error("Interrupted sleep.", e);
				}
			}
		}
		LogDataExtractor reader = new LogDataExtractor("console.log", fileStart);
		ensureThat(reader.isConsoleLogHasNoErrors());
		HashMap<String,ALineData> results = reader.getMyLines();
		ensureThat(results.get("0001-2").getSentBytes().equals("222"));
		ensureThat(results.get("0001-2").getReceiveBytes().equals("692"));
		ensureThat(results.get("0002-2").getSentBytes().equals("222"));
		ensureThat(results.get("0002-2").getReceiveBytes().equals("692"));
		ensureThat(results.get("0003-2").getSentBytes().equals("222"));
		ensureThat(results.get("0003-2").getReceiveBytes().equals("692"));
		ensureThat(results.get("0004-2").getSentBytes().equals("222"));
		ensureThat(results.get("0004-2").getReceiveBytes().equals("692"));
		ensureThat(results.get("0005-2").getSentBytes().equals("222"));
		ensureThat(results.get("0005-2").getReceiveBytes().equals("692"));
	}
	
	/**
	 * Test 1 Threaded Robots on 1 Threaded proxies with multiple steps per Robot.
	 */
	public final void shouldGen1RobotsOn1ProxyMultiStep() {

		numberOfRobots = 1;
		LinkedList<Robot> robots = new LinkedList<Robot>();
		
		for (int i = 0; i < numberOfRobots; i++) {
			loadGenProxyArray[i] = new Proxy(remotehost, remoteport);
		}
		try {
			for (int i = 0; i < numberOfRobots; i++) {
				Robot newRobot = new HTMLRobot(
						new FileInputStream("Script2Thread" + (i + 1) + ".xml"), 
						loadGenProxyArray[i].getLocalport(), 
						loadGenProxyArray[i]);
				robots.add(newRobot);
				newRobot.init();
				
			}
		} catch (FileNotFoundException e) {
			consoleLog.error("Could not find a script file.", e);
		}

		Iterator<Robot> robotIterator = robots.iterator();
		while (robotIterator.hasNext()) {
			Robot nextRobot = robotIterator.next();
			while (nextRobot.getThreadState().compareTo(Thread.State.TERMINATED) != 0) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					consoleLog.error("Interrupted sleep.", e);
				}
			}
		}
		robotIterator = robots.iterator();
		while (robotIterator.hasNext()) {
			Proxy testProxy = robotIterator.next().getCurrentProxy();
			try {
				testProxy.shutdown();
			} catch (IOException e) {
				consoleLog.error("Could not shutdown a proxy.", e);
			}
			while(testProxy.getThreadState().compareTo(Thread.State.TERMINATED) != 0) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					consoleLog.error("Interrupted sleep.", e);
				}
			}
		}
		LogDataExtractor reader = new LogDataExtractor("console.log", fileStart);
		ensureThat(reader.isConsoleLogHasNoErrors());
		HashMap<String,ALineData> results = reader.getMyLines();
		ensureThat(results.get("0001-2").getSentBytes().equals("222"));
		ensureThat(results.get("0001-2").getReceiveBytes().equals("692"));
		ensureThat(results.get("0001-3").getSentBytes().equals("222"));
		ensureThat(results.get("0001-3").getReceiveBytes().equals("6024"));
		ensureThat(results.get("0001-4").getSentBytes().equals("222"));
		ensureThat(results.get("0001-4").getReceiveBytes().equals("692"));
	}

	/**
	 * Setup general operations before each test.
	 */
	public final void setUp() {
		File consoleFile = new File("console.log");
		fileStart = consoleFile.length();
		PropertyConfigurator.configureAndWatch(LOAD_GEN_LOG_PROPS_LOC);
		loadGeneratorProperties = 
			(PropertyResourceBundle) ResourceBundle.getBundle(LOAD_GEN_PROPS_LOC);

		remotehost = loadGeneratorProperties.getString("proxyDefaultRemoteTarget");
		remoteport = Integer.parseInt(loadGeneratorProperties.getString("proxyDefaultRemotePort"));
		loadGenProxyArray = new Proxy[Integer.parseInt(loadGeneratorProperties.getString("maxNumberOfProxies"))];
	}
	
	/**
	 * Take down everything after tests.
	 */
	public final void tearDown() {
		for (int i = 0; i < numberOfRobots; i++) {
			try {
				loadGenProxyArray[i].shutdown();
			} catch (IOException e) {
				consoleLog.error("Could not shutdown a Proxy.", e);
			}
			while (loadGenProxyArray[i].getThreadState().compareTo(Thread.State.TERMINATED) != 0) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					consoleLog.error("Interrupted sleep.", e);
				}
			}
		}
	}
	
	/**
	 * Retrieves the number of proxies created.
	 * @return The number of proxies created
	 */
	public final int getNumberOfProxies() {
		return maxNumberOfProxies;
	}

}