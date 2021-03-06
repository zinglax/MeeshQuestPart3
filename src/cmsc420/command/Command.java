package cmsc420.command;

import java.awt.Color;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cmsc420.dijkstra.Dijkstranator;
import cmsc420.dijkstra.Path;
import cmsc420.drawing.CanvasPlus;
import cmsc420.geom.Circle2D;
import cmsc420.geom.Inclusive2DIntersectionVerifier;
import cmsc420.geom.Shape2DDistanceCalculator;
import cmsc420.geometry.City;
import cmsc420.geometry.CityLocationComparator;
import cmsc420.geometry.Geometry;
import cmsc420.geometry.Road;
import cmsc420.geometry.RoadAdjacencyList;
import cmsc420.levels.Leveler;
import cmsc420.pmquadtree.DuplicateCityCoordinateThrowable;
import cmsc420.pmquadtree.DuplicateCityNameThrowable;
import cmsc420.pmquadtree.DuplicatePortalCoordinatesThrowable;
import cmsc420.pmquadtree.DuplicatePortalNameThrowable;
import cmsc420.pmquadtree.EndPointDoesNotExistThrowable;
import cmsc420.pmquadtree.IsolatedCityAlreadyExistsThrowable;
import cmsc420.pmquadtree.OutOfBoundsThrowable;
import cmsc420.pmquadtree.PM3Quadtree;
import cmsc420.pmquadtree.PMQuadtree;
import cmsc420.pmquadtree.PortalIntersectsRoadThrowable;
import cmsc420.pmquadtree.PortalOutOfBoundsThrowable;
import cmsc420.pmquadtree.PortalViolatesPMRulesThrowable;
import cmsc420.pmquadtree.RedundantPortalThrowable;
import cmsc420.pmquadtree.RoadAlreadyExistsThrowable;
import cmsc420.pmquadtree.PMQuadtree.Black;
import cmsc420.pmquadtree.PMQuadtree.Gray;
import cmsc420.pmquadtree.PMQuadtree.Node;
import cmsc420.pmquadtree.RoadIntersectsAnotherRoadThrowable;
import cmsc420.pmquadtree.RoadNotMappedThrowable;
import cmsc420.pmquadtree.RoadNotOnOneLevelThrowable;
import cmsc420.pmquadtree.RoadOutOfBoundsThrowable;
import cmsc420.pmquadtree.RoadViolatesPMRulesThrowable;
import cmsc420.pmquadtree.StartEqualsEndThrowable;
import cmsc420.pmquadtree.StartOrEndIsPortalThrowable;
import cmsc420.pmquadtree.StartPointDoesNotExistThrowable;
import cmsc420.sortedmap.GuardedAvlGTree;
import cmsc420.sortedmap.StringComparator;
import cmsc420.xml.XmlUtility;

/**
 * Processes each command in the MeeshQuest program. Takes in an XML command
 * node, processes the node, and outputs the results.
 */
public class Command {

	// Levels
	protected Leveler l;

	/** output DOM Document tree */
	protected Document results;

	/** root node of results document */
	protected Element resultsNode;

	/**
	 * stores created cities sorted by their names (used with listCities
	 * command)
	 */
	// protected GuardedAvlGTree<String, City> citiesByName;

	/**
	 * stores created cities sorted by their locations (used with listCities
	 * command)
	 */
	// protected final TreeSet<City> citiesByLocation = new TreeSet<City>(
	// new CityLocationComparator());

	// private final RoadAdjacencyList roads = new RoadAdjacencyList();

	/** stores mapped cities in a spatial data structure */
	protected PMQuadtree pmQuadtree;

	/** order of the PM Quadtree */
	// protected int pmOrder;

	/** spatial width of the PM Quadtree */
	// protected int spatialWidth;

	/** spatial height of the PM Quadtree */
	// protected int spatialHeight;

	/**
	 * Set the DOM Document tree to send the results of processed commands to.
	 * Creates the root results node.
	 * 
	 * @param results
	 *            DOM Document tree
	 */
	public void setResults(Document results) {
		this.results = results;
		resultsNode = results.createElement("results");
		results.appendChild(resultsNode);
	}

	/**
	 * Creates a command result element. Initializes the command name.
	 * 
	 * @param node
	 *            the command node to be processed
	 * @return the results node for the command
	 */
	private Element getCommandNode(final Element node) {
		final Element commandNode = results.createElement("command");
		commandNode.setAttribute("name", node.getNodeName());

		if (node.hasAttribute("id")) {
			commandNode.setAttribute("id", node.getAttribute("id"));
		}
		return commandNode;
	}

	/**
	 * Processes an integer attribute for a command. Appends the parameter to
	 * the parameters node of the results. Should not throw a number format
	 * exception if the attribute has been defined to be an integer in the
	 * schema and the XML has been validated beforehand.
	 * 
	 * @param commandNode
	 *            node containing information about the command
	 * @param attributeName
	 *            integer attribute to be processed
	 * @param parametersNode
	 *            node to append parameter information to
	 * @return integer attribute value
	 */
	private int processIntegerAttribute(final Element commandNode,
			final String attributeName, final Element parametersNode) {
		final String value = commandNode.getAttribute(attributeName);

		if (parametersNode != null) {
			/* add the parameters to results */
			final Element attributeNode = results.createElement(attributeName);
			attributeNode.setAttribute("value", value);
			parametersNode.appendChild(attributeNode);
		}

		/* return the integer value */
		return Integer.parseInt(value);
	}

	/**
	 * Processes a string attribute for a command. Appends the parameter to the
	 * parameters node of the results.
	 * 
	 * @param commandNode
	 *            node containing information about the command
	 * @param attributeName
	 *            string attribute to be processed
	 * @param parametersNode
	 *            node to append parameter information to
	 * @return string attribute value
	 */
	private String processStringAttribute(final Element commandNode,
			final String attributeName, final Element parametersNode) {
		final String value = commandNode.getAttribute(attributeName);

		if (parametersNode != null) {
			/* add parameters to results */
			final Element attributeNode = results.createElement(attributeName);
			attributeNode.setAttribute("value", value);
			parametersNode.appendChild(attributeNode);
		}

		/* return the string value */
		return value;
	}

	/**
	 * Reports that the requested command could not be performed because of an
	 * error. Appends information about the error to the results.
	 * 
	 * @param type
	 *            type of error that occurred
	 * @param command
	 *            command node being processed
	 * @param parameters
	 *            parameters of command
	 */
	private void addErrorNode(final String type, final Element command,
			final Element parameters) {
		final Element error = results.createElement("error");
		error.setAttribute("type", type);
		error.appendChild(command);
		error.appendChild(parameters);
		resultsNode.appendChild(error);
	}

	/**
	 * Reports that a command was successfully performed. Appends the report to
	 * the results.
	 * 
	 * @param command
	 *            command not being processed
	 * @param parameters
	 *            parameters used by the command
	 * @param output
	 *            any details to be reported about the command processed
	 */
	private Element addSuccessNode(final Element command,
			final Element parameters, final Element output) {
		final Element success = results.createElement("success");
		success.appendChild(command);
		success.appendChild(parameters);
		success.appendChild(output);
		resultsNode.appendChild(success);
		return success;
	}

	/**
	 * Processes the commands node (root of all commands). Gets the spatial
	 * width and height of the map and send the data to the appropriate data
	 * structures.
	 * 
	 * @param node
	 *            commands node to be processed
	 */
	public void processCommands(final Element node) {
		int width = Integer.parseInt(node.getAttribute("spatialWidth"));
		int height = Integer.parseInt(node.getAttribute("spatialHeight"));
		int pmorder = Integer.parseInt(node.getAttribute("pmOrder"));
		int avlorder = Integer.parseInt(node.getAttribute("g"));

		// TODO Create a PM1 tree & check if command has errors
		l = new Leveler(width, height, pmorder, avlorder);

	}

	/**
	 * Removes a city with the specified name from data dictionary and the
	 * adjacency list. The criteria for success here is simply that the city
	 * exists.
	 * 
	 * A city may be deleted even if it exists in the quadtree. Any roads
	 * associated with the city (i.e., have the city as its start or end) must
	 * also be unmapped. Each such unmapped road must be printed in its own
	 * roadUnmapped tag like the one below. Sort tags asciibetically by start,
	 * tie breaking by end, as usual. Possible side effect: other cities may be
	 * unmapped if the last road to them is removed. These, however, won’t be
	 * printed.
	 */

	public void processDeleteCity(final Element node) {
		final Element commandNode = getCommandNode(node);
		final Element parametersNode = results.createElement("parameters");
		final Element outputNode = results.createElement("output");

		/* extract attribute values from command */
		final String city_name = processStringAttribute(node, "name",
				parametersNode);

		System.out.println(l.citiesByName.keySet());
		System.out.println(l.citiesByName.keySet().contains(city_name));
		System.out.println(l.citiesByName.containsKey(city_name));

		if (l.citiesByName.containsKey(city_name)) {
			// Gets the city
			City c = l.citiesByName.get(city_name);

			if (l.mappedCities.contains(c)) {
				addCityNode(outputNode, "cityUnmapped", c);
			}

			// Removes the city from the quadtree
			l.levels.get(c.getZ()).remove(c);

			// deletes the city and gets the roads that were connected to it
			TreeSet<Road> roadset = l.roads.deleteCity(c);

			// Cities that have become isolated due to the removal of subsequent
			// roads
			TreeSet<City> otherCities = new TreeSet<City>();

			// Removes all of the roads for the city
			for (Road r : roadset) {
				l.levels.get(c.getZ()).remove(r);
				addRoadNode(outputNode, "roadUnmapped", r);
				City other = r.getOtherCity(c.getName());
				otherCities.add(other);
			}

			// Removes the city from cities by name
			l.citiesByName.remove(c.getName());

			// Removes the city from cities by location
			l.citiesByLocation.remove(c);

			// Removes the city from the mapped cities
			l.mappedCities.remove(c);

			addSuccessNode(commandNode, parametersNode, outputNode);

			// TreeSet<City> citiesToRemove = new TreeSet<City>();

			// Removes any isolated cities created from prior removal
			for (City city : l.citiesByLocation) {
				if (l.levels.get(city.getZ()) != null
						&& otherCities.contains(city)) {
					// l.roads.getRoadSet(city)

					if (l.roads.getRoadSet(city).size() == 0
							&& !city.isPortal()) {
						l.levels.get(city.getZ()).remove(city);
						// citiesToRemove.add(city);
						// l.citiesByName.remove(city.getName());
						l.mappedCities.remove(city);
					}
				}
			}

			// l.citiesByLocation.removeAll(citiesToRemove);

		} else {
			addErrorNode("cityDoesNotExist", commandNode, parametersNode);
		}
	}

	/**
	 * Processes a createCity command. Creates a city in the dictionary (Note:
	 * does not map the city). An error occurs if a city with that name or
	 * location is already in the dictionary.
	 * 
	 * @param node
	 *            createCity node to be processed
	 */
	public void processCreateCity(final Element node) {
		final Element commandNode = getCommandNode(node);
		final Element parametersNode = results.createElement("parameters");
		final String name = processStringAttribute(node, "name", parametersNode);
		final int x = processIntegerAttribute(node, "x", parametersNode);
		final int y = processIntegerAttribute(node, "y", parametersNode);
		final int z = processIntegerAttribute(node, "z", parametersNode);
		final int radius = processIntegerAttribute(node, "radius",
				parametersNode);
		final String color = processStringAttribute(node, "color",
				parametersNode);

		/* create the city */
		final City city = new City(name, x, y, z, radius, color);

		try {
			final Element outputNode = results.createElement("output");
			l.createCity(city);
			addSuccessNode(commandNode, parametersNode, outputNode);
		} catch (DuplicateCityNameThrowable e) {
			addErrorNode("duplicateCityName", commandNode, parametersNode);
		} catch (DuplicateCityCoordinateThrowable e) {
			addErrorNode("duplicateCityCoordinates", commandNode,
					parametersNode);
		}

	}

	/**
	 * Clears all the data structures do there are not cities or roads in
	 * existence in the dictionary or on the map.
	 * 
	 * @param node
	 *            clearAll node to be processed
	 */
	public void processClearAll(final Element node) {
		final Element commandNode = getCommandNode(node);
		final Element parametersNode = results.createElement("parameters");
		final Element outputNode = results.createElement("output");

		/* clear data structures */
		l.clearall();

		/* clear canvas */
		// canvas.clear();
		/* add a rectangle to show where the bounds of the map are located */
		// canvas.addRectangle(0, 0, spatialWidth, spatialHeight, Color.BLACK,
		// false);
		/* add success node to results */
		addSuccessNode(commandNode, parametersNode, outputNode);
	}

	/**
	 * Lists all the cities, either by name or by location.
	 * 
	 * @param node
	 *            listCities node to be processed
	 */
	public void processListCities(final Element node) {
		final Element commandNode = getCommandNode(node);
		final Element parametersNode = results.createElement("parameters");
		final String sortBy = processStringAttribute(node, "sortBy",
				parametersNode);

		if (l.citiesByName.isEmpty()) {
			addErrorNode("noCitiesToList", commandNode, parametersNode);
		} else {
			final Element outputNode = results.createElement("output");
			final Element cityListNode = results.createElement("cityList");

			Collection<City> cityCollection = null;
			if (sortBy.equals("name")) {
				List<City> cities = new ArrayList<City>(
						l.citiesByLocation.size());
				for (City c : l.citiesByLocation)
					cities.add(c);
				Collections.sort(cities, new Comparator<City>() {

					// @Override
					public int compare(City arg0, City arg1) {
						return arg0.getName().compareTo(arg1.getName());
					}
				});
				cityCollection = cities;
			} else if (sortBy.equals("coordinate")) {
				cityCollection = l.citiesByLocation;
			} else {
				/* XML validator failed */
				System.exit(-1);
			}

			for (City c : cityCollection) {
				addCityNode(cityListNode, c);
			}
			outputNode.appendChild(cityListNode);

			/* add success node to results */
			addSuccessNode(commandNode, parametersNode, outputNode);
		}
	}

	/**
	 * Creates a city node containing information about a city. Appends the city
	 * node to the passed in node.
	 * 
	 * @param node
	 *            node which the city node will be appended to
	 * @param cityNodeName
	 *            name of city node
	 * @param city
	 *            city which the city node will describe
	 */
	private void addCityNode(final Element node, final String cityNodeName,
			final City city) {
		final Element cityNode = results.createElement(cityNodeName);
		cityNode.setAttribute("name", city.getName());
		cityNode.setAttribute("x", Integer.toString((int) city.getX()));
		cityNode.setAttribute("y", Integer.toString((int) city.getY()));
		cityNode.setAttribute("z", Integer.toString((int) city.getZ()));
		cityNode.setAttribute("radius",
				Integer.toString((int) city.getRadius()));
		cityNode.setAttribute("color", city.getColor());
		node.appendChild(cityNode);
	}

	private void addCityNode(final Element node, final City city) {
		addCityNode(node, "city", city);
	}

	private void addIsolatedCityNode(final Element node, final City city) {
		addCityNode(node, "isolatedCity", city);
	}

	private void addRoadNode(final Element node, final Road road) {
		addRoadNode(node, "road", road);
	}

	private void addRoadNode(final Element node, final String roadNodeName,
			final Road road) {
		final Element roadNode = results.createElement(roadNodeName);
		roadNode.setAttribute("start", road.getStart().getName());
		roadNode.setAttribute("end", road.getEnd().getName());
		node.appendChild(roadNode);
	}

	private void addRoadNode(final Element node, final String roadNodeName,
			final String roadStart, final String roadEnd) {
		final Element roadNode = results.createElement(roadNodeName);
		roadNode.setAttribute("start", roadStart);
		roadNode.setAttribute("end", roadEnd);
		node.appendChild(roadNode);
	}

	/**
	 * Inserts a road between the two cities (vertices) named by the start and
	 * end attributes in PM quadtree. The obvious error conditions follow: the
	 * start or end city does not exist; the start and end are the same; the
	 * start and end are not on the same level; the road from start to end
	 * already exists. There are two additional error conditions you must
	 * handle. First, the new road should not intersect any road already mapped
	 * at a point other than a vertex of the road (this is a requirement of the
	 * PM quadtree family). Also, you must check that inserting this road into
	 * the PM quadtree will not cause the tree to be partitioned beyond a 1 x 1
	 * square.
	 * 
	 * @param node
	 */
	// TODO Check if roads intersect
	// TODO Check if the PM quadtree will be partitioned beyond a 1x1 square
	// TODO roadNotOnOneLevel, startOrEndIsPortal, roadViolatesPMRules
	public void processMapRoad(Element node) {
		final Element commandNode = getCommandNode(node);
		final Element parametersNode = results.createElement("parameters");

		final String start = processStringAttribute(node, "start",
				parametersNode);
		final String end = processStringAttribute(node, "end", parametersNode);

		final Element outputNode = results.createElement("output");

		Rectangle2D.Float world = new Rectangle2D.Float(l.spatialOrigin.x,
				l.spatialOrigin.y, l.spatialWidth, l.spatialHeight);

		try {
			// add to spatial structure
			// pmQuadtree.addRoad(new Road((City) l.citiesByName.get(start),
			// (City) l.citiesByName.get(end)));

			l.addRoad(start, end);

			//
			// if (Inclusive2DIntersectionVerifier.intersects(l.citiesByName
			// .get(start).toPoint2D(), world)
			// && Inclusive2DIntersectionVerifier.intersects(
			// l.citiesByName.get(end).toPoint2D(),
			// world)) {
			// // add to adjacency list
			// l.roads.addRoad((City) l.citiesByName.get(start),
			// (City) l.citiesByName.get(end));
			// }
			// create roadCreated element
			final Element roadCreatedNode = results
					.createElement("roadCreated");
			roadCreatedNode.setAttribute("start", start);
			roadCreatedNode.setAttribute("end", end);
			outputNode.appendChild(roadCreatedNode);
			// add success node to results
			addSuccessNode(commandNode, parametersNode, outputNode);

			l.mappedCities.add(l.citiesByName.get(start));
			l.mappedCities.add(l.citiesByName.get(end));

		} catch (RoadAlreadyExistsThrowable e) {
			addErrorNode("roadAlreadyMapped", commandNode, parametersNode);
		} catch (EndPointDoesNotExistThrowable e) {
			addErrorNode("endPointDoesNotExist", commandNode, parametersNode);
		} catch (StartPointDoesNotExistThrowable e) {
			addErrorNode("startPointDoesNotExist", commandNode, parametersNode);
		} catch (StartEqualsEndThrowable e) {
			addErrorNode("startEqualsEnd", commandNode, parametersNode);
		} catch (RoadNotOnOneLevelThrowable e) {
			addErrorNode("roadNotOnOneLevel", commandNode, parametersNode);
		} catch (StartOrEndIsPortalThrowable e) {
			addErrorNode("startOrEndIsPortal", commandNode, parametersNode);
		} catch (RoadOutOfBoundsThrowable e) {
			addErrorNode("roadOutOfBounds", commandNode, parametersNode);
		} catch (RoadIntersectsAnotherRoadThrowable e) {
			addErrorNode("roadIntersectsAnotherRoad", commandNode,
					parametersNode);
		} catch (RoadViolatesPMRulesThrowable e) {
			addErrorNode("roadViolatesPMRules", commandNode, parametersNode);
		} catch (PortalViolatesPMRulesThrowable e) {
			addErrorNode("portalViolatesPMRules", commandNode, parametersNode);
		}

	}

	/**
	 * Removes a road and its associated endpoints from the map unless the
	 * endpoint (city) is part of another mapped road.
	 * 
	 * @param node
	 */
	// TODO startPointDoesNotExist, endPointDoesNotExist, startEqualsEnd,
	// roadNotMapped
	public void processUnmapRoad(Element node) {
		final Element commandNode = getCommandNode(node);
		final Element parametersNode = results.createElement("parameters");
		final Element outputNode = results.createElement("output");
		final String startName = processStringAttribute(node, "start",
				parametersNode);
		final String endName = processStringAttribute(node, "end",
				parametersNode);

		try {

			Road r = l.unmapRoad(startName, endName);

			addRoadNode(outputNode, "roadDeleted", startName, endName);

			addSuccessNode(commandNode, parametersNode, outputNode);

		} catch (StartPointDoesNotExistThrowable e) {
			addErrorNode("startPointDoesNotExist", commandNode, parametersNode);
		} catch (EndPointDoesNotExistThrowable e) {
			addErrorNode("endPointDoesNotExist", commandNode, parametersNode);
		} catch (StartEqualsEndThrowable e) {
			addErrorNode("startEqualsEnd", commandNode, parametersNode);
		} catch (RoadNotMappedThrowable e) {
			addErrorNode("roadNotMapped", commandNode, parametersNode);
		}
	}

	/**
	 * Removes a portal from the map. Isolates its z plane (can’t path to it
	 * from any other z) until a new portal is mapped for that z.
	 * 
	 * @param node
	 */
	// TODO portalDoesNotExist
	public void processUnmapPortal(Element node) {
		final Element commandNode = getCommandNode(node);
		final Element parametersNode = results.createElement("parameters");
		final Element outputNode = results.createElement("output");

		/* extract attribute values from command */
		final String portal_name = processStringAttribute(node, "name",
				parametersNode);

		for (City c : l.portals.values()) {
			if (c != null && c.getName().equals(portal_name)) {
				l.levels.get(c.getZ()).removePortal(c);

				addSuccessNode(commandNode, parametersNode, outputNode);
				l.portals.remove(c.getZ());
				return;
			}
		}
		addErrorNode("portalDoesNotExist", commandNode, parametersNode);
	}

	public void processPrintAvlTree(Element node) {
		final Element commandNode = getCommandNode(node);
		final Element parametersNode = results.createElement("parameters");
		final Element outputNode = results.createElement("output");

		if (l.citiesByName.isEmpty()) {
			addErrorNode("emptyTree", commandNode, parametersNode);
		} else {
			outputNode.appendChild(l.citiesByName.createXml(outputNode));
			addSuccessNode(commandNode, parametersNode, outputNode);
		}
	}

	/**
	 * Creates a portal at the specified x, y, and z coordinates. Note that
	 * unlike plain old cities, a portal doesn’t get its data from a createCity.
	 * That’s because it isn’t a city. (So you shouldn’t store it in any
	 * dictionaries. This is also, incidentally, why it can’t be the endpoint
	 * for any road.) It’s something else entirely. There’s only one portal per
	 * z. Any others are redundant. No two portals, furthermore, may share the
	 * same name or coordinates. Predictably-named errors should be returned
	 * when this occurs. Portals also can’t share names or coordinates with any
	 * city.
	 * 
	 * @param node
	 */
	// TODO redundantPortal, duplicatePortalName, duplicatePortalCoordinates,
	// portalOutOfBounds, portalIntersectsRoad, portalViolatesPMRules
	public void processMapPortal(Element node) {
		final Element commandNode = getCommandNode(node);
		final Element parametersNode = results.createElement("parameters");

		final String name = processStringAttribute(node, "name", parametersNode);
		final Element outputNode = results.createElement("output");
		final int x = processIntegerAttribute(node, "x", parametersNode);
		final int y = processIntegerAttribute(node, "y", parametersNode);
		final int z = processIntegerAttribute(node, "z", parametersNode);

		City newPort = new City(name, x, y, z);
		newPort.setPortal(true);

		try {
			l.addPortal(newPort);

			l.portals.put(newPort.getZ(), newPort);

			// System.out.println(l.levels.get(z).isTreeValid());

			/* add success node to results */
			addSuccessNode(commandNode, parametersNode, outputNode);
		} catch (PortalViolatesPMRulesThrowable e) {
			addErrorNode("portalViolatesPMRules", commandNode, parametersNode);
		} catch (RoadViolatesPMRulesThrowable e) {
			addErrorNode("roadViolatesPMRules", commandNode, parametersNode);
		} catch (PortalOutOfBoundsThrowable e) {
			addErrorNode("portalOutOfBounds", commandNode, parametersNode);
		} catch (PortalIntersectsRoadThrowable e) {
			addErrorNode("portalIntersectsRoad", commandNode, parametersNode);
		} catch (DuplicatePortalCoordinatesThrowable e) {
			addErrorNode("duplicatePortalCoordinates", commandNode,
					parametersNode);
		} catch (DuplicatePortalNameThrowable e) {
			addErrorNode("duplicatePortalName", commandNode, parametersNode);
		} catch (RedundantPortalThrowable e) {
			addErrorNode("redundantPortal", commandNode, parametersNode);
		}
	}

	public void processMapCity(Element node) {
		final Element commandNode = getCommandNode(node);
		final Element parametersNode = results.createElement("parameters");

		final String name = processStringAttribute(node, "name", parametersNode);
		final Element outputNode = results.createElement("output");

		if (!l.citiesByName.containsKey(name)) {
			addErrorNode("nameNotInDictionary", commandNode, parametersNode);
		} else {
			try {
				pmQuadtree.addIsolatedCity(l.citiesByName.get(name));
				/* add success node to results */
				addSuccessNode(commandNode, parametersNode, outputNode);

			} catch (RoadAlreadyExistsThrowable e) {
				addErrorNode("cityAlreadyMapped", commandNode, parametersNode);
			} catch (IsolatedCityAlreadyExistsThrowable e) {
				addErrorNode("cityAlreadyMapped", commandNode, parametersNode);
			} catch (OutOfBoundsThrowable e) {
				addErrorNode("cityOutOfBounds", commandNode, parametersNode);
			} catch (PortalViolatesPMRulesThrowable e) {
				addErrorNode("portalViolatesPMRules", commandNode,
						parametersNode);
			} catch (RoadViolatesPMRulesThrowable e) {
				addErrorNode("roadViolatesPMRules", commandNode, parametersNode);
			}
		}
	}

	/**
	 * Shortest path with start and end on the same z-level is the same as
	 * before. Shortest path with start and end on different levels requires
	 * jumping through portals. You need at least two portals to jump: one to
	 * jump into, one to jump out of. Hopping into a portal transports you to
	 * another portal, either the one directly above or below it, depending on
	 * where you want to go; after that, you’ll have to keep jumping through
	 * portals until you reach the portal to the level containing your final
	 * destination. Traveling from plane to plane thus requires one
	 * endpoint-to-portal off-road drive, one portal-to-endpoint off-road drive,
	 * and any number of portal-portal hops in between. Jumping through portals
	 * is free, no matter how many levels you skip. Each jump up or down counts
	 * as one hop. Driving on-road costs distance, as before. Driving off-road
	 * costs distance × 2. Hopping off-road from any mapped endpoint to the
	 * portal is technically possible. You must, however, choose the endpoint
	 * that minimizes on-road + off-road cost for a given path.
	 * 
	 * @param node
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 */
	// TODO Start and end cities are on different z levels, driving off road
	// costs x2
	// TODO portalDoesNotExist, noPathExists
	public void processShortestPathOld(final Element node) throws IOException,
			ParserConfigurationException, TransformerException {
		final Element commandNode = getCommandNode(node);
		final Element parametersNode = results.createElement("parameters");

		/* Processes parameters */
		final String start = processStringAttribute(node, "start",
				parametersNode);
		final String end = processStringAttribute(node, "end", parametersNode);

		/* Gets the cities */
		City startcity = l.citiesByName.get(start);
		City endcity = l.citiesByName.get(end);

		/* Save Functionality */
		String saveMapName = "";
		if (!node.getAttribute("saveMap").equals("")) {
			saveMapName = processStringAttribute(node, "saveMap",
					parametersNode);
		}

		/* Save Functionality */
		String saveHTMLName = "";
		if (!node.getAttribute("saveHTML").equals("")) {
			saveHTMLName = processStringAttribute(node, "saveHTML",
					parametersNode);
		}

		/* Start and End City Error checking */
		if (startcity == null || !l.mappedCities.contains(startcity)) {
			addErrorNode("nonExistentStart", commandNode, parametersNode);
			return;
		}
		if (endcity == null || !l.mappedCities.contains(endcity)) {
			addErrorNode("nonExistentEnd", commandNode, parametersNode);
			return;
		}

		/* Gets the start cities pmQuadtree */
		pmQuadtree = l.levels.get(startcity.getZ());

		/* Start City Check */
		if (!pmQuadtree.containsCity(start)) {
			addErrorNode("nonExistentStart", commandNode, parametersNode);
			/* End City Check */
		} else if (!pmQuadtree.containsCity(end)) {
			addErrorNode("nonExistentEnd", commandNode, parametersNode);
			/* Check to see if cities have roads mapped to them */
		} else if (!l.roads.getCitySet().contains(l.citiesByName.get(start))
				|| !l.roads.getCitySet().contains(l.citiesByName.get(end))) {
			/* One of the cities is isolated (NOT NECESSARY FOR PART 3) */
			/* Start and end are the same */
			if (start.equals(end)) {
				final Element outputNode = results.createElement("output");
				final Element pathNode = results.createElement("path");
				pathNode.setAttribute("length", "0.000");
				pathNode.setAttribute("hops", "0");

				LinkedList<City> cityList = new LinkedList<City>();
				cityList.add(l.citiesByName.get(start));
				/* Saving output */
				if (!saveMapName.equals("")) {
					saveShortestPathMap(saveMapName, cityList);
				}
				if (!saveHTMLName.equals("")) {
					saveShortestPathMap(saveHTMLName, cityList);
				}

				/* Processing output */
				outputNode.appendChild(pathNode);
				Element successNode = addSuccessNode(commandNode,
						parametersNode, outputNode);

				if (!saveHTMLName.equals("")) {
					/* save shortest path to HTML */
					Document shortestPathDoc = XmlUtility.getDocumentBuilder()
							.newDocument();
					org.w3c.dom.Node spNode = shortestPathDoc.importNode(
							successNode, true);
					shortestPathDoc.appendChild(spNode);
					XmlUtility.transform(shortestPathDoc, new File(
							"shortestPath.xsl"), new File(saveHTMLName
							+ ".html"));
				}
			} else {
				addErrorNode("noPathExists", commandNode, parametersNode);
			}
		} else {
			/* Dijkstra */
			final DecimalFormat decimalFormat = new DecimalFormat("#0.000");
			final Dijkstranator dijkstranator = new Dijkstranator(l.roads);

			final City startCity = (City) l.citiesByName.get(start);
			final City endCity = (City) l.citiesByName.get(end);

			final Path path = dijkstranator.getShortestPath(startCity, endCity);

			if (path == null) {
				addErrorNode("noPathExists", commandNode, parametersNode);
			} else {
				final Element outputNode = results.createElement("output");

				final Element pathNode = results.createElement("path");
				pathNode.setAttribute("length",
						decimalFormat.format(path.getDistance()));
				pathNode.setAttribute("hops", Integer.toString(path.getHops()));

				final LinkedList<City> cityList = path.getCityList();

				/* if required, save the map to an image */
				if (!saveMapName.equals("")) {
					saveShortestPathMap(saveMapName, cityList);
				}
				if (!saveHTMLName.equals("")) {
					saveShortestPathMap(saveHTMLName, cityList);
				}

				if (cityList.size() > 1) {
					/* add the first road */
					City city1 = cityList.remove();
					City city2 = cityList.remove();
					Element roadNode = results.createElement("road");
					roadNode.setAttribute("start", city1.getName());
					roadNode.setAttribute("end", city2.getName());
					pathNode.appendChild(roadNode);

					while (!cityList.isEmpty()) {
						City city3 = cityList.remove();

						/* process the angle */
						Arc2D.Float arc = new Arc2D.Float();
						arc.setArcByTangent(city1.toPoint2D(),
								city2.toPoint2D(), city3.toPoint2D(), 1);

						/* print out the direction */
						double angle = arc.getAngleExtent();
						final String direction;
						while (angle < 0) {
							angle += 360;
						}
						while (angle > 360) {
							angle -= 360;
						}
						if (angle > 180 && angle <= 180 + 135) {
							direction = "left";
						} else if (angle > 45 && angle <= 180) {
							direction = "right";
						} else {
							direction = "straight";
						}
						Element directionNode = results
								.createElement(direction);
						pathNode.appendChild(directionNode);

						/* print out the next road */
						roadNode = results.createElement("road");
						roadNode.setAttribute("start", city2.getName());
						roadNode.setAttribute("end", city3.getName());
						pathNode.appendChild(roadNode);

						/* increment city references */
						city1 = city2;
						city2 = city3;
					}
				}

				/* Output Processing */
				outputNode.appendChild(pathNode);
				Element successNode = addSuccessNode(commandNode,
						parametersNode, outputNode);

				/* Output Processing */
				if (!saveHTMLName.equals("")) {
					/* save shortest path to HTML */
					Document shortestPathDoc = XmlUtility.getDocumentBuilder()
							.newDocument();
					org.w3c.dom.Node spNode = shortestPathDoc.importNode(
							successNode, true);
					shortestPathDoc.appendChild(spNode);
					XmlUtility.transform(shortestPathDoc, new File(
							"shortestPath.xsl"), new File(saveHTMLName
							+ ".html"));
				}
			}
		}
	}

	public void processShortestPath(final Element node) throws IOException,
			ParserConfigurationException, TransformerException {
		final Element commandNode = getCommandNode(node);
		final Element parametersNode = results.createElement("parameters");

		/* Processes parameters */
		final String start = processStringAttribute(node, "start",
				parametersNode);
		final String end = processStringAttribute(node, "end", parametersNode);

		/* Gets the cities */
		City startcity = l.citiesByName.get(start);
		City endcity = l.citiesByName.get(end);

		/* Save Functionality */
		String saveMapName = "";
		if (!node.getAttribute("saveMap").equals("")) {
			saveMapName = processStringAttribute(node, "saveMap",
					parametersNode);
		}
		/* Save Functionality */
		String saveHTMLName = "";
		if (!node.getAttribute("saveHTML").equals("")) {
			saveHTMLName = processStringAttribute(node, "saveHTML",
					parametersNode);
		}

		/* Start and End City Error checking */
		if (startcity == null || !l.mappedCities.contains(startcity)) {
			addErrorNode("nonExistentStart", commandNode, parametersNode);
			return;
		}
		if (endcity == null || !l.mappedCities.contains(endcity)) {
			addErrorNode("nonExistentEnd", commandNode, parametersNode);
			return;
		}

		/* Start and End Cities are on the same level */
		if (startcity.getZ() == endcity.getZ()) {
			/* Gets the cities pmQuadtree */
			pmQuadtree = l.levels.get(startcity.getZ());

			// One of the cities is isolated or is itself
			if (!l.roads.getCitySet().contains(l.citiesByName.get(start))
					|| !l.roads.getCitySet().contains(l.citiesByName.get(end))) {
				/* Start and end are the same */
				if (start.equals(end)) {
					final Element outputNode = results.createElement("output");
					final Element pathNode = results.createElement("path");
					pathNode.setAttribute("length", "0.000");
					pathNode.setAttribute("hops", "0");

					LinkedList<City> cityList = new LinkedList<City>();
					cityList.add(l.citiesByName.get(start));
					/* Saving output */
					if (!saveMapName.equals("")) {
						saveShortestPathMap(saveMapName, cityList);
					}
					if (!saveHTMLName.equals("")) {
						saveShortestPathMap(saveHTMLName, cityList);
					}

					/* Processing output */
					outputNode.appendChild(pathNode);
					Element successNode = addSuccessNode(commandNode,
							parametersNode, outputNode);

					if (!saveHTMLName.equals("")) {
						/* save shortest path to HTML */
						Document shortestPathDoc = XmlUtility
								.getDocumentBuilder().newDocument();
						org.w3c.dom.Node spNode = shortestPathDoc.importNode(
								successNode, true);
						shortestPathDoc.appendChild(spNode);
						XmlUtility.transform(shortestPathDoc, new File(
								"shortestPath.xsl"), new File(saveHTMLName
								+ ".html"));
					}
				} else {
					addErrorNode("noPathExists", commandNode, parametersNode);
					return;
				}
			}

			/* Dijkstra */
			final DecimalFormat decimalFormat = new DecimalFormat("#0.000");
			final Dijkstranator dijkstranator = new Dijkstranator(l.roads);

			final City startCity = (City) l.citiesByName.get(start);
			final City endCity = (City) l.citiesByName.get(end);

			final Path path = dijkstranator.getShortestPath(startCity, endCity);

			if (path == null) {
				addErrorNode("noPathExists", commandNode, parametersNode);
				return;
			} else {
				final Element outputNode = results.createElement("output");

				final Element pathNode = results.createElement("path");
				pathNode.setAttribute("length",
						decimalFormat.format(path.getDistance()));
				pathNode.setAttribute("hops", Integer.toString(path.getHops()));

				final LinkedList<City> cityList = path.getCityList();

				/* if required, save the map to an image */
				if (!saveMapName.equals("")) {
					saveShortestPathMap(saveMapName, cityList);
				}
				if (!saveHTMLName.equals("")) {
					saveShortestPathMap(saveHTMLName, cityList);
				}

				if (cityList.size() > 1) {
					/* add the first road */
					City city1 = cityList.remove();
					City city2 = cityList.remove();
					Element roadNode = results.createElement("road");
					roadNode.setAttribute("start", city1.getName());
					roadNode.setAttribute("end", city2.getName());
					pathNode.appendChild(roadNode);

					while (!cityList.isEmpty()) {
						City city3 = cityList.remove();

						/* process the angle */
						Arc2D.Float arc = new Arc2D.Float();
						arc.setArcByTangent(city1.toPoint2D(),
								city2.toPoint2D(), city3.toPoint2D(), 1);

						/* print out the direction */
						double angle = arc.getAngleExtent();
						final String direction;
						while (angle < 0) {
							angle += 360;
						}
						while (angle > 360) {
							angle -= 360;
						}
						if (angle > 180 && angle <= 180 + 135) {
							direction = "left";
						} else if (angle > 45 && angle <= 180) {
							direction = "right";
						} else {
							direction = "straight";
						}
						Element directionNode = results
								.createElement(direction);
						pathNode.appendChild(directionNode);

						/* print out the next road */
						roadNode = results.createElement("road");
						roadNode.setAttribute("start", city2.getName());
						roadNode.setAttribute("end", city3.getName());
						pathNode.appendChild(roadNode);

						/* increment city references */
						city1 = city2;
						city2 = city3;
					}
				}

				/* Output Processing */
				outputNode.appendChild(pathNode);
				Element successNode = addSuccessNode(commandNode,
						parametersNode, outputNode);

				/* Output Processing */
				if (!saveHTMLName.equals("")) {
					/* save shortest path to HTML */
					Document shortestPathDoc = XmlUtility.getDocumentBuilder()
							.newDocument();
					org.w3c.dom.Node spNode = shortestPathDoc.importNode(
							successNode, true);
					shortestPathDoc.appendChild(spNode);
					XmlUtility.transform(shortestPathDoc, new File(
							"shortestPath.xsl"), new File(saveHTMLName
							+ ".html"));
				}
			}

			/* Start and End Cities are on different levels */
		} else {

			/* Getting the start and end Levels */
			PMQuadtree pmQuadtreeStart = l.levels.get(startcity.getZ());
			PMQuadtree pmQuadtreeEnd = l.levels.get(endcity.getZ());

			/* No portal on one of the levels */
			if (!pmQuadtreeStart.hasPortal || !pmQuadtreeEnd.hasPortal) {
				addErrorNode("portalDoesNotExist", commandNode, parametersNode);
				return;
			}

			/* Check Intermediate levels for portals and creation of hops Path */
			Path hopsPath = new Path(0); // Path of intermediate portal hops
			String hopDirection;
			l.levels.keySet();
			int startLevel = startcity.getZ();
			int endLevel = endcity.getZ();
			if (startLevel > endLevel) {
				hopDirection = "down";
				if (startLevel == 0) {
					addErrorNode("noPathExists", commandNode, parametersNode);
					return;
				}
				int count = startLevel-1;
				while (count > endLevel) {
					if (l.levels.get(count) != null && l.levels.get(count).hasPortal){
						hopsPath.pathList.addLast(l.levels.get(count).portal);
					}
					count--;
				}
				if (count != endLevel) {
					addErrorNode("noPathExists", commandNode, parametersNode);
					return;
				}

			} else {
				hopDirection = "up";

				int count = startLevel+1;
				while (count < endLevel) {
					if (l.levels.get(count) != null && l.levels.get(count).hasPortal){
						hopsPath.pathList.addLast(l.levels.get(count).portal);
					}
					count++;
				}
				if (count != endLevel) {
					addErrorNode("noPathExists", commandNode, parametersNode);
					return;
				}

			}

			/* Dijkstra */
			final DecimalFormat decimalFormat = new DecimalFormat("#0.000");
			final Dijkstranator dijkstranator = new Dijkstranator(l.roads);

			/* Portals on each level */
			City startPortal = pmQuadtreeStart.portal;
			City endPortal = pmQuadtreeEnd.portal;

			/* Nearest cities to the start and end portals */
			City NearestCityToStartPortal = nearestCityHelper(
					(Point2D.Float) startPortal.toPoint2D(), false, pmQuadtreeStart);
			City NearestCityToEndPortal = nearestCityHelper(
					(Point2D.Float) endPortal.toPoint2D(), false, pmQuadtreeEnd);

			/* Paths */
			Path startPath;
			Path endPath;

			/* Nearest city to portal is the start city */
			if (startcity.equals(NearestCityToStartPortal)) {
				/* Calculate OffRoad Distance from start city to start portal */
				double startDistance = Math.sqrt(Math.pow(
						(startcity.getX() - startPortal.getX()), 2)
						+ Math.pow((startcity.getY() - startPortal.getY()), 2));
				startDistance = 2 * startDistance;
				startPath = new Path(startDistance);
				startPath.addEdge(startPortal);
				startPath.addEdge(startcity);
			} else {
				/* Nearest city to portal is the end city */
				startPath = dijkstranator.getShortestPath(startcity,
						NearestCityToStartPortal);

				/* Checking if there is a path */
				if (startPath == null) {
					addErrorNode("noPathExists", commandNode, parametersNode);
					return;
				}

				/* Distance from startPortal to NearestCity */
				double startDistance = Math.sqrt(Math.pow(
						(NearestCityToStartPortal.getX() - startPortal.getX()),
						2)
						+ Math.pow(
								(NearestCityToStartPortal.getY() - startPortal
										.getY()), 2));
				startDistance = 2 * startDistance;
				startPath.setDistance(startPath.getDistance() + startDistance);

				startPath.pathList.addLast(startPortal);
			}

			/* Nearest city to portal is the end city */
			if (endcity.equals(NearestCityToEndPortal)) {
				/* Calculate OffRoad Distance from end city to end portal */
				double endDistance = Math.sqrt(Math.pow(
						(endcity.getX() - endPortal.getX()), 2)
						+ Math.pow((endcity.getY() - endPortal.getY()), 2));
				endDistance = 2 * endDistance;
				endPath = new Path(endDistance);
				endPath.addEdge(endcity);
				endPath.addEdge(endPortal);
			} else {
				/* Nearest city to portal is the end city */
				endPath = dijkstranator.getShortestPath(
						NearestCityToEndPortal, endcity);

				/* Checking if there is a path */
				if (endPath == null) {
					addErrorNode("noPathExists", commandNode, parametersNode);
					return;
				}

				/* Distance from startPortal to NearestCity */
				double endDistance = Math.sqrt(Math.pow(
						(NearestCityToEndPortal.getX() - endPortal.getX()), 2)
						+ Math.pow((NearestCityToEndPortal.getY() - endPortal
								.getY()), 2));
				endDistance = 2 * endDistance;
				endPath.setDistance(endPath.getDistance() + endDistance);

				endPath.pathList.addFirst(endPortal);
			}

			/*
			 * At this point there should be 3 paths one for the start level one
			 * for the end level and one for the hops in between
			 */

			/*
			 * Concatinating all of the path objects into one that can be looped
			 * over
			 */
			Path totalPath = new Path(startPath.getDistance()
					+ endPath.getDistance());
			totalPath.pathList.addAll(startPath.pathList);
			totalPath.pathList.addAll(hopsPath.pathList);
			totalPath.pathList.addAll(endPath.pathList);

			final Element outputNode = results.createElement("output");

			final Element pathNode = results.createElement("path");
			pathNode.setAttribute("length",
					decimalFormat.format(totalPath.getDistance()));
			pathNode.setAttribute("hops", Integer.toString(totalPath.getHops()));

			final LinkedList<City> cityList = totalPath.getCityList();

			/* if required, save the map to an image */
			if (!saveMapName.equals("")) {
				saveShortestPathMap(saveMapName, cityList);
			}
			if (!saveHTMLName.equals("")) {
				saveShortestPathMap(saveHTMLName, cityList);
			}

			if (cityList.size() > 1) {
				/* add the first road */
				City city1 = cityList.remove();
				City city2 = cityList.remove();
				
				Element roadNode;
				if (city2.isPortal()){
					roadNode = results.createElement("offroad");
				} else {
					roadNode = results.createElement("road");
				}
				roadNode.setAttribute("start", city1.getName());
				roadNode.setAttribute("end", city2.getName());
				pathNode.appendChild(roadNode);

				while (!cityList.isEmpty()) {
					City city3 = cityList.remove();
					final String direction;

					if (city2.getZ() != city3.getZ()){
						direction = hopDirection;
					} else {
					
						/* process the angle */
						Arc2D.Float arc = new Arc2D.Float();
						arc.setArcByTangent(city1.toPoint2D(), city2.toPoint2D(),
								city3.toPoint2D(), 1);
	
						/* print out the direction */
						double angle = arc.getAngleExtent();
						while (angle < 0) {
							angle += 360;
						}
						while (angle > 360) {
							angle -= 360;
						}
						if (angle > 180 && angle <= 180 + 135) {
							direction = "left";
						} else if (angle > 45 && angle < 180) {
							direction = "right";
						} else {
							direction = "straight";
						}
					}
					Element directionNode = results.createElement(direction);
					pathNode.appendChild(directionNode);

					/* print out the next road */
					if (city2.isPortal() && city3.isPortal()){
						roadNode = results.createElement("jump");
					} else if (city2.isPortal() || city3.isPortal()){
						roadNode = results.createElement("offroad");
					} else {
						roadNode = results.createElement("road");
					}
					
					roadNode.setAttribute("start", city2.getName());
					roadNode.setAttribute("end", city3.getName());
					pathNode.appendChild(roadNode);

					/* increment city references */
					city1 = city2;
					city2 = city3;
				}
			}

			/* Output Processing */
			outputNode.appendChild(pathNode);
			Element successNode = addSuccessNode(commandNode, parametersNode,
					outputNode);

			/* Output Processing */
			if (!saveHTMLName.equals("")) {
				/* save shortest path to HTML */
				Document shortestPathDoc = XmlUtility.getDocumentBuilder()
						.newDocument();
				org.w3c.dom.Node spNode = shortestPathDoc.importNode(
						successNode, true);
				shortestPathDoc.appendChild(spNode);
				XmlUtility.transform(shortestPathDoc, new File(
						"shortestPath.xsl"), new File(saveHTMLName + ".html"));
			}
		}
	}

	private void saveShortestPathMap(final String mapName,
			final List<City> cityList) throws IOException {
		final CanvasPlus map = new CanvasPlus();
		/* initialize map */
		map.setFrameSize(l.spatialWidth, l.spatialHeight);
		/* add a rectangle to show where the bounds of the map are located */
		map.addRectangle(0, 0, l.spatialWidth, l.spatialHeight, Color.BLACK,
				false);

		final Iterator<City> it = cityList.iterator();
		City city1 = it.next();

		/* map green starting point */
		map.addPoint(city1.getName(), city1.getX(), city1.getY(), Color.GREEN);

		if (it.hasNext()) {
			City city2 = it.next();
			/* map blue road */
			map.addLine(city1.getX(), city1.getY(), city2.getX(), city2.getY(),
					Color.BLUE);

			while (it.hasNext()) {
				/* increment cities */
				city1 = city2;
				city2 = it.next();

				/* map point */
				map.addPoint(city1.getName(), city1.getX(), city1.getY(),
						Color.BLUE);

				/* map blue road */
				map.addLine(city1.getX(), city1.getY(), city2.getX(),
						city2.getY(), Color.BLUE);
			}

			/* map red end point */
			map.addPoint(city2.getName(), city2.getX(), city2.getY(), Color.RED);

		}

		/* save map to image file */
		map.save(mapName);

		map.dispose();
	}

	/**
	 * Clears any lazily-deleted nodes loitering in the AVL-g (the <sentinel>s
	 * from printAvlTree). Like clearAll, this should never fail.
	 */
	// TODO
	private void processSweep(Element node) {

	}

	/**
	 * Processes a saveMap command. Saves the graphical map to a given file.
	 * 
	 * @param node
	 *            saveMap command to be processed
	 * @throws IOException
	 *             problem accessing the image file
	 */
	// TODO implement z or level functionality
	public void processSaveMap(final Element node) throws IOException {
		final Element commandNode = getCommandNode(node);
		final Element parametersNode = results.createElement("parameters");

		final int z = processIntegerAttribute(node, "z", parametersNode);
		final String name = processStringAttribute(node, "name", parametersNode);

		final Element outputNode = results.createElement("output");

		CanvasPlus canvas = drawPMQuadtree(node);

		/* save canvas to '(name).png' */
		canvas.save(name);

		canvas.dispose();

		/* add success node to results */
		addSuccessNode(commandNode, parametersNode, outputNode);
	}

	private CanvasPlus drawPMQuadtree(final Element node) {
		final Element commandNode = getCommandNode(node);
		final Element parametersNode = results.createElement("parameters");

		final int z = processIntegerAttribute(node, "z", parametersNode);

		final CanvasPlus canvas = new CanvasPlus("MeeshQuest");

		pmQuadtree = l.levels.get(z);

		/* initialize canvas */
		canvas.setFrameSize(l.spatialWidth, l.spatialHeight);

		/* add a rectangle to show where the bounds of the map are located */
		canvas.addRectangle(0, 0, l.spatialWidth, l.spatialHeight, Color.BLACK,
				false);

		/* draw PM Quadtree */
		drawPMQuadtreeHelper(pmQuadtree.getRoot(), canvas);

		return canvas;
	}

	private void drawPMQuadtreeHelper(Node node, CanvasPlus canvas) {
		if (node.getType() == Node.BLACK) {
			Black blackNode = (Black) node;
			for (Geometry g : blackNode.getGeometry()) {
				if (g.isCity()) {
					City city = (City) g;
					canvas.addPoint(city.getName(), city.getX(), city.getY(),
							Color.BLACK);
				} else {
					Road road = (Road) g;
					canvas.addLine(road.getStart().getX(), road.getStart()
							.getY(), road.getEnd().getX(),
							road.getEnd().getY(), Color.BLACK);
				}
			}
		} else if (node.getType() == Node.GRAY) {
			Gray grayNode = (Gray) node;
			canvas.addCross(grayNode.getCenterX(), grayNode.getCenterY(),
					grayNode.getHalfWidth(), Color.GRAY);
			for (int i = 0; i < 4; i++) {
				drawPMQuadtreeHelper(grayNode.getChild(i), canvas);
			}
		}
	}

	/**
	 * Prints out the structure of the PM Quadtree in an XML format.
	 * 
	 * @param node
	 *            printPMQuadtree command to be processed
	 */

	public void processPrintPMQuadtree(final Element node) {
		final Element commandNode = getCommandNode(node);
		final Element parametersNode = results.createElement("parameters");
		final Element outputNode = results.createElement("output");
		final int z = processIntegerAttribute(node, "z", parametersNode);

		pmQuadtree = l.levels.get(z);

		if (pmQuadtree == null || pmQuadtree.isEmpty()) {
			/* empty PR Quadtree */
			addErrorNode("levelIsEmpty", commandNode, parametersNode);

		} else {
			/* print PR Quadtree */
			final Element quadtreeNode = results.createElement("quadtree");
			quadtreeNode.setAttribute("order", Integer.toString(l.pmOrder));
			printPMQuadtreeHelper(pmQuadtree.getRoot(), quadtreeNode);

			outputNode.appendChild(quadtreeNode);

			/* add success node to results */
			addSuccessNode(commandNode, parametersNode, outputNode);
		}
	}

	/**
	 * Traverses each node of the PR Quadtree.
	 * 
	 * @param currentNode
	 *            PR Quadtree node being printed
	 * @param xmlNode
	 *            XML node representing the current PR Quadtree node
	 */

	private void printPMQuadtreeHelper(final Node currentNode,
			final Element xmlNode) {
		if (currentNode.getType() == Node.WHITE) {
			Element white = results.createElement("white");
			xmlNode.appendChild(white);
		} else if (currentNode.getType() == Node.BLACK) {
			Black currentLeaf = (Black) currentNode;
			Element blackNode = results.createElement("black");
			blackNode.setAttribute("cardinality",
					Integer.toString(currentLeaf.getGeometry().size()));
			for (Geometry g : currentLeaf.getGeometry()) {
				if (g.isCity()) {
					City c = (City) g;

					Element city;

					if (c.isPortal()) {
						city = results.createElement("portal");

					} else {
						city = results.createElement("city");
						city.setAttribute("radius",
								Integer.toString((int) c.getRadius()));
						city.setAttribute("color", c.getColor());
					}

					city.setAttribute("name", c.getName());
					city.setAttribute("x", Integer.toString((int) c.getX()));
					city.setAttribute("y", Integer.toString((int) c.getY()));
					city.setAttribute("z", Integer.toString((int) c.getZ()));

					blackNode.appendChild(city);
				} else {
					City c1 = ((Road) g).getStart();
					City c2 = ((Road) g).getEnd();
					Element road = results.createElement("road");
					road.setAttribute("start", c1.getName());
					road.setAttribute("end", c2.getName());
					blackNode.appendChild(road);
				}
			}
			xmlNode.appendChild(blackNode);
		} else {
			final Gray currentInternal = (Gray) currentNode;
			final Element gray = results.createElement("gray");
			gray.setAttribute("x",
					Integer.toString((int) currentInternal.getCenterX()));
			gray.setAttribute("y",
					Integer.toString((int) currentInternal.getCenterY()));
			for (int i = 0; i < 4; i++) {
				printPMQuadtreeHelper(currentInternal.getChild(i), gray);
			}
			xmlNode.appendChild(gray);
		}
	}

	/**
	 * Finds the mapped cities within the range of a given point.
	 * 
	 * @param node
	 *            rangeCities command to be processed
	 * @throws IOException
	 */
	// TODO Cities in range of x,y,z values
	public void processRangeCities(final Element node) throws IOException {
		final Element commandNode = getCommandNode(node);
		final Element parametersNode = results.createElement("parameters");
		final Element outputNode = results.createElement("output");

		final int x = processIntegerAttribute(node, "x", parametersNode);
		final int y = processIntegerAttribute(node, "y", parametersNode);
		final int z = processIntegerAttribute(node, "z", parametersNode);
		final int radius = processIntegerAttribute(node, "radius",
				parametersNode);

		pmQuadtree = l.levels.get(z);

		String pathFile = "";
		if (!node.getAttribute("saveMap").equals("")) {
			pathFile = processStringAttribute(node, "saveMap", parametersNode);
		}

		if (radius == 0 || pmQuadtree == null) {
			addErrorNode("noCitiesExistInRange", commandNode, parametersNode);
		} else {
			final TreeSet<Geometry> citiesInRange = new TreeSet<Geometry>();

			// For loop checking if level should be searched for cities
			for (int level : l.levels.keySet()) {

				// Check if the level will have a search circle
				if (Math.abs(level - z) <= radius) {
					double levelRadius = (double) Math
							.sqrt((Math.pow(radius, 2) - Math.pow(
									Math.abs(level - z), 2)));
					rangeHelper(new Circle2D.Double(x, y, levelRadius),
							l.levels.get(level).getRoot(), citiesInRange,
							false, true);

				}
			}

			// rangeHelper(new Circle2D.Double(x, y, radius),
			// pmQuadtree.getRoot(), citiesInRange, false, true);

			/* print out cities within range */
			if (citiesInRange.isEmpty()) {
				addErrorNode("noCitiesExistInRange", commandNode,
						parametersNode);
			} else {
				/* get city list */
				final Element cityListNode = results.createElement("cityList");
				for (Geometry g : citiesInRange) {
					addCityNode(cityListNode, (City) g);
				}
				outputNode.appendChild(cityListNode);

				/* add success node to results */
				addSuccessNode(commandNode, parametersNode, outputNode);

				if (pathFile.compareTo("") != 0) {
					/* save canvas to file with range circle */
					CanvasPlus canvas = drawPMQuadtree(node);
					canvas.addCircle(x, y, radius, Color.BLUE, false);
					canvas.save(pathFile);
					canvas.dispose();
				}
			}
		}
	}

	/**
	 * Lists all the roads present in the spatial map within a radius of a point
	 * x, y, z. Boundaries are inclusive, and x, y, and z are integer
	 * coordinates. Only mapped roads are relevant to this command. Remember
	 * that in-range roads can come from any level—you’ll have to look up and
	 * down. Design your structure accordingly. If you use a HashMap, for
	 * example, you’ll have to check every possible z-value within the radius
	 * (even the ones that have no points mapped). That won’t be fun. <success>
	 * will result from the existence of at least one <road> that satisfies the
	 * range check condition. If none do, then an <error> tag will be the
	 * result. If the radius is 0, no roads will ever exist in the range, even
	 * if there is a city or road at the range point. If the saveMap attribute
	 * is present, the current plane (the plane with z value being the one
	 * specified in the input parameters) will be saved to an image file (see
	 * saveMap), with features looking exactly like those of rangeCities.
	 * 
	 * @param node
	 * @throws IOException
	 */
	// TODO Roads on any x,y,z in range
	public void processRangeRoads(final Element node) throws IOException {
		final Element commandNode = getCommandNode(node);
		final Element parametersNode = results.createElement("parameters");
		final Element outputNode = results.createElement("output");

		final int x = processIntegerAttribute(node, "x", parametersNode);
		final int y = processIntegerAttribute(node, "y", parametersNode);
		final int z = processIntegerAttribute(node, "z", parametersNode);
		final int radius = processIntegerAttribute(node, "radius",
				parametersNode);

		pmQuadtree = l.levels.get(z);

		String pathFile = "";
		if (!node.getAttribute("saveMap").equals("")) {
			pathFile = processStringAttribute(node, "saveMap", parametersNode);
		}

		if (pmQuadtree == null || radius == 0) {
			addErrorNode("noRoadsExistInRange", commandNode, parametersNode);
		} else {
			final TreeSet<Geometry> roadsInRange = new TreeSet<Geometry>();

			// For loop checking if level should be searched for cities
			for (int level : l.levels.keySet()) {

				// Check if the level will have a search circle
				if (Math.abs(level - z) <= radius) {
					double levelRadius = (double) Math
							.sqrt((Math.pow(radius, 2) - Math.pow(
									Math.abs(level - z), 2)));
					rangeHelper(new Circle2D.Double(x, y, levelRadius),
							l.levels.get(level).getRoot(), roadsInRange, true,
							false);

				}
			}

			// rangeHelper(new Circle2D.Double(x, y, radius),
			// pmQuadtree.getRoot(), roadsInRange, true, false);

			/* print out cities within range */
			if (roadsInRange.isEmpty()) {
				addErrorNode("noRoadsExistInRange", commandNode, parametersNode);
			} else {
				/* get road list */
				final Element roadListNode = results.createElement("roadList");
				for (Geometry g : roadsInRange) {
					addRoadNode(roadListNode, (Road) g);
				}
				outputNode.appendChild(roadListNode);

				/* add success node to results */
				addSuccessNode(commandNode, parametersNode, outputNode);

				if (pathFile.compareTo("") != 0) {
					/* save canvas to file with range circle */
					CanvasPlus canvas = drawPMQuadtree(node);
					canvas.addCircle(x, y, radius, Color.BLUE, false);
					canvas.save(pathFile);
					canvas.dispose();
				}
			}
		}
	}

	/**
	 * Helper function for both rangeCities and rangeRoads
	 * 
	 * @param range
	 *            defines the range as a circle
	 * @param node
	 *            is the node in the pmQuadtree being processed
	 * @param gInRange
	 *            stores the results
	 * @param includeRoads
	 *            specifies if the range search should include roads
	 * @param includeCities
	 *            specifies if the range search should include cities
	 */
	private void rangeHelper(final Circle2D.Double range, final Node node,
			final TreeSet<Geometry> gInRange, final boolean includeRoads,
			final boolean includeCities) {
		if (node.getType() == Node.BLACK) {
			final Black leaf = (Black) node;
			for (Geometry g : leaf.getGeometry()) {
				if (includeCities
						&& g.isCity()
						&& ((City) g).isPortal() == false
						&& !gInRange.contains(g)
						&& Inclusive2DIntersectionVerifier.intersects(
								((City) g).toPoint2D(), range)) {
					gInRange.add(g);
				}
				if (includeRoads
						&& g.isRoad()
						&& !gInRange.contains(g)
						&& (((Road) g).toLine2D().ptSegDist(range.getCenter()) <= range
								.getRadius())) {
					gInRange.add(g);
				}
			}
		} else if (node.getType() == Node.GRAY) {
			final Gray internal = (Gray) node;
			for (int i = 0; i < 4; i++) {
				if (Inclusive2DIntersectionVerifier.intersects(
						internal.getChildRegion(i), range)) {
					rangeHelper(range, internal.getChild(i), gInRange,
							includeRoads, includeCities);
				}
			}
		}
	}

	public void processNearestCity(Element node) {
		final Element commandNode = getCommandNode(node);
		final Element parametersNode = results.createElement("parameters");
		final Element outputNode = results.createElement("output");

		/* extract attribute values from command */
		final int x = processIntegerAttribute(node, "x", parametersNode);
		final int y = processIntegerAttribute(node, "y", parametersNode);
		final int z = processIntegerAttribute(node, "z", parametersNode);

		pmQuadtree = l.levels.get(z);

		final Point2D.Float point = new Point2D.Float(x, y);

		if (pmQuadtree == null
				|| pmQuadtree.getNumCities()
						- pmQuadtree.getNumIsolatedCities() == 0) {
			addErrorNode("cityNotFound", commandNode, parametersNode);
		} else {
			addCityNode(outputNode, nearestCityHelper(point, false));
			addSuccessNode(commandNode, parametersNode, outputNode);
		}
	}

	public void processNearestIsolatedCity(Element node) {
		final Element commandNode = getCommandNode(node);
		final Element parametersNode = results.createElement("parameters");
		final Element outputNode = results.createElement("output");

		/* extract attribute values from command */
		final int x = processIntegerAttribute(node, "x", parametersNode);
		final int y = processIntegerAttribute(node, "y", parametersNode);

		final Point2D.Float point = new Point2D.Float(x, y);

		if (pmQuadtree.getNumIsolatedCities() == 0) {
			addErrorNode("cityNotFound", commandNode, parametersNode);
		} else {
			addIsolatedCityNode(outputNode, nearestCityHelper(point, true));
			addSuccessNode(commandNode, parametersNode, outputNode);
		}
	}

	private City nearestCityHelper(Point2D.Float point,
			boolean isNearestIsolatedCity) {
		Node n = pmQuadtree.getRoot();
		PriorityQueue<NearestSearchRegion> nearCities = new PriorityQueue<NearestSearchRegion>();

		if (n.getType() == Node.BLACK) {
			Black b = (Black) n;

			if (b.getCity() != null
					&& b.getCity().isPortal() == false
					&& pmQuadtree.isIsolatedCity(b.getCity()) == isNearestIsolatedCity) {
				return b.getCity();
			}
		}

		while (n.getType() == Node.GRAY) {
			Gray g = (Gray) n;
			Node kid;

			for (int i = 0; i < 4; i++) {
				kid = g.getChild(i);

				if (kid.getType() == Node.BLACK) {
					Black b = (Black) kid;
					City c = b.getCity();

					if (c != null
							&& c.isPortal() == false
							&& pmQuadtree.isIsolatedCity(c) == isNearestIsolatedCity) {
						double dist = point.distance(c.toPoint2D());
						nearCities.add(new NearestSearchRegion(kid, dist, c));
					}
				} else if (kid.getType() == Node.GRAY) {
					double dist = Shape2DDistanceCalculator.distance(point,
							g.getChildRegion(i));
					nearCities.add(new NearestSearchRegion(kid, dist, null));
				}
			}

			try {
				n = nearCities.remove().node;
			} catch (Exception ex) {
				throw new IllegalStateException();
			}
		}
		return ((Black) n).getCity();
	}
	
	private City nearestCityHelper(Point2D.Float point,
			boolean isNearestIsolatedCity, PMQuadtree quadtree) {
		Node n = quadtree.getRoot();
		PriorityQueue<NearestSearchRegion> nearCities = new PriorityQueue<NearestSearchRegion>();

		if (n.getType() == Node.BLACK) {
			Black b = (Black) n;

			if (b.getCity() != null
					&& b.getCity().isPortal() == false
					&& quadtree.isIsolatedCity(b.getCity()) == isNearestIsolatedCity) {
				return b.getCity();
			}
		}

		while (n.getType() == Node.GRAY) {
			Gray g = (Gray) n;
			Node kid;

			for (int i = 0; i < 4; i++) {
				kid = g.getChild(i);

				if (kid.getType() == Node.BLACK) {
					Black b = (Black) kid;
					City c = b.getCity();

					if (c != null
							&& c.isPortal() == false
							&& quadtree.isIsolatedCity(c) == isNearestIsolatedCity) {
						double dist = point.distance(c.toPoint2D());
						nearCities.add(new NearestSearchRegion(kid, dist, c));
					}
				} else if (kid.getType() == Node.GRAY) {
					double dist = Shape2DDistanceCalculator.distance(point,
							g.getChildRegion(i));
					nearCities.add(new NearestSearchRegion(kid, dist, null));
				}
			}

			try {
				n = nearCities.remove().node;
			} catch (Exception ex) {
				throw new IllegalStateException();
			}
		}
		return ((Black) n).getCity();
	}

	public void processNearestRoad(final Element node) throws IOException {
		final Element commandNode = getCommandNode(node);
		final Element parametersNode = results.createElement("parameters");
		final Element outputNode = results.createElement("output");

		/* extract values from command */
		final int x = processIntegerAttribute(node, "x", parametersNode);
		final int y = processIntegerAttribute(node, "y", parametersNode);

		if (pmQuadtree.getNumRoads() <= 0) {
			addErrorNode("roadNotFound", commandNode, parametersNode);
		} else {
			final Point2D.Float pt = new Point2D.Float(x, y);
			Road road = nearestRoadHelper(pt);
			addRoadNode(outputNode, road);
			addSuccessNode(commandNode, parametersNode, outputNode);
		}
	}

	private Road nearestRoadHelper(Point2D.Float point) {
		Node n = pmQuadtree.getRoot();
		PriorityQueue<NearestSearchRegion> nearRoads = new PriorityQueue<NearestSearchRegion>();
		NearestSearchRegion region = null;

		if (n.getType() == Node.BLACK) {
			List<Geometry> gList = ((Black) n).getGeometry();
			double minDist = Double.MAX_VALUE;
			Road road = null;

			for (Geometry geom : gList) {
				if (geom.isRoad()) {
					double d = ((Road) geom).toLine2D().ptSegDist(point);

					if (d < minDist) {
						minDist = d;
						road = (Road) geom;
					}
				}
			}
			return road;
		}

		while (n.getType() == Node.GRAY) {
			Gray g = (Gray) n;
			Node kid;

			for (int i = 0; i < 4; i++) {
				kid = g.getChild(i);

				if (kid.getType() == Node.BLACK) {
					Black b = (Black) kid;
					List<Geometry> gList = b.getGeometry();
					double minDist = Double.MAX_VALUE;
					Road road = null;

					for (Geometry geom : gList) {
						if (geom.isRoad()) {
							double d = ((Road) geom).toLine2D()
									.ptSegDist(point);

							if (d < minDist) {
								minDist = d;
								road = (Road) geom;
							}
						}
					}
					if (road == null) {
						continue;
					}
					nearRoads.add(new NearestSearchRegion(kid, minDist, road));
				} else if (kid.getType() == Node.GRAY) {
					double dist = Shape2DDistanceCalculator.distance(point,
							g.getChildRegion(i));
					nearRoads.add(new NearestSearchRegion(kid, dist, null));
				}
			}

			try {
				region = nearRoads.remove();
				n = region.node;
			} catch (Exception ex) {
				// should be impossible to reach here
				throw new IllegalStateException();
			}
		}
		assert region.node.getType() == Node.BLACK;
		return (Road) region.g;
	}

	public void processNearestCityToRoad(final Element node) throws IOException {
		final Element commandNode = getCommandNode(node);
		final Element parametersNode = results.createElement("parameters");
		final Element outputNode = results.createElement("output");

		final String start = processStringAttribute(node, "start",
				parametersNode);
		final String end = processStringAttribute(node, "end", parametersNode);

		final City startCity = (City) l.citiesByName.get(start);
		final City endCity = (City) l.citiesByName.get(end);
		if (startCity == null || endCity == null) {
			addErrorNode("roadIsNotMapped", commandNode, parametersNode);
			return;
		}
		final Road road = new Road(startCity, endCity);

		if (pmQuadtree.containsRoad(road)) {
			City nc = nearestCityToRoadHelper(road);
			if (nc == null) {
				addErrorNode("noOtherCitiesMapped", commandNode, parametersNode);
			} else {
				addCityNode(outputNode, nc);
				addSuccessNode(commandNode, parametersNode, outputNode);
			}
		} else {
			addErrorNode("roadIsNotMapped", commandNode, parametersNode);
		}
	}

	private City nearestCityToRoadHelper(Road road) {
		Node n = pmQuadtree.getRoot();
		PriorityQueue<NearestSearchRegion> nearCities = new PriorityQueue<NearestSearchRegion>();

		while (n.getType() == Node.GRAY) {
			Gray g = (Gray) n;
			Node kid;
			for (int i = 0; i < 4; i++) {
				kid = g.getChild(i);
				if (kid.getType() == Node.BLACK) {
					City c = ((Black) kid).getCity();
					if (c != null && !road.contains(c)) {
						double dist = road.toLine2D().ptSegDist(c.toPoint2D());
						nearCities.add(new NearestSearchRegion(kid, dist, c));
					}
				} else if (kid.getType() == Node.GRAY) {
					double dist = Shape2DDistanceCalculator.distance(
							road.toLine2D(), g.getChildRegion(i));
					nearCities.add(new NearestSearchRegion(kid, dist, null));
				}
			}
			try {
				if (nearCities.isEmpty()) {
					// no other cities mapped
					return null;
				}
				n = nearCities.remove().node;
			} catch (Exception ex) {
				throw new IllegalStateException();
			}
		}
		return ((Black) n).getCity();
	}

	/**
	 * Helper class for nearest everything (city/road/etc)
	 */
	private class NearestSearchRegion implements
			Comparable<NearestSearchRegion> {
		private Node node;
		private double distance;
		private Geometry g;

		public NearestSearchRegion(Node node, double distance, Geometry g) {
			this.node = node;
			this.distance = distance;
			this.g = g;
		}

		public int compareTo(NearestSearchRegion o) {
			if (distance == o.distance) {
				if (node.getType() == Node.BLACK
						&& o.node.getType() == Node.BLACK) {
					return g.compareTo(o.g);
				} else if (node.getType() == Node.BLACK
						&& o.node.getType() == Node.GRAY) {
					return 1;
				} else if (node.getType() == Node.GRAY
						&& o.node.getType() == Node.BLACK) {
					return -1;
				} else {
					return ((Gray) node).hashCode()
							- ((Gray) o.node).hashCode();
				}
			}
			return (distance < o.distance) ? -1 : 1;
		}
	}
}
