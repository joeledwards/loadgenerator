package com.awebstorm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.PropertyConfigurator;
import org.jbehave.core.mock.UsingMatchers;

public class ProxyBehaviour extends UsingMatchers {
	
	private static final String LOAD_GEN_LOG_PROPS_LOC = "log4j.properties";
	
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

	public void shouldGET() {
		Proxy testProxy = new Proxy(10000,"www.customercentrix.com",80);
		testProxy.init();
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
		int received = 0;
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
			String newLine = bufferedFromTarget.readLine();
			for ( int i = 1; newLine != null; i++ ) {
				received = i;
				System.out.print(newLine);
				newLine = bufferedFromTarget.readLine();
			}
			fromTarget.close();
			toTarget.close();
			outgoing.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
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
	public class Server implements Runnable {
		
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
	
	/**
	 * Helper class for shouldReceiveBytes(), replicates some server content
	 * @author Cromano
	 *
	 */
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
		
	}
	
	public final void setUp() {
		PropertyConfigurator.configureAndWatch(LOAD_GEN_LOG_PROPS_LOC);
	}
	
	public final void tearDown() {
		
	}
}
