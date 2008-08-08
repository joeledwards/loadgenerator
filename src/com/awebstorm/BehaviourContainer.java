package com.awebstorm;

import org.jbehave.core.behaviour.Behaviours;

import com.awebstorm.robot.HtmlActionBehaviour;
import com.awebstorm.robot.HtmlRobotBehaviour;
import com.awebstorm.robot.HtmlUnitProxyBehaviour;
import com.awebstorm.robot.HtmlVerifyBehaviour;
import com.awebstorm.robot.StyleParserBehaviour;

/**
 * Returns all the behaviour classes to be run.
 * @author Cromano
 *
 */
public class BehaviourContainer implements Behaviours {

	/**
	 * Get the behaviours.
	 * @return Classes to test
	 */
	@SuppressWarnings("unchecked")
	public final Class[] getBehaviours() {
		return new Class[] { 
			//(new StyleParserBehaviour()).getClass(),
			//(new HtmlRobotBehaviour()).getClass(),
			//(new HtmlActionBehaviour()).getClass(),
			//(new ProxyBehaviour()).getClass(),
			(new HtmlVerifyBehaviour()).getClass(),
			//(new HtmlUnitProxyBehaviour()).getClass(),
		};
	}

}
