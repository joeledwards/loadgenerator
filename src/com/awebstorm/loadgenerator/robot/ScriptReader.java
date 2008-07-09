package com.awebstorm.loadgenerator.robot;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.PriorityQueue;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;


/** 
 * Parses Steps and preferences from a Script XML file.
 * 
 * @author Cromano
 * @version 1.0
 *
 */
public class ScriptReader extends DefaultHandler {
	
	private Logger consoleLog = Logger.getLogger(this.getClass());
	private String tempString;
	private PriorityQueue<Step> stepQueue;
	private HashMap<String,String> prefs;
	private boolean propertiesMode;
	private int stepCounter;

	/**
	 * Parse a Script.
	 * @param script Location of the script to parse
	 * @param console Log Standard log
	 * @param stepQueue Queue to place new Steps into
	 * @param prefs HashMap to place Robot preferences into
	 */
		public void run(InputStream script, PriorityQueue<Step> stepQueue, HashMap<String,String> prefs) {

			SAXParserFactory newFactory;
			newFactory = SAXParserFactory.newInstance();
			SAXParser myXMLParser = null;
			this.stepQueue = stepQueue;
			this.prefs = prefs;
			propertiesMode = true;
			stepCounter = 0;
			
			try {
				myXMLParser = newFactory.newSAXParser();
			} catch (ParserConfigurationException e) {
				consoleLog.fatal("Error creating new parser.",e);
				e.printStackTrace();
			} catch (SAXException e) {
				consoleLog.fatal("SAX Parser exception, Script is unsafe.",e);
				e.printStackTrace();
			}

			try {
				myXMLParser.parse(script,this);
			} catch (FileNotFoundException e) {
				consoleLog.fatal("Script file not found.");
				e.printStackTrace();
			} catch (SAXException e) {
				consoleLog.fatal("SAX Parser exception, Script is unsafe.",e);
				e.printStackTrace();
			} catch (IOException e) {
				consoleLog.fatal("SAX Parser exception, Script is unsafe.",e);
				e.printStackTrace();
			}
			
		}
	
		/**
		 * Create a new Step at the start of any non-preference element
		 */
		public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

			if (!propertiesMode) {
				Attributes newList = new AttributesImpl(attributes);
				consoleLog.debug("Added Step: " + qName + " Value :" + attributes.getValue(0));
				stepQueue.add(new Step(qName,stepCounter,newList));
				stepCounter++;
			}
		}

		/**
		 * Collect characters between elements to be used
		 */
		public void characters(char[] ch, int start, int length) throws SAXException {
			tempString = new String(ch,start,length);
		}

		/**
		 * Create a new preference for every non-Step element
		 */
		public void endElement(String uri, String localName,
				String qName) throws SAXException {

			if ( propertiesMode) {
				if (qName.equals("Properties")) {
					propertiesMode = false;
					return;
				}
				consoleLog.debug("Added Pref: " + qName + " Value:" + tempString);
				prefs.put(qName,tempString);
			}

		}

}
