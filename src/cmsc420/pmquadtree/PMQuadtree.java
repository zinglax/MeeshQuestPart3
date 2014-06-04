package cmsc420.pmquadtree;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeSet;

import cmsc420.geom.Inclusive2DIntersectionVerifier;
import cmsc420.geometry.City;
import cmsc420.geometry.Geometry;
import cmsc420.geometry.Road;
import cmsc420.geometry.RoadNameComparator;

public abstract class PMQuadtree {

	/** stores all mapped roads in the PM Quadtree */
	final protected TreeSet<Road> allRoads;

	/** stores how many roads are connected to each city */
	final protected HashMap<String, Integer> numRoadsForCity;

	/** number of Portals */
	protected int numIsolatedCities;

	/** root of the PM Quadtree */
	protected Node root;

	/** spatial width of the PM Quadtree */
	final protected int spatialWidth;

	/** spatial height of the PM Quadtree */
	final protected int spatialHeight;

	/** spatial origin of the PM Quadtree (i.e. (0,0)) */
	final protected Point2D.Float spatialOrigin;

	/** validator for the PM Quadtree */
	final protected Validator validator;

	/** singleton white node */
	final protected White white = new White();

	/** order of the PM Quadtree (one of: {1,2,3}) */
	final protected int order;

	public abstract class Node {
		/** Type flag for an empty PM Quadtree leaf node */
		public static final int WHITE = 0;

		/** Type flag for a non-empty PM Quadtree leaf node */
		public static final int BLACK = 1;

		/** Type flag for a PM Quadtree internal node */
		public static final int GRAY = 2;

		/** type of PR Quadtree node (either empty, leaf, or internal) */
		protected final int type;

		/**
		 * Constructor for abstract Node class.
		 * 
		 * @param type
		 *            type of the node (either empty, leaf, or internal)
		 */
		protected Node(final int type) {
			this.type = type;
		}

		/**
		 * Gets the type of this PM Quadtree node. One of: BLACK, WHITE, GRAY.
		 * 
		 * @return type of this PM Quadtree node
		 */
		public int getType() {
			return type;
		}

		/**
		 * Adds a road to this PM Quadtree node.
		 * 
		 * @param g
		 *            road to be added
		 * @param origin
		 *            origin of the rectangular bounds of this node
		 * @param width
		 *            width of the rectangular bounds of this node
		 * @param height
		 *            height of the rectangular bounds of this node
		 * @return this node after the city has been added
		 * @throws RoadViolatesPMRulesThrowable
		 * @throws PortalViolatesPMRulesThrowable
		 * @throws InvalidPartitionThrowable
		 *             if the map if partitioned too deeply
		 * @throws IntersectingRoadsThrowable
		 *             if this road intersects with another road
		 */
		public Node add(final Geometry g, final Point2D.Float origin,
				final int width, final int height)
				throws RoadViolatesPMRulesThrowable,
				PortalViolatesPMRulesThrowable {
			throw new UnsupportedOperationException();
		}

		public Node remove(final Geometry g, final Point2D.Float origin,
				final int width, final int height) {
			throw new UnsupportedOperationException();
		}

		/**
		 * Returns if this node follows the rules of the PM Quadtree.
		 * 
		 * @return <code>true</code> if the node follows the rules of the PM
		 *         Quadtree; <code>false</code> otherwise
		 */
		public boolean isValid() {
			throw new UnsupportedOperationException();
		}

	}

	/**
	 * White class represents an empty PM Quadtree leaf node.
	 */
	public class White extends Node {
		/**
		 * Constructs and initializes an empty PM Quadtree leaf node.
		 */
		public White() {
			super(WHITE);
		}

		/**
		 * Adds a road to this PM Quadtree node.
		 * 
		 * @param g
		 *            road to be added
		 * @param origin
		 *            origin of the rectangular bounds of this node
		 * @param width
		 *            width of the rectangular bounds of this node
		 * @param height
		 *            height of the rectangular bounds of this node
		 * @return this node after the city has been added
		 * @throws RoadViolatesPMRulesThrowable
		 * @throws PortalViolatesPMRulesThrowable
		 * @throws InvalidPartitionThrowable
		 *             if the map if partitioned too deeply
		 * @throws IntersectingRoadsThrowable
		 *             if this road intersects with another road
		 */
		public Node add(final Geometry g, final Point2D.Float origin,
				final int width, final int height)
				throws RoadViolatesPMRulesThrowable,
				PortalViolatesPMRulesThrowable {
			final Black blackNode = new Black();
			return blackNode.add(g, origin, width, height);
		}

		public Node remove(final Geometry g, final Point2D.Float origin,
				final int width, final int height) {
			return this;
		}

		/**
		 * Returns if this node follows the rules of the PM Quadtree.
		 * 
		 * @return <code>true</code> if the node follows the rules of the PM
		 *         Quadtree; <code>false</code> otherwise
		 */
		public boolean isValid() {
			return true;
		}

		public String toString() {
			return "white";
		}
	}

	/**
	 * Black class represents a non-empty PM Quadtree leaf node. Black nodes are
	 * capable of storing both cities (points) and roads (line segments).
	 * <p>
	 * Each black node stores cities and roads into its own sorted geometry
	 * list.
	 * <p>
	 * Black nodes are split into a gray node if they do not satisfy the rules
	 * of the PM Quadtree.
	 */
	public class Black extends Node {

		/** list of cities and roads contained within black node */
		final protected LinkedList<Geometry> geometry;

		/** number of cities contained within this black node */
		protected int numPoints;

		/**
		 * Constructs and initializes a non-empty PM Quadtree leaf node.
		 */
		public Black() {
			super(BLACK);
			geometry = new LinkedList<Geometry>();
			numPoints = 0;
		}

		/**
		 * Gets a linked list of the cities and roads contained by this black
		 * node.
		 * 
		 * @return list of cities and roads contained within this black node
		 */
		public LinkedList<Geometry> getGeometry() {
			return geometry;
		}

		/**
		 * Gets the index of the road in this black node's geometry list.
		 * 
		 * @param g
		 *            road to be searched for in the sorted geometry list
		 * @return index of the search key, if it is contained in the list;
		 *         otherwise, (-(insertion point) - 1)
		 */
		private int getIndex(final Geometry g) {
			return Collections.binarySearch(geometry, g);
		}

		/**
		 * Adds a road to this black node. After insertion, if the node becomes
		 * invalid, it will be split into a Gray node.
		 * 
		 * @throws RoadViolatesPMRulesThrowable
		 * @throws PortalViolatesPMRulesThrowable
		 */
		public Node add(final Geometry g, final Point2D.Float origin,
				final int width, final int height)
				throws RoadViolatesPMRulesThrowable,
				PortalViolatesPMRulesThrowable {
			if (g.isRoad()) {
				// g is a road
				Road r = (Road) g;
				/* create region rectangle */
				final Rectangle2D.Float rect = new Rectangle2D.Float(origin.x,
						origin.y, width, height);

				/* check if start point intersects with region */
				if (Inclusive2DIntersectionVerifier.intersects(r.getStart()
						.toPoint2D(), rect)) {
					addGeometryToList(r.getStart());
				}

				/* check if end point intersects with region */
				if (Inclusive2DIntersectionVerifier.intersects(r.getEnd()
						.toPoint2D(), rect)) {
					addGeometryToList(r.getEnd());
				}

			}

			/* add the road or isolated city to the geometry list */
			addGeometryToList(g);

			// ][
			/* check if this node is valid */
			if (isValid()) {
				/* valid so return this black node */
				return this;
			} else {
				/* invalid so partition into a Gray node */
				Node newGray = partition(origin, width, height);
				if (!newGray.isValid()) {
					PMQuadtree.this.remove(g);

					if (g.isRoad()) {
						throw new RoadViolatesPMRulesThrowable();
					} else {
						throw new PortalViolatesPMRulesThrowable();
					}

				} else {
					return newGray;
				}

			}
		}

		/**
		 * Removes a geometry from the node
		 */
		public Node remove(final Geometry g, final Point2D.Float origin,
				final int width, final int height) {
			// decrements point count
			if (g.isCity())
				numPoints--;

			// removes geometry from list
			geometry.remove(g);

			// if the black node is now a white node
			if (geometry.size() == 0)
				return white;

			return this;
		}

		/**
		 * Adds a road to this node's geometry list.
		 * 
		 * @param g
		 *            road to be added
		 */
		private boolean addGeometryToList(final Geometry g) {
			/* search for the non-existent item */
			final int index = getIndex(g);

			/* add the non-existent item to the list */
			if (index < 0) {
				geometry.add(-index - 1, g);

				if (g.isCity()) {
					// g is a city
					numPoints++;
				}
				return true;
			}
			return false;
		}

		/**
		 * Returns if this node follows the rules of the PM Quadtree.
		 * 
		 * @return <code>true</code> if the node follows the rules of the PM
		 *         Quadtree; <code>false</code> otherwise
		 */
		public boolean isValid() {
			return validator.valid(this);
		}

		/**
		 * Gets the number of cities contained in this black node.
		 * 
		 * @return number of cities contained in this black node
		 */
		public int getNumPoints() {
			return numPoints;
		}

		/**
		 * Partitions an invalid back node into a gray node and adds this black
		 * node's roads to the new gray node.
		 * 
		 * @param origin
		 *            origin of the rectangular bounds of this node
		 * @param width
		 *            width of the rectangular bounds of this node
		 * @param height
		 *            height of the rectangular bounds of this node
		 * @return the new gray node
		 * @throws PortalViolatesPMRulesThrowable
		 * @throws RoadViolatesPMRulesThrowable
		 * @throws InvalidPartitionThrowable
		 *             if the quadtree was partitioned too deeply
		 * @throws IntersectingRoadsThrowable
		 *             if two roads intersect
		 */
		private Node partition(final Point2D.Float origin, final int width,
				final int height) throws RoadViolatesPMRulesThrowable,
				PortalViolatesPMRulesThrowable {
			// ][
			/* create new gray node */
			Node gray = new Gray(origin, width, height);

			// add isolated cities only; endpoints of roads are added in
			// recursive calls
			// to black.add()
			for (int i = 0; i < numPoints; i++) {
				final Geometry g = geometry.get(i);
				if (isIsolatedCity(g)) {
					gray = gray.add(g, origin, width, height);
				}
			}
			// add roads
			for (int i = numPoints; i < geometry.size(); i++) {
				final Geometry g = geometry.get(i);
				gray = gray.add(g, origin, width, height);
			}
			return gray;
		}

		/**
		 * Returns a string representing this black node and its road list.
		 * 
		 * @return a string representing this black node and its road list
		 */
		public String toString() {
			return "black: " + geometry.toString();
		}

		/**
		 * Returns if this black node contains a city.
		 * 
		 * @return if this black node contains a city
		 */
		public boolean containsCity() {
			return (numPoints > 0);
		}

		/**
		 * @return true if this black node contains at least a road
		 */
		public boolean containsRoad() {
			return (geometry.size() - numPoints) > 0;
		}

		/**
		 * If this black node contains a city, returns the city contained within
		 * this black node. Else returns <code>null</code>.
		 * 
		 * @return the city if it exists, else <code>null</code>
		 */
		public City getCity() {
			final Geometry g = geometry.getFirst();
			return g.isCity() ? (City) g : null;
		}
	}

	/**
	 * Gray class represents an internal PM Quadtree node.
	 */
	public class Gray extends Node {
		/** this gray node's 4 child nodes */
		final protected Node[] children;

		/** regions representing this gray node's 4 child nodes */
		final protected Rectangle2D.Float[] regions;

		/** origin of the rectangular bounds of this node */
		final protected Point2D.Float origin;

		/** the origin of rectangular bounds of each of the node's child nodes */
		final protected Point2D.Float[] origins;

		/** half the width of the rectangular bounds of this node */
		final protected int halfWidth;

		/** half the height of the rectangular bounds of this node */
		final protected int halfHeight;

		/**
		 * Constructs and initializes an internal PM Quadtree node.
		 * 
		 * @param origin
		 *            origin of the rectangular bounds of this node
		 * @param width
		 *            width of the rectangular bounds of this node
		 * @param height
		 *            height of the rectangular bounds of this node
		 */
		public Gray(final Point2D.Float origin, final int width,
				final int height) {
			super(GRAY);

			/* set this node's origin */
			this.origin = origin;

			/* initialize the children as white nodes */
			children = new Node[4];
			for (int i = 0; i < 4; i++) {
				children[i] = white;
			}

			/* get half the width and half the height */
			halfWidth = width >> 1;
			halfHeight = height >> 1;

			/* initialize the child origins */
			origins = new Point2D.Float[4];
			origins[0] = new Point2D.Float(origin.x, origin.y + halfHeight);
			origins[1] = new Point2D.Float(origin.x + halfWidth, origin.y
					+ halfHeight);
			origins[2] = new Point2D.Float(origin.x, origin.y);
			origins[3] = new Point2D.Float(origin.x + halfWidth, origin.y);

			/* initialize the child regions */
			regions = new Rectangle2D.Float[4];
			for (int i = 0; i < 4; i++) {
				regions[i] = new Rectangle2D.Float(origins[i].x, origins[i].y,
						halfWidth, halfHeight);
			}
		}

		/**
		 * Adds a road to this PM Quadtree node.
		 * 
		 * @param g
		 *            road to be added
		 * @param origin
		 *            origin of the rectangular bounds of this node
		 * @param width
		 *            width of the rectangular bounds of this node
		 * @param height
		 *            height of the rectangular bounds of this node
		 * @return this node after the city has been added
		 * @throws PortalViolatesPMRulesThrowable
		 * @throws RoadViolatesPMRulesThrowable
		 * @throws InvalidPartitionThrowable
		 *             if the map if partitioned too deeply
		 * @throws IntersectingRoadsThrowable
		 *             if this road intersects with another road
		 */
		public Node add(final Geometry g, final Point2D.Float origin,
				final int width, final int height)
				throws RoadViolatesPMRulesThrowable,
				PortalViolatesPMRulesThrowable {

			for (int i = 0; i < 4; i++) {
				if (g.isRoad()
						&& Inclusive2DIntersectionVerifier.intersects(
								((Road) g).toLine2D(), regions[i])
						|| g.isCity()
						&& Inclusive2DIntersectionVerifier.intersects(
								((City) g).toPoint2D(), regions[i])) {
					children[i] = children[i].add(g, origins[i], halfWidth,
							halfHeight);
				}
			}
			return this;
		}

		public Node remove(final Geometry g, final Point2D.Float origin,
				final int width, final int height) {

			// Removes the geometry from children
			if (g.isCity()) {
				for (int i = 0; i < 4; i++) {
					if (g.isRoad()
							&& Inclusive2DIntersectionVerifier.intersects(
									((Road) g).toLine2D(), regions[i])
							|| g.isCity()
							&& Inclusive2DIntersectionVerifier.intersects(
									((City) g).toPoint2D(), regions[i])) {
						children[i] = children[i].remove(g, origins[i],
								halfWidth, halfHeight);
					}
				}
			}

			int whiteCount = 0;
			// Count White children
			for (int i = 0; i < 4; i++) {
				if (children[i].type == Node.WHITE)
					whiteCount++;
			}

			// Check all white
			if (whiteCount == 4) {
				return white;
			}

			// Check if one is black
			if (whiteCount == 3) {
				for (int i = 0; i < 4; i++) {
					if (children[i].type == Node.BLACK)
						return children[i];
				}
			}

			int grayCount = 0;
			// Count Gray Children
			for (int i = 0; i < 4; i++) {
				if (children[i].type == Node.GRAY)
					grayCount++;
			}

			if (grayCount != 4) {
				Black b = new Black();
				b = addAllGeometries(b, this);
				if (b.isValid()) {
					return b;
				}
			}
			return this;

		}

		/**
		 * Returns if this node follows the rules of the PM Quadtree.
		 * 
		 * @return <code>true</code> if the node follows the rules of the PM
		 *         Quadtree; <code>false</code> otherwise
		 */
		public boolean isValid() {
			return validator.valid(this) && children[0].isValid()
					&& children[1].isValid() && children[2].isValid()
					&& children[3].isValid();
		}

		public String toString() {
			StringBuilder grayStringBuilder = new StringBuilder("gray:");
			for (Node child : children) {
				grayStringBuilder.append("\n\t");
				grayStringBuilder.append(child.toString());
			}
			return grayStringBuilder.toString();
		}

		/**
		 * Gets the child node of this node according to which quadrant it falls
		 * in.
		 * 
		 * @param quadrant
		 *            quadrant number (top left is 0, top right is 1, bottom
		 *            left is 2, bottom right is 3)
		 * @return child node
		 */
		public Node getChild(final int quadrant) {
			if (quadrant < 0 || quadrant > 3) {
				throw new IllegalArgumentException();
			} else {
				return children[quadrant];
			}
		}

		/**
		 * Gets the rectangular region for the specified child node of this
		 * internal node.
		 * 
		 * @param quadrant
		 *            quadrant that child lies within
		 * @return rectangular region for this child node
		 */
		public Rectangle2D.Float getChildRegion(int quadrant) {
			if (quadrant < 0 || quadrant > 3) {
				throw new IllegalArgumentException();
			} else {
				return regions[quadrant];
			}
		}

		/**
		 * Gets the center X coordinate of this node's rectangular bounds.
		 * 
		 * @return center X coordinate of this node's rectangular bounds
		 */
		public int getCenterX() {
			return (int) origin.x + halfWidth;
		}

		/**
		 * Gets the center Y coordinate of this node's rectangular bounds.
		 * 
		 * @return center Y coordinate of this node's rectangular bounds
		 */
		public int getCenterY() {
			return (int) origin.y + halfHeight;
		}

		/**
		 * Gets half the width of this internal node.
		 * 
		 * @return half the width of this internal node
		 */
		public int getHalfWidth() {
			return halfWidth;
		}

		/**
		 * Gets half the height of this internal node.
		 * 
		 * @return half the height of this internal node
		 */
		public int getHalfHeight() {
			return halfHeight;
		}
	}

	public PMQuadtree(final Validator validator, final int spatialWidth,
			final int spatialHeight, final int order) {
		if (order != 1 && order != 3) {
			throw new IllegalArgumentException("order must be one of: {1,3}");
		}

		root = white;
		this.validator = validator;
		this.spatialWidth = spatialWidth;
		this.spatialHeight = spatialHeight;
		spatialOrigin = new Point2D.Float(0.0f, 0.0f);
		allRoads = new TreeSet<Road>(new RoadNameComparator());
		numRoadsForCity = new HashMap<String, Integer>();
		this.order = order;
	}

	public Node getRoot() {
		return root;
	}

	public void addRoad(final Road g) throws RoadAlreadyExistsThrowable,
			RoadOutOfBoundsThrowable,
			RoadIntersectsAnotherRoadThrowable, RoadViolatesPMRulesThrowable,
			PortalViolatesPMRulesThrowable {
//		if (isIsolatedCity(g.getStart()) || isIsolatedCity(g.getEnd())) {
//			throw new IsolatedCityAlreadyExistsThrowable();
//		}

		final Road g2 = new Road(g.getEnd(), g.getStart());

		if (allRoads.contains(g) || allRoads.contains(g2)) {
			throw new RoadAlreadyExistsThrowable();
		}

		// Checks if the road being inserted will intersect any of the existing
		// roads
		for (Road r : allRoads) {
			if (Inclusive2DIntersectionVerifier.intersects(g.toLine2D(),
					r.toLine2D())) {
				// r's cities both intersect g
				if (Inclusive2DIntersectionVerifier.intersects(r.getStart()
						.toPoint2D(), g.toLine2D())
						&& Inclusive2DIntersectionVerifier.intersects(r
								.getEnd().toPoint2D(), g.toLine2D()))
					throw new RoadIntersectsAnotherRoadThrowable();

				// g's cities both intersect r
				if (Inclusive2DIntersectionVerifier.intersects(g.getStart()
						.toPoint2D(), r.toLine2D())
						&& Inclusive2DIntersectionVerifier.intersects(g
								.getEnd().toPoint2D(), r.toLine2D()))
					throw new RoadIntersectsAnotherRoadThrowable();

				if (!r.contains(g.getStart()) && !r.contains(g.getEnd()))
					throw new RoadIntersectsAnotherRoadThrowable();
			}
		}

		Rectangle2D.Float world = new Rectangle2D.Float(spatialOrigin.x,
				spatialOrigin.y, spatialWidth, spatialHeight);
//		if (!Inclusive2DIntersectionVerifier.intersects(g.toLine2D(), world)) {
//			throw new OutOfBoundsThrowable();
//		}

		root = root.add(g, spatialOrigin, spatialWidth, spatialHeight);
		allRoads.add(g);
		if (Inclusive2DIntersectionVerifier.intersects(
				g.getStart().toPoint2D(), world)
				&& Inclusive2DIntersectionVerifier.intersects(g.getEnd()
						.toPoint2D(), world)) {
			increaseNumRoadsMap(g.getStart().getName());
			increaseNumRoadsMap(g.getEnd().getName());

		}

		// TODO in converted test part2.dan.shortestPaths.xml a valid road has
		// both endpoints in the spatial map while in part2.dan.nearestcity2.xml
		// a road can be valid with only one point on the spatial map (this
		// causes problems with the adjacency list)
		// else if
		// (Inclusive2DIntersectionVerifier.intersects(g.getStart().toPoint2D(),
		// world)){
		// increaseNumRoadsMap(g.getStart().getName());
		// } else if
		// (Inclusive2DIntersectionVerifier.intersects(g.getEnd().toPoint2D(),
		// world)){
		// increaseNumRoadsMap(g.getEnd().getName());
		// }
		// if
		// (Inclusive2DIntersectionVerifier.intersects(g.getEnd().toPoint2D(),
		// world)) {
		// increaseNumRoadsMap(g.getEnd().getName());
		// }

	}

	/**
	 * 
	 * @param c
	 * @throws RoadViolatesPMRulesThrowable 
	 * @throws PortalViolatesPMRules
	 */
	// TODO redundantPortal, duplicatePortalName, duplicatePortalCoordinates,
	// portalOutOfBounds, portalIntersectsRoad,portalViolatesPMRules
	public void addPortal(final City c) throws RedundantPortalThrowable,
			DuplicatePortalNameThrowable, DuplicatePortalCoordinatesThrowable,
			PortalIntersectsRoadThrowable, PortalOutOfBoundsThrowable,
			PortalViolatesPMRulesThrowable, RoadViolatesPMRulesThrowable {
		
		// Portal out of Bounds
		if (!Inclusive2DIntersectionVerifier.intersects(c.toPoint2D(),
				new Rectangle2D.Float(spatialOrigin.x, spatialOrigin.y,
						spatialWidth, spatialHeight))) {
			throw new PortalOutOfBoundsThrowable();
		}

		// Already a portal on this level
		if (numIsolatedCities > 0){
			throw new RedundantPortalThrowable();
		}
		
		// Check City Names
		if (numRoadsForCity.get(c.getName()) != null){
			throw new DuplicatePortalNameThrowable();
		}
		
		// Check if Portal Intersects any Geometries already mapped
		for (Road r : allRoads) {
			// Portal Intersects Start City
			if (Inclusive2DIntersectionVerifier.intersects(c.toPoint2D(),
					r.getStart().toPoint2D())){
				throw new DuplicatePortalCoordinatesThrowable();
			}
			// Portal Intersects End City
			if (Inclusive2DIntersectionVerifier.intersects(c.toPoint2D(),
					r.getEnd().toPoint2D())){
				throw new DuplicatePortalCoordinatesThrowable();
			}
			// Portal Intersects Road
			if (Inclusive2DIntersectionVerifier.intersects(c.toPoint2D(),
					r.toLine2D())){
				throw new PortalIntersectsRoadThrowable();
			}
		}
		
		numIsolatedCities++;
		numRoadsForCity.put(c.getName(), 0);

		root = root.add(c, spatialOrigin, spatialWidth, spatialHeight);		
	}

	public void addIsolatedCity(final City c)
			throws IsolatedCityAlreadyExistsThrowable,
			RoadAlreadyExistsThrowable, OutOfBoundsThrowable,
			RoadViolatesPMRulesThrowable, PortalViolatesPMRulesThrowable {
		if (numRoadsForCity.get(c.getName()) != null) {
			if (numRoadsForCity.get(c.getName()) > 0) {
				throw new RoadAlreadyExistsThrowable();
			} else {
				throw new IsolatedCityAlreadyExistsThrowable();
			}
		}

		if (!Inclusive2DIntersectionVerifier.intersects(c.toPoint2D(),
				new Rectangle2D.Float(spatialOrigin.x, spatialOrigin.y,
						spatialWidth, spatialHeight))) {
			throw new OutOfBoundsThrowable();
		}
		
		numIsolatedCities++;
		numRoadsForCity.put(c.getName(), 0);

		root = root.add(c, spatialOrigin, spatialWidth, spatialHeight);
	}

	private void increaseNumRoadsMap(final String name) {
		Integer numRoads = numRoadsForCity.get(name);
		if (numRoads != null) {
			numRoads++;
			numRoadsForCity.put(name, numRoads);
		} else {
			numRoadsForCity.put(name, 1);
		}
	}

	public void clear() {
		root = white;
		allRoads.clear();
		numRoadsForCity.clear();
		numIsolatedCities = 0;
	}

	public boolean isEmpty() {
		return (root == white);
	}

	public boolean containsCity(final String name) {
		final Integer numRoads = numRoadsForCity.get(name);
		return (numRoads != null);
	}

	public boolean containsRoad(final Road road) {
		return allRoads.contains(road);
	}

	public int getOrder() {
		return order;
	}

	public int getNumCities() {
		return numRoadsForCity.keySet().size();
	}

	public int getNumIsolatedCities() {
		return numIsolatedCities;
	}

	public int getNumRoads() {
		return allRoads.size();
	}

	private Black addAllGeometries(Black black, Node node) {

		if (node.type == Node.BLACK) {
			Black b = (Black) node;
			for (Geometry g : b.geometry) {
				if (!(black.geometry.contains(g))) {
					black.addGeometryToList(g);
				}
			}

		} else if (node.type == Node.GRAY) {
			Gray g = (Gray) node;
			for (Node n : g.children) {
				black = addAllGeometries(black, n);
			}
		}

		return black;
	}

	public boolean isIsolatedCity(Geometry g) {
		if (!g.isCity()) {
			return false;
		}
		City c = (City) g;
		Integer n = numRoadsForCity.get(c.getName());
		if (n == null || n > 0) {
			return false;
		}
		return true;
	}

	public void remove(Geometry g) {
		root.remove(g, spatialOrigin, spatialWidth, spatialHeight);
	}
}
