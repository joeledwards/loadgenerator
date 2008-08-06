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
	 * Tear down Robots.
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
	 * Tear down Proxies.
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
	 * Test Submit by name.
	 */
	public final void shouldSubmitByName() {
		consoleLog.info("shouldSubmitByName");
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
	 * Test Submit by xpath.
	 */
	public final void shouldSubmitByXPath() {
		consoleLog.info("shouldSubmitByXPath");
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
	 * Test Submit by id.
	 */
	public final void shouldSubmitById() {
		consoleLog.info("shouldSubmitById");
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
	 * Test Submit by identifier.
	 */
	public final void shouldSubmitByIdentifier() {
		consoleLog.info("shouldSubmitByIdentifier");
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
	 * Test Type.
	 */
	public final void shouldTypeAndSubmit() {
		consoleLog.info("shouldTypeAndSubmit");
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
		ensureThat(outputTable.contains("comments=Type One Line"));
	}
	
	/**
	 * Test Click.
	 */
	public final void shouldClick() {
		consoleLog.info("shouldClick");
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
		ensureThat(outputTable.contains("checkbox2=Check out this 2nd box"));
	}
	
	/**
	 * Test Check.
	 */
	public final void shouldCheckAndSubmit() {
		consoleLog.info("shouldCheckAndSubmit");
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
		ensureThat(outputTable.contains("checkbox1=Check this box out!"));
	}
	
	/**
	 * Test Uncheck.
	 */
	public final void shouldUncheckAndSubmit() {
		consoleLog.info("shouldUncheckAndSubmit");
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
		ensureThat(!outputTable.contains("Check out this 2nd box"));
	}
	
	/**
	 * Test Select.
	 */
	public final void shouldSelectAndSubmit() {
		consoleLog.info("shouldSelectAndSubmit");
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
		ensureThat(outputTable.contains("States=NM"));
		ensureThat(outputTable.contains("computerbrands=IBM"));
		ensureThat(outputTable.contains("countries2=Aruba"));
	}
	
	/**
	 * Test addSelection.
	 */
	public final void shouldAddSelectionAndSubmit() {
		consoleLog.info("shouldAddSelectionAndSubmit");
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
		ensureThat(outputTable.contains("computerbrands=IBM"));
		ensureThat(outputTable.contains("computerbrands=DELL"));
		ensureThat(outputTable.contains("computerbrands=HP"));
	}
	
	/**
	 * Test Refresh.
	 */
	public final void shouldRefresh() {
		consoleLog.info("shouldRefresh");
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
		ensureThat(results.get("2011-4").getReceiveBytes().equals(results.get("2011-2").getReceiveBytes()));
	}
	/**
	 * Double-click an element and Submit it.
	 */
	public final void shouldDoubleClick() {
		consoleLog.info("shouldDoubleClick");
		try {
			newStreams.add(new FileInputStream("example_scripts" + System.getProperty("file.separator") + "DoubleClickAndSubmit.xml"));
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
		ensureThat(!outputTable.contains("Check this box out!"));
	}
	/**
	 * Remove a selection, thus removing it form the namevaluepairs returned.
	 */
	public final void shouldRemoveSelection() {
		consoleLog.info("shouldRemoveSelection");
		try {
			newStreams.add(new FileInputStream("example_scripts" + System.getProperty("file.separator") + "RemoveSelectionAndSubmit.xml"));
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
		ensureThat(!outputTable.contains("computerbrands=IBM"));
	}
	/**
	 * Open the page in a new window.
	 * Give the window a new name to verify against
	 */
	public final void shouldOpenWindow() {
		consoleLog.info("shouldOpenWindow");
		try {
			newStreams.add(new FileInputStream("example_scripts" + System.getProperty("file.separator") + "OpenWindowAndSubmit.xml"));
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
		ensureThat(results.get("2003-2").getReceiveBytes().equals(results.get("2003-3").getReceiveBytes()));
	}
	/**
	 * Set the speed of the steps. A time param will ensure that the
	 * test took a certain amount of time.
	 */
	public final void shouldSetSpeed() {
		consoleLog.info("shouldSetSpeed");
		try {
			newStreams.add(new FileInputStream("example_scripts" + System.getProperty("file.separator") + "SetSpeedAndSubmit.xml"));
		} catch (FileNotFoundException e) {
			consoleLog.error("Script File not found.", e);
		}
		long currentTime = System.currentTimeMillis();
		for (int i = 0;!newStreams.isEmpty();i++) {
			HtmlRobot newRobot = new HtmlRobot(newStreams.poll(), loadGenProxyArray[i].getLocalport(), loadGenProxyArray[i]);
			robots.add(newRobot);
			newRobot.init();
		}
		tearDown();
		
		LogDataExtractor reader = new LogDataExtractor("console.log", fileStart);
		ensureThat(reader.isConsoleLogHasNoErrors());
		ensureThat((System.currentTimeMillis()-currentTime)>16000);
	}
	
	/**
	 * Test the connection exception received from a non-existing server.
	 */
	public final void shouldConnectionExceptionFromNonExistingServer() {
		consoleLog.info("shouldConnectionExceptionFromNonExistingServer");
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
		consoleLog.info("shouldConnectionExceptionFromBadPort");
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
}
