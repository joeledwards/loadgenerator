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
		byte[] buffer = new byte[60];
		int numberRead = 0;
		OutputStream ToLocal;
		InputStream FromServer;

		try{
			ToLocal = incoming.getOutputStream();      
			FromServer = outgoing.getInputStream();
			while (true) {
				numberRead = FromServer.read(buffer, 0, 50);
				if (numberRead == -1){
						_myProxy.setProxyTimeEnded(System.currentTimeMillis());
						if (consoleLog.isDebugEnabled())
							consoleLog.debug("Closing a ProxyPipeOut: " + incoming.getLocalPort() + " " + outgoing.getPort());
						incoming.shutdownOutput();
						outgoing.close();
						//incoming.close();
					break;
				} else {
						if ( _myProxy.getProxyTimeResponded() == 0 )
							_myProxy.setProxyTimeResponded(System.currentTimeMillis());
						_myProxy.addProxyReceiveAmount(numberRead);
				}
				if(!outgoing.isClosed()) {
					ToLocal.write(buffer, 0, numberRead);
				}
			}
		} catch(IOException e) {
			consoleLog.error("Could not accept a connection", e);
		} catch(ArrayIndexOutOfBoundsException e) {
			consoleLog.error("Buffer Overflow on " + numberRead,e);
		}
	}
}
