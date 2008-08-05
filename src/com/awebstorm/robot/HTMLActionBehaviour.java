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

public class HtmlActionBehaviour extends UsingMatchers{
	
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
	private final String tableSuccess = "Table Successfully Submitted";
	
	/**
	 * Test Submit by name
	 */
	public final void shouldSubmitByName() {
		try {
			newStreams.add(new FileInputStream("example_scripts" + System.getProperty("file.separator") + "SubmitByName.xml"));
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
		String outputTable = readOutputFile();
		ensureThat(outputTable.contains(tableSuccess));
	}
	
	/**
	 * Test Submit by xpath
	 */
	public final void shouldSubmitByXPath() {
		try {
			newStreams.add(new FileInputStream("example_scripts" + System.getProperty("file.separator") + "SubmitByXPath.xml"));
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
		String outputTable = readOutputFile();
		ensureThat(outputTable.contains(tableSuccess));
	}
	
	/**
	 * Test Submit by id
	 */
	public final void shouldSubmitById() {
		try {
			newStreams.add(new FileInputStream("example_scripts" + System.getProperty("file.separator") + "SubmitByID.xml"));
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
		String outputTable = readOutputFile();
		ensureThat(outputTable.contains(tableSuccess));
	}
	
	/**
	 * Test Submit by identifier
	 */
	public final void shouldSubmitByIdentifier() {
		try {
			newStreams.add(new FileInputStream("example_scripts" + System.getProperty("file.separator") + "SubmitByIdentifier.xml"));
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
		String outputTable = readOutputFile();
		ensureThat(outputTable.contains(tableSuccess));
	}
	
	/**
	 * Test Type
	 */
	public final void shouldTypeAndSubmit() {
		try {
			newStreams.add(new FileInputStream("example_scripts" + System.getProperty("file.separator") + "TypeAndSubmit.xml"));
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
		String outputTable = readOutputFile();
		ensureThat(outputTable.contains("textBox=typeOneLine"));
	}
	
	/**
	 * Test Click
	 */
	public final void shouldClick() {
		try {
			newStreams.add(new FileInputStream("example_scripts" + System.getProperty("file.separator") + "Click.xml"));
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
		String outputTable = readOutputFile();
		ensureThat(outputTable.contains("checkBox=true"));
	}
	
	/**
	 * Test Check
	 */
	public final void shouldCheckAndSubmit() {
		try {
			newStreams.add(new FileInputStream("example_scripts" + System.getProperty("file.separator") + "CheckAndSubmit.xml"));
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
		String outputTable = readOutputFile();
		ensureThat(outputTable.contains("checkBox=true"));
	}
	
	/**
	 * Test Uncheck
	 */
	public final void shouldUncheckAndSubmit() {
		try {
			newStreams.add(new FileInputStream("example_scripts" + System.getProperty("file.separator") + "UncheckAndSubmit.xml"));
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
		String outputTable = readOutputFile();
		ensureThat(outputTable.contains("checkBox=false"));
	}
	
	/**
	 * Test Select
	 */
	public final void shouldSelectAndSubmit() {
		try {
			newStreams.add(new FileInputStream("example_scripts" + System.getProperty("file.separator") + "SelectAndSubmit.xml"));
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
		String outputTable = readOutputFile();
		ensureThat(outputTable.contains("select=value1"));
		ensureThat(outputTable.contains("select=value2"));
		ensureThat(outputTable.contains("select=value3"));
	}
	
	/**
	 * Test addSelection
	 */
	public final void shouldAddSelectionAndSubmit() {
		try {
			newStreams.add(new FileInputStream("example_scripts" + System.getProperty("file.separator") + "AddSelectionAndSubmit.xml"));
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
		String outputTable = readOutputFile();
		ensureThat(outputTable.contains("select=value1"));
		ensureThat(outputTable.contains("select=value2"));
	}
	
	/**
	 * Test Refresh
	 */
	public final void shouldRefresh() {
		try {
			newStreams.add(new FileInputStream("example_scripts" + System.getProperty("file.separator") + "Refresh.xml"));
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
		HashMap<String,ALineData> results = reader.getMyLines();
		ensureThat(results.get("2011-4").getReceiveBytes().equals(results.get("0001-2").getReceiveBytes()));
	}
	
	/**
	 * Test the connection exception received from a non-existing server.
	 */
	public final void shouldConnectionExceptionFromNonExistingServer() {
		try {
			newStreams.add(new FileInputStream("example_scripts" + System.getProperty("file.separator") + "ConnectionException.xml"));
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
		HashMap<String,ALineData> results = reader.getMyLines();
		ensureThat(results.get("2012-2").getResultMessage().startsWith("ConnectException"));
	}
	
	/**
	 * Test the connection exception received from an invalid or unusable port.
	 */
	public final void shouldConnectionExceptionFromBadPort() {
		try {
			newStreams.add(new FileInputStream("example_scripts" + System.getProperty("file.separator") + "ConnectionReset.xml"));
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
		HashMap<String,ALineData> results = reader.getMyLines();
		ensureThat(results.get("2013-2").getResultMessage().startsWith("ConnectException"));
	}
	
	public String readOutputFile() {
		File outFile = null;
		InputStream output = null;
		StringBuffer readBuffer = new StringBuffer();
		
		try {
			outFile = new File("output.log");
			output = new FileInputStream(outFile);
		} catch (FileNotFoundException e) {
			ensureThat(false);
		}
		try {
			int readOne = output.read();
			while (readOne != -1) {
				readBuffer.append((char)readOne);
				readOne = output.read();
			}
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
			ensureThat(false);
		}
		outFile.delete();
		return readBuffer.toString();
		
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
}
