package com.awebstorm.robot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PropertyResourceBundle;
import java.util.Queue;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jbehave.core.mock.UsingMatchers;

import com.awebstorm.Proxy;

/**
 * Provides Behaviours testing the acceptance of the verfiy commands
 * @author Cromano
 *
 */
public class HtmlVerifyBehaviour extends UsingMatchers{

	private PropertyResourceBundle loadGeneratorProperties;
	private static final String LOAD_GEN_PROPS_LOC = "LoadGenerator";
	private static final String LOAD_GEN_LOG_PROPS_LOC = "log4j.properties";
	private static int numberOfRobots = 1;
	private Proxy[] loadGenProxyArray;
	private String remotehost;
	private int remoteport;
	private Logger consoleLog = Logger.getLogger(this.getClass());
	private long fileStart;
	private Queue<InputStream> newStreams;
	private LinkedList<Robot> robots;
	
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
		numberOfRobots = 1;
		newStreams = new LinkedList<InputStream>();
		robots = new LinkedList<Robot>();
		for (int i = 0; i < numberOfRobots; i++) {
			loadGenProxyArray[i] = new Proxy(remotehost, remoteport);
		}
	}
	/**
	 * Take down everything after tests.
	 */
	public final void tearDown() {
		tearDownRobots();
		tearDownProxies();
	}
	/**
	 * Tear down Robots
	 */
	public final void tearDownRobots() {
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
	}
	/**
	 * Tear down Proxies
	 */
	public final void tearDownProxies() {
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
	 * Behaviour of the verifyChecked step
	 */
	public final void shouldVerifyChecked() {
		consoleLog.info("shouldVerifyChecked");
		try {
			newStreams.add(new FileInputStream("example_scripts" + System.getProperty("file.separator") + "VerifyChecked.xml"));
		} catch (FileNotFoundException e) {
			consoleLog.error("Script File not found.", e);
		}
		for (int i = 0;!newStreams.isEmpty();i++) {
			HtmlRobot newRobot = new HtmlRobot(newStreams.poll(), loadGenProxyArray[i].getLocalport(), loadGenProxyArray[i]);
			robots.add(newRobot);
			newRobot.init();
		}
		tearDown();
		
		LogDataExtractor reader = new LogDataExtractor("console.log", fileStart);
		ensureThat(reader.isConsoleLogHasNoErrors());
	}
	/**
	 * Behaviour of the verifyNotChecked step
	 */
	public final void shouldVerifyNotChecked() {
		consoleLog.info("shouldVerifyNotChecked");
		try {
			newStreams.add(new FileInputStream("example_scripts" + System.getProperty("file.separator") + "VerifyNotChecked.xml"));
		} catch (FileNotFoundException e) {
			consoleLog.error("Script File not found.", e);
		}
		for (int i = 0;!newStreams.isEmpty();i++) {
			HtmlRobot newRobot = new HtmlRobot(newStreams.poll(), loadGenProxyArray[i].getLocalport(), loadGenProxyArray[i]);
			robots.add(newRobot);
			newRobot.init();
		}
		tearDown();
		
		LogDataExtractor reader = new LogDataExtractor("console.log", fileStart);
		ensureThat(reader.isConsoleLogHasNoErrors());
	}
	/**
	 * Behaviour of the verifySelected step
	 */
	public final void shouldVerifySelected() {
		consoleLog.info("shouldVerifySelected");
		try {
			newStreams.add(new FileInputStream("example_scripts" + System.getProperty("file.separator") + "VerifySelected.xml"));
		} catch (FileNotFoundException e) {
			consoleLog.error("Script File not found.", e);
		}
		for (int i = 0;!newStreams.isEmpty();i++) {
			HtmlRobot newRobot = new HtmlRobot(newStreams.poll(), loadGenProxyArray[i].getLocalport(), loadGenProxyArray[i]);
			robots.add(newRobot);
			newRobot.init();
		}
		tearDown();
		
		LogDataExtractor reader = new LogDataExtractor("console.log", fileStart);
		ensureThat(reader.isConsoleLogHasNoErrors());
	}
	/**
	 * Behaviour of the verifyNotSelected step
	 */
	public final void shouldVerifyNotSelected() {
		consoleLog.info("shouldVerifyNotSelected");
		try {
			newStreams.add(new FileInputStream("example_scripts" + System.getProperty("file.separator") + "VerifyNotSelected.xml"));
		} catch (FileNotFoundException e) {
			consoleLog.error("Script File not found.", e);
		}
		for (int i = 0;!newStreams.isEmpty();i++) {
			HtmlRobot newRobot = new HtmlRobot(newStreams.poll(), loadGenProxyArray[i].getLocalport(), loadGenProxyArray[i]);
			robots.add(newRobot);
			newRobot.init();
		}
		tearDown();
		
		LogDataExtractor reader = new LogDataExtractor("console.log", fileStart);
		ensureThat(reader.isConsoleLogHasNoErrors());
	}
	/**
	 * Behaviour of the verifyTitle step
	 */
	public final void shouldVerifyTitle() {
		consoleLog.info("shouldVerifyTitle");
		try {
			newStreams.add(new FileInputStream("example_scripts" + System.getProperty("file.separator") + "VerifyTitle.xml"));
		} catch (FileNotFoundException e) {
			consoleLog.error("Script File not found.", e);
		}
		for (int i = 0;!newStreams.isEmpty();i++) {
			HtmlRobot newRobot = new HtmlRobot(newStreams.poll(), loadGenProxyArray[i].getLocalport(), loadGenProxyArray[i]);
			robots.add(newRobot);
			newRobot.init();
		}
		tearDown();
		
		LogDataExtractor reader = new LogDataExtractor("console.log", fileStart);
		ensureThat(reader.isConsoleLogHasNoErrors());
	}
	/**
	 * Behaviour of the verifyTextPresent step
	 */
	public final void shouldVerifyTextPresent() {
		consoleLog.info("shouldVerifyTextPresent");
		try {
			newStreams.add(new FileInputStream("example_scripts" + System.getProperty("file.separator") + "VerifyTextPresent.xml"));
		} catch (FileNotFoundException e) {
			consoleLog.error("Script File not found.", e);
		}
		for (int i = 0;!newStreams.isEmpty();i++) {
			HtmlRobot newRobot = new HtmlRobot(newStreams.poll(), loadGenProxyArray[i].getLocalport(), loadGenProxyArray[i]);
			robots.add(newRobot);
			newRobot.init();
		}
		tearDown();
		
		LogDataExtractor reader = new LogDataExtractor("console.log", fileStart);
		ensureThat(reader.isConsoleLogHasNoErrors());
	}
	/**
	 * Behaviour of the verifyText step
	 */
	public final void shouldVerifyText() {
		consoleLog.info("shouldVerifyText");
		try {
			newStreams.add(new FileInputStream("example_scripts" + System.getProperty("file.separator") + "VerifyText.xml"));
		} catch (FileNotFoundException e) {
			consoleLog.error("Script File not found.", e);
		}
		for (int i = 0;!newStreams.isEmpty();i++) {
			HtmlRobot newRobot = new HtmlRobot(newStreams.poll(), loadGenProxyArray[i].getLocalport(), loadGenProxyArray[i]);
			robots.add(newRobot);
			newRobot.init();
		}
		tearDown();
		
		LogDataExtractor reader = new LogDataExtractor("console.log", fileStart);
		ensureThat(reader.isConsoleLogHasNoErrors());
	}
	/**
	 * Behaviour of the verifyCookiePresent step
	 */
	public final void shouldVerifyCookiePresent() {
		consoleLog.info("shouldVerifyCookiePresent");
		try {
			newStreams.add(new FileInputStream("example_scripts" + System.getProperty("file.separator") + "VerifyCookiePresent.xml"));
		} catch (FileNotFoundException e) {
			consoleLog.error("Script File not found.", e);
		}
		for (int i = 0;!newStreams.isEmpty();i++) {
			HtmlRobot newRobot = new HtmlRobot(newStreams.poll(), loadGenProxyArray[i].getLocalport(), loadGenProxyArray[i]);
			robots.add(newRobot);
			newRobot.init();
		}
		tearDown();
		
		LogDataExtractor reader = new LogDataExtractor("console.log", fileStart);
		ensureThat(reader.isConsoleLogHasNoErrors());
	}
	/**
	 * Behaviour of the verifyLocation step
	 */
	public final void shouldVerifyLocation() {
		consoleLog.info("shouldVerifyLocation");
		try {
			newStreams.add(new FileInputStream("example_scripts" + System.getProperty("file.separator") + "VerifyLocation.xml"));
		} catch (FileNotFoundException e) {
			consoleLog.error("Script File not found.", e);
		}
		for (int i = 0;!newStreams.isEmpty();i++) {
			HtmlRobot newRobot = new HtmlRobot(newStreams.poll(), loadGenProxyArray[i].getLocalport(), loadGenProxyArray[i]);
			robots.add(newRobot);
			newRobot.init();
		}
		tearDown();
		
		LogDataExtractor reader = new LogDataExtractor("console.log", fileStart);
		ensureThat(reader.isConsoleLogHasNoErrors());
	}
}

