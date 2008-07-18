package com.awebstorm.loadgenerator;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.awebstorm.loadgenerator.robot.Robot;

/**
 * Listens to the proxy Socket and counts the bytes received and sent to the Robot.
 * @author Cromano
 *
 */
public class ProxyThread extends Thread {
	private Socket incoming, outgoing;
	private Logger consoleLog = Logger.getLogger(this.getClass());
	private Robot myRobotOwner;

	/**
	 * Creates a new ProxyThread to listen on this Socket with the specified Robot owner and pass on any info on in.
	 * @param in Socket to listen on
	 * @param out Socket to write on
	 * @param myRobotOwner Robot that owns this listener
	 */
	ProxyThread(Socket in, Socket out, Robot myRobotOwner){
		incoming = in;
		outgoing = out;
		this.myRobotOwner=myRobotOwner;
	}

	/**
	 * Run this ProxyThread
	 */
	public void run(){
		byte[] buffer = new byte[60];
		int numberRead = 0;
		OutputStream ToClient;
		InputStream FromClient;

		try{
			ToClient = outgoing.getOutputStream();      
			FromClient = incoming.getInputStream();
			while (true) {
				numberRead = FromClient.read(buffer, 0, 50);
				if(numberRead == -1){
					if (consoleLog.isDebugEnabled())
						consoleLog.debug("Socket closing: " + myRobotOwner.getMyThread());
					if(incoming.getPort() == 80) {
						myRobotOwner.getCurrentStep().setStepTimeEnded(System.currentTimeMillis());
					} else {

					}
					break;
				} else {
					if (incoming.getPort() == 80) {
						myRobotOwner.getCurrentStep().addProxyReceiveAmount(numberRead);
						if ( myRobotOwner.getCurrentStep().getStepProxyTimeResponse() == 0 )
							myRobotOwner.getCurrentStep().setStepProxyTimeResponse(System.currentTimeMillis());
					} else {
						myRobotOwner.getCurrentStep().addProxySentAmount(numberRead);
						if ( myRobotOwner.getCurrentStep().getStepTimeStarted() == 0 )
							myRobotOwner.getCurrentStep().setStepTimeStarted(System.currentTimeMillis());
					}
				}
				if(!incoming.isClosed()) {
					ToClient.write(buffer, 0, numberRead);
				}

			}
		} catch(IOException e) {
			consoleLog.error("Could not accept a connection on: " + myRobotOwner.getMyThread(),e);
		} catch(ArrayIndexOutOfBoundsException e) {
			consoleLog.error("Buffer Overflow on " + numberRead,e);
		}
	}
}
