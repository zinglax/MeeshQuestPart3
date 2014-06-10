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
import cmsc420.pmquadtree.EndPointDoesNotExistThrowable;
import cmsc420.pmquadtree.IsolatedCityAlreadyExistsThrowable;
import cmsc420.pmquadtree.OutOfBoundsThrowable;
import cmsc420.pmquadtree.PM1Quadtree;
import cmsc420.pmquadtree.PM3Quadtree;
import cmsc420.pmquadtree.PMQuadtree;
import cmsc420.pmquadtree.PortalIntersectsRoadThrowable;
import cmsc420.pmquadtree.PortalOutOfBoundsThrowable;
import cmsc420.pmquadtree.PortalViolatesPMRulesThrowable;
import cmsc420.pmquadtree.RedundantPortalThrowable;
import cmsc420.pmquadtree.RoadAlreadyExistsThrowable;
import cmsc420.pmquadtree.RoadIntersectsAnotherRoadThrowable;
import cmsc420.pmquadtree.RoadNotOnOneLevelThrowable;
import cmsc420.pmquadtree.RoadOutOfBoundsThrowable;
import cmsc420.pmquadtree.RoadViolatesPMRulesThrowable;
import cmsc420.pmquadtree.StartEqualsEndThrowable;
import cmsc420.pmquadtree.StartOrEndIsPortalThrowable;
import cmsc420.pmquadtree.StartPointDoesNotExistThrowable;
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

	// USING HASHMAP FOR TESTING PURPOSES
	//public HashMap<String, City> citiesByName;
	public GuardedAvlGTree<String, City> citiesByName;
	
	public TreeSet<City> citiesByLocation = new TreeSet<City>(
			new CityLocationComparator());

	public RoadAdjacencyList roads = new RoadAdjacencyList();

	// PM Quadtree associated with each level
	public HashMap<Integer, PMQuadtree> levels = new HashMap<Integer, PMQuadtree>();

	// Portals associated with specific level
	public HashMap<Integer, City> portals = new HashMap<Integer, City>();

	// Constructor
	public Leveler(int spatialWidth, int spatialHeight, int pmOrder,
			int avlOrder) {
		this.spatialWidth = spatialWidth;
		this.spatialHeight = spatialHeight;
		this.pmOrder = pmOrder;
		
		// USING HASHMAP FOR TESTING PURPOSES
		//this.citiesByName = new HashMap<String, City>();
		this.citiesByName = new GuardedAvlGTree<String, City>(
				new StringComparator(), avlOrder);

		this.spatialOrigin = new Point2D.Float((float) this.spatialWidth,
				(float) this.spatialHeight);

	}

	// Adding a portal
	public void addPortal(City c) throws RedundantPortalThrowable,
			DuplicatePortalCoordinatesThrowable, DuplicatePortalNameThrowable,
			PortalOutOfBoundsThrowable, PortalIntersectsRoadThrowable,
			PortalViolatesPMRulesThrowable, RoadViolatesPMRulesThrowable {

		// Check if portal already exists on level
		if (portals.containsKey(c.getZ()) && portals.get(c.getZ()) != null) {
			throw new RedundantPortalThrowable();
		}

		// Another Portal has the same coordinates
		for (City i : portals.values()) {
			if (i != null){
				if (i.getLocationString().equals(c.getLocationString())) {
					throw new DuplicatePortalCoordinatesThrowable();
				}
			}
		}

		// Another Portal has the same name
		for (City i : portals.values()) {
			if (i != null){
				if (i.getName().equals(c.getName())) {
					throw new DuplicatePortalNameThrowable();
				}
			}
		}
		
		// Portal has same name or coordinates as existing city
		for (City city : citiesByName.values()){
			if (c.getName().equals(city.getName())){
				throw new DuplicatePortalNameThrowable();
			}
			if (c.getLocationString().equals(city.getLocationString())){
				throw new DuplicatePortalCoordinatesThrowable();
			}
		}

		// Portal out of Bounds
		if (!Inclusive2DIntersectionVerifier.intersects(c.toPoint2D(),
				new Rectangle2D.Float(0, 0,
						spatialWidth, spatialHeight))) {
			throw new PortalOutOfBoundsThrowable();
		}

		// Create new level if it does not exist already
		if (!levels.containsKey(c.getZ())) {
			if (pmOrder == 1) {
				PMQuadtree pmQuadtree = new PM1Quadtree(spatialWidth,
						spatialHeight);
				levels.put(c.getZ(), pmQuadtree);
			} else {
				PMQuadtree pmQuadtree = new PM3Quadtree(spatialWidth,
						spatialHeight);
				levels.put(c.getZ(), pmQuadtree);
			}
			// Sets portal value to null for now, need to make sure that it can be added properly
			portals.put(c.getZ(), null);
		}

		levels.get(c.getZ()).addPortal(c);

	}

	// Adding a road
	public void addRoad(String start, String end)
			throws StartPointDoesNotExistThrowable,
			EndPointDoesNotExistThrowable, StartEqualsEndThrowable,
			RoadNotOnOneLevelThrowable, StartOrEndIsPortalThrowable,
			RoadOutOfBoundsThrowable, RoadAlreadyExistsThrowable,
			RoadIntersectsAnotherRoadThrowable, RoadViolatesPMRulesThrowable,
			PortalViolatesPMRulesThrowable {

		// startPointDoesNotExist
		if (!citiesByName.containsKey(start)) {
			throw new StartPointDoesNotExistThrowable();
		}

		// endPointDoesNotExist
		if (!citiesByName.containsKey(end)) {
			throw new EndPointDoesNotExistThrowable();
		}

		Road r = new Road((City) citiesByName.get(start),
				(City) citiesByName.get(end));

		// startEqualsEnd
		if (r.getStart().getName().equals(r.getEnd().getName())) {
			throw new StartEqualsEndThrowable();
		}

		// roadNotOnOneLevel
		if (r.getStart().getZ() != r.getEnd().getZ()) {
			throw new RoadNotOnOneLevelThrowable();
		}

		// startOrEndIsPortal
		for (City portal : portals.values()) {
			if (portal != null) {
				if (r.getStart().getLocationString()
						.equals(portal.getLocationString())) {
					throw new StartOrEndIsPortalThrowable();
				}
				if (r.getEnd().getLocationString()
						.equals(portal.getLocationString())) {
					throw new StartOrEndIsPortalThrowable();
				}
			}
		}

		// roadOutOfBounds
		Rectangle2D.Float world = new Rectangle2D.Float(0, 0, spatialWidth,
				spatialHeight);
		if (!Inclusive2DIntersectionVerifier.intersects(r.toLine2D(), world)) {
			throw new RoadOutOfBoundsThrowable();
		}

		// Create new level if it does not exist already
		if (!levels.containsKey(r.getStart().getZ())) {
			if (pmOrder == 1) {
				PMQuadtree pmQuadtree = new PM1Quadtree(spatialWidth,
						spatialHeight);
				levels.put(r.getStart().getZ(), pmQuadtree);
			} else {
				PMQuadtree pmQuadtree = new PM3Quadtree(spatialWidth,
						spatialHeight);
				levels.put(r.getStart().getZ(), pmQuadtree);
			}
			// Portal value set to null for level
			portals.put(r.getStart().getZ(), null);
		}

		// Adds road to PMQuadtree at specific level z
		levels.get(r.getStart().getZ()).addRoad(r);

		// Updates Adjacency List
		if (Inclusive2DIntersectionVerifier.intersects(
				r.getStart().toPoint2D(), world)
				&& Inclusive2DIntersectionVerifier.intersects(r.getEnd()
						.toPoint2D(), world)) {
			// add to adjacency list
			roads.addRoad(r.getStart(), r.getEnd());
		}
	}

	// Creating a City
	public void createCity(City c) throws DuplicateCityNameThrowable,
			DuplicateCityCoordinateThrowable {

		// City Name Conflicts with another city name
		if (citiesByName.containsKey(c.getName())) {
			throw new DuplicateCityNameThrowable();
		}

		// City coordinate conflicts with another city coordinate
		if (citiesByLocation.contains(c)) {
			throw new DuplicateCityCoordinateThrowable();
		}

		// City name conflicts with a portal name
		for (City portal : portals.values()) {
			if (portal.getName().equals(c.getName())) {
				throw new DuplicateCityNameThrowable();
			}
		}

		// City coordinate conflicts with a portal coordinate
		for (City portal : portals.values()) {
			if (portal.getLocationString().equals(c.getLocationString())) {
				throw new DuplicateCityCoordinateThrowable();
			}
		}

		/* add city to dictionary */
		citiesByName.put(c.getName(), c);
		citiesByLocation.add(c);
	}

	// Deleting a City
	public void deleteCity(City c) {

		// cityDoesNotExist
	}

	// Clear all, resets all of the structures
	public void clearall() {
		citiesByName.clear();
		citiesByLocation.clear();
		roads.clear();
		portals.clear();
		levels.clear();
	}

	// Delete Road
	public void deleteRoad(Road r) {

	}

	// Deletes a portal
	public void deletePortal(City c) {

	}

	// Prints quadtree at specific level
	public void printPMQuadtree(int level) {

	}

	// lists all the cities in a radius of a sphere (multilevel search)
	public void rangeCities() {

	}

	// Lists all of the roads that are in the radius of a sphere (multilevel
	// search)
	public void rangeRoads() {

	}

	// lists the closest city to a given point (NOT multilevelsearch) could
	// disgard method
	public void nearestCity() {

	}

	// gets the shortest path from point a to b (multilevel navigation)
	public void shortestPath() {

	}

	// Sweep, clears all of the marked deleted notes in AVL tree
	public void sweep() {

	}

	// Lists all of the cities
	public void listcities() {

	}
}
