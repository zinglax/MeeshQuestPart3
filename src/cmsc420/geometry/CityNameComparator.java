/*
 * @(#)CityNameComparator.java        1.0 2007/01/23
 *
 * Copyright Ben Zoller (University of Maryland, College Park), 2007
 * All rights reserved. Permission is granted for use and modification in CMSC420 
 * at the University of Maryland.
 */
package cmsc420.geometry;

import java.util.Comparator;

/**
 * Compares two cities based on their names.
 * 
 * @author Ben Zoller
 * @version 1.0, 23 Jan 2007
 */
public class CityNameComparator implements Comparator<City> {
	public int compare(final City c1, final City c2) {
		return c1.getName().compareTo(c2.getName());
	}
}