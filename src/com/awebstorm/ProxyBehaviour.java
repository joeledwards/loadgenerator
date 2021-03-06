package com.awebstorm;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jbehave.core.mock.UsingMatchers;

import com.awebstorm.robot.LogDataExtractor;

/**
 * Tests the proxy behaviour for correct functionality against the customercentrix.com web server
 * @author Cromano
 *
 */
public class ProxyBehaviour extends UsingMatchers {
	
	private static final String LOAD_GEN_LOG_PROPS_LOC = "log4j.properties";
	private Logger consoleLog = Logger.getLogger(this.getClass());
	private long fileStart;
	
	/**
	 * Retrieve 1 object on a new port.
	 */
	public final void shouldGet1() {
		
		if (consoleLog.isDebugEnabled()) {
			consoleLog.debug("Beginning Test shouldGet1()");
		}
		Proxy testProxy = new Proxy("www.customercentrix.com", 80);

		String line1 = "GET http://www.customercentrix.com/themes/pushbutton/header-a.jpg HTTP/1.1\r\n";
		String line2 = "Host: www.customercentrix.com\r\n";
		String line3 = "Accept: image/jpg\r\n";
		String line7 = "User-Agent: Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; SLCC1; .NET CLR 2.0.50727; .NET CLR 3.0.04506)\r\n";
		String line8 = "Connection: Keep-Alive\r\n\r\n";

		OutputStream toTarget = null;
		Socket outgoing = null;
		try {
			outgoing = new Socket("localhost", testProxy.getLocalport());
			if (outgoing != null) {
				outgoing.setReuseAddress(true);
			}
			
			toTarget = outgoing.getOutputStream();
			toTarget.write(line1.getBytes());
			toTarget.write(line2.getBytes());
			toTarget.write(line3.getBytes());
			toTarget.write(line7.getBytes());
			toTarget.write(line8.getBytes());
			InputStream fromTarget = outgoing.getInputStream();
			int temp = 0;
			int counter = 0;
			while (true) {
				if (temp == -1)
					break;
				temp = fromTarget.read();
				counter++;
				if (counter == 692)
					break;
			}
			outgoing.close();
			//add one byte to compensate for the eof
			if (counter != testProxy.getProxyReceiveAmount()) {
				consoleLog.debug("counter: " + counter + " " 
						+ "ReceiveAmount: " + testProxy.getProxyReceiveAmount() + " " 
						+ "Sent: " + testProxy.getProxySentAmount());
			}
			ensureThat(counter == testProxy.getProxyReceiveAmount());
			ensureThat(counter == 692);
			if (testProxy.getProxySentAmount() != 263) {
				consoleLog.debug("counter: " + counter + " " 
						+ "ReceiveAmount: " + testProxy.getProxyReceiveAmount() + " " 
						+ "Sent: " + testProxy.getProxySentAmount());
			}
			ensureThat(testProxy.getProxySentAmount() == 263);
			ensureThat(testProxy.getProxySentAmount() == 
				(line1.length() + line2.length() + line3.length() + line7.length() + line8.length()));

		} catch (UnknownHostException e) {
			consoleLog.error("Could not identify the Host for the Proxy test.", e);
		} catch (IOException e) {
			consoleLog.error("IO Error during a Proxy test.", e);
		}
		try {
			testProxy.shutdown();
		} catch (IOException e) {
			consoleLog.error("Could not shutdown a proxy.", e);
		}
		while (testProxy.getThreadState().compareTo(Thread.State.TERMINATED) != 0) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				consoleLog.error("Interrupted sleep.", e);
			}
		}
		LogDataExtractor reader = new LogDataExtractor("console.log", fileStart);
		ensureThat(reader.isConsoleLogHasNoErrors());
		if (consoleLog.isDebugEnabled()) {
			consoleLog.debug("Finished Test shouldGet1()");
		}
	}
	
	/**
	 * Retrieve 3 objects on Proxy Port.
	 */
	public final void shouldGet3WithoutAccum() {
		if (consoleLog.isDebugEnabled()) {
			consoleLog.debug("Beginning Test shouldGet3WithoutAccum()");
		}
		Proxy testProxy = new Proxy("www.customercentrix.com", 80);
		String line1 = "GET /themes/pushbutton/header-a.jpg HTTP/1.1\r\n";
		String line2 = "Host: www.customercentrix.com\r\n";
		String line3 = "Accept: image/jpg\r\n";
		String line7 = "User-Agent: Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; SLCC1; .NET CLR 2.0.50727; .NET CLR 3.0.04506)\r\n";
		String line8 = "Connection: Keep-Alive\r\n\r\n";

		for (int i = 0; i < 3; i++) {
			OutputStream toTarget = null;
			Socket outgoing = null;
			try {
				outgoing = new Socket("127.0.0.1", testProxy.getLocalport());
				if (outgoing != null) {
					outgoing.setReuseAddress(true);
				}
				
				toTarget = outgoing.getOutputStream();
				toTarget.write(line1.getBytes());
				toTarget.write(line2.getBytes());
				toTarget.write(line3.getBytes());
				toTarget.write(line7.getBytes());
				toTarget.write(line8.getBytes());
				InputStream fromTarget = outgoing.getInputStream();
				int temp = 0;
				int counter = 0;
				while (true) {
					if (temp == -1)
						break;
					temp = fromTarget.read();
					counter++;
					//Break as the required number of bytes was reached
					if (counter == 692)
						break;
				}
				outgoing.close();
				ensureThat(counter == testProxy.getProxyReceiveAmount());
				ensureThat(counter == 692);
				ensureThat(testProxy.getProxySentAmount() == 233);
				ensureThat(testProxy.getProxySentAmount()
						== (line1.length() 
								+ line2.length() 
								+ line3.length() 
								+ line7.length() 
								+ line8.length()));
				testProxy.resetProxyCounters();
			} catch (UnknownHostException e) {
				consoleLog.error("Could not recognize the the Host in a Proxy test.", e);
			} catch (IOException e) {
				consoleLog.error("IO error during a Proxy test.", e);
			}
		}
		try {
			testProxy.shutdown();
		} catch (IOException e) {
			consoleLog.error("Could not shutdown a proxy.", e);
		}
		while (testProxy.getThreadState().compareTo(Thread.State.TERMINATED) != 0) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				consoleLog.error("Interrupted sleep.", e);
			}
		}
		LogDataExtractor reader = new LogDataExtractor("console.log", fileStart);
		ensureThat(reader.isConsoleLogHasNoErrors());
		if (consoleLog.isDebugEnabled()) {
			consoleLog.debug("Finished Test shouldGet3WithoutAccum()");
		}
	}
	
	/**
	 * Retrieve 2 objects on the port that shouldGet3WithoutAccum() 
	 * will be using to test Proxy shutdown and
	 * to test the ability to accumulate multiple request bytes.
	 */
	public final void shouldGet2WithAccum() {
		if (consoleLog.isDebugEnabled()) {
			consoleLog.debug("Beginning Test shouldGet2WithAccum()");
		}
		Proxy testProxy = new Proxy("www.customercentrix.com", 80);
		String line1 = "GET /themes/pushbutton/header-a.jpg HTTP/1.1\r\n";
		String line2 = "Host: www.customercentrix.com\r\n";
		String line3 = "Accept: image/jpg\r\n";
		String line7 = "User-Agent: Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; SLCC1; .NET CLR 2.0.50727; .NET CLR 3.0.04506)\r\n";
		String line8 = "Connection: Keep-Alive\r\n\r\n";

		for (int i = 0; i < 2; i++) {
			OutputStream toTarget = null;
			Socket outgoing = null;
			try {
				outgoing = new Socket("localhost", testProxy.getLocalport());
				if (outgoing != null)
					outgoing.setReuseAddress(true);
				
				toTarget = outgoing.getOutputStream();
				toTarget.write(line1.getBytes());
				toTarget.write(line2.getBytes());
				toTarget.write(line3.getBytes());
				toTarget.write(line7.getBytes());
				toTarget.write(line8.getBytes());
				InputStream fromTarget = outgoing.getInputStream();
				int temp = 0;
				int counter = 0;
				while (true) {
					if (temp == -1)
						break;
					
					temp = fromTarget.read();
					counter++;
					//Break as the required number of bytes was reached
					if (counter == 692)
						break;
				}
				outgoing.close();
			} catch (UnknownHostException e) {
				consoleLog.error("Unknown host used for a Proxy test.", e);
			} catch (IOException e) {
				consoleLog.error("IO error during a Proxy test.", e);
			}
		}
		ensureThat(testProxy.getProxyReceiveAmount() == 692 * 2);
		ensureThat(testProxy.getProxySentAmount() == 233 * 2);
		ensureThat(testProxy.getProxySentAmount()
				== (line1.length()
						+ line2.length()
						+ line3.length()
						+ line7.length()
						+ line8.length()) * 2);
		try {
			testProxy.shutdown();
		} catch (IOException e) {
			consoleLog.error("Could not shutdown a Proxy.", e);
		}
		while (testProxy.getThreadState().compareTo(Thread.State.TERMINATED) != 0) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				consoleLog.error("Interrupted sleep.", e);
			}
		}
		LogDataExtractor reader = new LogDataExtractor("console.log", fileStart);
		ensureThat(reader.isConsoleLogHasNoErrors());
		if (consoleLog.isDebugEnabled()) {
			consoleLog.debug("Finished Test shouldGet2WithAccum()");
		}
	}
	
	/**
	 * Generates 10 Proxies and makes one request on each
	 *  to test multiple proxy non---interference.
	 */
	public final void shouldGenAndUse10Proxies() {
		if (consoleLog.isDebugEnabled()) {
			consoleLog.debug("Beginning Test shouldGenAndUse10Proxies()");
		}
		Proxy[] proxyArray = new Proxy[10];
		for (int i = 0; i < 10; i++) {
			proxyArray[i] = new Proxy("www.customercentrix.com", 80);
		}
		String line1 = "GET /themes/pushbutton/header-a.jpg HTTP/1.1\r\n";
		String line2 = "Host: www.customercentrix.com\r\n";
		String line3 = "Accept: image/jpg\r\n";
		String line7 = "User-Agent: Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; SLCC1; .NET CLR 2.0.50727; .NET CLR 3.0.04506)\r\n";
		String line8 = "Connection: Keep-Alive\r\n\r\n";

		for (int k = 0; k < 10; k++) {
			for (int i = 0; i < 1; i++) {
				OutputStream toTarget = null;
				Socket outgoing = null;
				try {
					outgoing = new Socket("127.0.0.1", proxyArray[k].getLocalport());
					if (outgoing != null)
						outgoing.setReuseAddress(true);

					toTarget = outgoing.getOutputStream();
					toTarget.write(line1.getBytes());
					toTarget.write(line2.getBytes());
					toTarget.write(line3.getBytes());
					toTarget.write(line7.getBytes());
					toTarget.write(line8.getBytes());
					InputStream fromTarget = outgoing.getInputStream();
					int temp = 0;
					int counter = 0;
					while (true) {
						if (temp == -1)
							break;
						
						temp = fromTarget.read();
						counter++;
						//Break as the required number of bytes was reached
						if (counter == 692)
							break;
					}
					outgoing.close();
					ensureThat(counter == proxyArray[k].getProxyReceiveAmount());
					ensureThat(counter == 692);
					ensureThat(proxyArray[k].getProxySentAmount() == 233);
					ensureThat(proxyArray[k].getProxySentAmount()
							== (line1.length()
									+ line2.length()
									+ line3.length()
									+ line7.length()
									+ line8.length()));
					
					proxyArray[k].resetProxyCounters();

				} catch (UnknownHostException e) {
					consoleLog.error("Unknown host found during a Proxy test.", e);
				} catch (IOException e) {
					consoleLog.error("IO error during a Proxy test.", e);
				}
			}
			try {
				proxyArray[k].shutdown();
			} catch (IOException e) {
				consoleLog.error("Could not shutdown a proxy.", e);
			}
			while (proxyArray[k].getThreadState().compareTo(Thread.State.TERMINATED) != 0) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					consoleLog.error("Interrupted sleep.", e);
				}
			}
		}
		LogDataExtractor reader = new LogDataExtractor("console.log", fileStart);
		ensureThat(reader.isConsoleLogHasNoErrors());
		if (consoleLog.isDebugEnabled()) {
			consoleLog.debug("Finished Test shouldGenAndUse10Proxies()");
		}
	}
	
	public final void setUp() {
		File consoleFile = new File("console.log");
		fileStart = consoleFile.length();
		PropertyConfigurator.configureAndWatch(LOAD_GEN_LOG_PROPS_LOC);
	}

	public final void tearDown() {
		
	}
}
