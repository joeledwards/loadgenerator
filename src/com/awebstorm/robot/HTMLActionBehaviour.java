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

public class HTMLActionBehaviour extends UsingMatchers{
	
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
/*	public final void shouldSubmitByName() {
		try {
			newStreams.add(new FileInputStream("example_scripts\\SubmitByName.xml"));
		} catch (FileNotFoundException e) {
			consoleLog.error("Script File not found.", e);
		}
		for (int i = 0;!newStreams.isEmpty();i++) {
			HTMLRobot newRobot = new HTMLRobot(newStreams.poll(), loadGenProxyArray[i].getLocalport(), loadGenProxyArray[i]);
			robots.add(newRobot);
			newRobot.init();
		}
		tearDown();
		LogDataExtractor reader = new LogDataExtractor("console.log", fileStart);
		ensureThat(reader.isConsoleLogHasNoErrors());
		File outFile = null;
		InputStream output = null;
		StringBuffer readBuffer = new StringBuffer();
		
		try {
			outFile = new File("output.log");
			output = new FileInputStream(outFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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
		String outputTable = readBuffer.toString();
		ensureThat(outputTable.contains(tableSuccess));
		outFile.delete();
	}
	
	*//**
	 * Test Submit by xpath
	 *//*
	public final void shouldSubmitByXPath() {
		try {
			newStreams.add(new FileInputStream("example_scripts\\SubmitByXPath.xml"));
		} catch (FileNotFoundException e) {
			consoleLog.error("Script File not found.", e);
		}
		for (int i = 0;!newStreams.isEmpty();i++) {
			HTMLRobot newRobot = new HTMLRobot(newStreams.poll(), loadGenProxyArray[i].getLocalport(), loadGenProxyArray[i]);
			robots.add(newRobot);
			newRobot.init();
		}
		tearDown();
		LogDataExtractor reader = new LogDataExtractor("console.log", fileStart);
		ensureThat(reader.isConsoleLogHasNoErrors());
		File outFile = null;
		InputStream output = null;
		StringBuffer readBuffer = new StringBuffer();
		
		try {
			outFile = new File("output.log");
			output = new FileInputStream(outFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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
		String outputTable = readBuffer.toString();
		ensureThat(outputTable.contains(tableSuccess));
		outFile.delete();
	}
	
	*//**
	 * Test Submit by id
	 *//*
	public final void shouldSubmitById() {
		try {
			newStreams.add(new FileInputStream("example_scripts\\SubmitByID.xml"));
		} catch (FileNotFoundException e) {
			consoleLog.error("Script File not found.", e);
		}
		for (int i = 0;!newStreams.isEmpty();i++) {
			HTMLRobot newRobot = new HTMLRobot(newStreams.poll(), loadGenProxyArray[i].getLocalport(), loadGenProxyArray[i]);
			robots.add(newRobot);
			newRobot.init();
		}
		tearDown();
		LogDataExtractor reader = new LogDataExtractor("console.log", fileStart);
		ensureThat(reader.isConsoleLogHasNoErrors());
		File outFile = null;
		InputStream output = null;
		StringBuffer readBuffer = new StringBuffer();
		
		try {
			outFile = new File("output.log");
			output = new FileInputStream(outFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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
		String outputTable = readBuffer.toString();
		ensureThat(outputTable.contains(tableSuccess));
		outFile.delete();
	}
	
	*//**
	 * Test Submit by identifier
	 *//*
	public final void shouldSubmitByIdentifier() {
		try {
			newStreams.add(new FileInputStream("example_scripts\\SubmitByIdentifier.xml"));
		} catch (FileNotFoundException e) {
			consoleLog.error("Script File not found.", e);
		}
		for (int i = 0;!newStreams.isEmpty();i++) {
			HTMLRobot newRobot = new HTMLRobot(newStreams.poll(), loadGenProxyArray[i].getLocalport(), loadGenProxyArray[i]);
			robots.add(newRobot);
			newRobot.init();
		}
		tearDown();
		LogDataExtractor reader = new LogDataExtractor("console.log", fileStart);
		ensureThat(reader.isConsoleLogHasNoErrors());
		File outFile = null;
		InputStream output = null;
		StringBuffer readBuffer = new StringBuffer();
		
		try {
			outFile = new File("output.log");
			output = new FileInputStream(outFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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
		String outputTable = readBuffer.toString();
		ensureThat(outputTable.contains(tableSuccess));
		outFile.delete();
	}
	
	*//**
	 * Test Type
	 *//*
	public final void shouldTypeAndSubmit() {
		try {
			newStreams.add(new FileInputStream("example_scripts\\TypeAndSubmit.xml"));
		} catch (FileNotFoundException e) {
			consoleLog.error("Script File not found.", e);
		}
		for (int i = 0;!newStreams.isEmpty();i++) {
			HTMLRobot newRobot = new HTMLRobot(newStreams.poll(), loadGenProxyArray[i].getLocalport(), loadGenProxyArray[i]);
			robots.add(newRobot);
			newRobot.init();
		}
		tearDown();
		
		LogDataExtractor reader = new LogDataExtractor("console.log", fileStart);
		ensureThat(reader.isConsoleLogHasNoErrors());
		File outFile = null;
		InputStream output = null;
		StringBuffer readBuffer = new StringBuffer();
		
		try {
			outFile = new File("output.log");
			output = new FileInputStream(outFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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
		String outputTable = readBuffer.toString();
		ensureThat(outputTable.contains("textBox=typeOneLine"));
		outFile.delete();
	}
	
	*//**
	 * Test Click
	 *//*
	public final void shouldClick() {
		try {
			newStreams.add(new FileInputStream("example_scripts\\Click.xml"));
		} catch (FileNotFoundException e) {
			consoleLog.error("Script File not found.", e);
		}
		for (int i = 0;!newStreams.isEmpty();i++) {
			HTMLRobot newRobot = new HTMLRobot(newStreams.poll(), loadGenProxyArray[i].getLocalport(), loadGenProxyArray[i]);
			robots.add(newRobot);
			newRobot.init();
		}
		tearDown();
		
		LogDataExtractor reader = new LogDataExtractor("console.log", fileStart);
		ensureThat(reader.isConsoleLogHasNoErrors());
		File outFile = null;
		InputStream output = null;
		StringBuffer readBuffer = new StringBuffer();
		
		try {
			outFile = new File("output.log");
			output = new FileInputStream(outFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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
		String outputTable = readBuffer.toString();
		ensureThat(outputTable.contains("checkBox=true"));
		outFile.delete();
	}
	
	*//**
	 * Test Check
	 *//*
	public final void shouldCheckAndSubmit() {
		try {
			newStreams.add(new FileInputStream("example_scripts\\CheckAndSubmit.xml"));
		} catch (FileNotFoundException e) {
			consoleLog.error("Script File not found.", e);
		}
		for (int i = 0;!newStreams.isEmpty();i++) {
			HTMLRobot newRobot = new HTMLRobot(newStreams.poll(), loadGenProxyArray[i].getLocalport(), loadGenProxyArray[i]);
			robots.add(newRobot);
			newRobot.init();
		}
		tearDown();
		
		LogDataExtractor reader = new LogDataExtractor("console.log", fileStart);
		ensureThat(reader.isConsoleLogHasNoErrors());
		File outFile = null;
		InputStream output = null;
		StringBuffer readBuffer = new StringBuffer();
		
		try {
			outFile = new File("output.log");
			output = new FileInputStream(outFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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
		String outputTable = readBuffer.toString();
		ensureThat(outputTable.contains("checkBox=true"));
		outFile.delete();
	}
	
	*//**
	 * Test Uncheck
	 *//*
	public final void shouldUncheckAndSubmit() {
		try {
			newStreams.add(new FileInputStream("example_scripts\\UncheckAndSubmit.xml"));
		} catch (FileNotFoundException e) {
			consoleLog.error("Script File not found.", e);
		}
		for (int i = 0;!newStreams.isEmpty();i++) {
			HTMLRobot newRobot = new HTMLRobot(newStreams.poll(), loadGenProxyArray[i].getLocalport(), loadGenProxyArray[i]);
			robots.add(newRobot);
			newRobot.init();
		}
		tearDown();
		
		LogDataExtractor reader = new LogDataExtractor("console.log", fileStart);
		ensureThat(reader.isConsoleLogHasNoErrors());
		File outFile = null;
		InputStream output = null;
		StringBuffer readBuffer = new StringBuffer();
		
		try {
			outFile = new File("output.log");
			output = new FileInputStream(outFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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
		String outputTable = readBuffer.toString();
		ensureThat(outputTable.contains("checkBox=false"));
		outFile.delete();
	}
	
	*//**
	 * Test Select
	 *//*
	public final void shouldSelectAndSubmit() {
		try {
			newStreams.add(new FileInputStream("example_scripts\\SelectAndSubmit.xml"));
		} catch (FileNotFoundException e) {
			consoleLog.error("Script File not found.", e);
		}
		for (int i = 0;!newStreams.isEmpty();i++) {
			HTMLRobot newRobot = new HTMLRobot(newStreams.poll(), loadGenProxyArray[i].getLocalport(), loadGenProxyArray[i]);
			robots.add(newRobot);
			newRobot.init();
		}
		tearDown();
		
		LogDataExtractor reader = new LogDataExtractor("console.log", fileStart);
		ensureThat(reader.isConsoleLogHasNoErrors());
		File outFile = null;
		InputStream output = null;
		StringBuffer readBuffer = new StringBuffer();
		
		try {
			outFile = new File("output.log");
			output = new FileInputStream(outFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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
		String outputTable = readBuffer.toString();
		ensureThat(outputTable.contains("select=value1"));
		ensureThat(outputTable.contains("select=value2"));
		ensureThat(outputTable.contains("select=value3"));
		outFile.delete();
	}
	
	*//**
	 * Test addSelection
	 *//*
	public final void shouldAddSelectionAndSubmit() {
		try {
			newStreams.add(new FileInputStream("example_scripts\\AddSelectionAndSubmit.xml"));
		} catch (FileNotFoundException e) {
			consoleLog.error("Script File not found.", e);
		}
		for (int i = 0;!newStreams.isEmpty();i++) {
			HTMLRobot newRobot = new HTMLRobot(newStreams.poll(), loadGenProxyArray[i].getLocalport(), loadGenProxyArray[i]);
			robots.add(newRobot);
			newRobot.init();
		}
		tearDown();
		
		LogDataExtractor reader = new LogDataExtractor("console.log", fileStart);
		ensureThat(reader.isConsoleLogHasNoErrors());
		File outFile = null;
		InputStream output = null;
		StringBuffer readBuffer = new StringBuffer();
		
		try {
			outFile = new File("output.log");
			output = new FileInputStream(outFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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
		String outputTable = readBuffer.toString();
		ensureThat(outputTable.contains("select=value1"));
		ensureThat(outputTable.contains("select=value2"));
		outFile.delete();
	}
	
	*//**
	 * Test Refresh
	 *//*
	public final void shouldRefresh() {
		try {
			newStreams.add(new FileInputStream("example_scripts\\Refresh.xml"));
		} catch (FileNotFoundException e) {
			consoleLog.error("Script File not found.", e);
		}
		for (int i = 0;!newStreams.isEmpty();i++) {
			HTMLRobot newRobot = new HTMLRobot(newStreams.poll(), loadGenProxyArray[i].getLocalport(), loadGenProxyArray[i]);
			robots.add(newRobot);
			newRobot.init();
		}
		tearDown();
		
		LogDataExtractor reader = new LogDataExtractor("console.log", fileStart);
		ensureThat(reader.isConsoleLogHasNoErrors());
		HashMap<String,ALineData> results = reader.getMyLines();
		ensureThat(results.get("0001-4").getReceiveBytes().equals(results.get("0001-2").getReceiveBytes()));
	}*/
	
	/**
	 * Test Connection Reset
	 */
	public final void shouldConnectReset() {
		tearDown();
		try {
			newStreams.add(new FileInputStream("example_scripts\\ConnectionReset.xml"));
		} catch (FileNotFoundException e) {
			consoleLog.error("Script File not found.", e);
		}
		for (int i = 0;!newStreams.isEmpty();i++) {
			HTMLRobot newRobot = new HTMLRobot(newStreams.poll(), loadGenProxyArray[i].getLocalport(), loadGenProxyArray[i]);
			robots.add(newRobot);
			newRobot.init();
		}
		LogDataExtractor reader = new LogDataExtractor("console.log", fileStart);
		ensureThat(!reader.isConsoleLogHasNoErrors());
		HashMap<String,ALineData> results = reader.getMyLines();
		ensureThat(results.get("0001-2").getResultMessage().equals("Socket Reset"));
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
