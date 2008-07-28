package com.awebstorm;
import java.io.IOException;
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
	private String _remotehost;
	private int remoteport;
	private Logger consoleLog = Logger.getLogger(this.getClass());
	private Thread t;
	private long proxyTimeResponded;
	private long proxyReceiveAmount;
	private long proxySentAmount;
	private long proxyTimeEnded;
	private long proxyTimeStarted;
	private ServerSocket server;
	
	/**
	 * Proxy Constructor.
	 * @param _remotehost Remote host to make requests on
	 * @param remoteport Remote Port to talk on
	 */
    public Proxy (String remotehost, int remoteport) {
		this._remotehost=remotehost;
		this.remoteport=remoteport;
    	// Check for valid remote port and hostname not null
    	if ( consoleLog.isDebugEnabled())
    		consoleLog.debug("Opening a Proxy at " + _remotehost + " Port " + remoteport);
    	if(remoteport <=0) {
    		consoleLog.error("Error: Invalid Remote Port Specification " + "\n");
    		return;
    	}
    	if(_remotehost == null) {
    		consoleLog.error("Error: Invalid Remote Host Specification " + "\n");
    		return;
    	}
    	//Test and create a listening socket at Proxy
    	try{
    		server = new ServerSocket(0);
    		this.localport = server.getLocalPort();
    	}
    	catch(IOException e) {
    		consoleLog.error("Could not create the proxy on random port: ", e);
    		return;
    	}
		this.init();
    }
    
	/**
	 * Initialize a new Proxy in its own Daemon thread.
	 */
	private void init() {	    	
		t=new Thread(this);
		t.setDaemon(true);
		t.start();
	}
    
    /**
     * Run the proxy.
     */
    public void run () {

    	//Could not create a socket port, end execution now
    	if (server == null || !server.isBound() || server.isClosed()) {
    		return;
    	}

    	Socket localSocket = null, targetSocket = null;

    	//Loop to listen for incoming connection, and accept if there is one
    	while (true) {
    		try {
    			try {
    				localSocket = server.accept();
    			} catch (SocketException e) {
    		    	if(consoleLog.isDebugEnabled())
    		    		consoleLog.debug("Closing a Proxy at " + server.getLocalPort());
        			if (localSocket != null && consoleLog.isDebugEnabled())
            			consoleLog.debug("Local Socket is Closed: " + localSocket.isClosed());
            			if (targetSocket != null)
            				consoleLog.debug("Target Socket is Closed: " + targetSocket.isClosed());
    		    	break;
    			}
    			//Create the 2 threads for the incoming and outgoing traffic of Proxy server
    			targetSocket = new Socket(_remotehost, remoteport);
    			Thread thread1 = null;
    			thread1 = new Thread (new ProxyPipeOut(localSocket, targetSocket, this));
    			thread1.setDaemon(true);
    			if (consoleLog.isDebugEnabled()) {
    				consoleLog.debug("Spawning ProxyPipeOut: " + thread1.getName());
    			}
    			thread1.start();
    			
    			Thread thread2 = null;
    			thread2 = new Thread (new ProxyPipeIn(targetSocket, localSocket, this));
    			thread2.setDaemon(true);
    			if (consoleLog.isDebugEnabled()) {
    				consoleLog.debug("Spawning ProxyPipeIn: " + thread2.getName());
    			}
    			thread2.start();
    		} catch (UnknownHostException e) {
    			consoleLog.fatal("Error: Unknown Host " + _remotehost, e);
    			System.exit(-1);
    		} catch(IOException e){
    			consoleLog.fatal("IOException when accepting new connection.", e);
    			System.exit(-2);
    		}
    	}
    }
    
    /**
     * Reset the Timers and byte counters.
     */
    public void resetProxyCounters() {
    	this.proxyTimeResponded = 0;
    	this.proxyReceiveAmount = 0;
    	this.proxySentAmount = 0;
    	this.proxyTimeEnded = 0;
    	this.proxyTimeStarted = 0;
    }
    
    /**
     * Shutdown the proxy.
     */
    public void shutdown() throws IOException {
    	server.close();
    }
    
	public String getRemotehost() {
		return _remotehost;
	}
	public void setRemotehost(String remotehost) {
		this._remotehost = remotehost;
	}
	public int getRemoteport() {
		return remoteport;
	}
	public void setRemoteport(int remoteport) {
		this.remoteport = remoteport;
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
		return this.t.getState();
	}
	/**
	 * Retrieves the local port on which the ServerSocket is listening.
	 * @return ServerSocket on which the proxy is listening
	 */
	public int getLocalport() {
		return localport;
	}
}


        
      
            
            
            
            
            
            
            
            
            
            
