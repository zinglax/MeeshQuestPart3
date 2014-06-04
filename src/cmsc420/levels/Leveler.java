package cmsc420.levels;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.TreeSet;

import cmsc420.geom.Inclusive2DIntersectionVerifier;
import cmsc420.geometry.City;
import cmsc420.geometry.CityLocationComparator;
import cmsc420.geometry.Road;
import cmsc420.geometry.RoadAdjacencyList;
import cmsc420.pmquadtree.DuplicateCityCoordinateThrowable;
import cmsc420.pmquadtree.DuplicateCityNameThrowable;
import cmsc420.pmquadtree.DuplicatePortalCoordinatesThrowable;
import cmsc420.pmquadtree.DuplicatePortalNameThrowable;
import cmsc420.pmquadtree.PMQuadtree;
import cmsc420.pmquadtree.PortalOutOfBoundsThrowable;
import cmsc420.pmquadtree.RedundantPortalThrowable;
import cmsc420.pmquadtree.Validator;
import cmsc420.sortedmap.GuardedAvlGTree;
import cmsc420.sortedmap.StringComparator;

public class Leveler {
	
	// Spatial Information used for PM quadtree generation
	public int spatialWidth;
	public int spatialHeight;
	public Point2D.Float spatialOrigin;
	public Validator validator;
	public int pmOrder;
	
	public GuardedAvlGTree<String, City> citiesByName;
	public TreeSet<City> citiesByLocation = new TreeSet<City>(
			new CityLocationComparator());
	
	public RoadAdjacencyList roads = new RoadAdjacencyList();

	// PM Quadtree associated with each level
	public HashMap<Integer, PMQuadtree> levels = new HashMap<Integer, PMQuadtree>();
	
	// Portals associated with specific level
	public HashMap<Integer, City> portals = new HashMap<Integer, City>();
	
	// Constructor
	public Leveler(int spatialWidth, int spatialHeight, int pmOrder, int avlOrder){
		this.spatialWidth = spatialWidth;
		this.spatialHeight = spatialHeight;
		this.pmOrder = pmOrder;
		this.citiesByName = new GuardedAvlGTree<String, City>(new StringComparator(), avlOrder); 
		
	}
	


	// Adding a portal
	public void addPortal(City c) throws RedundantPortalThrowable, DuplicatePortalCoordinatesThrowable, DuplicatePortalNameThrowable, PortalOutOfBoundsThrowable{
		
		// Check if portal already exists on level
		if (portals.containsKey(c.getZ())){
			throw new RedundantPortalThrowable();
		}
		
		// Duplicate Portal Coordinates
		for (City i : portals.values()){
			if (i.getLocationString().equals(c.getLocationString())){
				throw new DuplicatePortalCoordinatesThrowable();
			}
		}
		
		// Duplicate Portal Name
		for (City i : portals.values()){
			if (i.getName().equals(c.getName())){
				throw new DuplicatePortalNameThrowable();
			}
		}
		
		// Portal out of Bounds
		if (!Inclusive2DIntersectionVerifier.intersects(c.toPoint2D(),
				new Rectangle2D.Float(spatialOrigin.x, spatialOrigin.y,
						spatialWidth, spatialHeight))) {
			throw new PortalOutOfBoundsThrowable();
		}
		
		// TODO Create a new PMQuadtree for the level if it doesn't exist already
		
	}
	
	
	// Adding a road
	public void addRoad(Road r){
		
		// startPointDoesNotExist
		
		// endPointDoesNotExist
		
		// startEqualsEnd
		
		// roadNotOnOneLevel
		
		// startOrEndIsPortal
		
		// roadOutOfBounds
		
		//TODO Create a new PMQuadtree for the level if it doesn't exist already
		
		// roadAlreadyMapped
	}

	// Creating a City
	public void createCity(City c) throws DuplicateCityNameThrowable, DuplicateCityCoordinateThrowable{
		
		// City Name Conflicts with another city name
		if (citiesByName.containsKey(c.getName())){
			throw new DuplicateCityNameThrowable();
		}
		
		// City coordinate conflicts with another city coordinate
		if (citiesByLocation.contains(c)){
			throw new DuplicateCityCoordinateThrowable();
		}
		
		// City name conflicts with a portal name
		for (City portal : portals.values()){
			if (portal.getName().equals(c.getName())){
				throw new DuplicateCityNameThrowable();
			}
		}
		
		// City coordinate conflicts with a portal coordinate
		for (City portal : portals.values()){
			if (portal.getLocationString().equals(c.getLocationString())){
				throw new DuplicateCityCoordinateThrowable();
			}
		}
		
		/* add city to dictionary */
		citiesByName.put(c.getName(), c);
		citiesByLocation.add(c);
	}
	
	// Deleting a City
	public void deleteCity(City c){
		
		// cityDoesNotExist
	}
	
	// Clear all, resets all of the structures
	public void clearall(){

	}
	
	// Delete Road
	public void deleteRoad(Road r){
		
	}
	
	// Deletes a portal
	public void deletePortal(City c){
		
	}
	
	// Prints quadtree at specific level
	public void printPMQuadtree(int level){
		
	}
	
	// lists all the cities in a radius of a sphere (multilevel search)
	public void rangeCities(){
		
	}
	
	// Lists all of the roads that are in the radius of a sphere (multilevel search)
	public void rangeRoads(){
		
	}
	
	// lists the closest city to a given point (NOT multilevelsearch) could disgard method
	public void nearestCity(){
		
	}
	
	// gets the shortest path from point a to b (multilevel navigation)
	public void shortestPath(){
		
	}
	
	// Sweep, clears all of the marked deleted notes in AVL tree
	public void sweep(){
		
	}
	
	// Lists all of the cities
	public void listcities(){
		
	}
}
