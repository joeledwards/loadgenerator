package com.awebstorm.loadgenerator.robot;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PropertyResourceBundle;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.auth.CredentialsProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.SimpleLog;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.WriterAppender;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.SubmitMethod;
import com.gargoylesoftware.htmlunit.WebRequestSettings;

public class TestSpeed {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PropertyResourceBundle loadGeneratorProperties;
		PropertyConfigurator.configureAndWatch("log4j.properties");
		
		Logger testLog = Logger.getLogger("com.awebstorm.loadgenerator.robot.Step.resultLog");
		Logger wireLog = Logger.getLogger("httpclient.wire.header");
		//OutputStream out = new OutputStream();
		Appender streamer = new WriterAppender();
		wireLog.addAppender(streamer);
		
		testLog.info("Max Memory:   " + Runtime.getRuntime().maxMemory() / ( 1024.0 * 1024.0));
		testLog.info("Free Memory:  " + Runtime.getRuntime().freeMemory() / ( 1024.0 * 1024.0));
		testLog.info("Total Memory: " + Runtime.getRuntime().totalMemory() / ( 1024.0 * 1024.0));
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
		BrowserState testRobot = new BrowserState(prefs,10000);
		URL latencyURL = null;
		try {
			latencyURL = new URL("http","www.math.unm.edu","/images/rowan.fla");
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		WebRequestSettings newSettings = new WebRequestSettings(latencyURL,SubmitMethod.GET);
		int i = 0;
		long summer = 0;
			while (i < 100) {
				try {
					
					long startTime = System.currentTimeMillis();
					Page testPage = testRobot.getVUser().getPage(newSettings);
					testLog.info(testPage.getWebResponse().getLoadTimeInMilliSeconds());
					testLog.info("Content: " + testPage.getWebResponse().getContentAsStream().available());
					testLog.info("Content: " + testPage.getWebResponse().getContentAsStream());
					summer = (summer + testPage.getWebResponse().getLoadTimeInMilliSeconds())/(long)2.0;
				} catch (FailingHttpStatusCodeException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				i++;
			}
			testLog.info("Average: " + summer);
/*				MultiThreadedHttpConnectionManager connectionManager =
	            new MultiThreadedHttpConnectionManager();
				HttpClient httpClient_ = new HttpClient(connectionManager);

	            // Disable informational messages from httpclient
	            final Log log = LogFactory.getLog("httpclient.wire");
	            if (log instanceof SimpleLog) {
	                ((SimpleLog) log).setLevel(SimpleLog.LOG_LEVEL_WARN);
	            }

	            httpClient_.getHttpConnectionManager().getParams().setSoTimeout(5000);
	            httpClient_.getHttpConnectionManager().getParams().setConnectionTimeout(5000);

	            //if (virtualHost_ != null) {
	                //httpClient_.getParams().setVirtualHost(virtualHost_);
	            //}
	        

	        // Tell the client where to get its credentials from
	        // (it may have changed on the webClient since last call to getHttpClientFor(...))
	        	httpClient_.getParams().setParameter(CredentialsProvider.PROVIDER, testRobot.getVUser().getCredentialsProvider());
	        	HttpMethodBase httpMethod = makeHttpMethod(newSettings);
	        	httpClient_.executeMethod(newSettings,(HttpMethod)httpMethod);*/
	}
}
