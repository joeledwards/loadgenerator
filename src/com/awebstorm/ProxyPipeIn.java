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
public class ProxyPipeIn implements Runnable {
	private Socket incoming, outgoing;
	private Logger consoleLog = Logger.getLogger(this.getClass());
	private Proxy _myProxy;

	/**
	 * Creates a new ProxyPipeIn to listen on this Socket with the specified Robot owner and pass on any info on in.
	 * @param in Socket to listen on
	 * @param out Socket to write on
	 * @param myRobotOwner Robot that owns this listener
	 */
	ProxyPipeIn(Socket in, Socket out, Proxy myProxy){
		incoming = in;
		outgoing = out;
		this._myProxy=myProxy;
	}

	/**
	 * Run this ProxyPipeIn to accumulate necessary information in its Proxy
	 */
	public void run(){
		int numberRead = 0;
		OutputStream toLocal;
		InputStream fromServer;

		try{
			toLocal = outgoing.getOutputStream();      
			fromServer = incoming.getInputStream();
			boolean notEnd=true;
			while (notEnd) {
				numberRead = fromServer.read();
				if (numberRead == -1){
					if (consoleLog.isDebugEnabled()) {
						consoleLog.debug("Closing a ProxyPipeIn: " 
								+ incoming.getPort() + " " 
								+ incoming.getLocalPort() + " " 
								+ outgoing.getPort() + " " 
								+ outgoing.getLocalPort());
						//outgoing.shutdownOutput();
						//incoming.close();
						consoleLog.debug(incoming.isClosed());
						consoleLog.debug(incoming.isInputShutdown());
						consoleLog.debug(incoming.isOutputShutdown());
						consoleLog.debug(outgoing.isClosed());
						consoleLog.debug(outgoing.isInputShutdown());
						consoleLog.debug(outgoing.isOutputShutdown());
					}
					notEnd=false;
				} else {
					_myProxy.incrementProxyReceiveAmount();
					if (_myProxy.getProxyTimeResponded() == 0)
						_myProxy.setProxyTimeResponded(System.currentTimeMillis());
					
					toLocal.write(numberRead);
				}
			}
		} catch(IOException e) {
			consoleLog.error("Could not accept a connection", e);
		} catch(ArrayIndexOutOfBoundsException e) {
			consoleLog.error("Buffer Overflow on " + numberRead,e);
		}
	}
}
