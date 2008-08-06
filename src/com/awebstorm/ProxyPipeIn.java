package com.awebstorm;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.BindException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.PortUnreachableException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

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
		} catch (SocketTimeoutException e) {
			//Normal exception
			if (consoleLog.isDebugEnabled()) {
				consoleLog.debug("Socket Timed Out", e);
			}
			myProxyOwner.setProxyMessage("SocketTimeoutException");
			try {
				incoming.close();
			} catch (IOException e1) {
				consoleLog.error("Could not close the local socket.", e1);
			}
		} catch (UnknownHostException e) {
			//Fatal Exception
			consoleLog.fatal("UnknownHostException " + incoming.getInetAddress(), e);
			myProxyOwner.setProxyMessage("UnknownHostException " + incoming.getInetAddress());
			try {
				incoming.close();
			} catch (IOException e1) {
				consoleLog.error("Could not close the local socket.", e1);
			}
		} catch (BindException e) {
			//Fatal Exception
			consoleLog.fatal("BindException to remoteport: " + incoming.getPort(), e);
			myProxyOwner.setProxyMessage("BindException to remoteport: " + incoming.getPort());
			try {
				incoming.close();
			} catch (IOException e1) {
				consoleLog.error("Could not close the local socket.", e1);
			}
		} catch (ConnectException e) {
			//Fatal Exception
			consoleLog.fatal("ConnectException to: " + incoming.getInetAddress() + ":" + incoming.getPort(), e);
			myProxyOwner.setProxyMessage("ConnectException to: " + incoming.getInetAddress() + ":" + incoming.getPort());
			try {
				incoming.close();
			} catch (IOException e1) {
				consoleLog.error("Could not close the local socket.", e1);
			}
		} catch (NoRouteToHostException e) {
			//Fatal Exception
			consoleLog.fatal("NoRouteToHostException to remote host: " + incoming.getInetAddress(), e);
			myProxyOwner.setProxyMessage("NoRouteToHostException to remote host: " + incoming.getInetAddress());
			try {
				incoming.close();
			} catch (IOException e1) {
				consoleLog.error("Could not close the local socket.", e1);
			}
		} catch (PortUnreachableException e) {
			//Fatal Exception
			consoleLog.fatal("PortUnreachableException to remote port: " + incoming.getPort(), e);
			myProxyOwner.setProxyMessage("PortUnreachableException to remote port: " + incoming.getPort());
			try {
				incoming.close();
			} catch (IOException e1) {
				consoleLog.error("Could not close the local socket.", e1);
			}
		} catch (SocketException e) {
			//Occurs when a connection resets on a bad port
			//Fatal Exception
			consoleLog.fatal("SocketException to remote port: " + incoming.getPort(), e);
			myProxyOwner.setProxyMessage("SocketException to remote port: " + incoming.getPort());
			try {
				incoming.close();
				outgoing.close();
			} catch (IOException e1) {
				consoleLog.error("Could not close the local socket.", e1);
			}
		} catch (IOException e) {
			//Normal Exception
			consoleLog.error("IOException when accepting new connection.", e);
			myProxyOwner.setProxyMessage("IO Error");
		} catch (ArrayIndexOutOfBoundsException e) {
			//Buffer overflow, should be impossible
			consoleLog.error("Buffer Overflow on " + numberRead, e);
		}
	}
}
