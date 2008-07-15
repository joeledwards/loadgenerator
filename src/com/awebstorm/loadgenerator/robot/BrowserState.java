package com.awebstorm.loadgenerator.robot;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.HashSet;
import org.apache.log4j.Logger;

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
	private String _targetDomain;
	private HashSet<String> browserHistory = new HashSet<String>();
	private HashMap<String,String> _preferences;
	private Logger consoleLog = Logger.getLogger(this.getClass());
	
	/**
	 * Default Constructor ensures no NullPointerExceptions.
	 * @param preferences Prefs loaded from script
	 */
	public BrowserState(final HashMap<String, String> preferences) {
		_preferences = preferences;
		configureState();
	}
	
	/**
	 * Configure the state of the Robot from the preferences in the script.
	 */
	private void configureState() {
		
		boolean javaScriptEnabled = Boolean.parseBoolean(_preferences.get("javaScriptEnabled"));
		boolean redirectEnabled = Boolean.parseBoolean(_preferences.get("redirectEnabled"));
		boolean throwExceptionOnScriptError = Boolean.parseBoolean(_preferences.get("throwExceptionOnScriptError"));
		boolean useInsecureSSL = Boolean.parseBoolean(_preferences.get("useInsecureSSL"));
		boolean popupBlockerEnabled = Boolean.parseBoolean(_preferences.get("popupBlockerEnabled"));
		boolean throwExceptionOnFailingStatusCode = Boolean.parseBoolean(_preferences.get("throwExceptionOnFailingStatusCode"));
		boolean printContentOnFailingStatusCode = Boolean.parseBoolean(_preferences.get("printContentOnFailingStatusCode"));
		String browVersionString = _preferences.get("htmlRobotBrowserVersion");
		String proxyHost = _preferences.get("proxyHost");
		int proxyPort = Integer.parseInt(_preferences.get("proxyPort"));
		_targetDomain = _preferences.get("domain");

		WebClient client;
		if (proxyHost.equalsIgnoreCase("none") 
				&& !browVersionString.equals("none")) {
			BrowserVersionFactory browVerFactory = 
				new BrowserVersionFactory(browVersionString);
			client = new WebClient(browVerFactory.getNewBrowserVersion());
		} else if (!browVersionString.equals("none")) {
			BrowserVersionFactory browVerFactory =
				new BrowserVersionFactory(browVersionString);
			client = 
				new WebClient(browVerFactory.getNewBrowserVersion(), proxyHost, proxyPort);
		} else {
			client = new WebClient();
		}
		
		client.setJavaScriptEnabled(javaScriptEnabled);
		consoleLog.debug("Set pref redirecteEnabled: " + redirectEnabled);
		client.setPopupBlockerEnabled(popupBlockerEnabled);
		client.setRedirectEnabled(redirectEnabled);
		client.setCache(new Cache());
		client.setPrintContentOnFailingStatusCode(printContentOnFailingStatusCode);
		client.setThrowExceptionOnFailingStatusCode(throwExceptionOnFailingStatusCode);
		client.setThrowExceptionOnScriptError(throwExceptionOnScriptError);
		
		try {
			client.setUseInsecureSSL(useInsecureSSL);
		} catch (GeneralSecurityException e) {
			consoleLog.error("Failed Attempt to change InsecureSSL");
			e.printStackTrace();
		}
		
		vUser = client;
	}
	
	/**
	 * Get the target Domain.
	 * @return The target domain
	 */
	public final String getDomain() {
		return _targetDomain;
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
	
}
