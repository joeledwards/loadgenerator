package com.awebstorm.loadgenerator.robot;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PropertyResourceBundle;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

public class TestFile {

	private static int robots =1;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PropertyResourceBundle loadGeneratorProperties;
		PropertyConfigurator.configureAndWatch("../log4j.properties");
		
		Logger testLog = Logger.getLogger("com.awebstorm.loadgenerator.robot.Step.resultLog");
		
		testLog.info("Max Memory:   " + Runtime.getRuntime().maxMemory() / ( 1024.0 * 1024.0));
		testLog.info("Free Memory:  " + Runtime.getRuntime().freeMemory() / ( 1024.0 * 1024.0));
		testLog.info("Total Memory: " + Runtime.getRuntime().totalMemory() / ( 1024.0 * 1024.0));

		ArrayList<BrowserState> tempArray = new ArrayList<BrowserState>(robots);
		for ( int i = 0; i < robots; i++) {
			HashMap<String,String> prefs = new HashMap<String,String>() {{
				put("cacheSize", "50");
				put("domain", "http://www.customercentrix.com");
				put("jobID", "0001");
				put("timeout", "5000");
				put("waitstep", "1000");
				put("iteration", "1");
				put("duration", "60000");
				put("proxyPort", "8099");
				put("proxyHost", "none");
				put("javaScriptEnabled", "false");
				put("redirectEnabled", "true");
				put("throwExceptionOnScriptError", "true");
				put("useInsecureSSL", "false");
				put("popupBlockerEnabled", "true");
				put("htmlRobotBrowserVersion", "none");
			}};
			tempArray.add(i, new BrowserState(prefs));
			try {
				tempArray.get(i).getVUser().getPage(new URL("file","127.0.0.1","C:\\Users\\Cromano\\Desktop\\OpenOffice.org 2.4 (en-US) Installation Files\\licenses\\license_en-US.html"));
			} catch (FailingHttpStatusCodeException e) {
				System.out.println("File not found.");
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//if ((i) % 100 == 0) {
				testLog.info(tempArray.size()+1);
				testLog.info("Max Memory:   " + Runtime.getRuntime().maxMemory() / ( 1024.0 * 1024.0));
				testLog.info("Free Memory:  " + Runtime.getRuntime().freeMemory() / ( 1024.0 * 1024.0));
				testLog.info("Total Memory: " + Runtime.getRuntime().totalMemory() / ( 1024.0 * 1024.0));
			//}
		}
	}

}
