package com.awebstorm;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

/**
 * Listens to the proxy Socket and counts the bytes received and sent to the Robot.
 * @author Cromano
 *
 */
public class ProxyListener implements Runnable {
	private Socket incoming, outgoing;
	private Logger consoleLog = Logger.getLogger(this.getClass());
	private Proxy _myProxy;

	/**
	 * Creates a new ProxyListener to listen on this Socket with the specified Robot owner and pass on any info on in.
	 * @param in Socket to listen on
	 * @param out Socket to write on
	 * @param myRobotOwner Robot that owns this listener
	 */
	ProxyListener(Socket in, Socket out, Proxy myProxy){
		incoming = in;
		outgoing = out;
		this._myProxy=myProxy;
	}

	/**
	 * Run this ProxyListener to accumulate necessary information in its Proxy
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
				if (_myProxy.isShouldStopRunning()) {
					if(!outgoing.isClosed())
						outgoing.close();
					if(!incoming.isClosed())
						incoming.close();
					break;
				}
				if (numberRead == -1){
					
					if (consoleLog.isDebugEnabled())
						consoleLog.debug("Closing a ProxyListener: " + incoming.getPort());
					if(incoming.getPort() == 80) {
						System.out.println("in: " + numberRead);
						_myProxy.setProxyTimeEnded(System.currentTimeMillis());
						outgoing.shutdownOutput();
					} else {
						System.out.println("out: " + numberRead);
						incoming.shutdownInput();
					}
					
					break;
				} else {
					if (incoming.getPort() == 80) {
						System.out.println("in: " + numberRead);
						if ( _myProxy.getProxyTimeResponded() == 0 )
							_myProxy.setProxyTimeResponded(System.currentTimeMillis());
						_myProxy.addProxyReceiveAmount(numberRead);
					} else {
						System.out.println("out: " + numberRead);
						_myProxy.addProxySentAmount(numberRead);
						_myProxy.setProxyTimeStarted(System.currentTimeMillis());
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
