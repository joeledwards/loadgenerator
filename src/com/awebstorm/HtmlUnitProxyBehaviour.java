package com.awebstorm;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.log4j.PropertyConfigurator;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;

public class HtmlUnitProxyBehaviour {

	private static final String LOAD_GEN_LOG_PROPS_LOC = "log4j.properties";
	private String remoteHost = "upload.wikimedia.org";
	private int remotePort = 80;
	
	public void shouldGet() {
	
		Proxy testProxy = new Proxy(remoteHost,remotePort);
		System.out.println(testProxy.getLocalport());
		
		WebClient testClient = new WebClient(BrowserVersion.INTERNET_EXPLORER_7_0, "localhost",testProxy.getLocalport());
		testClient.setRedirectEnabled(true);
		testClient.setPrintContentOnFailingStatusCode(false);
		testClient.setThrowExceptionOnFailingStatusCode(false);
		try {
			@SuppressWarnings("unused")
			Page newPage = testClient.getPage("http://upload.wikimedia.org/wikipedia/commons/thumb/9/91/Wikiversity-logo.svg/30px-Wikiversity-logo.svg.png");
		} catch (FailingHttpStatusCodeException e1) {
			e1.printStackTrace();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try {
			testProxy.shutdown();
		} catch (IOException e) {
			e.printStackTrace();
		}
		while(testProxy.getThreadState().compareTo(Thread.State.TERMINATED) != 0) {
			
		}
	}
	
	public void setUp() {
		PropertyConfigurator.configureAndWatch(LOAD_GEN_LOG_PROPS_LOC);
	}
	
	public void tearDown() {
		
	}
	
}
