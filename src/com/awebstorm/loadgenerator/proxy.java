package com.awebstorm.loadgenerator;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import com.awebstorm.loadgenerator.proxyThread;
import com.awebstorm.loadgenerator.robot.Robot;

import org.apache.log4j.Logger;

public class proxy implements Runnable {
	
	private int localport;
	private String remotehost;
	private int remoteport;
	private Logger consoleLog = Logger.getLogger(this.getClass());
	private Robot myRobotOwner;
	private Thread t;

	public void init()	
	{	    	

		t=new Thread(this);
		t.start();
	}
	
	public void start() {
		
	}
	
    public proxy (int localport, String remotehost, int remoteport) {
		this.localport=localport;
		this.remotehost=remotehost;
		this.remoteport=remoteport;
    }
    
    public void run () {

    	boolean error = false;
    	Socket incoming, outgoing = null;
    	ServerSocket Server = null;

    	// Check for valid local and remote port, hostname not null
    	consoleLog.info("Checking: Port" + localport + " to " + remotehost + " Port " + remoteport);
    	if(localport <= 0){
    		consoleLog.fatal("Error: Invalid Local Port Specification " + "\n");
    		error = true;
    	}
    	if(remoteport <=0){
    		consoleLog.fatal("Error: Invalid Remote Port Specification " + "\n");
    		error = true;
    	}
    	if(remotehost == null){
    		consoleLog.fatal("Error: Invalid Remote Host Specification " + "\n");
    		error = true;
    	}

    	//If any errors so far, exit program
    	if(error)
    		System.exit(-1);

    	//Test and create a listening socket at proxy
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
    			//Create the 2 threads for the incoming and outgoing traffic of proxy server
    			outgoing = new Socket(remotehost, remoteport); 

    			proxyThread thread1 = new proxyThread(incoming, outgoing, myRobotOwner);
    			thread1.start();

    			proxyThread thread2 = new proxyThread(outgoing, incoming, myRobotOwner);
    			thread2.start();
    		} 
    		catch (UnknownHostException e) {
    			//Test and make connection to remote host
    			consoleLog.fatal("Error: Unknown Host " + remotehost, e);
    			System.exit(-1);
    		} 
    		catch(IOException e){
    			consoleLog.fatal("IOException when accepting new connection.", e);
    			System.exit(-2);//continue;
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


        
      
            
            
            
            
            
            
            
            
            
            
