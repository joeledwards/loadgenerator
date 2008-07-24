package com.awebstorm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.PropertyConfigurator;
import org.jbehave.core.mock.UsingMatchers;

public class ProxyBehaviour extends UsingMatchers {
	
	private static final String LOAD_GEN_LOG_PROPS_LOC = "log4j.properties";
	private boolean notSetUp = true;
	
	/**
	 * Retrieve 1 object on a new port
	 */
	public void shouldGet1() {
		Proxy testProxy = new Proxy(10000,"www.customercentrix.com",80);
		Thread myThread = new Thread(testProxy);
		myThread.setDaemon(true);
		myThread.start();
		String line1 = "GET /themes/pushbutton/header-a.jpg HTTP/1.1\r\n";
		String line2 = "Host: www.customercentrix.com\r\n";
		String line3 = "Accept: image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-ms-application, application/vnd.ms-xpsdocument, application/xaml+xml, application/x-ms-xbap, application/x-shockwave-flash, */*\r\n";
		String line4 = "Accept-Language: en-us\r\n";
		String line5 = "UA-CPU: x86\r\n";
		String line6 = "Accept-Encoding: gzip, deflate\r\n";
		String line7 = "User-Agent: Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; SLCC1; .NET CLR 2.0.50727; .NET CLR 3.0.04506)\r\n";
		String line8 = "Connection: Keep-Alive\r\n\r\n";

		OutputStream toTarget = null;
		Socket outgoing= null;
		try {
			outgoing = new Socket("127.0.0.1",10000);
			if (outgoing != null)
				outgoing.setReuseAddress(true);

			toTarget = outgoing.getOutputStream();
			toTarget.write(line1.getBytes());
			toTarget.write(line2.getBytes());
			toTarget.write(line3.getBytes());
			toTarget.write(line4.getBytes());
			toTarget.write(line5.getBytes());
			toTarget.write(line6.getBytes());
			toTarget.write(line7.getBytes());
			toTarget.write(line8.getBytes());
			outgoing.shutdownOutput();
			InputStreamReader fromTarget = new InputStreamReader(outgoing.getInputStream());
			BufferedReader bufferedFromTarget = new BufferedReader(fromTarget);
			int temp = 0;
			int counter = 0;
			while(true) {
				if(temp == -1)
					break;
				temp = bufferedFromTarget.read();
				counter++;
			}
			ensureThat(counter-1 == testProxy.getProxyReceiveAmount());
			ensureThat(counter-1 == 692);
			ensureThat(testProxy.getProxySentAmount() == 487);
			ensureThat(testProxy.getProxySentAmount() == 
				(line1.length() + line2.length() + line3.length() + line4.length() + line5.length() + line6.length()
				 + line7.length() + line8.length()));
			outgoing.shutdownInput();
			fromTarget.close();
			toTarget.close();
			outgoing.close();

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			testProxy.shutdown();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Retrieve 3 objects on the same port as shouldGet3()
	 */
	public void shouldGet2() {
		Proxy testProxy = new Proxy(10001,"www.customercentrix.com",80);
		Thread myThread = new Thread(testProxy);
		myThread.setDaemon(true);
		myThread.start();
		String line1 = "GET /themes/pushbutton/header-a.jpg HTTP/1.1\r\n";
		String line2 = "Host: www.customercentrix.com\r\n";
		String line3 = "Accept: image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-ms-application, application/vnd.ms-xpsdocument, application/xaml+xml, application/x-ms-xbap, application/x-shockwave-flash, */*\r\n";
		String line4 = "Accept-Language: en-us\r\n";
		String line5 = "UA-CPU: x86\r\n";
		String line6 = "Accept-Encoding: gzip, deflate\r\n";
		String line7 = "User-Agent: Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; SLCC1; .NET CLR 2.0.50727; .NET CLR 3.0.04506)\r\n";
		String line8 = "Connection: Keep-Alive\r\n\r\n";
		
		for ( int i = 0; i < 3; i ++) {
		OutputStream toTarget = null;
		Socket outgoing= null;
		try {
			outgoing = new Socket("127.0.0.1",10001);
			if (outgoing != null)
				outgoing.setReuseAddress(true);

			toTarget = outgoing.getOutputStream();
			toTarget.write(line1.getBytes());
			toTarget.write(line2.getBytes());
			toTarget.write(line3.getBytes());
			toTarget.write(line4.getBytes());
			toTarget.write(line5.getBytes());
			toTarget.write(line6.getBytes());
			toTarget.write(line7.getBytes());
			toTarget.write(line8.getBytes());
			outgoing.shutdownOutput();
			InputStreamReader fromTarget = new InputStreamReader(outgoing.getInputStream());
			BufferedReader bufferedFromTarget = new BufferedReader(fromTarget);
			int temp = 0;
			int counter = 0;
			while(true) {
				if(temp == -1)
					break;
				temp = bufferedFromTarget.read();
				counter++;
			}
			ensureThat(counter-1 == testProxy.getProxyReceiveAmount());
			ensureThat(counter-1 == 692);
			ensureThat(testProxy.getProxySentAmount() == 487);
			ensureThat(testProxy.getProxySentAmount() == 
				(line1.length() + line2.length() + line3.length() + line4.length() + line5.length() + line6.length()
				 + line7.length() + line8.length()));
			outgoing.shutdownInput();
			fromTarget.close();
			toTarget.close();
			outgoing.close();
			testProxy.resetProxyCounters();

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		}
		try {
			testProxy.shutdown();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Retrieve 1 object on the same port as shouldGet2() uses
	 */
	public void shouldGet3() {
		Proxy testProxy = new Proxy(10001,"www.customercentrix.com",80);
		Thread myThread = new Thread(testProxy);
		myThread.setDaemon(true);
		myThread.start();
		String line1 = "GET /themes/pushbutton/header-a.jpg HTTP/1.1\r\n";
		String line2 = "Host: www.customercentrix.com\r\n";
		String line3 = "Accept: image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-ms-application, application/vnd.ms-xpsdocument, application/xaml+xml, application/x-ms-xbap, application/x-shockwave-flash, */*\r\n";
		String line4 = "Accept-Language: en-us\r\n";
		String line5 = "UA-CPU: x86\r\n";
		String line6 = "Accept-Encoding: gzip, deflate\r\n";
		String line7 = "User-Agent: Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; SLCC1; .NET CLR 2.0.50727; .NET CLR 3.0.04506)\r\n";
		String line8 = "Connection: Keep-Alive\r\n\r\n";

		for ( int i = 0; i < 2; i ++) {
			OutputStream toTarget = null;
			Socket outgoing= null;
			try {
				outgoing = new Socket("127.0.0.1",10001);
				if (outgoing != null)
					outgoing.setReuseAddress(true);

				toTarget = outgoing.getOutputStream();
				toTarget.write(line1.getBytes());
				toTarget.write(line2.getBytes());
				toTarget.write(line3.getBytes());
				toTarget.write(line4.getBytes());
				toTarget.write(line5.getBytes());
				toTarget.write(line6.getBytes());
				toTarget.write(line7.getBytes());
				toTarget.write(line8.getBytes());
				outgoing.shutdownOutput();
				InputStreamReader fromTarget = new InputStreamReader(outgoing.getInputStream());
				BufferedReader bufferedFromTarget = new BufferedReader(fromTarget);
				int temp = 0;
				int counter = 0;
				while(true) {
					if(temp == -1)
						break;
					temp = bufferedFromTarget.read();
					counter++;
				}
				ensureThat(counter-1 == testProxy.getProxyReceiveAmount());
				ensureThat(counter-1 == 692);
				ensureThat(testProxy.getProxySentAmount() == 487);
				ensureThat(testProxy.getProxySentAmount() == 
					(line1.length() + line2.length() + line3.length() + line4.length() + line5.length() + line6.length()
							+ line7.length() + line8.length()));
				outgoing.shutdownInput();
				fromTarget.close();
				toTarget.close();
				outgoing.close();
				testProxy.resetProxyCounters();

			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			testProxy.shutdown();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Generates 10 Proxies and makes one request on each
	 */
	public void shouldGenAndUse10Proxies() {
		Proxy[] proxyArray = new Proxy[10];
		for (int i = 0; i < 10; i++) {
			proxyArray[i] = new Proxy(10000+i,"www.customercentrix.com",80);
			Thread myThread = new Thread(proxyArray[i]);
			myThread.setDaemon(true);
			myThread.start();
		}
		String line1 = "GET /themes/pushbutton/header-a.jpg HTTP/1.1\r\n";
		String line2 = "Host: www.customercentrix.com\r\n";
		String line3 = "Accept: image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-ms-application, application/vnd.ms-xpsdocument, application/xaml+xml, application/x-ms-xbap, application/x-shockwave-flash, */*\r\n";
		String line4 = "Accept-Language: en-us\r\n";
		String line5 = "UA-CPU: x86\r\n";
		String line6 = "Accept-Encoding: gzip, deflate\r\n";
		String line7 = "User-Agent: Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; SLCC1; .NET CLR 2.0.50727; .NET CLR 3.0.04506)\r\n";
		String line8 = "Connection: Keep-Alive\r\n\r\n";

		for (int k = 0; k < 10; k++) {
			for (int i = 0; i < 1; i ++) {
				OutputStream toTarget = null;
				Socket outgoing= null;
				try {
					outgoing = new Socket("127.0.0.1",10000+k);
					if (outgoing != null)
						outgoing.setReuseAddress(true);

					toTarget = outgoing.getOutputStream();
					toTarget.write(line1.getBytes());
					toTarget.write(line2.getBytes());
					toTarget.write(line3.getBytes());
					toTarget.write(line4.getBytes());
					toTarget.write(line5.getBytes());
					toTarget.write(line6.getBytes());
					toTarget.write(line7.getBytes());
					toTarget.write(line8.getBytes());
					outgoing.shutdownOutput();
					InputStreamReader fromTarget = new InputStreamReader(outgoing.getInputStream());
					BufferedReader bufferedFromTarget = new BufferedReader(fromTarget);
					int temp = 0;
					int counter = 0;
					while(true) {
						if(temp == -1)
							break;
						temp = bufferedFromTarget.read();
						counter++;
					}
					ensureThat(counter-1 == proxyArray[k].getProxyReceiveAmount());
					ensureThat(counter-1 == 692);
					ensureThat(proxyArray[k].getProxySentAmount() == 487);
					ensureThat(proxyArray[k].getProxySentAmount() == 
						(line1.length() + line2.length() + line3.length() + line4.length() + line5.length() + line6.length()
								+ line7.length() + line8.length()));
					outgoing.shutdownInput();
					fromTarget.close();
					toTarget.close();
					outgoing.close();
					proxyArray[k].resetProxyCounters();

				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			try {
				proxyArray[k].shutdown();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Generates 10 Proxies and makes 5 requests on each
	 */
	public void shouldGenAndUse10ProxiesWith2Requests() {
		Proxy[] proxyArray = new Proxy[10];
		for (int i = 0; i < 10; i++) {
			proxyArray[i] = new Proxy(10000+i,"www.customercentrix.com",80);
			Thread myThread = new Thread(proxyArray[i]);
			myThread.setDaemon(true);
			myThread.start();
		}
		String line1 = "GET /themes/pushbutton/header-a.jpg HTTP/1.1\r\n";
		String line2 = "Host: www.customercentrix.com\r\n";
		String line3 = "Accept: image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-ms-application, application/vnd.ms-xpsdocument, application/xaml+xml, application/x-ms-xbap, application/x-shockwave-flash, */*\r\n";
		String line4 = "Accept-Language: en-us\r\n";
		String line5 = "UA-CPU: x86\r\n";
		String line6 = "Accept-Encoding: gzip, deflate\r\n";
		String line7 = "User-Agent: Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; SLCC1; .NET CLR 2.0.50727; .NET CLR 3.0.04506)\r\n";
		String line8 = "Connection: Keep-Alive\r\n\r\n";

		for (int k = 0; k < 10; k++) {
			for (int i = 0; i < 2; i ++) {
				OutputStream toTarget = null;
				Socket outgoing= null;
				try {
					outgoing = new Socket("127.0.0.1",10000+k);
					if (outgoing != null)
						outgoing.setReuseAddress(true);

					toTarget = outgoing.getOutputStream();
					toTarget.write(line1.getBytes());
					toTarget.write(line2.getBytes());
					toTarget.write(line3.getBytes());
					toTarget.write(line4.getBytes());
					toTarget.write(line5.getBytes());
					toTarget.write(line6.getBytes());
					toTarget.write(line7.getBytes());
					toTarget.write(line8.getBytes());
					outgoing.shutdownOutput();
					InputStreamReader fromTarget = new InputStreamReader(outgoing.getInputStream());
					BufferedReader bufferedFromTarget = new BufferedReader(fromTarget);
					int temp = 0;
					int counter = 0;
					while(true) {
						if(temp == -1)
							break;
						temp = bufferedFromTarget.read();
						counter++;
					}
					ensureThat(counter-1 == proxyArray[k].getProxyReceiveAmount());
					ensureThat(counter-1 == 692);
					ensureThat(proxyArray[k].getProxySentAmount() == 487);
					ensureThat(proxyArray[k].getProxySentAmount() == 
						(line1.length() + line2.length() + line3.length() + line4.length() + line5.length() + line6.length()
								+ line7.length() + line8.length()));
					outgoing.shutdownInput();
					fromTarget.close();
					toTarget.close();
					outgoing.close();
					proxyArray[k].resetProxyCounters();

				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			try {
				proxyArray[k].shutdown();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public final void setUp() {
		setUpLoggerOnce();
	}
	
	private void setUpLoggerOnce() {
		if (notSetUp) {
			PropertyConfigurator.configureAndWatch(LOAD_GEN_LOG_PROPS_LOC);
			notSetUp = false;
		}
	}

	public final void tearDown() {
		
	}
}
