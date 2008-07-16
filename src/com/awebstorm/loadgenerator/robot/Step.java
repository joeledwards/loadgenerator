package com.awebstorm.loadgenerator.robot;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.log4j.Logger;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.Attributes;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.SubmitMethod;
import com.gargoylesoftware.htmlunit.WebRequestSettings;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlLink;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlStyle;

/**
 * Holds the script step information and operations.
 * Details of a step are stored in an Attributes List
 * @author Cromano
 */
public class Step implements Comparable<Step> {
	
	public static enum ActionTypes {
		
		STEPS,
		INVOKE,
		VERIFY_TITLE,
		BUTTON,
		WAIT,
		POST,
		FILL_FORM,
		CLICK_LINK
		
	}
	
	private BrowserState _currentBrowserState;
	private String _stepName;
	private int _stepNum;
	private Attributes _stepAttributeList;
	private Logger consoleLog = Logger.getLogger(this.getClass());
	private Logger resultLog = Logger.getLogger("com.awebstorm.loadgenerator.robot.Step.resultLog");
	private long loadTime;
	private int loadAmount; 
	private int proxyLoadAmount = 0;

	public Step(String name, int value, Attributes list) {
		_stepName = name;
		_stepNum = value;
		_stepAttributeList = list;
	}
	
	/**
	 * Name of the Step
	 * @return
	 */
	public String getName() {
		return _stepName;
	}
	
	/**
	 * Attributes of the Step
	 * @return
	 */
	public Attributes getList() {
		return _stepAttributeList;
	}

	/**
	 * Compare two steps by value
	 */
	public int compareTo(Step o) {
		return _stepNum - o.getValue();
	}

	/**
	 * Execute a Step
	 * @param jobID Robot job ID
	 * @param browserState currentState of the robot's browser
	 */
	public void execute(String jobID, BrowserState browserState) {
		loadTime = 0;
		loadAmount = 0;
		_currentBrowserState = browserState;
		ActionTypes currentType = null;
		boolean stepReturnStatus = true;
		try {
			currentType = ActionTypes.valueOf(_stepName);
		} catch (IllegalArgumentException e) {
			consoleLog.error("Unknown Step type found.",e);
			return;
		}
		if ( currentType == null ) {
			return;
		}
		
		switch (currentType) {
		case STEPS:
			return;
		case WAIT:
			this.waitStep();
			break;
		case INVOKE:
			stepReturnStatus = this.invoke();
			break;
		case VERIFY_TITLE:
			stepReturnStatus = this.verifyTitle();
			break;
		case BUTTON:
			stepReturnStatus = this.button();
			break;
		case FILL_FORM:
			stepReturnStatus = this.fillForm();
			break;
		case POST:
			stepReturnStatus = this.post();
			break;
		case CLICK_LINK:
			stepReturnStatus = this.clickLink();
			break;
		default:
			consoleLog.warn("Unknown Step type found.");
			stepReturnStatus = false;
			break;
		}
		
		report(stepReturnStatus, jobID);
		
	}
	
	/**
	 * Click a link on a webpage.
	 * Not Yet Implemented
	 * @return
	 */
	private boolean clickLink() {
		
		
		
		return false;
	}

	/**
	 * Standard POST operation using the parameters stored in the postList
	 * @return Success?
	 */
	private boolean post() {
		
		List<NameValuePair> postList = new LinkedList<NameValuePair>();
		for (int i = 1;i < _stepAttributeList.getLength(); i++) {
			postList.add(new NameValuePair(_stepAttributeList.getLocalName(i),_stepAttributeList.getValue(i)));
		}
		WebRequestSettings newSettings = null;
		HtmlPage postPage = null;
		String currentPath = _currentBrowserState.getDomain() + _stepAttributeList.getValue(0);
		try {
			newSettings = new WebRequestSettings(new URL(currentPath),SubmitMethod.POST);
		} catch (MalformedURLException e1) {
			consoleLog.error("Bad URL passed to a POST operation.",e1);
			return false;
		}
		newSettings.setRequestParameters(postList);
		try {
			postPage = (HtmlPage) _currentBrowserState.getVUser().getPage(newSettings);
		} catch (FailingHttpStatusCodeException e) {
			consoleLog.error("POST operation, " + _stepName + " has a bad status message.",e);
			return false;
		} catch (IOException e) {
			consoleLog.error("IOException thrown during a POST operation.",e);
			return false;
		}
		_currentBrowserState.setCurrentPage(postPage);
		postList.clear();
		loadTime = postPage.getWebResponse().getLoadTimeInMilliSeconds();
		try {
			loadAmount = postPage.getWebResponse().getContentAsStream().available();
		} catch (IOException e1) {
			consoleLog.error("IOException while reading content as from post stream to count loadAmount.", e1);
		}
		
		return collectResources(postPage);
	}
	
	/**
	 * Fill a Form with information. Usually followed by a BUTTON or CLICK.
	 * Not Yet Implemented.
	 * @return
	 */
	private boolean fillForm() {
		boolean tempStatus = true;
		
		
		return tempStatus;
	}

	/**
	 * Press the button whose name is given
	 * @return
	 */
	private boolean button() {
		HtmlButton tempButton = null;
		if( _currentBrowserState.getCurrentPage() == null )
			return false;
		List<HtmlForm> tempForms = _currentBrowserState.getCurrentPage().getForms();
		
		if(tempForms == null) {
			consoleLog.error("No Forms to search for buttons.");
			return false;
		}
		for (HtmlForm i: tempForms) {
			try {
				tempButton = i.getButtonByName(_stepAttributeList.getValue(0));
			} catch (ElementNotFoundException e) {
				consoleLog.error("No such Button exists");
				return false;
			}
		}
		if(tempButton == null) {
			consoleLog.error("No Button to click.");
			return false;
		}
		try {
			tempButton.click();
		} catch (IOException e) {
			consoleLog.error("Bad Button Clicked.");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Standard GET operation
	 * @return
	 */
	private boolean invoke() {
		String currentPath = _stepAttributeList.getValue(0);
		currentPath = _currentBrowserState.getDomain() + currentPath;
		HtmlPage invokePage = null;
		try {
			invokePage = (HtmlPage) _currentBrowserState.getVUser().getPage(currentPath);
		} catch (FailingHttpStatusCodeException e) {
			consoleLog.error("Bad Status Code.",e);
			return false;
		} catch (MalformedURLException e) {
			consoleLog.error("MalformedURL",e);
			return false;
		} catch (SocketTimeoutException e ) {
			consoleLog.info("Socket Timed Out from licit/illicit factors.");
			return false;
		} catch (IOException e) {
			consoleLog.error("IO Error during Invoke.",e);
			return false;
		}
		_currentBrowserState.setCurrentPage(invokePage);
		loadTime = invokePage.getWebResponse().getLoadTimeInMilliSeconds();
		loadAmount = invokePage.getWebResponse().getResponseBody().length;
		return collectResources(invokePage);
	}
	/**
	 * Collects local resources from the page passed 
	 * i.e. images, style sheets, javascript.
	 * @param invokePage Page to collect necessary resources for
	 * @return
	 */
	private boolean collectResources(HtmlPage invokePage) {
		
		boolean tempStatus = true;
		if (invokePage.getWebResponse().getStatusCode() < 200
				|| invokePage.getWebResponse().getStatusCode() > 399) {
			tempStatus = false;
		}
		Iterable<HtmlElement> tempList = 
			invokePage.getDocumentElement().getAllHtmlChildElements();
		StringBuffer resourcesCollector = new StringBuffer();
		String tempAttr;
		WebResponse temporary;
		NamedNodeMap tempAttrs;
		for (HtmlElement temp : tempList) {
			try {
				temporary = null;
				tempAttrs = temp.getAttributes();
				tempAttr = temp.getAttribute("src");
				if (tempAttrs.getNamedItem("src") != null) {
					if (!tempAttr.startsWith("http")) {
						if (tempAttr.charAt(0) == '/') {
							tempAttr = _currentBrowserState.getDomain() + tempAttr;
						} else {
							tempAttr = _currentBrowserState.getDomain() + '/' + tempAttr;
						}
					}
					if (_currentBrowserState.addUrlToHistory(tempAttr)) {
						temporary = _currentBrowserState.getVUser().getPage(tempAttr).getWebResponse();
						if (consoleLog.isDebugEnabled()) {
							resourcesCollector.append("Resources obtained: " 
								+ tempAttr + '\n');
						}
						loadTime += temporary.getLoadTimeInMilliSeconds();
						loadAmount += temporary.getResponseBody().length;
						if (temporary.getStatusCode() != 200) {
							tempStatus = false;
						}
					}
				} else if (temp.getClass() == HtmlStyle.class) {
					tempAttrs = ((HtmlStyle) temp).getAttributes();
					tempAttr = ((HtmlStyle) temp).getTextContent();
					LinkedList<String> styleResourceList = 
						StyleParsers.grabStyleElementText(tempAttr);
					String aResource;
					while (!styleResourceList.isEmpty()){
						aResource = styleResourceList.poll();
						if (!aResource.startsWith("http")) {
							if (aResource.charAt(0) == '/') {
								aResource = _currentBrowserState.getDomain() 
								+ aResource;
							} else {
								aResource = _currentBrowserState.getDomain() 
								+ '/' + aResource;
							}
						}
						if (_currentBrowserState.addUrlToHistory(aResource)) {
							temporary = _currentBrowserState.getVUser().getPage(aResource).getWebResponse();
							if (consoleLog.isDebugEnabled()) {
								resourcesCollector.append("Import resources obtained: "
									+ aResource + '\n');
							}
							loadTime += temporary.getLoadTimeInMilliSeconds();
							loadAmount += temporary.getResponseBody().length;
							if (temporary.getStatusCode() != 200 ) {
								tempStatus = false;
							}
							if (aResource.endsWith(".css")) {
								LinkedList<String> tempHolder = 
									StyleParsers.grabStyleSheetText(temporary.getContentAsString());
								String path = 
									StyleParsers.subDirBuilder(temporary.getUrl());
								while (!tempHolder.isEmpty()) {
									if (consoleLog.isDebugEnabled()) {
										consoleLog.debug(tempHolder.peek());
									}
									styleResourceList.add(path + tempHolder.poll());
								}
							}
						}
					}
				} else if (temp.getClass() == HtmlLink.class) {
					String aResource;
					tempAttrs = ((HtmlLink) temp).getAttributes();
					for (int i = 0; i < tempAttrs.getLength(); i++) {
						if (tempAttrs.getNamedItem("rel").getNodeValue().equals("stylesheet")) {
							aResource = tempAttrs.getNamedItem("href").getNodeValue();
							if (!aResource.startsWith("http")) {
								if (aResource.charAt(0) == '/') {
									aResource = _currentBrowserState.getDomain() + aResource;
								} else {
									aResource = _currentBrowserState.getDomain() + '/' + aResource;
								}
							}
							if (_currentBrowserState.addUrlToHistory(aResource)) {
								temporary = _currentBrowserState.getVUser().getPage(aResource).getWebResponse();
								if (consoleLog.isDebugEnabled()) {
									resourcesCollector.append("Link Resources obtained: " + aResource + '\n');
								}
								loadTime += temporary.getLoadTimeInMilliSeconds();
								loadAmount += temporary.getResponseBody().length;
								if (temporary.getStatusCode() != 200) {
									tempStatus = false;
								}
								LinkedList<String> styleResourceList = StyleParsers.grabStyleSheetText(temporary.getContentAsString());
								while (!styleResourceList.isEmpty()) {
									aResource = StyleParsers.subDirBuilder(temporary.getUrl()) + styleResourceList.poll();
									if (!aResource.startsWith("http")) {
										if (aResource.charAt(0) == '/') {
											aResource = _currentBrowserState.getDomain() + aResource;
										} else {
											aResource = _currentBrowserState.getDomain() + '/' + aResource;
										}
									}
									if (_currentBrowserState.addUrlToHistory(aResource)) {
										temporary = _currentBrowserState.getVUser().getPage(aResource).getWebResponse();
										if (consoleLog.isDebugEnabled()) {
											resourcesCollector.append("Style Sheet Pic Resources obtained: " + aResource + '\n');
										}
										loadTime += temporary.getLoadTimeInMilliSeconds();
										loadAmount += temporary.getResponseBody().length;
										if (temporary.getStatusCode() != 200) {
											tempStatus = false;
										}
									}
								}
							}
						}
					}
				}
			} catch (FailingHttpStatusCodeException e) {
				consoleLog.error("Bad Status Code.", e);
				tempStatus = false;
			} catch (MalformedURLException e) {
				consoleLog.error("Bad URL Entered during resource retrieval.", e);
				tempStatus = false;
			} catch (IOException e) {
				consoleLog.error("IO Error Occured during resource retrieval.", e);
				tempStatus = false;
			}
		}
		if (consoleLog.isDebugEnabled()) {
			consoleLog.debug(resourcesCollector.toString());
		}
		return tempStatus;
	}

	/**
	 * Verify the currentpage title with an Attribute value.
	 * @return True if title is correct, else false
	 */
	private boolean verifyTitle() {
		if (consoleLog.isDebugEnabled()) {
			consoleLog.debug(_currentBrowserState.getCurrentPage().getTitleText());
		}
		if (_currentBrowserState.getCurrentPage() != null) {
			return _currentBrowserState.getCurrentPage().getTitleText().equals(_stepAttributeList.getValue(0));
		}
		return false;
	}

	/**
	 * Instruct this thread to wait for a given period of time.
	 */
	private void waitStep() {
		if (_stepAttributeList.getValue(0).equals("")) {
/*			try {
				this.wait(Robot.DEFAULT_WAIT_STEP);
			} catch (InterruptedException e) {
				consoleLog.error("Interrupted Exception during a default WAIT step", e);
				e.printStackTrace();
			}*/
		} else {
/*			try {
				this.wait(Integer.parseInt(_list.getValue(0)));
			} catch (NumberFormatException e) {
				consoleLog.error("Could not determine the wait length during a WAIT Step", e);
				try {
					this.wait(Robot.DEFAULT_WAIT_STEP);
				} catch (InterruptedException e1) {
					consoleLog.error("Interrupted Exception during a WAIT step", e);
					e1.printStackTrace();
				}
				e.printStackTrace();
			} catch (InterruptedException e) {
				consoleLog.error("Interrupted Exception during a WAIT step", e);
				e.printStackTrace();
			}*/
		}
	}
	
	/**
	 * Report the results of a Step.
	 * @param stepStatus Success if true, Failure if false
	 * @param jobID Current job ID
	 */
	private void report(boolean stepStatus, String jobID) {
		
		StringBuffer tempResult = new StringBuffer();
		tempResult.append(jobID);
		tempResult.append(',');
		tempResult.append(_stepName);
		tempResult.append(',');
		tempResult.append(_stepNum);
		tempResult.append(',');
		tempResult.append(System.currentTimeMillis());
		tempResult.append(',');
		tempResult.append(loadTime);
		tempResult.append(',');
		tempResult.append(loadAmount);
		tempResult.append(',');
		if (loadTime == 0) {
			loadTime = 1;
		}
		tempResult.append(proxyLoadAmount);
		tempResult.append(',');
		tempResult.append(loadAmount / loadTime);
		tempResult.append(',');
		if (stepStatus) {
			tempResult.append("success");
		} else {
			tempResult.append("failure");
		}
		resultLog.info(tempResult);
	}

	/**
	 * Retrieve the number of a Step.
	 * @return The number of this Step
	 */
	public final int getValue() {
		return _stepNum;
	}

	/**
	 * Set the browser state of a Step.
	 * @param newState The new state of the Browser
	 */
	public final void setState(BrowserState newState) {
		this._currentBrowserState = newState;
	}

	public void addProxyLoadAmount(int loadAmount) {
		this.proxyLoadAmount += loadAmount;
	}

}
