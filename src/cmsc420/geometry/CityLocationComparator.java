/*
 * @(#)CityLocationComparator.java        1.0 2007/01/23
 *
 * Copyright Ben Zoller (University of Maryland, College Park), 2007
 * All rights reserved. Permission is granted for use and modification in CMSC420 
 * at the University of Maryland.
 */
package cmsc420.geometry;

import java.util.Comparator;

/**
 * Compares two cities based on location of x, y, and z coordinates. First compares
 * the x values of each {@link City}. If the x values are the same, then the y values of
 * each City are compared. then the Z!
 * 
 * @author Ben Zoller
 * @version 1.0, 23 Jan 2007
 */
public class CityLocationComparator implements Comparator<City> {

	public int compare(final City one, final City two) {
		if (one.getX() < two.getX()) {
			return -1;
		} else if (one.getX() > two.getX()) {
			return 1;
		} else {
			/* one.getX() == two.getX() */
			if (one.getY() < two.getY()) {
				return -1;
			} else if (one.getY() > two.getY()) {
				return 1;
			} else {
				/* one.getY() == two.getY() */
				if (one.getZ() < two.getZ()){
					return -1;
				} else if (one.getZ() > two.getZ()){
					return 1;
				} else {
					return 0;	
				}				
			}
		}
	}
}