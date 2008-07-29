package com.awebstorm.robot;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jbehave.core.mock.UsingMatchers;

public class StyleParserBehaviour extends UsingMatchers {
	private static final String LOAD_GEN_LOG_PROPS_LOC = "log4j.properties";
	private Logger consoleLog = Logger.getLogger(this.getClass());
	
	/**
	 * Should retrieve the appropriate String resources from the Element Text.
	 */
	public void shouldParseCorrectly() {
		LinkedList<String> test = StyleParser.grabStyleElementText("@import \"found1\" @import avnower[onvaso[vnsdvobasvvaservoin\'found1/found2/found3.html'");
		ensureThat(test.get(0).equals("found1"));
		ensureThat(test.get(1).equals("found1/found2/found3.html"));
	}
	
	/**
	 * Should retrieve the appropriate String resources from the StyleShet text.
	 */
	public void shouldParseStyleSheetCorrectly() {
		LinkedList<String> test = StyleParser.grabStyleSheetText("url(hello1/test.img) ((())) asdiovnaovavuiobnauipvqaviburl(hello2\")");
		ensureThat(test.get(0).equals("hello1/test.img"));
		ensureThat(test.get(1).equals("hello2"));
	}
	
	/**
	 * Should build the subdirectory from the test URL correctly.
	 */
	public void shouldBuildSubDirCorrectly() {
		URL str = null;
		try {
			str = new URL ("http://dir/dir/dir/dir/fake.css");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		ensureThat(StyleParser.subDirBuilder(str).equals("http://dir/dir/dir/dir/"));
	}
	
	public void setUp() {
		PropertyConfigurator.configureAndWatch(LOAD_GEN_LOG_PROPS_LOC);
	}
	
	public void tearDown() {
		
	}
}
