package com.awebstorm;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import com.awebstorm.ProxyListener;
import org.apache.log4j.Logger;

/**
 * Proxy server to count bytes coming through.
 * @author Cromano
 *
 */
public class Proxy implements Runnable {
	
	private int localport;
	private String remotehost;
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
	 * Initialize a new Proxy in its own Daemon thread.
	 */
	public void init() {	    	
		t=new Thread(this);
		t.setDaemon(true);
		t.start();
	}
	
	/**
	 * Proxy Constructor.
	 * @param localport Local port to talk to the Robot
	 * @param remotehost Remote host to make requests on
	 * @param remoteport Remote Port to talk on
	 */
    public Proxy (int localport, String remotehost, int remoteport) {
		this.localport=localport;
		this.remotehost=remotehost;
		this.remoteport=remoteport;
    }
    
    /**
     * Run the proxy.
     */
    public void run () {

    	boolean error = false;
    	Socket incoming = null, outgoing = null;

    	// Check for valid local and remote port, hostname not null
    	if ( consoleLog.isDebugEnabled())
    		consoleLog.debug("Checking: Port " + localport + " to " + remotehost + " Port " + remoteport);
    	if(localport <= 0) {
    		consoleLog.fatal("Error: Invalid Local Port Specification " + "\n");
    		error = true;
    	}
    	if(remoteport <=0) {
    		consoleLog.fatal("Error: Invalid Remote Port Specification " + "\n");
    		error = true;
    	}
    	if(remotehost == null) {
    		consoleLog.fatal("Error: Invalid Remote Host Specification " + "\n");
    		error = true;
    	}
    	if(error)
    		System.exit(-1);

    	//Test and create a listening socket at Proxy
    	try{
    		server = new ServerSocket(localport);
    	}
    	catch(IOException e) {
    		consoleLog.fatal("Could not create the proxy on port: " + localport, e);
    		System.exit(-2);
    	}

    	//Loop to listen for incoming connection, and accept if there is one
    	while (true) {
    		try {
    			try {
    				incoming = server.accept();
    			} catch (SocketException e) {
    		    	if(consoleLog.isDebugEnabled())
    		    		consoleLog.debug("Closing a Proxy at " + server.getLocalPort());
    		    	break;
    			}
    			//Create the 2 threads for the incoming and outgoing traffic of Proxy server
    			outgoing = new Socket(remotehost, remoteport); 
    			Thread thread1 = new Thread (new ProxyListener(incoming, outgoing, this));
    			thread1.setDaemon(true);
    			if (consoleLog.isDebugEnabled()) {
    				consoleLog.debug("Spawning Proxy Listener: " + thread1.getName());
    			}
    			thread1.start();
    			
    			Thread thread2 = new Thread (new ProxyListener(outgoing, incoming, this));
    			thread2.setDaemon(true);
    			if (consoleLog.isDebugEnabled()) {
    				consoleLog.debug("Spawning Proxy Listener: " + thread2.getName());
    			}
    			thread2.start();
    			incoming = null;
    		} catch (UnknownHostException e) {
    			consoleLog.fatal("Error: Unknown Host " + remotehost, e);
    			System.exit(-1);
    		} catch(IOException e){
    			consoleLog.fatal("IOException when accepting new connection.", e);
    			System.exit(-2);
    		}
    	}
/*    	if(consoleLog.isDebugEnabled())
    		consoleLog.debug("Closing a Proxy at " + Server.getLocalPort());
    	try {
			Server.close();
		} catch (IOException e) {
			consoleLog.error("Could not close the Server Socket on the Proxy.",e);
		}*/
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
    
    /** Getters and Setters */
	public String getRemotehost() {
		return remotehost;
	}
	public void setRemotehost(String remotehost) {
		this.remotehost = remotehost;
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
	public void addProxyReceiveAmount(int numberRead) {
		this.proxyReceiveAmount+=numberRead;
	}
	public void addProxySentAmount(int numberRead) {
		this.proxySentAmount+=numberRead;
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
}


        
      
            
            
            
            
            
            
            
            
            
            
