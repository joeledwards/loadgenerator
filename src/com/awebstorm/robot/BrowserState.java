package com.awebstorm.robot;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.HashSet;
import org.apache.log4j.Logger;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.Cache;
import com.gargoylesoftware.htmlunit.TopLevelWindow;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * The current state of an HTML Robot
 * @author Cromano
 *
 */
public class BrowserState {

	private WebClient vUser = new WebClient();
	private HtmlPage currentPage;
	private WebWindow mainWindow;
	private WebWindow resourceWindow;
	private HashSet<String> browserCached = new HashSet<String>();
	private HashSet<String> browserHistory = new HashSet<String>();
	private HashMap<String,String> statePreferences;
	private Logger consoleLog = Logger.getLogger(this.getClass());
	private int localProxyPort;
	
	/**
	 * Default Constructor loads settings from the preferences procured from the script InputStream.
	 * @param preferences Prefs loaded from script
	 * @param proxyPort Port on which to contact the local proxy
	 */
	public BrowserState(final HashMap<String, String> preferences, final int proxyPort) {
		this.localProxyPort=proxyPort;
		statePreferences = preferences;
		configureState();
	}
	
	/**
	 * Configure the state of the Robot from the preferences in the script.
	 */
	private void configureState() {
		
		WebClient client = null;
		String proxyHost = statePreferences.get("proxyHost");
		client = new WebClient(BrowserVersion.INTERNET_EXPLORER_7_0, proxyHost, localProxyPort);
		mainWindow = new TopLevelWindow("Main Window", client);
		resourceWindow = new TopLevelWindow("Side Window", client);
		client.setThrowExceptionOnFailingStatusCode(false);
		client.setPrintContentOnFailingStatusCode(false);
		String temp = "";
		temp = statePreferences.get("javaScriptEnabled");
		if (temp != null) {
			client.setJavaScriptEnabled(Boolean.parseBoolean(temp));
		} else {
			consoleLog.debug("A preference was not set: " + "javaScriptEnabled");
		}
		temp = statePreferences.get("redirectEnabled");
		if (temp != null) {
			client.setRedirectEnabled(Boolean.parseBoolean(temp));
		} else {
			consoleLog.debug("A preference was not set: " + "redirectEnabled");
		}
		temp = statePreferences.get("throwExceptionOnScriptError");
		if (temp != null) {
			client.setThrowExceptionOnScriptError(Boolean.parseBoolean(temp));
		} else {
			consoleLog.debug("A preference was not set: " + "throwExceptionOnScriptError");
		}
		temp = statePreferences.get("useInsecureSSL");
		if (temp != null) {
			try {
				client.setUseInsecureSSL(Boolean.parseBoolean(temp));
			} catch (GeneralSecurityException e) {
				consoleLog.error("Failed Attempt to change InsecureSSL");
				e.printStackTrace();
			}
		} else {
			consoleLog.debug("A preference was not set: " + "useInsecureSSL");
		}
		temp = statePreferences.get("popupBlockerEnabled");
		if (temp != null) {
			client.setPopupBlockerEnabled(Boolean.parseBoolean(temp));
		} else {
			consoleLog.debug("A preference was not set: " + "popupBlockerEnabled");
		}
		temp = statePreferences.get("cacheSize");
		if (temp != null) {
			client.setCache(new Cache());
		} else {
			consoleLog.debug("A preference was not set: " + "cacheSize");
		}
		vUser = client;
		vUser.setCurrentWindow(mainWindow);
	}
	
	/**
	 * Set the state's current HtmlPage.
	 * @param invokePage New Page
	 */
	public final void setCurrentPage(final HtmlPage invokePage) {
		currentPage = invokePage;
	}
	/**
	 * Get the state's virtual user.
	 * @return Virtual user
	 */
	public final WebClient getVUser() {
		return vUser;
	}
	/**
	 * Get the state's current HtmlPage.
	 * @return Current Page
	 */
	public final HtmlPage getCurrentPage() {
		return currentPage;
	}
	/**
	 * Add a URL to the list of previously obtained resources.
	 * @param resource New resource to index
	 * @return True if browser cache does not contain the
	 *  resource, else false if browser cache already contains the resource
	 */
	public final boolean addUrlToCached(final String resource) {
		return browserCached.add(resource);
	}
	
	/**
	 * Add a URL to the list of previous History.
	 * @param resource New resource to Index
	 * @return True if the browser history does not contain the
	 * resource, else false if browser history already contains the resource 
	 */
	public final boolean addURLToHistory(final String resource) {
		return browserHistory.add(resource);
	}

	/**
	 * Get the proxyPort of this state
	 * @return Proxy port of this state
	 */
	public int getProxyPort() {
		return localProxyPort;
	}
	/**
	 * Get the main window used to open the Html Pages
	 * @return The main window
	 */
	public WebWindow getMainWindow() {
		return mainWindow;
	}
	/**
	 * Get the resource window used to load Html resources such as images, javascript, and other external files 
	 * @return The resource window
	 */
	public WebWindow getResourceWindow() {
		return resourceWindow;
	}
	
}
