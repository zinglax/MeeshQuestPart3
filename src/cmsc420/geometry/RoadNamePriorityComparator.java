/*
 * @(#)RoadNamePriorityComparator.java        1.0 2005
 *
 * Copyright David Renie (University of Maryland, College Park), 2005
 * All rights reserved. Permission is granted for use and modification in CMSC420 
 * at the University of Maryland.
 */
package cmsc420.geometry;

import java.util.Comparator;

/**
 * This comparator assumes two roads share a city. It compares the roads by
 * comparing the names of the unshared cities.
 * 
 * @author Dave Renie
 * @author Ben Zoller (Javadoc)
 * @version 1.0, 2005
 */
public class RoadNamePriorityComparator implements Comparator<Road> {
	/** name of shared city */
	final String commonCity;

	/**
	 * Constructs the comparator by initializing the name of the shared city.
	 * 
	 * @param cityName
	 *            name of shared city
	 */
	public RoadNamePriorityComparator(final String cityName) {
		commonCity = cityName;
	}

	/**
	 * Assumes two roads share a common city. The compare method looks at the
	 * two unshared cities and returns the city name asciibetically less than
	 * the other.
	 * 
	 * @param one
	 *            one road
	 * @param two
	 *            another road
	 * @return result of comparison
	 */
	public int compare(final Road one, final Road two) {
		/* assumes the two edges share a common vertex */
		
		/* names of unshared cities */
		final String oneCityName, twoCityName;
		
		/* set name of first unshared city */
		if (one.getStart().getName().equals(commonCity)) {
			oneCityName = one.getEnd().getName();
		} else {
			oneCityName = one.getStart().getName();
		}
		
		/* set name of second unshared city */
		if (two.getStart().getName().equals(commonCity)) {
			twoCityName = two.getEnd().getName();
		} else {
			twoCityName = two.getStart().getName();
		}

		/* compare the names of the unshared cities */
		return oneCityName.compareTo(twoCityName);
	}
}
