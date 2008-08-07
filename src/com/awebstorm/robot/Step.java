package com.awebstorm.robot;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.NoHttpResponseException;
import org.apache.log4j.Logger;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.Attributes;

import com.awebstorm.Proxy;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.ClickableElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlLink;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlStyle;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;

/**
 * Holds the script step information and operations that can be executed by a Robot.
 * Details of a step are stored in an Attributes List.
 * To add a new ActionType, simply type the new action enum into the ActionType enum class. Secondly, place a
 * case statement in the .execute() to tell this step how to act if it is the new action. Thirdly, provide
 * implementation for the method call that should run the action. Finally, place a case statement
 * in the report method to tell the step how to report your new action type.
 * (Additionally, if the action report log output needs to be checked, then the LogDataExtractor in
 * combination with the HtmlRobotBehaviour may be useful.
 * 
 * @author Cromano
 */
public class Step implements Comparable<Step> {
	
	/**
	 * The standard HTML steps to be supported.
	 * @author Cromano
	 *
	 */
	public static enum ActionType {
		steps,
		open,
		verifyTitle,
		verifyChecked,
		verifyNotChecked,
		verifySelected,
		verifyNotSelected,
		verifyTextPresent,
		verifyText,
		verifyCookiePresent,
		verifyLocation,
		pause,
		click,
		check,
		uncheck,
		select,
		addSelection,
		removeSelection,
		submit,
		refresh,
		type,
		doubleClick,
		openWindow,
		setSpeed
	}
	
	private BrowserState currentBrowserState;
	private Robot stepRobotOwner;
	private String stepName;
	private int stepNumber;
	private Attributes stepAttributeList;
	private Logger consoleLog = Logger.getLogger(this.getClass());
	private Logger resultLog = Logger.getLogger("com.awebstorm.robot.Step.resultLog");
	private long replyTime = 0;
	private long bodyByteAmount = 0; 
	private String targetDomain;
	private Proxy myProxy;
	private String resultMessage = null;

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
		stepAttributeList = list;
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
		return stepAttributeList;
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
		bodyByteAmount = 0; 
		currentBrowserState = browserState;
		resultMessage = null;
		//HtmlRobot handles http protocols
		targetDomain = "http://" + stepRobotOwner.getTargetDomain();
		if (!stepRobotOwner.getTargetDomain().equals(myProxy.getRemotehost())) {
			myProxy.setRemotehost(stepRobotOwner.getTargetDomain());
		}
		if (!(stepRobotOwner.getTargetPort() == (myProxy.getRemoteport()))) {
			myProxy.setRemoteport(stepRobotOwner.getTargetPort());
		}
		ActionType currentType = null;
		boolean stepReturnStatus = true;
		try {
			currentType = ActionType.valueOf(stepName);
		} catch (IllegalArgumentException e) {
				consoleLog.error("Unknown Step type found.",e);
			return;
		}
		if (currentType == null) {
			return;
		}
		
		switch (currentType) {
		case steps:
			return;
		case pause:
			this.pause();
			break;
		case open:
			stepReturnStatus = this.open();
			break;
		case verifyTitle:
			stepReturnStatus = this.verifyTitle();
			break;
		case verifyChecked:
			stepReturnStatus = this.verifyChecked();
			break;
		case verifyNotChecked:
			stepReturnStatus = this.verifyNotChecked();
			break;
		case verifySelected:
			stepReturnStatus = this.verifySelected();
			break;
		case verifyNotSelected:
			stepReturnStatus = this.verifyNotSelected();
			break;
		case verifyTextPresent:
			stepReturnStatus = this.verifyTextPresent();
			break;
		case verifyText:
			stepReturnStatus = this.verifyText();
			break;
		case verifyCookiePresent:
			stepReturnStatus = this.verifyCookiePresent();
			break;
		case verifyLocation:
			stepReturnStatus = this.verifyLocation();
			break;
		case type:
			stepReturnStatus = this.type();
			break;
		case submit:
			stepReturnStatus = this.submit();
			break;
		case click:
			stepReturnStatus = this.click();
			break;
		case check:
			stepReturnStatus = this.check();
			break;
		case uncheck:
			stepReturnStatus = this.uncheck();
			break;
		case select:
			stepReturnStatus = this.select();
			break;
		case addSelection:
			stepReturnStatus = this.addSelection();
			break;
		case removeSelection:
			stepReturnStatus = this.removeSelection();
			break;
		case refresh:
			stepReturnStatus = this.refresh();
			break;
		default:
			consoleLog.warn("Unknown Step type found.");
			stepReturnStatus = false;
			break;
		}
		
		report(stepReturnStatus, jobID);
	}

	/**
	 * Verify that the URL stored in the attributes list is 
	 * EXACTLY the same as the currently focused page's URL.toExternalForm().
	 * @return Step return status
	 */
	private boolean verifyLocation() {
		String url = stepAttributeList.getValue(0);
		if (url == null || url.equals("")) {
			resultMessage = "URL checker was passed null or an empty string";
			return false;	
		}
		if (url.equals(currentBrowserState.getCurrentPage().getWebResponse().getUrl().toExternalForm())) {
			return true;
		} else {
			resultMessage = "Location indicated is not " + currentBrowserState.getCurrentPage().getWebResponse().getUrl().toExternalForm();
			return false;
		}
	}
	
	/**
	 * Checks the current page response headers for the cookie headers and then extracts the cookie to check against.
	 * Basic implementation does not check attributes, only checks if the cookie name exists.
	 * @return
	 */
	private boolean verifyCookiePresent() {
		String cookieName = stepAttributeList.getValue(0);
		if (cookieName == null) {
			resultMessage = "Cookie name was empty";
			return false;
		} else if (cookieName.equals("")) {
			resultMessage = "Cookie name was empty string";
			return false;
		}
		List<NameValuePair> headers = currentBrowserState.getCurrentPage().getWebResponse().getResponseHeaders();
		if (headers == null) {
			resultMessage = "No headers";
			return false;
		}
		ListIterator<NameValuePair> iterator = headers.listIterator();
		while (iterator.hasNext()) {
			NameValuePair next = iterator.next();
			if ("Set-Cookie".equals(next.getName())) {
				//New cookie
				String nextValue = next.getValue();
				int stop = nextValue.indexOf('=');
				if (stop != -1 && nextValue.substring(0, stop).trim().equals(cookieName)) {
					return true;
				}
			}
		}
		resultMessage = "Cookie was not found";
		return false;
	}

	/**
	 * Verifies the text pattern against the visible text returned by HtmlElement.asText().
	 * @return Step return status
	 */
	private boolean verifyText() {
		HtmlElement targetElement = null;
		try {
			targetElement = locator();
		} catch (ElementNotFoundException e) {
			resultMessage = "Could not locate the element";
			return false;
		}
		if (targetElement == null) {
			resultMessage = "Could not locate the element";
			return false;
		}
		String pattern = stepAttributeList.getValue(1);
		if (pattern == null) {
			resultMessage = "Pattern was null";
			return false;
		}
		if (pattern.equals(targetElement.asText())) {
			return true;
		} else {
			resultMessage = "Pattern did not match the text content";
			return false;
		}
	}

	/**
	 * Checks the entire page to see if the given text pattern is present.
	 * @return Step return status
	 */
	private boolean verifyTextPresent() {
		String pattern = stepAttributeList.getValue(0);
		if (pattern == null || pattern.equals("")) {
			resultMessage = "Pattern is null or empty string";
			return false;
		}
		if (currentBrowserState.getCurrentPage().asXml().contains(pattern)) {
			return true;
		}
		resultMessage = "Pattern was not found";
		return false;
	}

	/**
	 * Check if the element at the locator is a select and if so, check if the indicated option is not selected.
	 * @return Step Return status
	 */
	private boolean verifySelected() {
		HtmlElement targetElement = null;
		try {
			targetElement = locator();
		} catch (ElementNotFoundException e) {
			resultMessage = "Could not locate the select element";
			return false;
		}
		if (targetElement == null) {
			resultMessage = "Could not locate the select element";
			return false;
		}
		if (HtmlSelect.class.isAssignableFrom(targetElement.getClass())) {
			HtmlSelect tempSelect = ((HtmlSelect)targetElement);
			HtmlOption tempOption = optionLocator(tempSelect);
			if (tempOption == null) {
				if (tempSelect.getOptionSize() > 0) {
					tempOption = tempSelect.getOption(0);
				} else {
					resultMessage = "No options to use";
					return false;
				}
			}
			if (tempOption.isSelected()) {
				return true;
			} else {
				resultMessage = "Option was not selected";
				return false;
			}
		} else {
			resultMessage = "Element is not a select Element";
			return false;
		}
	}
	
	/**
	 * Check if the element at the locator is a select and if so, check if the indicated option is selected.
	 * @return Step Return status
	 */
	private boolean verifyNotSelected() {
		HtmlElement targetElement = null;
		try {
			targetElement = locator();
		} catch (ElementNotFoundException e) {
			resultMessage = "Could not locate the select element";
			return false;
		}
		if (targetElement == null) {
			resultMessage = "Could not locate the select element";
			return false;
		}
		if (HtmlSelect.class.isAssignableFrom(targetElement.getClass())) {
			HtmlSelect tempSelect = ((HtmlSelect)targetElement);
			HtmlOption tempOption = optionLocator(tempSelect);
			if (tempOption == null) {
				if (tempSelect.getOptionSize() > 0) {
					tempOption = tempSelect.getOption(0);
				} else {
					resultMessage = "No options to use";
					return false;
				}
			}
			if (!tempOption.isSelected()) {
				return true;
			} else {
				resultMessage = "Option was selected";
				return false;
			}
		} else {
			resultMessage = "Element is not a select Element";
			return false;
		}
	}
	
	/**
	 * Check if the element at the locator is a checkbox and if so, check if it is not checked.
	 * @return Step Return status
	 */
	private boolean verifyNotChecked() {
		HtmlElement targetElement = null;
		try {
			targetElement = locator();
		} catch (ElementNotFoundException e) {
			resultMessage = "Could not locate the checkbox";
			return false;
		}
		if (targetElement == null) {
			resultMessage = "Could not locate the checkbox";
			return false;
		}
		if (HtmlCheckBoxInput.class.isAssignableFrom(targetElement.getClass())) {
			if (!((HtmlCheckBoxInput)targetElement).isChecked()) {
				return true;
			} else {
				resultMessage = "Element was checked";
				return false;
			}
		} else {
			resultMessage = "Element is not a checkbox";
			return false;
		}
	}

	/**
	 * Check if the element at the locator is a checkbox and if so, check if it is checked.
	 * @return Step Return status
	 */
	private boolean verifyChecked() {
		HtmlElement targetElement = null;
		try {
			targetElement = locator();
		} catch (ElementNotFoundException e) {
			resultMessage = "Could not locate the checkbox";
			return false;
		}
		if (targetElement == null) {
			resultMessage = "Could not locate the checkbox";
			return false;
		}
		if (HtmlCheckBoxInput.class.isAssignableFrom(targetElement.getClass())) {
			if (((HtmlCheckBoxInput)targetElement).isChecked()) {
				return true;
			} else {
				resultMessage = "Element was not checked";
				return false;
			}
		} else {
			resultMessage = "Element is not a checkbox";
			return false;
		}
	}

	/**
	 * Types some text in a text box indicated by the locator stored in the first entry of the stepAttributeList
	 * @return Step return status
	 */
	private boolean type() {
		HtmlElement targetElement = null;
		try {
			targetElement = locator();
		} catch (ElementNotFoundException e) {
			resultMessage = "Could not locate the text area element";
			return false;
		}
		if (targetElement == null) {
			resultMessage = "Could not locate the text area element";
			return false;
		}
		if (HtmlTextArea.class.isAssignableFrom(targetElement.getClass())) {
			String value = stepAttributeList.getValue(1);
			if (value == null) {
				resultMessage = "No text value specififed";
				return false;
			}
			((HtmlTextArea)targetElement).setText(value);
		} else {
			resultMessage = "Element is not a text area";
			return false;
		}
		return true;
	}

	/**
	 * Refreshes the current page.
	 * @return Step return status
	 */
	private boolean refresh() {
		currentBrowserState.getCurrentPage().getWebResponse().getUrl();
		
		HtmlPage invokePage = null;
		Page tempPage;

		try {
			tempPage = currentBrowserState.getVUser().getPage(currentBrowserState.getCurrentPage().getWebResponse().getUrl());
		} catch (FailingHttpStatusCodeException e) {
			//Should be impossible due to settings on the WebClient
			consoleLog.error("Bad Status Code.", e);
			return false;
		} catch (SocketTimeoutException e) {
			//Only possible if there is a problem with the proxy
			if (resultMessage == null)
				resultMessage = "Socket Timeout";
				if (consoleLog.isDebugEnabled())
					consoleLog.debug("Socket Timed Out from licit/illicit factors.");
				if (myProxy.getThreadState().compareTo(Thread.State.BLOCKED) == 0) {
					consoleLog.error("Server is unresponsive for" + currentBrowserState.getCurrentPage().getWebResponse().getUrl());
					if (resultMessage == null)
						resultMessage = "Server is unresponsive for " + currentBrowserState.getCurrentPage().getWebResponse().getUrl();
				}
			stepRobotOwner.setEnd(true);
			return false;
		} catch (ConnectException e) {
			//Only possible if there is a problem with the proxy
			if (resultMessage == null)
				resultMessage = "Server could not be contacted.";
			if (consoleLog.isDebugEnabled())
				consoleLog.debug("Server could not be contacted. Exiting step sequence.", e);
			stepRobotOwner.setEnd(true);
			return false;
		} catch (NoHttpResponseException e) {
			//Should be the normal catch response if there is a problem with the target
			//The proxy will close the socket and will pass the error reason back to the Robot
			if (resultMessage == null)
				resultMessage = myProxy.getProxyMessage();
			return false;
		} catch (IOException e) {
			//Only possible if there is a problem with the proxy
			if (resultMessage == null)
				resultMessage = "IO Error";
			consoleLog.error("IO Error during Invoke.", e);
			stepRobotOwner.setEnd(true);
			return false;
		}
		if (tempPage == null) {
			if (consoleLog.isDebugEnabled()) {
				consoleLog.debug("Opened Page is null.");
			}
			resultMessage = "Refreshed to a null page";
			return false;
		} else if (tempPage.getClass() == HtmlPage.class) {
			invokePage = (HtmlPage) tempPage;
			currentBrowserState.setCurrentPage(invokePage);
			replyTime = invokePage.getWebResponse().getLoadTimeInMilliSeconds();
			try {
				bodyByteAmount = invokePage.getWebResponse().getContentAsStream().available();
			} catch (IOException e1) {
				consoleLog.error("IOException while reading content as from post stream to count bodyByteAmount.", e1);
			}
			return collectResources(invokePage);
		} else {
			boolean tempStatus = true;
			replyTime = tempPage.getWebResponse().getLoadTimeInMilliSeconds();
			try {
				bodyByteAmount = tempPage.getWebResponse().getContentAsStream().available();
			} catch (IOException e1) {
				consoleLog.error("IOException while reading content as from post stream to count bodyByteAmount.", e1);
			}
			if (currentBrowserState.addUrlToCached("/favicon.ico")) {
				try {
					WebResponse temporary = currentBrowserState.getVUser().getPage(targetDomain + "/favicon.ico").getWebResponse();
					if (tempPage.getWebResponse().getStatusCode() < 200
							|| tempPage.getWebResponse().getStatusCode() > 399) {
					} else {
						replyTime += temporary.getLoadTimeInMilliSeconds();
						bodyByteAmount += temporary.getResponseBody().length;
					}
				} catch (SocketTimeoutException e) {
					if (resultMessage == null)
						resultMessage = "Socket Timeout";
					consoleLog.debug("Socket Timed Out from licit/illicit factors.");
				} catch (FailingHttpStatusCodeException e) {
					//Should be impossible
					consoleLog.error("Bad Status Code.", e);
				} catch (MalformedURLException e) {
					if (resultMessage == null)
						resultMessage = "Bad Script - Malformed URL for " + targetDomain + "/favicon.ico";
					consoleLog.error("MalformedURL", e);
					tempStatus = false;
				} catch (IOException e) {
					if (resultMessage == null)
						resultMessage = "IO Error retrieving favicon";
					consoleLog.error("IO Error during Invoke.", e);
					tempStatus = false;
				}
			}
			if (tempPage.getWebResponse().getStatusCode() < 200
					|| tempPage.getWebResponse().getStatusCode() > 399) {
				if (resultMessage == null)
					resultMessage = "Bad Status Code " + tempPage.getWebResponse().getStatusCode() + " for " + currentBrowserState.getCurrentPage().getWebResponse().getUrl();
				return false;
			}
			return tempStatus;
		}
	}

	/**
	 * Submit a form
	 * @return
	 */
	private boolean submit() {
		HtmlElement targetElement = null;
		try {
			targetElement = locator();
		} catch (ElementNotFoundException e) {
			resultMessage = "Could not locate the element to submit";
			return false;
		}
		if (targetElement == null) {
			resultMessage = "Could not locate the element to submit";
			return false;
		}
		if (!HtmlForm.class.isAssignableFrom(targetElement.getClass())) {
			resultMessage = "Element is not a form";
			return false;
		}
		HtmlForm tempForm = ((HtmlForm)targetElement);
		try {
			Page newPage = tempForm.submit(null);
			replyTime = newPage.getWebResponse().getLoadTimeInMilliSeconds();
			bodyByteAmount = newPage.getWebResponse().getContentAsStream().available();
			if (HtmlPage.class.isAssignableFrom(newPage.getClass())) {
				return collectResources((HtmlPage)newPage);
			} else {
				return !(newPage.getWebResponse().getStatusCode() < 200 || newPage.getWebResponse().getStatusCode() > 299);
			}
		} catch (IOException e) {
			resultMessage = "IOException during Submit";
			consoleLog.error("IOException during Submit");
		}
		return false;
	}
	
	/**
	 * Click on the Element indicated by the locator stored in the first element of the AttributeList.
	 * This step could qualify as an entirely new page load, so it is reported like an open
	 * @return Step Status
	 */
	private boolean click() {
		HtmlElement targetElement = null;
		try {
			targetElement = locator();
		} catch (ElementNotFoundException e) {
			resultMessage = "Could not locate the element to click";
			return false;
		}
		if (targetElement == null) {
			resultMessage = "Could not locate the element to click";
			return false;
		}
		if (!ClickableElement.class.isAssignableFrom(targetElement.getClass())) {
			resultMessage = "Element is not clickable";
			return false;
		}
		try {
			Page newPage = ((ClickableElement)targetElement).click();
			if (newPage == null) {
				resultMessage = "Returned a null page";
				return false;
			} else if (newPage.equals(currentBrowserState.getCurrentPage())) {
				return collectResources((HtmlPage)newPage);
			} else if (HtmlPage.class.isAssignableFrom(newPage.getClass())) {
				replyTime = newPage.getWebResponse().getLoadTimeInMilliSeconds();
				bodyByteAmount = newPage.getWebResponse().getContentAsStream().available();
				currentBrowserState.setCurrentPage((HtmlPage)newPage);
				return collectResources((HtmlPage)newPage);
			} else {
				replyTime = newPage.getWebResponse().getLoadTimeInMilliSeconds();
				bodyByteAmount = newPage.getWebResponse().getContentAsStream().available();
				return !(newPage.getWebResponse().getStatusCode() < 200 || newPage.getWebResponse().getStatusCode() > 299);
			}
		} catch (IOException e1) {
			consoleLog.error("IOException during a click.", e1);
			resultMessage = "IOException - Click operation failed";
		}
		return false;
	}
	
	/**
	 * Check the checkbox indicated by the locator stored in the first entry of the AttributeList.
	 * This does not act as clicking the check box, rather the box will remain clicked if this
	 * method is called.
	 * @return Step success status
	 */
	private boolean check() {
		HtmlElement targetElement = null;
		try {
			targetElement = locator();
		} catch (ElementNotFoundException e) {
			resultMessage = "Could not locate the element to check";
			return false;
		}
		if (targetElement == null) {
			resultMessage = "Could not locate the element to check";
			return false;
		}
		if (HtmlCheckBoxInput.class.isAssignableFrom(targetElement.getClass())) {
			try {
				Page newPage = ((HtmlCheckBoxInput)targetElement).setChecked(true);
				if (newPage == null) {
					resultMessage = "Returned a null page";
					return false;
				} else if (newPage.equals(currentBrowserState.getCurrentPage())) {
					return collectResources((HtmlPage)newPage);
				} else if (HtmlPage.class.isAssignableFrom(newPage.getClass())) {
					replyTime = newPage.getWebResponse().getLoadTimeInMilliSeconds();
					bodyByteAmount = newPage.getWebResponse().getContentAsStream().available();
					currentBrowserState.setCurrentPage((HtmlPage)newPage);
					return collectResources((HtmlPage)newPage);
				} else {
					replyTime = newPage.getWebResponse().getLoadTimeInMilliSeconds();
					bodyByteAmount = newPage.getWebResponse().getContentAsStream().available();
					return !(newPage.getWebResponse().getStatusCode() < 200 || newPage.getWebResponse().getStatusCode() > 299);
				}
			} catch (IOException e1) {
				consoleLog.error("IOException during a check.", e1);
				resultMessage = "IOException - Check operation failed";
			}
		} else {
			resultMessage = "Element is not a check box";
			return false;
		}
		return false;
	}
	
	/**
	 * Uncheck the checkbox indicated by the locator stored in the first entry of the stepAttributeList.
	 * @return Step success status
	 */
	private boolean uncheck() {
		HtmlElement targetElement = null;
		try {
			targetElement = locator();
		} catch (ElementNotFoundException e) {
			resultMessage = "Could not locate the element to uncheck";
			return false;
		}
		if (targetElement == null) {
			resultMessage = "Could not locate the element to uncheck";
			return false;
		}
		if (HtmlCheckBoxInput.class.isAssignableFrom(targetElement.getClass())) {
			try {
				Page newPage = ((HtmlCheckBoxInput)targetElement).setChecked(false);
				if (newPage == null) {
					resultMessage = "Returned a null page";
					return false;
				} else if (newPage.equals(currentBrowserState.getCurrentPage())) {
					return collectResources((HtmlPage)newPage);
				} else if (HtmlPage.class.isAssignableFrom(newPage.getClass())) {
					replyTime = newPage.getWebResponse().getLoadTimeInMilliSeconds();
					bodyByteAmount = newPage.getWebResponse().getContentAsStream().available();
					currentBrowserState.setCurrentPage((HtmlPage)newPage);
					return collectResources((HtmlPage)newPage);
				} else {
					replyTime = newPage.getWebResponse().getLoadTimeInMilliSeconds();
					bodyByteAmount = newPage.getWebResponse().getContentAsStream().available();
					return !(newPage.getWebResponse().getStatusCode() < 200 || newPage.getWebResponse().getStatusCode() > 299);
				}
			} catch (IOException e1) {
				consoleLog.error("IOException during an uncheck.", e1);
				resultMessage = "IOException - Uncheck operation failed";
			}
			return true;
		} else {
			resultMessage = "Element is not a check box";
			return false;
		}
	}
	
	/**
	 * Select the element indicated by the locator stored in the first entry of the stepAttributeList 
	 * and unselect all others.
	 * @return Step success status
	 */
	private boolean select() {
		HtmlElement targetElement = null;
		try {
			targetElement = locator();
		} catch (ElementNotFoundException e) {
			resultMessage = "Could not locate the select element";
			return false;
		}
		if (targetElement == null) {
			resultMessage = "Could not locate the select element";
			return false;
		}
		if (HtmlSelect.class.isAssignableFrom(targetElement.getClass())) {
			HtmlSelect tempSelect = ((HtmlSelect)targetElement);
			HtmlOption tempOption = optionLocator(tempSelect);
			if (tempOption == null) {
				if (tempSelect.getOptionSize() > 0) {
					tempOption = tempSelect.getOption(0);
				} else {
					resultMessage = "No options to use";
					return false;
				}
			}
			boolean tempStatus = true;
			try {
				Iterator<HtmlOption> selectedOptions = tempSelect.getSelectedOptions().iterator();
				while (selectedOptions.hasNext()) {
					HtmlOption nextSelectedOption = selectedOptions.next();
					if (!nextSelectedOption.equals(tempOption)) {
						Page newPage = tempOption.setSelected(false);
						if (newPage == null) {
							resultMessage = "Returned a null page";
							return false;
						} else if (newPage.equals(currentBrowserState.getCurrentPage())) {
							tempStatus = tempStatus && collectResources((HtmlPage)newPage);
						} else if (HtmlPage.class.isAssignableFrom(newPage.getClass())) {
							replyTime = newPage.getWebResponse().getLoadTimeInMilliSeconds();
							bodyByteAmount = newPage.getWebResponse().getContentAsStream().available();
							currentBrowserState.setCurrentPage((HtmlPage)newPage);
							tempStatus = tempStatus && collectResources((HtmlPage)newPage);
						} else {
							replyTime = newPage.getWebResponse().getLoadTimeInMilliSeconds();
							bodyByteAmount = newPage.getWebResponse().getContentAsStream().available();
							tempStatus = tempStatus && !(newPage.getWebResponse().getStatusCode() < 200 || newPage.getWebResponse().getStatusCode() > 299);
						}
					}
				}
				Page newPage = tempOption.setSelected(true);
				if (newPage == null) {
					resultMessage = "Returned a null page";
					return false;
				} else if (newPage.equals(currentBrowserState.getCurrentPage())) {
					tempStatus = tempStatus && collectResources((HtmlPage)newPage);
				} else if (HtmlPage.class.isAssignableFrom(newPage.getClass())) {
					replyTime = newPage.getWebResponse().getLoadTimeInMilliSeconds();
					bodyByteAmount = newPage.getWebResponse().getContentAsStream().available();
					currentBrowserState.setCurrentPage((HtmlPage)newPage);
					tempStatus = tempStatus && collectResources((HtmlPage)newPage);
				} else {
					replyTime = newPage.getWebResponse().getLoadTimeInMilliSeconds();
					bodyByteAmount = newPage.getWebResponse().getContentAsStream().available();
					tempStatus = tempStatus && !(newPage.getWebResponse().getStatusCode() < 200 || newPage.getWebResponse().getStatusCode() > 299);
				}
			} catch (IOException e1) {
				consoleLog.error("IOException during a select.", e1);
				resultMessage = "IOException - Select operation failed";
			}
			return tempStatus;
		} else {
			resultMessage = "Element is not a select Element";
			return false;
		}
	}
	
	/**
	 * Select the element in the select element indicated by the locator stored 
	 * in the first entry of the stepAttributeList
	 * @return Step success status
	 */
	private boolean addSelection() {
		HtmlElement targetElement = null;
		try {
			targetElement = locator();
		} catch (ElementNotFoundException e) {
			resultMessage = "Could not locate the select element";
			return false;
		}
		if (targetElement == null) {
			resultMessage = "Could not locate the select element";
			return false;
		}
		if (HtmlSelect.class.isAssignableFrom(targetElement.getClass())) {
			HtmlSelect tempSelect = ((HtmlSelect)targetElement);
			if (!tempSelect.isMultipleSelectEnabled()) {
				resultMessage = "Cannot addSelection to a nonmulti-select element";
				return false;
			}
			HtmlOption tempOption = optionLocator(tempSelect);
			if (tempOption == null) {
				if (tempSelect.getOptionSize() > 0) {
					tempOption = tempSelect.getOption(0);
				} else {
					resultMessage = "No options to use";
					return false;
				}
			}
			try {
				Page newPage = tempOption.setSelected(true);
				if (newPage == null) {
					resultMessage = "Returned a null page";
					return false;
				} else if (newPage.equals(currentBrowserState.getCurrentPage())) {
					return collectResources((HtmlPage)newPage);
				} else if (HtmlPage.class.isAssignableFrom(newPage.getClass())) {
					replyTime = newPage.getWebResponse().getLoadTimeInMilliSeconds();
					bodyByteAmount = newPage.getWebResponse().getContentAsStream().available();
					currentBrowserState.setCurrentPage((HtmlPage)newPage);
					return collectResources((HtmlPage)newPage);
				} else {
					replyTime = newPage.getWebResponse().getLoadTimeInMilliSeconds();
					bodyByteAmount = newPage.getWebResponse().getContentAsStream().available();
					return !(newPage.getWebResponse().getStatusCode() < 200 || newPage.getWebResponse().getStatusCode() > 299);
				}
			} catch (IOException e1) {
				consoleLog.error("IOException during a addSelection.", e1);
				resultMessage = "IOException - addSelection operation failed";
			}
			return true;
		} else {
			resultMessage = "Element is not a select Element";
			return false;
		}
	}
	
	/**
	 * Unselect the element in the select element indicated by the locator stored 
	 * in the first entry of the stepAttributeList
	 * @return Step success status
	 */
	private boolean removeSelection() {
		HtmlElement targetElement = null;
		try {
			targetElement = locator();
		} catch (ElementNotFoundException e) {
			resultMessage = "Could not locate the select element";
			return false;
		}
		if (targetElement == null) {
			resultMessage = "Could not locate the select element";
			return false;
		}
		if (HtmlSelect.class.isAssignableFrom(targetElement.getClass())) {
			HtmlSelect tempSelect = ((HtmlSelect)targetElement);
			HtmlOption tempOption = optionLocator(tempSelect);
			if (tempOption == null) {
				return false;
			} else {
				try {
					Page newPage = tempOption.setSelected(false);
					if (newPage == null) {
						resultMessage = "Returned a null page";
						return false;
					} else if (newPage.equals(currentBrowserState.getCurrentPage())) {
						return collectResources((HtmlPage)newPage);
					} else if (HtmlPage.class.isAssignableFrom(newPage.getClass())) {
						replyTime = newPage.getWebResponse().getLoadTimeInMilliSeconds();
						bodyByteAmount = newPage.getWebResponse().getContentAsStream().available();
						currentBrowserState.setCurrentPage((HtmlPage)newPage);
						return collectResources((HtmlPage)newPage);
					} else {
						replyTime = newPage.getWebResponse().getLoadTimeInMilliSeconds();
						bodyByteAmount = newPage.getWebResponse().getContentAsStream().available();
						return !(newPage.getWebResponse().getStatusCode() < 200 || newPage.getWebResponse().getStatusCode() > 299);
					}
				} catch (IOException e1) {
					consoleLog.error("IOException during a removeSelection.", e1);
					resultMessage = "IOException - removeSelection operation failed";
				}
				return true;
			}
		} else {
			resultMessage = "Element is not a select Element";
			return false;
		}
	}
	
	/**
	 * Locates an option in the HtmlSelect passed using the 2nd attribute in the stepAttributeList.
	 * @param select Select element in which to search for the option
	 */
	private HtmlOption optionLocator(HtmlSelect select) {
		String name = stepAttributeList.getQName(1);
		String value = stepAttributeList.getValue(1);
		if (name == null) {
			name = "label";
		}
		if (value == null) {
			resultMessage = "Bad option locator";
			return null;
		}
		if (name.equals("label")) {
			Iterator<HtmlOption> options = select.getOptions().iterator();
			while (options.hasNext()) {
				HtmlOption nextOption = options.next();
				if (nextOption.asText().equals(value)) {
					return nextOption;
				}	
			}
			resultMessage = "Bad option locator label";
			return null;
		} else if (name.equals("value")) {
			try {
				return select.getOptionByValue(value);
			} catch (ElementNotFoundException e) {
				resultMessage = "Bad option locator value";
			}
			return null;
		} else if (name.equals("index")) {
			if (value == null)
				return null;
			int indexNum = -1;
			try {
				indexNum = Integer.parseInt(value);
			} catch (NumberFormatException e) {
				resultMessage = "Bad option locator index";
				return null;
			}
			if (indexNum < 0 || indexNum >= select.getOptionSize()) {
				resultMessage = "Bad option locator index";
				return null;
			}
			return select.getOption(indexNum);
		} else if (name.equals("id")) {
			Iterator<HtmlOption> options = select.getOptions().iterator();
			while (options.hasNext()) {
				HtmlOption nextOption = options.next();
				if (nextOption.getAttributeValue(name).equals(value)) {
					return nextOption;
				}	
			}
			resultMessage = "Bad option locator id";
			return null;
		}
		resultMessage = "Bad option locator";
		return null;
	}
	
	/**
	 * Determines the appropriate action to find a particular resource in an HtmlPage
	 * indicated by the first Attribute in the AttributeList.
	 * @return The first HtmlElement that was located
	 * @throws ElementNotFoundException If the Element was not found
	 */
	private final HtmlElement locator() throws ElementNotFoundException {
		String name = stepAttributeList.getQName(0);
		if (name.equals("xpath")) {
			Object tempNode = currentBrowserState.getCurrentPage().getFirstByXPath(stepAttributeList.getValue(0));
			if (tempNode.getClass().isInstance(HtmlElement.class)) {
				return (HtmlElement)tempNode;
			} else {
				return null;
			}
		} else if (name.equals("link")) {
			List<HtmlAnchor> tempListAnchors = currentBrowserState.getCurrentPage().getAnchors();
			HtmlAnchor[] tempArrayAnchors = (HtmlAnchor[]) tempListAnchors.toArray();
			for (int i = 0; i < tempArrayAnchors.length; i++) {
				String textContent = tempArrayAnchors[i].getTextContent();
					if (textContent.equals(stepAttributeList.getValue(0))) {
						return tempArrayAnchors[i];
					}
			}
			return null;
		} else if (name.equals("identifier")) {
			HtmlElement tempElement = currentBrowserState.getCurrentPage().getHtmlElementById(stepAttributeList.getValue(0));
			if (tempElement == null) {
				return currentBrowserState.getCurrentPage().getHtmlElementsByName(stepAttributeList.getValue(0)).get(0);
			}
			return tempElement;
		} else if (name.equals("id")) {
			return currentBrowserState.getCurrentPage().getHtmlElementById(stepAttributeList.getValue(0));
		} else if (name.equals("name")) {
			return currentBrowserState.getCurrentPage().getHtmlElementsByName(stepAttributeList.getValue(0)).get(0);
		} else {
			if (name.startsWith("//")) {
				Object tempNode = currentBrowserState.getCurrentPage().getFirstByXPath(name);
				if (tempNode.getClass().isInstance(HtmlElement.class)) {
					return (HtmlElement)tempNode;
				} else {
					return null;
				}
			} else {
				HtmlElement tempElement = currentBrowserState.getCurrentPage().getHtmlElementById(name);
				if (tempElement == null) {
					return currentBrowserState.getCurrentPage().getHtmlElementsByName(name).get(0);
				}
				return tempElement;
			}
		}
	}

	/**
	 * Standard GET STEP.
	 * @return Step status
	 */
	private boolean open() {
		String currentPath = stepAttributeList.getValue(0);
		currentPath = targetDomain + currentPath;
		
		HtmlPage invokePage = null;
		Page tempPage;

		try {
			tempPage = currentBrowserState.getVUser().getPage(currentPath);
		} catch (FailingHttpStatusCodeException e) {
			//Should be impossible due to settings on the WebClient
			consoleLog.error("Bad Status Code.", e);
			return false;
		} catch (MalformedURLException e) {
			//This should only occur if the HtmlRobot is passed a bad script
			if (resultMessage == null)
				resultMessage = "Bad Script - Malformed URL for " + currentPath;
			consoleLog.error("MalformedURL", e);
			return false;
		} catch (SocketTimeoutException e) {
			//Only possible if there is a problem with the proxy
			if (resultMessage == null)
				resultMessage = "Socket Timeout";
				if (consoleLog.isDebugEnabled())
					consoleLog.debug("Socket Timed Out from licit/illicit factors.");
				if (myProxy.getThreadState().compareTo(Thread.State.BLOCKED) == 0) {
					consoleLog.error("Server is unresponsive for" + currentPath);
					if (resultMessage == null)
						resultMessage = "Server is unresponsive for " + currentPath;
				}
			stepRobotOwner.setEnd(true);
			return false;
		} catch (ConnectException e) {
			//Only possible if there is a problem with the proxy
			if (resultMessage == null)
				resultMessage = "Server could not be contacted.";
			if (consoleLog.isDebugEnabled())
				consoleLog.debug("Server could not be contacted. Exiting step sequence.", e);
			stepRobotOwner.setEnd(true);
			return false;
		} catch (NoHttpResponseException e) {
			//Should be the normal catch response if there is a problem with the target
			//The proxy will close the socket and will pass the error reason back to the Robot
			if (resultMessage == null)
				resultMessage = myProxy.getProxyMessage();
			return false;
		} catch (IOException e) {
			//Only possible if there is a problem with the proxy
			if (resultMessage == null)
				resultMessage = "IO Error";
			consoleLog.error("IO Error during Invoke.", e);
			stepRobotOwner.setEnd(true);
			return false;
		}
		
		if (tempPage == null) {
			if (consoleLog.isDebugEnabled()) {
				consoleLog.debug("Opened Page is null.");
			}
			return false;
		} else if (tempPage.getClass() == HtmlPage.class) {
			invokePage = (HtmlPage) tempPage;
			currentBrowserState.setCurrentPage(invokePage);
			replyTime = invokePage.getWebResponse().getLoadTimeInMilliSeconds();
			try {
				bodyByteAmount = invokePage.getWebResponse().getContentAsStream().available();
			} catch (IOException e1) {
				consoleLog.error("IOException while reading content as from post stream to count bodyByteAmount.", e1);
			}
			return collectResources(invokePage);
		} else {
			boolean tempStatus = true;
			replyTime = tempPage.getWebResponse().getLoadTimeInMilliSeconds();
			try {
				bodyByteAmount = tempPage.getWebResponse().getContentAsStream().available();
			} catch (IOException e1) {
				consoleLog.error("IOException while reading content as from post stream to count bodyByteAmount.", e1);
			}
			if (currentBrowserState.addUrlToCached("/favicon.ico")) {
				try {
					WebResponse temporary = currentBrowserState.getVUser().getPage(targetDomain + "/favicon.ico").getWebResponse();
					if (tempPage.getWebResponse().getStatusCode() < 200
							|| tempPage.getWebResponse().getStatusCode() > 399) {
					} else {
						replyTime += temporary.getLoadTimeInMilliSeconds();
						bodyByteAmount += temporary.getResponseBody().length;
					}
				} catch (SocketTimeoutException e) {
					if (resultMessage == null)
						resultMessage = "Socket Timeout";
					consoleLog.debug("Socket Timed Out from licit/illicit factors.");
				} catch (FailingHttpStatusCodeException e) {
					//Should be impossible
					consoleLog.error("Bad Status Code.", e);
				} catch (MalformedURLException e) {
					if (resultMessage == null)
						resultMessage = "Bad Script - Malformed URL for " + targetDomain + "/favicon.ico";
					consoleLog.error("MalformedURL", e);
					tempStatus = false;
				} catch (IOException e) {
					if (resultMessage == null)
						resultMessage = "IO Error retrieving favicon";
					consoleLog.error("IO Error during Invoke.", e);
					tempStatus = false;
				}
			}
			if (tempPage.getWebResponse().getStatusCode() < 200
					|| tempPage.getWebResponse().getStatusCode() > 399) {
				if (resultMessage == null)
					resultMessage = "Bad Status Code " + tempPage.getWebResponse().getStatusCode() + " for " + currentPath;
				return false;
			}
			return tempStatus;
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
		currentBrowserState.getVUser().setCurrentWindow(currentBrowserState.getResourceWindow());
		if (invokePage.getWebResponse().getStatusCode() < 200
				|| invokePage.getWebResponse().getStatusCode() > 299) {
			if (resultMessage == null)
				resultMessage = "Bad Status Code " + invokePage.getWebResponse().getStatusCode() + " for " + invokePage.getWebResponse().getUrl().toExternalForm();
			tempStatus = false;
		}
		if (currentBrowserState.addUrlToCached("/favicon.ico")) {
			try {
				WebResponse iconResponse = currentBrowserState.getVUser().getPage(targetDomain + "/favicon.ico").getWebResponse();
				if (iconResponse.getStatusCode() < 200 || iconResponse.getStatusCode() > 299) {
				} else {
					replyTime += iconResponse.getLoadTimeInMilliSeconds();
					bodyByteAmount += iconResponse.getResponseBody().length;
				}
			} catch (SocketTimeoutException e) {
				//Socket timeouts may be normal if the server is under load.
				consoleLog.debug("Socket Timed Out from licit/illicit factors.");
			} catch (FailingHttpStatusCodeException e) {
				//Should be impossible
				consoleLog.error("Bad Status Code.", e);
				tempStatus = false;
			} catch (MalformedURLException e) {
				consoleLog.error("MalformedURL", e);
				tempStatus = false;
			} catch (IOException e) {
				consoleLog.error("IO Error during Invoke.", e);
				tempStatus = false;
			}
		}
		Iterable<HtmlElement> tempList = 
			invokePage.getDocumentElement().getAllHtmlChildElements();
		StringBuffer resourcesCollector = new StringBuffer();
		String tempAttr;
		WebResponse temporary;
		NamedNodeMap tempAttrs;
		for (HtmlElement temp : tempList) {
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
					currentBrowserState.addUrlToCached(tempAttr);
				}
				if (currentBrowserState.addUrlToCached(tempAttr)) {
					consoleLog.debug("Collecting resource: " + tempAttr);
					try {
						temporary = currentBrowserState.getVUser().getPage(tempAttr).getWebResponse();
						if (consoleLog.isDebugEnabled()) {
							resourcesCollector.append("Resources obtained: " 
									+ tempAttr + '\n');
						}
						replyTime += temporary.getLoadTimeInMilliSeconds();
						bodyByteAmount += temporary.getResponseBody().length;
						if (temporary.getStatusCode() < 200 || temporary.getStatusCode() > 299) {
							if (resultMessage == null)
								resultMessage = "Bad Status Code " + temporary.getStatusCode() + " for " + tempAttr;
							tempStatus = false;
						}
					} catch (FailingHttpStatusCodeException e) {
						//Should be impossible
						consoleLog.error("Bad Status Code.", e);
						tempStatus = false;
					} catch (MalformedURLException e) {
						if (resultMessage == null)
							resultMessage = "Bad Script - Malformed URL";
						consoleLog.error("Bad URL Entered during resource retrieval.", e);
						tempStatus = false;
					} catch (IOException e) {
						if (resultMessage == null)
							resultMessage = "IO Error";
						consoleLog.error("IO Error Occured during resource retrieval.", e);
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
						currentBrowserState.addUrlToCached(aResource);
					}
					if (currentBrowserState.addUrlToCached(aResource)) {
						consoleLog.debug("Collecting resource: " + aResource);
						try {
							temporary = currentBrowserState.getVUser().getPage(aResource).getWebResponse();
							if (consoleLog.isDebugEnabled()) {
								resourcesCollector.append("Import resources obtained: "
										+ aResource + '\n');
							}
							replyTime += temporary.getLoadTimeInMilliSeconds();
							bodyByteAmount += temporary.getResponseBody().length;
							if (temporary.getStatusCode() < 200 ||  temporary.getStatusCode() > 299) {
								if (resultMessage == null)
									resultMessage = "Bad Status Code " + temporary.getStatusCode() + " for " + aResource;
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
						} catch (FailingHttpStatusCodeException e) {
							//Should be impossible
							consoleLog.error("Bad Status Code.", e);
							tempStatus = false;
						} catch (MalformedURLException e) {
							if (resultMessage == null)
								resultMessage = "Bad Script - Malformed URL";
							consoleLog.error("Bad URL Entered during resource retrieval.", e);
							tempStatus = false;
						} catch (IOException e) {
							if (resultMessage == null)
								resultMessage = "IO Error";
							consoleLog.error("IO Error Occured during resource retrieval.", e);
							tempStatus = false;
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
							currentBrowserState.addUrlToCached(aResource);
						}
						if (currentBrowserState.addUrlToCached(aResource)) {
							consoleLog.debug("Collecting resource: " + aResource);
							try {
								temporary = currentBrowserState.getVUser().getPage(aResource).getWebResponse();
								if (consoleLog.isDebugEnabled()) {
									resourcesCollector.append("Link Resources obtained: " + aResource + '\n');
								}
								replyTime += temporary.getLoadTimeInMilliSeconds();
								bodyByteAmount += temporary.getResponseBody().length;
								if (temporary.getStatusCode() < 200 || temporary.getStatusCode() > 299) {
									if (resultMessage == null)
										resultMessage = "Bad Status Code " + temporary.getStatusCode() + " for " + aResource;
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
									if (currentBrowserState.addUrlToCached(aResource)) {
										try {
											temporary = currentBrowserState.getVUser().getPage(aResource).getWebResponse();
											if (consoleLog.isDebugEnabled()) {
												resourcesCollector.append("Style Sheet Pic Resources obtained: " + aResource + '\n');
											}
											replyTime += temporary.getLoadTimeInMilliSeconds();
											bodyByteAmount += temporary.getResponseBody().length;
											if (temporary.getStatusCode() < 200 || temporary.getStatusCode() > 299) {
												if (resultMessage == null)
													resultMessage = "Bad Status Code " + temporary.getStatusCode() + " for " + aResource;
												tempStatus = false;
											}
										} catch (FailingHttpStatusCodeException e) {
											//Should be impossible
											consoleLog.error("Bad Status Code.", e);
											tempStatus = false;
										} catch (MalformedURLException e) {
											if (resultMessage == null)
												resultMessage = "Bad Script - Malformed URL";
											consoleLog.error("Bad URL Entered during resource retrieval.", e);
											tempStatus = false;
										} catch (IOException e) {
											if (resultMessage == null)
												resultMessage = "IO Error";
											consoleLog.error("IO Error Occured during resource retrieval.", e);
											tempStatus = false;
										}
									}
								}
							} catch (FailingHttpStatusCodeException e) {
								//Should be impossible
								consoleLog.error("Bad Status Code.", e);
								tempStatus = false;
							} catch (MalformedURLException e) {
								if (resultMessage == null)
									resultMessage = "Bad Script - Malformed URL";
								consoleLog.error("Bad URL Entered during resource retrieval.", e);
								tempStatus = false;
							} catch (IOException e) {
								if (resultMessage == null)
									resultMessage = "IO Error";
								consoleLog.error("IO Error Occured during resource retrieval.", e);
								tempStatus = false;
							}
						}
					}
				}
			}
		}
		if (consoleLog.isDebugEnabled()) {
			consoleLog.debug(resourcesCollector.toString());
		}
		currentBrowserState.getVUser().setCurrentWindow(currentBrowserState.getMainWindow());
		return tempStatus;
	}

	/**
	 * Verify the currentpage title with an Attribute value.
	 * @return True if title is correct, else false
	 */
	private boolean verifyTitle() {
		if (currentBrowserState.getCurrentPage() != null) {
			if (consoleLog.isDebugEnabled()) {
				consoleLog.debug(currentBrowserState.getCurrentPage().getTitleText());
			}
			return currentBrowserState.getCurrentPage().getTitleText().equals(stepAttributeList.getValue(0));
		}
		if (resultMessage == null)
			resultMessage = "Current page is null";
		return false;
	}

	/**
	 * Instruct this thread to wait for a given period of time as this is a WAIT STEP.
	 */
	private void pause() {
		if (stepAttributeList.getValue(0).equals("")) {
			try {
				Thread.sleep(Robot.getDefaultWaitStep());
			} catch (InterruptedException e) {
				if (resultMessage == null)
					resultMessage = "Interrupted";
				consoleLog.error("Interrupted Exception during a default WAIT step", e);
			}
		} else {
			try {
				Thread.sleep(Integer.parseInt(stepAttributeList.getValue(0)));
			} catch (NumberFormatException e) {
				if (resultMessage == null)
					resultMessage = "Bad Script - Bad waitTime";
				consoleLog.error("Could not determine the wait length during a WAIT Step", e);
				try {
					Thread.sleep(Robot.getDefaultWaitStep());
				} catch (InterruptedException e1) {
					if (resultMessage == null)
						resultMessage = "Interrupted";
					consoleLog.error("Interrupted Exception during a WAIT step", e);
				}
			} catch (InterruptedException e) {
				if (resultMessage == null)
					resultMessage = "Interrupted";
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
		ActionType currentType;
		try {
			currentType = ActionType.valueOf(stepName);
		} catch (IllegalArgumentException e) {
			consoleLog.error("Unknown Step type found.", e);
			return;
		}
		if (currentType == null) {
			return;
		}
		switch (currentType) {
		case open:
		case click:
		case submit:
		case check:
		case uncheck:
		case select:
		case addSelection:
		case removeSelection:
		case refresh:
		case type:
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
				tempResult.append("stepProxyTimeResponded-stepProxyTimeStarted: " + (stepProxyTimeResponded - stepProxyTimeStarted));
				tempResult.append('\n');
				tempResult.append("bodyByteAmount: " + bodyByteAmount);
				tempResult.append('\n');
				tempResult.append("proxyReceiveAmount: " + proxyReceiveAmount);
				tempResult.append('\n');
				tempResult.append("proxySentAmount: " + proxySentAmount);
				tempResult.append('\n');
				if (replyTime == 0) {
					replyTime = 1;
				}
				tempResult.append((double) (proxyReceiveAmount + proxySentAmount) / ((double) replyTime / 1000));
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
			tempResult.append(bodyByteAmount);
			tempResult.append(',');
			tempResult.append(proxyReceiveAmount);
			tempResult.append(',');
			tempResult.append(proxySentAmount);
			tempResult.append(',');
			if (replyTime == 0) {
				replyTime = 1;
			}
			tempResult.append((double) (proxyReceiveAmount + proxySentAmount) / ((double) replyTime / 1000));
			tempResult.append(',');
			if (stepStatus) {
				tempResult.append("success");
			} else {
				tempResult.append("failure");
			}
			tempResult.append(',');
			tempResult.append(resultMessage);
			tempResult.append(',');
			resultLog.info(tempResult);
			//Check to see if the failure was fatal to the Robot
			if (!stepStatus) {
				if (!resultMessage.startsWith("IOException") && !resultMessage.startsWith("SocketTimeoutException")) {
					tempResult = new StringBuffer();
					tempResult.append(jobID);
					tempResult.append(',');
					tempResult.append(stepNumber);
					tempResult.append(',');
					tempResult.append(System.currentTimeMillis());
					tempResult.append(',');
					resultLog.fatal(tempResult.toString() + "Robot is exiting due to critical errors");
					tempResult.append(',');
					stepRobotOwner.setEnd(true);
				}
			}
			break;
		case pause:
		case verifyTitle:
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
			tempResult.append(',');
			tempResult.append(resultMessage);
			tempResult.append(',');
			resultLog.info(tempResult);
			break;
			default:
				consoleLog.error("An unknown Step was found during reporting.");
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
		this.currentBrowserState = newState;
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
