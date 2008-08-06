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
 * Test the behaviour of the HtmlRobot class.
 * @author Cromano
 *
 */
public class HtmlRobotBehaviour extends UsingMatchers {
	
	private PropertyResourceBundle loadGeneratorProperties;
	private static final String LOAD_GEN_PROPS_LOC = "LoadGenerator";
	private static final String LOAD_GEN_LOG_PROPS_LOC = "log4j.properties";
	private static int numberOfRobots = 1;
	private Proxy[] loadGenProxyArray;
	private String remotehost;
	private int remoteport;
	private Logger consoleLog = Logger.getLogger(this.getClass());
	private long fileStart;
	
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
	 * Test 1 Threaded Robot on 1 Threaded Proxy making a bad request.
	 */
	public final void shouldGenA404Request() {
		consoleLog.info("shouldGenA404Request");
		numberOfRobots = 1;
		Queue<InputStream> newStreams = new LinkedList<InputStream>();
		LinkedList<Robot> robots = new LinkedList<Robot>();
		for (int i = 0; i < numberOfRobots; i++) {
			loadGenProxyArray[i] = new Proxy(remotehost, remoteport);
		}
		try {
			newStreams.add(new FileInputStream("example_scripts" 
					+ System.getProperty("file.separator") 
					+ "404ResponseHTMLScript.xml"));
		} catch (FileNotFoundException e) {
			consoleLog.error("Script File not found.", e);
		}
		for (int i = 0;!newStreams.isEmpty();i++) {
			HtmlRobot newRobot = new HtmlRobot(newStreams.poll(), loadGenProxyArray[i].getLocalport(), loadGenProxyArray[i]);
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
		ensureThat(results.get("0011-1").getResultMessage().equals("null"));
		ensureThat(results.get("0011-1").getStepStatus().equals("success"));
		ensureThat(results.get("0011-2").getResultMessage().equals("Bad Status Code 404 for http://www.customercentrix.com/none.htm"));
		ensureThat(results.get("0011-2").getStepStatus().equals("failure"));
		ensureThat(results.get("0011-2").getSentBytes().equals(String.valueOf(10351)));
		ensureThat(results.get("0011-2").getReceiveBytes().equals(String.valueOf(73244)));
	}
	
	/**
	 * Test 1 Threaded Robot on 1 Threaded Proxy making a bad request.
	 */
	public final void shouldGenARobotWithNoWait() {
		consoleLog.info("shouldGenARobotWithNoWait");
		numberOfRobots = 1;
		Queue<InputStream> newStreams = new LinkedList<InputStream>();
		LinkedList<Robot> robots = new LinkedList<Robot>();
		for (int i = 0; i < numberOfRobots; i++) {
			loadGenProxyArray[i] = new Proxy(remotehost, remoteport);
		}
		try {
			newStreams.add(new FileInputStream("example_scripts" + System.getProperty("file.separator") + "NoWaitBasicHTMLScript.xml"));
		} catch (FileNotFoundException e) {
			consoleLog.error("Script File not found.", e);
		}
		for (int i = 0;!newStreams.isEmpty();i++) {
			HtmlRobot newRobot = new HtmlRobot(newStreams.poll(), loadGenProxyArray[i].getLocalport(), loadGenProxyArray[i]);
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
		ensureThat(results.get("0010-1").getResultMessage().equals("null"));
		ensureThat(results.get("0010-1").getStepStatus().equals("success"));
		ensureThat(results.get("0010-2").getResultMessage().equals("null"));
		ensureThat(results.get("0010-2").getStepStatus().equals("success"));
		ensureThat(results.get("0010-2").getSentBytes().equals(String.valueOf(222+203)));
		ensureThat(results.get("0010-2").getReceiveBytes().equals(String.valueOf(6024+570)));
	}
	
	/**
	 * Test 1 Threaded Robot on 1 Threaded proxy making a good request.
	 */
	public final void shouldGenARobotOnProxy() {
		consoleLog.info("shouldGenARobotOnProxy");
		numberOfRobots = 1;
		Queue<InputStream> newStreams = new LinkedList<InputStream>();
		LinkedList<Robot> robots = new LinkedList<Robot>();
		
		for (int i = 0; i < numberOfRobots; i++) {
			loadGenProxyArray[i] = new Proxy(remotehost, remoteport);
		}
		try {
			while (newStreams.size() < numberOfRobots) {
				newStreams.add(new FileInputStream("example_scripts" + System.getProperty("file.separator") + "BasicHTMLScript" + (newStreams.size() + 1) + ".xml"));
			}
		} catch (FileNotFoundException e) {
			consoleLog.error("Could not find the script file.", e);
		}
		for (int i = 0;!newStreams.isEmpty();i++) {
			HtmlRobot newRobot = new HtmlRobot(newStreams.poll(), loadGenProxyArray[i].getLocalport(), loadGenProxyArray[i]);
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
		ensureThat(results.get("0001-1").getStepStatus().equals("success"));
		ensureThat(results.get("0001-1").getResultMessage().equals("null"));
		ensureThat(results.get("0001-2").getResultMessage().equals("null"));
		ensureThat(results.get("0001-2").getStepStatus().equals("success"));
		ensureThat(results.get("0001-2").getSentBytes().equals(String.valueOf(222+203)));
		ensureThat(results.get("0001-2").getReceiveBytes().equals(String.valueOf(692+570)));
	}
	
	/**
	 * Test 5 Threaded Robots on 5 Threaded Proxies making requests on a single known target
	 */
	public final void shouldGen5RobotsOnProxy() {
		consoleLog.info("shouldGen5RobotsOnProxy");
		numberOfRobots = 5;
		LinkedList<Robot> robots = new LinkedList<Robot>();
		
		for (int i = 0; i < numberOfRobots; i++) {
			loadGenProxyArray[i] = new Proxy(remotehost, remoteport);
		}
		try {
			for (int i = 0; i < numberOfRobots; i++) {
				HtmlRobot newRobot = new HtmlRobot(
						new FileInputStream("example_scripts" + System.getProperty("file.separator") + "BasicHTMLScript" + (i + 1) + ".xml"), 
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
		ensureThat(results.get("0001-2").getStepStatus().equals("success"));
		ensureThat(results.get("0001-2").getResultMessage().equals("null"));
		ensureThat(results.get("0001-2").getSentBytes().equals(String.valueOf(222+203)));
		ensureThat(results.get("0001-2").getReceiveBytes().equals(String.valueOf(692+570)));
		ensureThat(results.get("0002-2").getStepStatus().equals("success"));
		ensureThat(results.get("0002-2").getResultMessage().equals("null"));
		ensureThat(results.get("0002-2").getSentBytes().equals(String.valueOf(222+203)));
		ensureThat(results.get("0002-2").getReceiveBytes().equals(String.valueOf(692+570)));
		ensureThat(results.get("0003-2").getStepStatus().equals("success"));
		ensureThat(results.get("0003-2").getResultMessage().equals("null"));
		ensureThat(results.get("0003-2").getSentBytes().equals(String.valueOf(222+203)));
		ensureThat(results.get("0003-2").getReceiveBytes().equals(String.valueOf(692+570)));
		ensureThat(results.get("0004-2").getStepStatus().equals("success"));
		ensureThat(results.get("0004-2").getResultMessage().equals("null"));
		ensureThat(results.get("0004-2").getSentBytes().equals(String.valueOf(222+203)));
		ensureThat(results.get("0004-2").getReceiveBytes().equals(String.valueOf(692+570)));
		ensureThat(results.get("0005-2").getStepStatus().equals("success"));
		ensureThat(results.get("0005-2").getResultMessage().equals("null"));
		ensureThat(results.get("0005-2").getSentBytes().equals(String.valueOf(222+203)));
		ensureThat(results.get("0005-2").getReceiveBytes().equals(String.valueOf(692+570)));
	}
	
	/**
	 * Test 1 Threaded Robots on 1 Threaded proxies with multiple steps per Robot.
	 */
	public final void shouldGen1RobotsOn1ProxyMultiStep() {
		consoleLog.info("shouldGen1RobotsOn1ProxyMultiStep");
		numberOfRobots = 1;
		LinkedList<Robot> robots = new LinkedList<Robot>();
		
		for (int i = 0; i < numberOfRobots; i++) {
			loadGenProxyArray[i] = new Proxy(remotehost, remoteport);
		}
		try {
			for (int i = 0; i < numberOfRobots; i++) {
				Robot newRobot = new HtmlRobot(
						new FileInputStream("example_scripts" + System.getProperty("file.separator") + "MultiStepHTMLScript" + (i + 1) + ".xml"), 
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
		ensureThat(results.get("0111-2").getStepStatus().equals("success"));
		ensureThat(results.get("0111-2").getResultMessage().equals("null"));
		ensureThat(results.get("0111-2").getSentBytes().equals(String.valueOf(222+203)));
		ensureThat(results.get("0111-2").getReceiveBytes().equals(String.valueOf(692+570)));
		ensureThat(results.get("0111-3").getStepStatus().equals("success"));
		ensureThat(results.get("0111-3").getResultMessage().equals("null"));
		ensureThat(results.get("0111-3").getSentBytes().equals("222"));
		ensureThat(results.get("0111-3").getReceiveBytes().equals("6024"));
		ensureThat(results.get("0111-4").getStepStatus().equals("success"));
		ensureThat(results.get("0111-4").getResultMessage().equals("null"));
		ensureThat(results.get("0111-4").getSentBytes().equals("222"));
		ensureThat(results.get("0111-4").getReceiveBytes().equals("692"));
	}

}