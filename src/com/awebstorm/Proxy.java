package com.awebstorm;
import java.io.IOException;
import java.net.BindException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.PortUnreachableException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.awebstorm.ProxyPipeIn;
import org.apache.log4j.Logger;

/**
 * Proxy server to count bytes coming through.
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
    		} catch (UnknownHostException e) {
    			consoleLog.fatal("Unknown Host " + targethost, e);
    			this.proxyMessage = "Unknown Host " + targethost;
    		} catch (BindException e) {
    			consoleLog.fatal("Could not bind to remoteport: " + targetport, e);
    			this.proxyMessage = "Could not bind to remoteport: " + targetport;
    		} catch (ConnectException e) {
    			consoleLog.fatal("Could not connect to: " + targethost + ":" + targetport, e);
    			this.proxyMessage = "Could not connect to: " + targethost + ":" + targetport;
    		} catch (NoRouteToHostException e) {
    			consoleLog.fatal("Could not find a route to remote host: " + targethost, e);
    			this.proxyMessage = "Could not find a route to remote host: " + targethost;
    		} catch (PortUnreachableException e) {
    			consoleLog.fatal("Could not reach the remote port: " + targetport, e);
    			this.proxyMessage = "Could not reach the remote port: " + targetport;
    		} catch (IOException e) {
    			consoleLog.error("IOException when accepting new connection.", e);
    			this.proxyMessage = "IO Error";
    		} finally {
/*    			try {
					localSocket.close();
				} catch (IOException e1) {
					consoleLog.error("Could not close the local socket.", e1);
				}*/
    		}
    	}
    }
    
    /**
     * Reset the Timers and byte counters.
     */
    public final void resetProxyCounters() {
    	this.proxyTimeResponded = 0;
    	this.proxyReceiveAmount = 0;
    	this.proxySentAmount = 0;
    	this.proxyTimeEnded = 0;
    	this.proxyTimeStarted = 0;
    }
    
    /**
     * Shutdown the proxy.
     */
    public final void shutdown() throws IOException {
    	if (!proxyServerSocket.isClosed())
    		proxyServerSocket.close();
    }
    
	public String getRemotehost() {
		return targethost;
	}
	public void setRemotehost(String remotehost) {
		this.targethost = remotehost;
	}
	public int getRemoteport() {
		return targetport;
	}
	public void setRemoteport(int remoteport) {
		this.targetport = remoteport;
	}
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
	public void setProxyTimeEnded(long currentTimeMillis) {
		this.proxyTimeEnded=currentTimeMillis;
	}
	public long getProxyTimeEnded() {
		return proxyTimeEnded;
	}
	public long getProxyTimeResponded() {
		return proxyTimeResponded;
	}
	public void setProxyTimeStarted(long currentTimeMillis) {
		this.proxyTimeStarted=currentTimeMillis;
	}
	public long getProxyReceiveAmount() {
		return proxyReceiveAmount;
	}
	public long getProxySentAmount() {
		return proxySentAmount;
	}
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
	 * @return ServerSocket on which the proxy is listening
	 */
	public int getLocalport() {
		return localport;
	}
	public String getProxyMessage() {
		return proxyMessage;
	}
}


        
      
            
            
            
            
            
            
            
            
            
            
