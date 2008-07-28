package com.awebstorm;

import java.io.IOException;

import org.apache.log4j.PropertyConfigurator;

public class HtmlUnitProxyBehaviour {

	private static final String LOAD_GEN_LOG_PROPS_LOC = "log4j.properties";
	private String remoteHost = "www.customercentrix.com";
	private int remotePort = 80;
	
	public void shouldGet() {
	
		Proxy testProxy = new Proxy(remoteHost,remotePort);
		System.out.println(testProxy.getLocalport());
		testProxy.init();
		
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
