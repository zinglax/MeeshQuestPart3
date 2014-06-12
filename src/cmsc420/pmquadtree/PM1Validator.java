package cmsc420.pmquadtree;


import java.util.LinkedList;

import cmsc420.geometry.Geometry;
import cmsc420.pmquadtree.PMQuadtree.Black;
import cmsc420.pmquadtree.PMQuadtree.Gray;
import cmsc420.geometry.Road;

public class PM1Validator implements Validator {
	
	public boolean valid(final Black node) {
		/*
		 * A PM1 Quadtree is valid when:
		 * 	it has at most 1 city in it and all the roads in the node are connected to that city
		 * 	0R
		 * 	it only has one road
		 * */
	
		// Too many cities
		if (node.getNumPoints() > 1)
			return false;
		
		// A road passes through the node
		if ((node.geometry.size() == 1 && node.getNumPoints() == 0))
			return true;
		
		// One city with all roads going to it
		if (node.getNumPoints() == 1){
			// Copies the list of Geo. items in the node
			LinkedList<Geometry> temp = new LinkedList<Geometry>();
			temp.addAll(node.geometry);
			
			// Removes city from temp list (They all should be roads now)
			temp.remove((Geometry)node.getCity());
			
			// Loops over each road making sure that one of the endpoints is the city in the node
			for (Geometry g : temp)
				// if it does not contain the city it is not valid
				if (!((Road) g).contains(node.getCity()))
					return false;	
			return true;
		}
		
		return false;
		
	}
	
	public boolean valid(final Gray node){
		if (node.halfHeight < 1 || node.halfWidth < 1){
			return false;
		} else {
			return true;
		}
	}
	
}
