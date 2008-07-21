package com.awebstorm;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.awebstorm.robot.Robot;

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
				for(int i = 0; i < buffer.length; i++) {
					System.out.print((char)buffer[i]);
				}
				if (numberRead == -1){
					if (consoleLog.isDebugEnabled())
						consoleLog.debug("Closing a ProxyThread.");
					if(incoming.getPort() == 80) {
						//myRobotOwner.getCurrentStep().setStepTimeEnded(System.currentTimeMillis());
					} else {
						if (outgoing.getKeepAlive())
							myRobotOwner.getCurrentStep().setStepTimeStarted(System.currentTimeMillis());
					}
					break;
				} else {
					if (incoming.getPort() == 80) {
						myRobotOwner.getCurrentStep().addProxyReceiveAmount(numberRead);
						if ( myRobotOwner.getCurrentStep().getStepProxyTimeResponse() == 0 )
							myRobotOwner.getCurrentStep().setStepProxyTimeResponse(System.currentTimeMillis());
					} else {
						myRobotOwner.getCurrentStep().addProxySentAmount(numberRead);
						if ( myRobotOwner.getCurrentStep().getStepProxyTimeEnded() == 0 )
							myRobotOwner.getCurrentStep().setStepProxyTimeEnded(System.currentTimeMillis());
					}
				}
				if(!incoming.isClosed()) {
					ToClient.write(buffer, 0, numberRead);
				}

			}
		} catch(IOException e) {
			consoleLog.error("Could not accept a connection", e);
		} catch(ArrayIndexOutOfBoundsException e) {
			consoleLog.error("Buffer Overflow on " + numberRead,e);
		}
	}
}
