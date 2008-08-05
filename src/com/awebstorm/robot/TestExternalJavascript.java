package com.awebstorm.robot;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.PropertyConfigurator;

import com.awebstorm.Proxy;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.Cache;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.TopLevelWindow;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequestSettings;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.WebWindowImpl;
/**
 * Temporary file for debugging
 * @author Cromano
 *
 */
public class TestExternalJavascript {

	private PropertyResourceBundle loadGeneratorProperties;
	private static final String LOAD_GEN_PROPS_LOC = "LoadGenerator";
	private static final String LOAD_GEN_LOG_PROPS_LOC = "log4j.properties";
	
	public static void main(String[] args) {
		new TestExternalJavascript().runTest();
	}
	
	public void runTest() {
		PropertyConfigurator.configureAndWatch(LOAD_GEN_LOG_PROPS_LOC);
		loadGeneratorProperties = 
			(PropertyResourceBundle) ResourceBundle.getBundle(LOAD_GEN_PROPS_LOC);
		String remotehost = "www.customercentrix.com";
		int remoteport = 80;
		int numberOfRobots = 1;
		Proxy[] loadGenProxyArray = new Proxy[numberOfRobots];
		for (int i = 0; i < numberOfRobots; i++) {
			loadGenProxyArray[i] = new Proxy(remotehost, remoteport);
		}
		WebClient newClient = new WebClient(BrowserVersion.INTERNET_EXPLORER_7_0, "localhost", loadGenProxyArray[0].getLocalport());
		WebWindow newWindow = new TopLevelWindow("Main Window", newClient);
		newClient.setThrowExceptionOnFailingStatusCode(false);
		newClient.setPrintContentOnFailingStatusCode(false);
		newClient.setJavaScriptEnabled(false);
		newClient.setRedirectEnabled(true);
		newClient.setThrowExceptionOnScriptError(true);
		newClient.setPopupBlockerEnabled(true);
		newClient.setCache(new Cache());
		try {
			newClient.setCurrentWindow(newWindow);
			newClient.setUseInsecureSSL(false);
		} catch (GeneralSecurityException e1) {
			e1.printStackTrace();
		}
		try{
			WebRequestSettings newSettings = new WebRequestSettings(new URL("http://www.customercentrix.com/about_us.htm"));
			newClient.getPage(newWindow, newSettings);;
		} catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		try {
			loadGenProxyArray[0].shutdown();
		} catch (IOException e) {
			e.printStackTrace();
		}
		while(loadGenProxyArray[0].getThreadState().compareTo(Thread.State.TERMINATED) != 0) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	
}
