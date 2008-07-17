package com.awebstorm.loadgenerator;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.awebstorm.loadgenerator.robot.HTMLRobot;
import com.awebstorm.loadgenerator.robot.Robot;

public class ProxyThread extends Thread {
	private Socket incoming, outgoing;
	private Logger consoleLog = Logger.getLogger(this.getClass());
	private long byteCounter = 0;
	private HTMLRobot myRobotOwner;

	ProxyThread(Socket in, Socket out, Robot myRobotOwner){
		incoming = in;
		outgoing = out;
		this.myRobotOwner=(HTMLRobot)myRobotOwner;
	}

	public void run(){
		byte[] buffer = new byte[60];
		int numberRead = 0;
		OutputStream ToClient;
		InputStream FromClient;

		try{
			ToClient = outgoing.getOutputStream();      
			FromClient = incoming.getInputStream();
			while( true){
				numberRead = FromClient.read(buffer, 0, 50);
				//System.out.println("read " + numberRead);
				if(numberRead == -1){
					incoming.close();
					outgoing.close();
				} else {
					if ( outgoing.getLocalPort() > 9000 ) {
						myRobotOwner.getCurrentStep().addProxyReceiveAmount(numberRead);
					} else {
						myRobotOwner.getCurrentStep().addProxySentAmount(numberRead);
					}
				}
				ToClient.write(buffer, 0, numberRead);
			}

		} catch(IOException e) {
			consoleLog.warn("Could not accept a connection.",e);
		} catch(ArrayIndexOutOfBoundsException e) {
			consoleLog.warn("Buffer Overflow.",e);
		}

	}

	/**Getters and Setters*/
	public long getByteCounter() {
		return byteCounter;
	}
	public void setByteCounter(long byteCounter) {
		this.byteCounter = byteCounter;
	}
    
}
