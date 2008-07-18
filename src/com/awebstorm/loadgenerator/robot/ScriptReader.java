package com.awebstorm.loadgenerator.robot;
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
	 * @param newStepQueue Queue to place new Steps into
	 * @param newPrefs HashMap to place Robot preferences into
	 */
		public final void run(final InputStream script, final PriorityQueue<Step> newStepQueue, final HashMap<String, String> newPrefs) {

			SAXParserFactory newFactory;
			newFactory = SAXParserFactory.newInstance();
			SAXParser myXMLParser = null;
			this.stepQueue = newStepQueue;
			this.prefs = newPrefs;
			propertiesMode = true;
			stepCounter = 0;
			
			try {
				myXMLParser = newFactory.newSAXParser();
			} catch (ParserConfigurationException e) {
				consoleLog.fatal("Error creating new parser.", e);
				e.printStackTrace();
			} catch (SAXException e) {
				consoleLog.fatal("SAX Parser exception, Script is unsafe.", e);
				e.printStackTrace();
			}

			try {
				myXMLParser.parse(script, this);
			} catch (FileNotFoundException e) {
				consoleLog.fatal("Script file not found.");
				e.printStackTrace();
			} catch (SAXException e) {
				consoleLog.fatal("SAX Parser exception, Script is unsafe.", e);
				e.printStackTrace();
			} catch (IOException e) {
				consoleLog.fatal("SAX Parser exception, Script is unsafe.", e);
				e.printStackTrace();
			}
			
		}
	
		/**
		 * Create a new Step at the start of any non-preference element.
		 * @param uri The uri of the element
		 * @param localName of the element
		 * @param qName Qualified name of the element
		 * @param attributes Attributes of the element
		 * @throws SAXException Thrown if the SAX parser encounters an error
		 */
		public final void startElement(final String uri, final String localName, final String qName,
			final Attributes attributes) throws SAXException {

			if (!propertiesMode) {
				Attributes newList = new AttributesImpl(attributes);
				consoleLog.debug("Added Step: " + qName + " Value :"
						+ attributes.getValue(0));
				stepQueue.add(new Step(qName, stepCounter, newList));
				stepCounter++;
			}
		}

		/**
		 * Collect characters between elements to be used.
		 * @param ch Char array collected from the SAX element
		 * @param start Location to begin parsing chars
		 * @param length Location from start to end parsing the chars
		 * @throws SAXException Thrown if the SAX parser encounters an error
		 */
		public final void characters(final char[] ch, final int start, final int length) throws SAXException {
			tempString = new String(ch, start, length);
		}

		/**
		 * Create a new preference for every non-Step element.
		 * @param uri Resource Indicator of the end Element
		 * @param localName Local name of the Element
		 * @param qName Qualified name of the element
		 * @throws SAXException Thrown if the SAX parser encounters an error
		 */
		public final void endElement(final String uri, final String localName,
				final String qName) throws SAXException {

			if (propertiesMode) {
				if (qName.equals("Properties")) {
					propertiesMode = false;
					return;
				}
				consoleLog.debug("Added Pref: " 
						+ qName + " Value:" + tempString);
				prefs.put(qName, tempString);
			}

		}

}
