package com.awebstorm.robot;


import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.log4j.PropertyConfigurator;

import com.awebstorm.Proxy;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class HtmlUnitProxyBehaviour {

	private static final String LOAD_GEN_LOG_PROPS_LOC = "log4j.properties";
	private String remoteHost = "my.ccx01.info";
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
			Page newPage = testClient.getPage("http://my.ccx01.info/form.htm");
			HtmlPage page = (HtmlPage)newPage;
			System.out.println(page.getDocumentElement().getFirstChild().getCanonicalXPath());
			System.out.println(((HtmlElement)page.getByXPath("//html/body/form[@name='test_form' and @id='one']").get(0)).getCanonicalXPath());
			
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
