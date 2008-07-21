package com.awebstorm.robot;

import org.jbehave.core.behaviour.Behaviours;

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
		return new Class[] { (new HTMLRobotBehaviour()).getClass() };
	}

}
