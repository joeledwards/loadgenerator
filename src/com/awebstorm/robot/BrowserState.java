package com.awebstorm.robot;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.HashSet;
import org.apache.log4j.Logger;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.Cache;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * The current state of a virtual browser.
 * @author Cromano
 *
 */
public class BrowserState {

	private WebClient vUser = new WebClient();
	private HtmlPage currentPage;
	private HashSet<String> browserHistory = new HashSet<String>();
	private HashMap<String,String> _preferences;
	private Logger consoleLog = Logger.getLogger(this.getClass());
	private int _proxyPort;
	
	/**
	 * Default Constructor ensures no NullPointerExceptions.
	 * @param preferences Prefs loaded from script
	 * @param proxyPort Port on which to contact the local proxy
	 */
	public BrowserState(final HashMap<String, String> preferences, final int proxyPort) {
		this._proxyPort=proxyPort;
		_preferences = preferences;
		configureState();
	}
	
	/**
	 * Configure the state of the Robot from the preferences in the script.
	 */
	private void configureState() {
		
		WebClient client = null;
		String browVersionString = _preferences.get("htmlRobotBrowserVersion");
		String proxyHost = _preferences.get("proxyHost");
		if (proxyHost == null) {
			client = new WebClient(BrowserVersion.INTERNET_EXPLORER_7_0);
		} else if (browVersionString != null && proxyHost != null) {
			client = new WebClient(BrowserVersion.INTERNET_EXPLORER_7_0, proxyHost, _proxyPort);
		} else {
			client = new WebClient();
		}
		String temp = "";
		temp = _preferences.get("javaScriptEnabled");
		if (temp != null) {
			client.setJavaScriptEnabled(Boolean.parseBoolean(temp));
		} else {
			consoleLog.debug("A preference was not set: " + temp);
		}
		temp = _preferences.get("redirectEnabled");
		if (temp != null) {
			client.setRedirectEnabled(Boolean.parseBoolean(temp));
		} else {
			consoleLog.debug("A preference was not set: " + temp);
		}
		temp = _preferences.get("throwExceptionOnScriptError");
		if (temp != null) {
			client.setThrowExceptionOnScriptError(Boolean.parseBoolean(temp));
		} else {
			consoleLog.debug("A preference was not set: " + temp);
		}
		temp = _preferences.get("useInsecureSSL");
		if (temp != null) {
			try {
				client.setUseInsecureSSL(Boolean.parseBoolean(temp));
			} catch (GeneralSecurityException e) {
				consoleLog.error("Failed Attempt to change InsecureSSL");
				e.printStackTrace();
			}
		} else {
			consoleLog.debug("A preference was not set: " + temp);
		}
		temp = _preferences.get("popupBlockerEnabled");
		if (temp != null) {
			client.setPopupBlockerEnabled(Boolean.parseBoolean(temp));
		} else {
			consoleLog.debug("A preference was not set: " + temp);
		}
		temp = _preferences.get("timeout");
		if (temp != null) {
			client.setTimeout(Integer.parseInt(temp));
		} else {
			consoleLog.debug("A preference was not set: " + temp);
		}
		temp = _preferences.get("cacheSize");
		if (temp != null) {
			client.setCache(new Cache());
		} else {
			consoleLog.debug("A preference was not set: " + temp);
		}
		vUser = client;
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
	 * @return True if browser history does not contain the
	 *  resource, else if browser history already contains the resource
	 */
	public final boolean addUrlToHistory(final String resource) {
		return browserHistory.add(resource);
	}

	/**
	 * Get the proxyPort of this state
	 * @return Proxy port of this state
	 */
	public int getProxyPort() {
		return _proxyPort;
	}
	
}
