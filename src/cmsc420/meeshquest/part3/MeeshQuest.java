package cmsc420.meeshquest.part3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cmsc420.command.Command;
import cmsc420.xml.XmlUtility;

public class MeeshQuest {
	
	
	/* input stream/file */
	//private final InputStream xmlInput = System.in;
	
	
	// UNCOMMENT THIS TO TEST WITH FILES
	private File xmlOutput = new File("test1.txt");

	// Converted part 2 tests
//	private File xmlInput = new File("input.Matt.slightlyLessMasterfulInput.xml");
//	private File xmlInput = new File("JohnnyMao.shortestPath.xml");
//	private File xmlInput = new File("MasterInput.xml");
//	private File xmlInput = new File("MasterRange.xml");
//	private File xmlInput = new File("part2.dan.errortest1.xml");
//	private File xmlInput = new File("part2.dan.errortest2.xml");
//	private File xmlInput = new File("part2.dan.nearestcity1.xml");
//	private File xmlInput = new File("part2.dan.nearestcity2.xml");
//	private File xmlInput = new File("part2.dan.shortestpath2.xml");
//	private File xmlInput = new File("part2.dan.shortestPaths.xml");
//	private File xmlInput = new File("part2.dander.shortestErrors.xml");
//	private File xmlInput = new File("part2.dander.testSpatialFunctions.xml");
//	private File xmlInput = new File("part2.dean.shortestPath.xml");
//	private File xmlInput = new File("part2.JaredBergman.errorTest.xml");
//	private File xmlInput = new File("part2.reeve.roadOnEdge.input.xml");
//	private File xmlInput = new File("pm_insert_rangeroads.input.xml");
//	private File xmlInput = new File("printavl_printpm.xml");
//	private File xmlInput = new File("rangeRoadsTest.xml");
	
	// Part 3 Public tests
//	private File xmlInput = new File("testfiles/part3.public.portal.input.xml");
//	private File xmlInput = new File("testfiles/part3.public.primary.input.xml");

	// Part 3 Student Tests
//	private File xmlInput = new File("part3.danzou.insertDelete.xml");
	private File xmlInput = new File("pm1_insert_delete.xml");
	
	
    /* output DOM Document tree */
    private Document results;

    /* processes each command */
    private Command command;

    public static void main(String[] args) {
        final MeeshQuest m = new MeeshQuest();
        m.processInput();
    }

    public void processInput() {
        try {
            /* validate document */
            Document doc = XmlUtility.validateNoNamespace(xmlInput);

            /* create output */
            results = XmlUtility.getDocumentBuilder().newDocument();
            command = new Command();
            command.setResults(results);
            
            /* process commands element */
            Element commandNode = doc.getDocumentElement();
            processCommand(commandNode);

            /* process each command */
            final NodeList nl = commandNode.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                if (nl.item(i).getNodeType() == Document.ELEMENT_NODE) {
                    /* need to check if Element (ignore comments) */
                    commandNode = (Element) nl.item(i);
                    processCommand(commandNode);
                }
            }
        } catch (SAXException e) {
            e.printStackTrace();
            addFatalError();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            addFatalError();
        } catch (IOException e) {
            e.printStackTrace();
            addFatalError();
        } catch (TransformerException e) {
            e.printStackTrace();
            addFatalError();
        } finally {
            try {
            	
            	// UNCOMMENT THIS TO TEST WITH FILES
				XmlUtility.write(results, xmlOutput);
            	
                XmlUtility.print(results);
            } catch (TransformerException e) {
                System.exit(-1);
            }
			// UNCOMMENT THIS TO TEST WITH FILES
			 catch (FileNotFoundException e) {
				e.printStackTrace();
			}
        }
    }

    private void addFatalError() {
        try {
            results = XmlUtility.getDocumentBuilder().newDocument();
            final Element fatalError = results.createElement("fatalError");
            results.appendChild(fatalError);
        } catch (ParserConfigurationException e) {
            System.exit(-1);
        }
    }

    private void processCommand(final Element commandNode) throws IOException, ParserConfigurationException,
            TransformerException {
        final String name = commandNode.getNodeName();
        
        if (name.equals("commands")) {
            command.processCommands(commandNode);
        } else if (name.equals("createCity")) {
            command.processCreateCity(commandNode);
        } else if (name.equals("clearAll")) {
            command.processClearAll(commandNode);
        } else if (name.equals("listCities")) {
            command.processListCities(commandNode);
        } else if (name.equals("printAvlTree")) {
            command.processPrintAvlTree(commandNode);
        } else if (name.equals("mapRoad")) {
            command.processMapRoad(commandNode);
        } else if (name.equals("mapCity")) {
            command.processMapCity(commandNode);
        } else if (name.equals("mapPortal")) {
            command.processMapPortal(commandNode);
        } else if (name.equals("printPMQuadtree")) {
            command.processPrintPMQuadtree(commandNode);
        } else if (name.equals("saveMap")) {
            command.processSaveMap(commandNode);
        } else if (name.equals("rangeCities")) {
            command.processRangeCities(commandNode);
        } else if (name.equals("rangeRoads")) {
            command.processRangeRoads(commandNode);
        } else if (name.equals("nearestCity")) {
            command.processNearestCity(commandNode);
        } else if (name.equals("nearestIsolatedCity")) {
            command.processNearestIsolatedCity(commandNode);
        } else if (name.equals("nearestRoad")) {
            command.processNearestRoad(commandNode);
        } else if (name.equals("nearestCityToRoad")) {
            command.processNearestCityToRoad(commandNode);
        } else if (name.equals("shortestPath")) {
            command.processShortestPath(commandNode);
        } else {
            /* problem with the validator */
            System.exit(-1);
        }
    }
}
