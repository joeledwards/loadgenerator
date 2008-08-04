package com.awebstorm;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

import org.apache.log4j.Logger;

/**
 * Listens to the proxy Socket and counts the bytes received and sent.
 * @author Cromano
 *
 */
public class ProxyPipeIn implements Runnable {
	private Socket incoming, outgoing;
	private Logger consoleLog = Logger.getLogger(this.getClass());
	private Proxy myProxyOwner;

	/**
	 * Creates a new ProxyPipeIn to listen on this Socket with the specified Proxy owner and pass on any info on in.
	 * @param in Socket to listen on
	 * @param out Socket to write on
	 * @param myProxy Proxy that created this ProxyPipeIn in order to keep byte and time recording
	 */
	ProxyPipeIn(final Socket in, final Socket out, final Proxy myProxy) {
		incoming = in;
		outgoing = out;
		this.myProxyOwner = myProxy;
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
			boolean notEnd = true;
			while (notEnd) {
				if(incoming.isInputShutdown())
					break;
				numberRead = fromServer.read();
				if (numberRead == -1){
					if (consoleLog.isDebugEnabled()) {
						consoleLog.debug("Closing a ProxyPipeIn: " 
								+ incoming.getPort() + " " 
								+ incoming.getLocalPort() + " " 
								+ outgoing.getPort() + " " 
								+ outgoing.getLocalPort());
					}
					incoming.close();
					notEnd = false;
				} else {
					myProxyOwner.incrementProxyReceiveAmount();
					if (myProxyOwner.getProxyTimeResponded() == 0) {
						myProxyOwner.setProxyTimeResponded(System.currentTimeMillis());
					}
					
					toLocal.write(numberRead);
				}
			}
		} catch (SocketException e) {
			if (consoleLog.isDebugEnabled())
				consoleLog.error("Socket Exception", e);
		} catch (IOException e) {
			consoleLog.error("Could not accept a connection", e);
		} catch (ArrayIndexOutOfBoundsException e) {
			consoleLog.error("Buffer Overflow on " + numberRead, e);
		}
	}
}
