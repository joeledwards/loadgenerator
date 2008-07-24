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
	private int counter;
	
	/**
	 * Should forward the sent bytes to server socket.
	 */
/*	public final void shouldForwardBytes() {
		Proxy testProxy = new Proxy(10000,"localhost",10003);
		int numberOfBytesToSend = 5000;
		testProxy.init();
		Socket outgoing = null;
		OutputStream toProxy = null;
		byte[] message = new byte[numberOfBytesToSend];
		try {
			outgoing = new Socket(InetAddress.getLocalHost(),10000);
			toProxy = outgoing.getOutputStream();
			Server myListener = new Server(10003);
			Thread myListenerThread = new Thread(myListener);
			myListenerThread.setDaemon(true);
			myListenerThread.start();
			toProxy.write(message);
			toProxy.close();
			while (myListener.received < numberOfBytesToSend) {
				
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(testProxy.getProxyReceiveAmount());
		System.out.println(testProxy.getProxySentAmount());
		ensureThat(testProxy.getProxySentAmount() == numberOfBytesToSend);
		ensureThat(testProxy.getProxyReceiveAmount() == 0);
	}*/
	
	/**
	 * Should receive bytes from a server and forward them to the local client.
	 */
/*	public void shouldReceiveBytes() {
		Proxy testProxy = new Proxy(10003,"71.222.136.129",10000);
		int numberOfBytesToSend = 5;
		int received=0;
		testProxy.init();
		Socket outgoing = null;
		OutputStream toProxy = null;
		byte[] message = new byte[numberOfBytesToSend];
		try {
			outgoing = new Socket(InetAddress.getLocalHost(),10003);
			Server2 myServer = new Server2(10001);
			Thread myListenerThread = new Thread(myServer);
			myListenerThread.setDaemon(true);
			myListenerThread.start();
			toProxy = outgoing.getOutputStream();
			InputStreamReader fromProxy = new InputStreamReader(outgoing.getInputStream());
			toProxy.write(message);
			outgoing = new Socket(InetAddress.getLocalHost(),10003);
			int newByte = fromProxy.read();
			for ( int i = 1; newByte > -1; i++ ) {
				received = i;
				newByte = fromProxy.read();
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
	
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
		
		String line9 = "GET /themes/pushbutton/header-b.jpg HTTP/1.1\r\n";
		
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
			System.out.println("Test Shutdown.");
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
		while(myThread.getState().compareTo(Thread.State.TERMINATED) != 0) {
			
		}
	}
	
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
		
		String line9 = "GET /themes/pushbutton/header-b.jpg HTTP/1.1\r\n";
		
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
			System.out.println(counter-1);
			ensureThat(counter-1 == testProxy.getProxyReceiveAmount());
			ensureThat(counter-1 == 692);
			ensureThat(testProxy.getProxySentAmount() == 487);
			ensureThat(testProxy.getProxySentAmount() == 
				(line1.length() + line2.length() + line3.length() + line4.length() + line5.length() + line6.length()
				 + line7.length() + line8.length()));
			System.out.println("Test Shutdown.");
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
		while(myThread.getState().compareTo(Thread.State.TERMINATED) != 0) {
			
		}
		
	}
	
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

		String line9 = "GET /themes/pushbutton/header-b.jpg HTTP/1.1\r\n";

		for ( int i = 0; i < 1; i ++) {
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
				System.out.println(testProxy.getProxyReceiveAmount());
				ensureThat(counter-1 == testProxy.getProxyReceiveAmount());
				ensureThat(counter-1 == 692);
				ensureThat(testProxy.getProxySentAmount() == 487);
				ensureThat(testProxy.getProxySentAmount() == 
					(line1.length() + line2.length() + line3.length() + line4.length() + line5.length() + line6.length()
							+ line7.length() + line8.length()));
				System.out.println("Test Shutdown.");
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
		
		while(myThread.getState().compareTo(Thread.State.TERMINATED) != 0) {
			
		}
		
	}
	
	/*public void shouldGet3() {
		Proxy testProxy = new Proxy(10000,"www.customercentrix.com",80);
		Thread myThread = new Thread(testProxy);
		myThread.setDaemon(true);
		myThread.start();
		String line1 = "GET /themes/pushbutton/header-a.jpg HTTP/1.1\r\n";
		String line2 = "Host: www.customercentrix.com\r\n";*/
		//String line3 = "Accept: image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-ms-application, application/vnd.ms-xpsdocument, application/xaml+xml, application/x-ms-xbap, application/x-shockwave-flash, */*\r\n";
		/*String line4 = "Accept-Language: en-us\r\n";
		String line5 = "UA-CPU: x86\r\n";
		String line6 = "Accept-Encoding: gzip, deflate\r\n";
		String line7 = "User-Agent: Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; SLCC1; .NET CLR 2.0.50727; .NET CLR 3.0.04506)\r\n";
		String line8 = "Connection: Keep-Alive\r\n\r\n";
		
		String line9 = "/themes/pushbutton/header-b.jpg HTTP/1.1\r\n";
		
		OutputStream toTarget = null;
		Socket outgoing= null;
		try {
			outgoing = new Socket("127.0.0.1",10000);
			toTarget = outgoing.getOutputStream();
			toTarget.write(line1.getBytes());
			toTarget.write(line2.getBytes());
			toTarget.write(line3.getBytes());
			toTarget.write(line4.getBytes());
			toTarget.write(line5.getBytes());
			toTarget.write(line6.getBytes());
			toTarget.write(line7.getBytes());
			toTarget.write(line8.getBytes());
			
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
			System.out.println("Test Shutdown.");
			outgoing.shutdownInput();
			fromTarget.close();
			toTarget.close();
			outgoing.close();
			testProxy.setShouldStopRunning(true);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}*/
	
/*	public void shouldTestProxyOnBrowser() {
		Proxy testProxy = new Proxy(10000,"www.customercentrix.com",80);
		testProxy.init();
		while(true) {
			
		}
	}*/
	
	/**
	 * Helper Class for the shouldForwardBytes() method.
	 * @author Cromano
	 *
	 */
/*	public class Server implements Runnable {
		
		private int received = 0;
		private int _localport;
		
		public Server(int localport) {
			_localport = localport;
		}
		
		public void run() {
			ServerSocket incomingServer = null;
			Socket incoming = null;
			InputStream fromProxy = null;
			try {
				incomingServer = new ServerSocket(_localport);
				incoming = incomingServer.accept();
				fromProxy = incoming.getInputStream();
				int newByte = fromProxy.read();
				for (int i = 1; newByte > -1; i++) {
					received = i;
					System.out.print(newByte);
					newByte = fromProxy.read();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public int getReceived() {
			return received;
		}
		
	}
	
	*//**
	 * Helper class for shouldReceiveBytes(), replicates some server content
	 * @author Cromano
	 *
	 *//*
	public class Server2 implements Runnable {
		
		private int received = 0;
		private int _localport;
		
		public Server2(int localport) {
			_localport = localport;
		}
		
		public void run() {
			ServerSocket incomingServer = null;
			Socket incoming = null;
			InputStream fromProxy = null;
			OutputStream toProxy = null;
			StringBuffer newbuf= new StringBuffer(500);
			try {
				incomingServer = new ServerSocket(_localport);
				incoming = incomingServer.accept();
				fromProxy = incoming.getInputStream();
				toProxy = incoming.getOutputStream();
				int newByte = fromProxy.read();
				for (int i = 0; newByte > -1; i++) {
					received = i+1;
					newbuf.append(newByte);
					System.out.print(newByte);
					newByte = fromProxy.read();
				}
				for(int i = 0; i < newbuf.length(); i++) {
					toProxy.write(newbuf.codePointAt(i));
				}
				toProxy.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public int getReceived() {
			return received;
		}
		
	}*/
	
	public final void setUp() {
		PropertyConfigurator.configureAndWatch(LOAD_GEN_LOG_PROPS_LOC);
	}
	
	public final void tearDown() {
		
	}
}
