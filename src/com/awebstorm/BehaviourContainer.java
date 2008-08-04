package com.awebstorm;

import org.jbehave.core.behaviour.Behaviours;

import com.awebstorm.robot.HTMLActionBehaviour;
import com.awebstorm.robot.HTMLRobotBehaviour;
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
			(new HTMLRobotBehaviour()).getClass(),
			//(new HTMLActionBehaviour()).getClass(),
			//(new ProxyBehaviour()).getClass(),
		};
	}

}
