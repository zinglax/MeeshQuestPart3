package cmsc420.testing;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import cmsc420.meeshquest.part3.MeeshQuest;

public class RegressionTests {

	private File xmlOutput = new File("test1.txt");
	private File expected;
	
	
	@Test
	public void mattSlightlyLessMasterful() throws IOException{
        final MeeshQuest m = new MeeshQuest("input.Matt.slightlyLessMasterfulInput.xml");
        m.processInput();
        expected = new File("part3.Matt.slightlyLessMasterful.output.xml");
        Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
	}
	
	@Test
	public void masterInput() throws IOException {
		final MeeshQuest m = new MeeshQuest("MasterInput.xml");
        m.processInput();       
        expected = new File("part3.MasterInput.output.xml");
        Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
	}

	@Test
	public void masterRange() throws IOException {
		final MeeshQuest m = new MeeshQuest("MasterRange.xml");
        m.processInput();       
        expected = new File("part3.MasterRange.output.xml");
        Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
	}
	
	@Test
	public void danErrorTest1() throws IOException {
		final MeeshQuest m = new MeeshQuest("part2.dan.errortest1.xml");
        m.processInput();       
        expected = new File("part3.dan.errortest1.output.xml");
        Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
	}
	
	@Test
	public void danErrorTest2() throws IOException {
		final MeeshQuest m = new MeeshQuest("part2.dan.errortest2.xml");
        m.processInput();       
        expected = new File("part3.dan.errortest2.output.xml");
        Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
	}
	
	@Test
	public void danNearestCity1() throws IOException {
		final MeeshQuest m = new MeeshQuest("part2.dan.nearestcity1.xml");
        m.processInput();       
        expected = new File("part3.dan.nearestcity1.output.xml");
        Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
	}
	
	@Test
	public void danNearestCity2() throws IOException {
		final MeeshQuest m = new MeeshQuest("part2.dan.nearestcity2.xml");
        m.processInput();       
        expected = new File("part3.dan.nearestcity2.output.xml");
        Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
	}
	
	@Test
	public void danShortestPath2() throws IOException {
		final MeeshQuest m = new MeeshQuest("part2.dan.shortestpath2.xml");
        m.processInput();       
        expected = new File("part3.dan.shortestpath2.output.xml");
        Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
	}
	
	@Test
	public void danShortestPaths() throws IOException {
		final MeeshQuest m = new MeeshQuest("part2.dan.shortestPaths.xml");
        m.processInput();       
        expected = new File("part3.dan.shortestPaths.output.xml");
        Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
	}
	@Test
	public void danderShortestErrors() throws IOException {
		final MeeshQuest m = new MeeshQuest("part2.dan.shortestErrors.xml");
        m.processInput();       
        expected = new File("part3.dan.shortestErrors.output.xml");
        Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
	}
	
	@Test
	public void danderTestSpatialFunctions() throws IOException {
		final MeeshQuest m = new MeeshQuest("part2.dan.testSpatialFunctions.xml");
        m.processInput();       
        expected = new File("part3.dan.testSpatialFunctions.output.xml");
        Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
	}

	@Test
	public void deanShortestPath() throws IOException {
		final MeeshQuest m = new MeeshQuest("part2.dean.shortestPath.xml");
        m.processInput();       
        expected = new File("part3.dean.shortestpath.output.xml");
        Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
	}
	
	@Test
	public void jaredBergmanErrorTest() throws IOException {
		final MeeshQuest m = new MeeshQuest("part2.JaredBergman.errorTest.xml");
        m.processInput();       
        expected = new File("part3.JaredBergman.errorTest.output.xml");
        Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
	}
	
	@Test
	public void reeveRoadOnEdge() throws IOException {
		final MeeshQuest m = new MeeshQuest("part2.reeve.roadOnEdge.input.xml");
        m.processInput();       
        expected = new File("part3.reeve.roadOnEdge.output.xml");
        Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
	}
	
	@Test
	public void pm_insert_rangeroads() throws IOException {
		final MeeshQuest m = new MeeshQuest("pm_insert_rangeroads.input.xml");
        m.processInput();       
        expected = new File("part3.pm_insert_rangeroads.output.xml");
        Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
	}
	
	@Test
	public void printavl_printpm() throws IOException {
		final MeeshQuest m = new MeeshQuest("printavl_printpm.xml");
        m.processInput();       
        expected = new File("part3.printavl_printpm.output.xml");
        Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
	}
	
	@Test
	public void rangeRoadsTest() throws IOException {
		final MeeshQuest m = new MeeshQuest("printavl_printpm.xml");
        m.processInput();       
        expected = new File("part3.printavl_printpm.output.xml");
        Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
	}
	
}
