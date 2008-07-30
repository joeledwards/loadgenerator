package com.awebstorm.robot;

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

import com.awebstorm.Proxy;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
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
 * Holds the script step information and operations that can be executed by a Robot.
 * Details of a step are stored in an Attributes List
 * @author Cromano
 */
public class Step implements Comparable<Step> {
	
	/**
	 * The standard HTML steps to be supported.
	 * @author Cromano
	 *
	 */
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
	private Robot stepRobotOwner;
	private String stepName;
	private int stepNumber;
	private Attributes _stepAttributeList;
	private Logger consoleLog = Logger.getLogger(this.getClass());
	private Logger resultLog = Logger.getLogger("com.awebstorm.robot.Step.resultLog");
	private long replyTime = 0;
	private long BodyByteAmount = 0; 
	private String targetDomain;
	private Proxy myProxy;

	/**
	 * Creates a new Step.
	 * @param name Name of the Step
	 * @param value Step Number
	 * @param list Attributes of the step
	 * @param myRobotOwner Robot that owns this step
	 */
	public Step(final String name, final int value, final Attributes list, final Robot robotOwner) {
		stepName = name;
		stepNumber = value;
		_stepAttributeList = list;
		stepRobotOwner = robotOwner;
		myProxy = stepRobotOwner.getCurrentProxy();
	}
	
	/**
	 * Name of the Step.
	 * @return Name of this step
	 */
	public String getName() {
		return stepName;
	}
	
	/**
	 * Attributes of the Step.
	 * @return
	 */
	public Attributes getList() {
		return _stepAttributeList;
	}

	/**
	 * Compare two steps by value.
	 * @param o Step to compare this step to
	 * @return Will return negative, 1, or positive if o is greater, equal, or less than this Step
	 */
	public int compareTo(final Step o) {
		return stepNumber - o.getValue();
	}

	/**
	 * Execute a Step.
	 * @param jobID Robot job ID
	 * @param browserState currentState of the robot's browser
	 */
	public void execute(final String jobID, BrowserState browserState) {
		myProxy.resetProxyCounters();
		replyTime = 0;
		BodyByteAmount = 0; 
		_currentBrowserState = browserState;
		//HTMLRobot handles http protocols
		targetDomain = "http://" + stepRobotOwner.getTargetDomain();
		if (!stepRobotOwner.getTargetDomain().equals(myProxy.getRemotehost())) {
			myProxy.setRemotehost(stepRobotOwner.getTargetDomain());
		}
		if (!(stepRobotOwner.getTargetPort() == (myProxy.getRemoteport()))) {
			myProxy.setRemoteport(stepRobotOwner.getTargetPort());
		}
		ActionTypes currentType = null;
		boolean stepReturnStatus = true;
		try {
			currentType = ActionTypes.valueOf(stepName);
		} catch (IllegalArgumentException e) {
			consoleLog.error("Unknown Step type found.",e);
			return;
		}
		if (currentType == null) {
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
	 * Click a link on a webpage. CLICK_LINK STEP
	 * Not Yet Implemented
	 * @return The step status
	 */
	private boolean clickLink() {
		return false;
	}

	/**
	 * Standard POST STEP using the parameters stored in the postList.
	 * @return Success?
	 */
	private boolean post() {
		
		List<NameValuePair> postList = new LinkedList<NameValuePair>();
		for (int i = 1; i < _stepAttributeList.getLength(); i++) {
			postList.add(new NameValuePair(_stepAttributeList.getLocalName(i), _stepAttributeList.getValue(i)));
		}
		WebRequestSettings newSettings = null;
		HtmlPage postPage = null;
		String currentPath = targetDomain + _stepAttributeList.getValue(0);
		try {
			newSettings = new WebRequestSettings(new URL(currentPath), SubmitMethod.POST);
		} catch (MalformedURLException e1) {
			consoleLog.error("Bad URL passed to a POST operation.", e1);
			return false;
		}
		newSettings.setRequestParameters(postList);
		Page tempResponsePage;
		try {
			tempResponsePage = _currentBrowserState.getVUser().getPage(newSettings);
		} catch (FailingHttpStatusCodeException e) {
			consoleLog.error("POST operation, " + stepName + " has a bad status message.", e);
			return false;
		} catch (IOException e) {
			consoleLog.error("IOException thrown during a POST operation.", e);
			return false;
		}
		
		if (tempResponsePage.getClass() == HtmlPage.class) {
			postPage = (HtmlPage) tempResponsePage;
			_currentBrowserState.setCurrentPage(postPage);
			postList.clear();
			replyTime = postPage.getWebResponse().getLoadTimeInMilliSeconds();
			try {
				BodyByteAmount = postPage.getWebResponse().getContentAsStream().available();
			} catch (IOException e1) {
				consoleLog.error("IOException while reading content as from post stream to count BodyByteAmount.", e1);
			}
			return collectResources(postPage);
		} else {
			replyTime = tempResponsePage.getWebResponse().getLoadTimeInMilliSeconds();
			try {
				BodyByteAmount = tempResponsePage.getWebResponse().getContentAsStream().available();
			} catch (IOException e1) {
				consoleLog.error("IOException while reading content as from post stream to count BodyByteAmount.", e1);
			}
			if (tempResponsePage.getWebResponse().getStatusCode() < 200
					|| tempResponsePage.getWebResponse().getStatusCode() > 399) {
				return false;
			}
			return true;
		}

	}
	
	/**
	 * Fill a Form with information. Usually followed by a BUTTON or CLICK.
	 * Not Yet Implemented.
	 * @return Step status
	 */
	private boolean fillForm() {
		boolean tempStatus = true;
		return tempStatus;
	}

	/**
	 * Press the button whose name is given.
	 * @return Step status
	 */
	private boolean button() {
		HtmlButton tempButton = null;
		if (_currentBrowserState.getCurrentPage() == null) {
			return false;
		}
		List<HtmlForm> tempForms = _currentBrowserState.getCurrentPage().getForms();
		
		if (tempForms == null) {
			consoleLog.debug("No Forms to search for buttons.");
			return false;
		}
		for (HtmlForm i : tempForms) {
			try {
				tempButton = i.getButtonByName(_stepAttributeList.getValue(0));
			} catch (ElementNotFoundException e) {
				consoleLog.debug("No such Button exists");
				return false;
			}
		}
		if (tempButton == null) {
			consoleLog.debug("No Button to click.");
			return false;
		}
		try {
			tempButton.click();
		} catch (IOException e) {
			consoleLog.debug("Bad Button Clicked.");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Standard GET STEP.
	 * @return Step status
	 */
	private boolean invoke() {
		String currentPath = _stepAttributeList.getValue(0);
		currentPath = targetDomain + currentPath;
		HtmlPage invokePage = null;
		Page tempPage;
		
		try {
			tempPage = _currentBrowserState.getVUser().getPage(currentPath);
		} catch (FailingHttpStatusCodeException e) {
			consoleLog.error("Bad Status Code.", e);
			return false;
		} catch (MalformedURLException e) {
			consoleLog.error("MalformedURL", e);
			return false;
		} catch (SocketTimeoutException e) {
			consoleLog.debug("Socket Timed Out from licit/illicit factors.");
			return false;
		} catch (IOException e) {
			consoleLog.error("IO Error during Invoke.", e);
			return false;
		}
		
		if (tempPage.getClass() == HtmlPage.class) {
			invokePage = (HtmlPage) tempPage;
			_currentBrowserState.setCurrentPage(invokePage);
			replyTime = invokePage.getWebResponse().getLoadTimeInMilliSeconds();
			try {
				BodyByteAmount = invokePage.getWebResponse().getContentAsStream().available();
			} catch (IOException e1) {
				consoleLog.error("IOException while reading content as from post stream to count BodyByteAmount.", e1);
			}
			return collectResources(invokePage);
		} else {
			replyTime = tempPage.getWebResponse().getLoadTimeInMilliSeconds();
			try {
				BodyByteAmount = tempPage.getWebResponse().getContentAsStream().available();
			} catch (IOException e1) {
				consoleLog.error("IOException while reading content as from post stream to count BodyByteAmount.", e1);
			}
			if (tempPage.getWebResponse().getStatusCode() < 200
					|| tempPage.getWebResponse().getStatusCode() > 399) {
				return false;
			}
			return true;
		}
	}
	
	/**
	 * Collects local resources from the page passed 
	 * i.e. images, style sheets, javascript.
	 * @param invokePage Page to collect necessary resources for
	 * @return True if no resource retrieval failed, else false
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
							tempAttr = targetDomain + tempAttr;
						} else if (tempAttr.startsWith("https") 
								|| tempAttr.startsWith("ftp") 
								|| tempAttr.startsWith("file") 
								|| tempAttr.startsWith("jar")) {
							consoleLog.debug("Bad protocol encountered.");
						} else {
							tempAttr = targetDomain + '/' + tempAttr;
						}
						//In case of domain seperate from the domain for the script
					} else if (!tempAttr.startsWith(targetDomain)) {
						consoleLog.debug("Bad domain encountered: " + tempAttr);
						_currentBrowserState.addUrlToHistory(tempAttr);
					}
					if (_currentBrowserState.addUrlToHistory(tempAttr)) {
						temporary = _currentBrowserState.getVUser().getPage(tempAttr).getWebResponse();
						if (consoleLog.isDebugEnabled()) {
							resourcesCollector.append("Resources obtained: " 
								+ tempAttr + '\n');
						}
						replyTime += temporary.getLoadTimeInMilliSeconds();
						BodyByteAmount += temporary.getResponseBody().length;
						if (temporary.getStatusCode() != 200) {
							tempStatus = false;
						}
					}
				} else if (temp.getClass() == HtmlStyle.class) {
					tempAttrs = ((HtmlStyle) temp).getAttributes();
					tempAttr = ((HtmlStyle) temp).getTextContent();
					LinkedList<String> styleResourceList = 
						StyleParser.grabStyleElementText(tempAttr);
					String aResource;
					while (!styleResourceList.isEmpty()){
						aResource = styleResourceList.poll();
						if (!aResource.startsWith("http")) {
							if (aResource.charAt(0) == '/') {
								aResource = targetDomain 
								+ aResource;
							} else {
								aResource = targetDomain 
								+ '/' + aResource;
							}
						} else if (!aResource.startsWith(targetDomain)) {
							consoleLog.debug("Bad domain encountered: " + aResource);
							_currentBrowserState.addUrlToHistory(aResource);
						}
						if (_currentBrowserState.addUrlToHistory(aResource)) {
							temporary = _currentBrowserState.getVUser().getPage(aResource).getWebResponse();
							if (consoleLog.isDebugEnabled()) {
								resourcesCollector.append("Import resources obtained: "
									+ aResource + '\n');
							}
							replyTime += temporary.getLoadTimeInMilliSeconds();
							BodyByteAmount += temporary.getResponseBody().length;
							if (temporary.getStatusCode() != 200 ) {
								tempStatus = false;
							}
							if (aResource.endsWith(".css")) {
								LinkedList<String> tempHolder = 
									StyleParser.grabStyleSheetText(temporary.getContentAsString());
								String path = 
									StyleParser.subDirBuilder(temporary.getUrl());
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
									aResource = targetDomain + aResource;
								} else {
									aResource = targetDomain + '/' + aResource;
								}
							} else if (!aResource.startsWith(targetDomain)) {
								consoleLog.debug("Bad domain encountered: " + aResource);
								_currentBrowserState.addUrlToHistory(aResource);
							}
							if (_currentBrowserState.addUrlToHistory(aResource)) {
								temporary = _currentBrowserState.getVUser().getPage(aResource).getWebResponse();
								if (consoleLog.isDebugEnabled()) {
									resourcesCollector.append("Link Resources obtained: " + aResource + '\n');
								}
								replyTime += temporary.getLoadTimeInMilliSeconds();
								BodyByteAmount += temporary.getResponseBody().length;
								if (temporary.getStatusCode() != 200) {
									tempStatus = false;
								}
								LinkedList<String> styleResourceList = StyleParser.grabStyleSheetText(temporary.getContentAsString());
								while (!styleResourceList.isEmpty()) {
									aResource = StyleParser.subDirBuilder(temporary.getUrl()) + styleResourceList.poll();
									if (!aResource.startsWith("http")) {
										if (aResource.charAt(0) == '/') {
											aResource = targetDomain + aResource;
										} else {
											aResource = targetDomain + '/' + aResource;
										}
									}
									if (_currentBrowserState.addUrlToHistory(aResource)) {
										temporary = _currentBrowserState.getVUser().getPage(aResource).getWebResponse();
										if (consoleLog.isDebugEnabled()) {
											resourcesCollector.append("Style Sheet Pic Resources obtained: " + aResource + '\n');
										}
										replyTime += temporary.getLoadTimeInMilliSeconds();
										BodyByteAmount += temporary.getResponseBody().length;
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
		if (_currentBrowserState.getCurrentPage() != null) {
			if (consoleLog.isDebugEnabled()) {
				consoleLog.debug(_currentBrowserState.getCurrentPage().getTitleText());
			}
			return _currentBrowserState.getCurrentPage().getTitleText().equals(_stepAttributeList.getValue(0));
		}
		return false;
	}

	/**
	 * Instruct this thread to wait for a given period of time as this is a WAIT STEP.
	 */
	private void waitStep() {
		if (_stepAttributeList.getValue(0).equals("")) {
			try {
				Thread.sleep(Robot.getDefaultWaitStep());
			} catch (InterruptedException e) {
				consoleLog.error("Interrupted Exception during a default WAIT step", e);
			}
		} else {
			try {
				Thread.sleep(Integer.parseInt(_stepAttributeList.getValue(0)));
			} catch (NumberFormatException e) {
				consoleLog.error("Could not determine the wait length during a WAIT Step", e);
				try {
					Thread.sleep(Robot.getDefaultWaitStep());
				} catch (InterruptedException e1) {
					consoleLog.error("Interrupted Exception during a WAIT step", e);
				}
				e.printStackTrace();
			} catch (InterruptedException e) {
				consoleLog.error("Interrupted Exception during a WAIT step", e);
			}
		}
	}
	
	/**
	 * Report the results of a Step.
	 * @param stepStatus Success if true, Failure if false
	 * @param jobID Current job ID
	 */
	private synchronized void report(final boolean stepStatus, final String jobID) {

		long proxyReceiveAmount = myProxy.getProxyReceiveAmount();
		long proxySentAmount = myProxy.getProxySentAmount();
		long stepProxyTimeStarted = myProxy.getProxyTimeStarted();
		long stepProxyTimeResponded = myProxy.getProxyTimeResponded();
		long stepProxyTimeEnded = myProxy.getProxyTimeEnded();

		StringBuffer tempResult = new StringBuffer();
		ActionTypes currentType;
		try {
			currentType = ActionTypes.valueOf(stepName);
		} catch (IllegalArgumentException e) {
			consoleLog.error("Unknown Step type found.",e);
			return;
		}
		if (currentType == null) {
			return;
		}
		switch (currentType) {
		case INVOKE:
		case POST:
		case BUTTON:
		case CLICK_LINK:
			if (resultLog.isDebugEnabled()) {
				tempResult.append("jobID: " +jobID);
				tempResult.append('\n');
				tempResult.append("stepName: " + stepName);
				tempResult.append('\n');
				tempResult.append("stepNumber: " + stepNumber);
				tempResult.append('\n');
				tempResult.append("current Time: " + formatTime(System.currentTimeMillis()));
				tempResult.append('\n');
				tempResult.append("replyTime: " + replyTime);
				tempResult.append('\n');
				tempResult.append("stepProxyTimeStarted: " + formatTime(stepProxyTimeStarted));
				tempResult.append('\n');
				tempResult.append("stepProxyTimeEnded: " + formatTime(stepProxyTimeEnded));
				tempResult.append('\n');
				tempResult.append("stepProxyTimeResponded: " + formatTime(stepProxyTimeResponded));
				tempResult.append('\n');
				tempResult.append("stepProxyTimeResponded-stepProxyTimeStarted: " + (stepProxyTimeResponded-stepProxyTimeStarted));
				tempResult.append('\n');
				tempResult.append("BodyByteAmount: " + BodyByteAmount);
				tempResult.append('\n');
				tempResult.append("proxyReceiveAmount: " + proxyReceiveAmount);
				tempResult.append('\n');
				tempResult.append("proxySentAmount: " + proxySentAmount);
				tempResult.append('\n');
				if (replyTime == 0) {
					replyTime = 1;
				}
				tempResult.append((double)(proxyReceiveAmount + proxySentAmount) / ((double)replyTime / 1000));
				tempResult.append('\n');
				if (stepStatus) {
					tempResult.append("success");
				} else {
					tempResult.append("failure");
				}
				resultLog.debug(tempResult);
			}
			tempResult = new StringBuffer();
			tempResult.append(jobID);
			tempResult.append(',');
			tempResult.append(stepName);
			tempResult.append(',');
			tempResult.append(stepNumber);
			tempResult.append(',');
			tempResult.append(System.currentTimeMillis());
			tempResult.append(',');
			tempResult.append(replyTime);
			tempResult.append(',');
			tempResult.append(stepProxyTimeStarted);
			tempResult.append(',');
			tempResult.append(stepProxyTimeEnded);
			tempResult.append(',');
			tempResult.append(stepProxyTimeResponded);
			tempResult.append(',');
			tempResult.append((stepProxyTimeEnded-replyTime));
			tempResult.append(',');
			tempResult.append(BodyByteAmount);
			tempResult.append(',');
			tempResult.append(proxyReceiveAmount);
			tempResult.append(',');
			tempResult.append(proxySentAmount);
			tempResult.append(',');
			if (replyTime == 0) {
				replyTime = 1;
			}
			tempResult.append((double)(proxyReceiveAmount + proxySentAmount) / ((double)replyTime / 1000));
			tempResult.append(',');
			if (stepStatus) {
				tempResult.append("success");
			} else {
				tempResult.append("failure");
			}
			resultLog.info(tempResult);
			
			break;
		case WAIT:
		case FILL_FORM:
		case VERIFY_TITLE:
			tempResult.append(jobID);
			tempResult.append(',');
			tempResult.append(stepName);
			tempResult.append(',');
			tempResult.append(stepNumber);
			tempResult.append(',');
			tempResult.append(System.currentTimeMillis());
			tempResult.append(',');
			if (stepStatus) {
				tempResult.append("success");
			} else {
				tempResult.append("failure");
			}
			resultLog.info(tempResult);
			break;
			default:
				consoleLog.error("A unknown Step was found during reporting.");
			break;
			
		}
		
	}

	/**
	 * Retrieve the number of a Step.
	 * @return The number of this Step
	 */
	public final int getValue() {
		return stepNumber;
	}

	/**
	 * Set the browser state of a Step.
	 * @param newState The new state of the Browser
	 */
	public final void setState(BrowserState newState) {
		this._currentBrowserState = newState;
	}
	
	/**
	 * Result Debugging helper method.
	 * @param milliseconds Time to convert in milliseconds
	 * @return Well-formatted time message
	 */
	private String formatTime(final long milliseconds) {
		long millisInDay = milliseconds;
		long millisecond = millisInDay % 1000;
		millisInDay /= 1000;
		long second = millisInDay % 60;
		millisInDay /= 60;
		long minute = millisInDay % 60;
		millisInDay /= 60;
		long hour = millisInDay % 60;
		StringBuffer buf = new StringBuffer(12);
		if (hour < 10) {
			buf.append("0").append(hour);
		} else {
			buf.append(hour);
		}
		buf.append(":");
		if (minute < 10) {
			buf.append("0").append(minute);
		} else {
			buf.append(minute);
		}
		buf.append(":");
		if (second < 10) {
			buf.append("0").append(second);
		} else {
			buf.append(second);
		}
		buf.append(":");
		if (millisecond < 10) {
			buf.append("00").append(millisecond);
		} else if (millisecond < 100) {
			buf.append("0").append(millisecond);
		} else {
			buf.append(millisecond);
		}
		return buf.toString();
	}

}
