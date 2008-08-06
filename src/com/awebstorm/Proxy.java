package com.awebstorm;
import java.io.IOException;
import java.net.BindException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.PortUnreachableException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import com.awebstorm.ProxyPipeIn;
import org.apache.log4j.Logger;

/**
 * Proxy server for establishing connections and counting bytes coming through.
 * @author Cromano
 *
 */
public class Proxy implements Runnable {
	
	private int localport;
	private String targethost;
	private int targetport;
	private Logger consoleLog = Logger.getLogger(this.getClass());
	private Thread myProxyThread;
	private long proxyTimeResponded;
	private long proxyReceiveAmount;
	private long proxySentAmount;
	private long proxyTimeEnded;
	private long proxyTimeStarted;
	private ServerSocket proxyServerSocket;
	private String proxyMessage = null;
	
	/**
	 * Proxy Constructor.
	 * @param remotehost Remote host to make requests on
	 * @param remoteport Remote Port to talk on
	 */
    public Proxy(final String remotehost, final int remoteport) {
		this.targethost = remotehost;
		this.targetport = remoteport;
    	// Check for valid remote port and hostname not null
    	if (consoleLog.isDebugEnabled()) {
    		consoleLog.debug("Opening a Proxy to " + targethost + " Port " + remoteport);
    	}
    	if (remoteport <= 0) {
    		consoleLog.error("Error: Invalid Remote Port Specification " + "\n");
    		return;
    	}
    	if (targethost == null) {
    		consoleLog.error("Error: Invalid Remote Host Specification " + "\n");
    		return;
    	}
    	//Test and create a listening socket at Proxy
    	try {
    		proxyServerSocket = new ServerSocket(0);
    		this.localport = proxyServerSocket.getLocalPort();
    	} catch (IOException e) {
    		consoleLog.error("Could not create the proxy on random port: ", e);
    		return;
    	}
		this.init();
    }
    
	/**
	 * Initialize a new Proxy in its own Daemon thread.
	 */
	private void init() {	    	
		myProxyThread = new Thread(this);
		myProxyThread.setDaemon(true);
		myProxyThread.start();
	}
    
    /**
     * Run the proxy.
     */
    public final void run() {

    	//Could not create a socket port, end now
    	if (proxyServerSocket == null || !proxyServerSocket.isBound() || proxyServerSocket.isClosed()) {
    		return;
    	}

    	Socket localSocket = null, targetSocket = null;

    	//Loop to listen for incoming connection, and accept if there is one
    	while (true) {
    		try {
    			try {
    				localSocket = proxyServerSocket.accept();
    			} catch (SocketException e) {
    		    	if (consoleLog.isDebugEnabled()) {
    		    		consoleLog.debug("Closing a Proxy at " + proxyServerSocket.getLocalPort());
    		    	}
        			if (localSocket != null && consoleLog.isDebugEnabled()) {
            			if (!localSocket.isClosed())
            				localSocket.close();
            			if (targetSocket != null)
            				if (!targetSocket.isClosed())
            					targetSocket.close();
        			}
    		    	break;
    			}
    			//Create the 2 threads for the incoming and outgoing traffic of Proxy proxyServerSocket
    			targetSocket = new Socket(targethost, targetport);
    			Thread thread1 = null;
    			thread1 = new Thread(new ProxyPipeOut(localSocket, targetSocket, this));
    			thread1.setDaemon(true);
    			if (consoleLog.isDebugEnabled()) {
    				consoleLog.debug("Spawning ProxyPipeOut: " + thread1.getName());
    			}
    			thread1.start();
    			
    			Thread thread2 = null;
    			thread2 = new Thread(new ProxyPipeIn(targetSocket, localSocket, this));
    			thread2.setDaemon(true);
    			if (consoleLog.isDebugEnabled()) {
    				consoleLog.debug("Spawning ProxyPipeIn: " + thread2.getName());
    			}
    			thread2.start();
    			
    		} catch (SocketTimeoutException e) {
    			//Semi-normal, not a major exception
    			if (consoleLog.isDebugEnabled()) {
    				consoleLog.debug("Socket Timed Out", e);
    			}
    			this.proxyMessage = "SocketTimeoutException";
    			try {
					localSocket.close();
				} catch (IOException e1) {
					consoleLog.error("Could not close the local socket.", e1);
				}
    		} catch (UnknownHostException e) {
    			//Major exception
    			consoleLog.fatal("UnknownHostException " + targethost, e);
    			this.proxyMessage = "UnknownHostException " + targethost;
    			try {
					localSocket.close();
				} catch (IOException e1) {
					consoleLog.error("Could not close the local socket.", e1);
				}
    		} catch (BindException e) {
    			//Major exception
    			consoleLog.fatal("BindException to remoteport: " + targetport, e);
    			this.proxyMessage = "BindException to remoteport: " + targetport;
    			try {
					localSocket.close();
				} catch (IOException e1) {
					consoleLog.error("Could not close the local socket.", e1);
				}
    		} catch (ConnectException e) {
    			//Major exception
    			consoleLog.fatal("ConnectException to: " + targethost + ":" + targetport, e);
    			this.proxyMessage = "ConnectException to: " + targethost + ":" + targetport;
    			try {
					localSocket.close();
				} catch (IOException e1) {
					consoleLog.error("Could not close the local socket.", e1);
				}
    		} catch (NoRouteToHostException e) {
    			//Major exception
    			consoleLog.fatal("NoRouteToHostException to remote host: " + targethost, e);
    			this.proxyMessage = "NoRouteToHostException to remote host: " + targethost;
    			try {
					localSocket.close();
				} catch (IOException e1) {
					consoleLog.error("Could not close the local socket.", e1);
				}
    		} catch (PortUnreachableException e) {
    			//Major exception
    			consoleLog.fatal("PortUnreachableException to remote port: " + targetport, e);
    			this.proxyMessage = "PortUnreachableException to remote port: " + targetport;
    			try {
					localSocket.close();
				} catch (IOException e1) {
					consoleLog.error("Could not close the local socket.", e1);
				}
    		} catch (IOException e) {
    			//May be normal
    			consoleLog.error("IOException when accepting new connection.", e);
    			this.proxyMessage = "IO Error";
    		}
    	}
    }
    
    /**
     * Reset the Timers and Byte counters.
     */
    public final void resetProxyCounters() {
    	this.proxyTimeResponded = 0;
    	this.proxyReceiveAmount = 0;
    	this.proxySentAmount = 0;
    	this.proxyTimeEnded = 0;
    	this.proxyTimeStarted = 0;
    }
    
    /**
     * Shutdown the Proxy.
     */
    public final void shutdown() throws IOException {
    	if (!proxyServerSocket.isClosed())
    		proxyServerSocket.close();
    }
    /**
     * Get the remote host the Proxy will contact.
     * @return The remote host the Proxy will contact
     */
	public String getRemotehost() {
		return targethost;
	}
	/**
	 * Set the remote host the Proxy will contact.
	 * @param remotehost The remote host the Proxy will contact
	 */
	public void setRemotehost(String remotehost) {
		this.targethost = remotehost;
	}
	/**
	 * Get the remote port the Proxy will contact.
	 * @return The remote port the Proxy will contact
	 */
	public int getRemoteport() {
		return targetport;
	}
	/**
	 * Set the remote port the Proxy will contact.
	 * @param remoteport The remote port the Proxy will contact
	 */
	public void setRemoteport(int remoteport) {
		this.targetport = remoteport;
	}
	/**
	 * Set the time that the Proxy received a response after sending a request.
	 * @param currentTimeMillis The time the Proxy received a response after sending a request
	 */
	public void setProxyTimeResponded(long currentTimeMillis) {
		this.proxyTimeResponded=currentTimeMillis;
	}
	/**
	 * Increase the proxyReceiveAmount by 1.
	 */
	public synchronized void incrementProxyReceiveAmount() {
		this.proxyReceiveAmount++;
	}
	/**
	 * Increase the proxySentAmount by 1.
	 */
	public synchronized void incrementProxySentAmount() {
		this.proxySentAmount++;
	}
	/**
	 * Set the time that the Proxy finished reading the response after sending a request.
	 * @param currentTimeMillis The time the Proxy finished reading a response after sending a request
	 */
	public void setProxyTimeEnded(long currentTimeMillis) {
		this.proxyTimeEnded=currentTimeMillis;
	}
	/**
	 * Get the time that the Proxy finished reading the response after sending a request.
	 * @return The time that the Proxy finished reading the response after sending a request
	 */
	public long getProxyTimeEnded() {
		return proxyTimeEnded;
	}
	/**
	 * Get the time that the Proxy received a response after sending a request.
	 * @return The time the Proxy received a response after sending a request
	 */
	public long getProxyTimeResponded() {
		return proxyTimeResponded;
	}
	/**
	 * Set the time that the Proxy started sending a request.
	 * @param currentTimeMillis The time that the Proxy started sending a request
	 */
	public void setProxyTimeStarted(long currentTimeMillis) {
		this.proxyTimeStarted=currentTimeMillis;
	}
	/**
	 * Get the amount of bytes received by the Proxy.
	 * @return The amount of bytes received by the Proxy
	 */
	public long getProxyReceiveAmount() {
		return proxyReceiveAmount;
	}
	/**
	 * Get the amount of bytes sent by the Proxy.
	 * @return The amount of bytes sent by the Proxy
	 */
	public long getProxySentAmount() {
		return proxySentAmount;
	}
	/**
	 * Get the time that the Proxy started sending a request.
	 * @return The time that the Proxy started sending a request
	 */
	public long getProxyTimeStarted() {
		return proxyTimeStarted;
	}
	/**
	 * Returns the Thread.getState() of this Proxy's Thread.
	 * @return This Proxy's Thread's State
	 */
	public Thread.State getThreadState() {
		return this.myProxyThread.getState();
	}
	/**
	 * Retrieves the local port on which the ServerSocket is listening.
	 * @return ServerSocket on which the Proxy is listening
	 */
	public int getLocalport() {
		return localport;
	}
	/**
	 * Get the reason for the Proxy failure
	 * @return The reason that the proxy failed.
	 */
	public String getProxyMessage() {
		return proxyMessage;
	}
	/**
	 * Set the reason for the Proxy failure
	 * @param string Proxy Failure message
	 */
	public void setProxyMessage(String string) {
		this.proxyMessage = string;
	}
}


        
      
            
            
            
            
            
            
            
            
            
            
