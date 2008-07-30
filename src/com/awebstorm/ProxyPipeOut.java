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
	private Proxy myProxyOwner;

	/**
	 * Creates a new ProxyPipeIn to listen on this Socket with the specified Robot owner and pass on any info on in.
	 * @param in Socket to listen on
	 * @param out Socket to write on
	 * @param myProxy Proxy that created this ProxyPipeOut in order to keep track of bytes and time
	 */
	ProxyPipeOut(final Socket out, final Socket in, final Proxy myProxy) {
		outgoing = out;
		incoming = in;
		this.myProxyOwner = myProxy;
	}

	/**
	 * Run this ProxyPipeIn to accumulate necessary information in its Proxy.
	 */
	public final void run() {
		int numberRead = 0;
		OutputStream toServer;
		InputStream fromLocal;

		try{
			toServer = incoming.getOutputStream();      
			fromLocal = outgoing.getInputStream();
			boolean notEnd = true;
			while (notEnd) {
				numberRead = fromLocal.read();
				if (numberRead == -1) {
					myProxyOwner.setProxyTimeEnded(System.currentTimeMillis());
					if (consoleLog.isDebugEnabled()) {
						consoleLog.debug("Closing a ProxyPipeOut: " 
								+ outgoing.getLocalPort() + " "
								+ outgoing.getPort() + " " 
								+ incoming.getLocalPort() + " " 
								+ incoming.getPort());
					}
					outgoing.close();
					notEnd = false;
				} else {
					if ( myProxyOwner.getProxyTimeStarted() == 0 )
						myProxyOwner.setProxyTimeStarted(System.currentTimeMillis());
					myProxyOwner.incrementProxySentAmount();
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
