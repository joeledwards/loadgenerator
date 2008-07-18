package com.awebstorm.loadgenerator;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import com.awebstorm.loadgenerator.ProxyThread;
import com.awebstorm.loadgenerator.robot.Robot;

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
	private Robot myRobotOwner;
	private Thread t;

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
    	Socket incoming, outgoing = null;
    	ServerSocket Server = null;

    	// Check for valid local and remote port, hostname not null
    	consoleLog.info("Checking: Port " + localport + " to " + remotehost + " Port " + remoteport);
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
    		Server = new ServerSocket(localport);
    	}
    	catch(IOException e) {
    		e.printStackTrace();
    	}

    	//Loop to listen for incoming connection, and accept if there is one
    	while(true)
    	{
    		try{
    			incoming = Server.accept();
    			//Create the 2 threads for the incoming and outgoing traffic of Proxy server
    			outgoing = new Socket(remotehost, remoteport); 

    			ProxyThread thread1 = new ProxyThread(incoming, outgoing, myRobotOwner);
    			thread1.setDaemon(true);
    			thread1.start();

    			ProxyThread thread2 = new ProxyThread(outgoing, incoming, myRobotOwner);
    			thread2.setDaemon(true);
    			thread2.start();
    		} 
    		catch (UnknownHostException e) {
    			consoleLog.fatal("Error: Unknown Host " + remotehost, e);
    			System.exit(-1);
    		} 
    		catch(IOException e){
    			consoleLog.fatal("IOException when accepting new connection.", e);
    			System.exit(-2);
    		}

    	}
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
	public void setMyRobotOwner(Robot myRobotOwner) {
		this.myRobotOwner = myRobotOwner;
	}
	public Robot getMyRobotOwner() {
		return myRobotOwner;
	}
}


        
      
            
            
            
            
            
            
            
            
            
            
