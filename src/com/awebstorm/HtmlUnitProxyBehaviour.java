package com.awebstorm;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.log4j.PropertyConfigurator;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;

public class HtmlUnitProxyBehaviour {

	private static final String LOAD_GEN_LOG_PROPS_LOC = "log4j.properties";
	private String proxyHost = "localhost";
	private int _proxyPort = 10000; 
	private String remoteHost = "www.customercentrix.com";
	private int remotePort = 80;
	
	public void shouldGet() {
	
		Proxy testProxy = new Proxy(_proxyPort,remoteHost,remotePort);
		testProxy.init();
		
/*		WebClient testClient = new WebClient(BrowserVersion.FIREFOX_2, proxyHost, _proxyPort); 
		Page testPage = null;
		testProxy.resetProxyCounters();
		try {
			testPage = testClient.getPage("http://www.customercentrix.com/themes/pushbutton/header-a.jpg");
		} catch (FailingHttpStatusCodeException e1) {
			e1.printStackTrace();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		List<NameValuePair> testList = testPage.getWebResponse().getResponseHeaders();
		for (int i = 0; i < testList.size(); i++) {
			System.out.println((testList.get(i).getName()));
			System.out.println((testList.get(i).getValue()));
		}
		
		System.out.println(testProxy.getProxyReceiveAmount());
		System.out.println(testProxy.getProxySentAmount());
		System.out.println(testProxy.getProxyTimeStarted());
		System.out.println(testProxy.getProxyTimeResponded());
		System.out.println(testProxy.getProxyTimeEnded());
		try {
			testProxy.shutdown();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			e.printStackTrace();
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
