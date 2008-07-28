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
public class ProxyPipeOut implements Runnable {
	private Socket outgoing, incoming;
	private Logger consoleLog = Logger.getLogger(this.getClass());
	private Proxy _myProxy;

	/**
	 * Creates a new ProxyPipeIn to listen on this Socket with the specified Robot owner and pass on any info on in.
	 * @param in Socket to listen on
	 * @param out Socket to write on
	 * @param myRobotOwner Robot that owns this listener
	 */
	ProxyPipeOut(Socket out, Socket in, Proxy myProxy){
		outgoing = out;
		incoming = in;
		this._myProxy=myProxy;
	}

	/**
	 * Run this ProxyPipeIn to accumulate necessary information in its Proxy
	 */
	public void run(){
		int numberRead = 0;
		OutputStream toServer;
		InputStream fromLocal;

		try{
			toServer = incoming.getOutputStream();      
			fromLocal = outgoing.getInputStream();
			boolean notEnd=true;
			while (notEnd) {
				numberRead = fromLocal.read();
				if (numberRead == -1) {
					_myProxy.setProxyTimeEnded(System.currentTimeMillis());
					if (consoleLog.isDebugEnabled()) {
						consoleLog.debug("Closing a ProxyPipeOut: " 
								+ outgoing.getLocalPort() + " "
								+ outgoing.getPort() + " " 
								+ incoming.getLocalPort() + " " 
								+ incoming.getPort());
						consoleLog.debug(incoming.isClosed());
						consoleLog.debug(incoming.isInputShutdown());
						consoleLog.debug(incoming.isOutputShutdown());
						consoleLog.debug(outgoing.isClosed());
						consoleLog.debug(outgoing.isInputShutdown());
						consoleLog.debug(outgoing.isOutputShutdown());
					}
					outgoing.close();
					incoming.close();
					notEnd = false;
				} else {
					if ( _myProxy.getProxyTimeStarted() == 0 )
						_myProxy.setProxyTimeStarted(System.currentTimeMillis());
					_myProxy.incrementProxySentAmount();
					toServer.write(numberRead);
				}
			}
		} catch(IOException e) {
			consoleLog.error("Could not accept a connection", e);
		} catch(ArrayIndexOutOfBoundsException e) {
			consoleLog.error("Buffer Overflow on " + numberRead,e);
		}
	}
}
