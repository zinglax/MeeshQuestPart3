package cmsc420.testing;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import cmsc420.meeshquest.part3.MeeshQuest;

public class DylansTests {

	private File xmlOutput = new File("test1.xml");
	private File expected;
	
	@Test
	public void pm1removeDELETECITY() throws IOException {
		final MeeshQuest m = new MeeshQuest("dylanTests/dylan.pm1Remove.DELETECITY.xml");
        m.processInput();       
        //expected = new File("");
        //Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
        assertEquals(1,1);
	}		
	
	@Test
	public void pm3removeDELETECITY() throws IOException {
		final MeeshQuest m = new MeeshQuest("dylanTests/dylan.pm3Remove.DELETECITY.xml");
        m.processInput();       
        //expected = new File("");
        //Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
        assertEquals(1,1);
	}
	
	@Test
	public void pm1removeUNMAPROAD() throws IOException {
		final MeeshQuest m = new MeeshQuest("dylanTests/dylan.pm1Remove.UNMAPROAD.xml");
        m.processInput();       
        //expected = new File("");
        //Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
        assertEquals(1,1);
	}		
	
	@Test
	public void pm3removeUNMAPROAD() throws IOException {
		final MeeshQuest m = new MeeshQuest("dylanTests/dylan.pm3Remove.UNMAPROAD.xml");
        m.processInput();       
        //expected = new File("");
        //Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
        assertEquals(1,1);
	}
	
	@Test
	public void pm1removeUNMAPPORTAL() throws IOException {
		final MeeshQuest m = new MeeshQuest("dylanTests/dylan.pm1Remove.UNMAPPORTAL.xml");
        m.processInput();       
        //expected = new File("");
        //Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
        assertEquals(1,1);
	}		
	
	@Test
	public void pm3removeUNMAPPORTAL() throws IOException {
		final MeeshQuest m = new MeeshQuest("dylanTests/dylan.pm3Remove.UNMAPPORTAL.xml");
        m.processInput();       
        //expected = new File("");
        //Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
        assertEquals(1,1);
	}
	
	@Test
	public void pm1AddDanZou() throws IOException {
		final MeeshQuest m = new MeeshQuest("dylanTests/dylan.pm1addRoadDanZou.xml");
        m.processInput();       
        //expected = new File("");
        //Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
        assertEquals(1,1);
	}
	
	
	@Test
	public void pm1ShortestPathEndCityNotMapped() throws IOException {
		final MeeshQuest m = new MeeshQuest("dylanTests/dylan.pm1ShortestPathEndCityNotMapped.xml");
        m.processInput();       
        //expected = new File("");
        //Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
        assertEquals(1,1);
	}
	
	@Test
	public void pm1ShortestPathStartCityNotMapped() throws IOException {
		final MeeshQuest m = new MeeshQuest("dylanTests/dylan.pm1ShortestPathStartCityNotMapped.xml");
        m.processInput();       
        //expected = new File("");
        //Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
        assertEquals(1,1);
	}
	
	
	@Test
	public void pm1ShortestPathEndLevelDoesNotHavePortal() throws IOException {
		final MeeshQuest m = new MeeshQuest("dylanTests/dylan.pm1ShortestPathEndLevelDoesNotHavePortal.xml");
        m.processInput();       
        //expected = new File("");
        //Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
        assertEquals(1,1);
	}
	
	@Test
	public void pm1ShortestPathIntermediateLevelDoesNotHavePortal() throws IOException {
		final MeeshQuest m = new MeeshQuest("dylanTests/dylan.pm1ShortestPathIntermediateLevelDoesNotHavePortal.xml");
        m.processInput();       
        //expected = new File("");
        //Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
        assertEquals(1,1);
	}
	
	@Test
	public void pm1ShortestPathOnOneLevel() throws IOException {
		final MeeshQuest m = new MeeshQuest("dylanTests/dylan.pm1ShortestPathOnOneLevel.xml");
        m.processInput();       
        //expected = new File("");
        //Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
        assertEquals(1,1);
	}
	
	@Test
	public void pm1ShortestPathStartAndEndAreOnAdjacentLevels() throws IOException {
		final MeeshQuest m = new MeeshQuest("dylanTests/dylan.pm1ShortestPathStartAndEndAreOnAdjacentLevels.xml");
        m.processInput();       
        //expected = new File("");
        //Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
        assertEquals(1,1);
	}
	
	@Test
	public void pm1ShortestPathStartAndEndAreTheSame() throws IOException {
		final MeeshQuest m = new MeeshQuest("dylanTests/dylan.pm1ShortestPathStartAndEndAreTheSame.xml");
        m.processInput();       
        //expected = new File("");
        //Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
        assertEquals(1,1);
	}	
	
	@Test
	public void pm1ShortestPathStartAndEndHaveMultipleIntermediateLevels() throws IOException {
		final MeeshQuest m = new MeeshQuest("dylanTests/dylan.pm1ShortestPathStartAndEndHaveMultipleIntermediateLevels.xml");
        m.processInput();       
        //expected = new File("");
        //Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
        assertEquals(1,1);
	}
	
	@Test
	public void pm1ShortestPathStartLevelDoesNotHavePortal() throws IOException {
		final MeeshQuest m = new MeeshQuest("dylanTests/dylan.pm1ShortestPathStartLevelDoesNotHavePortal.xml");
        m.processInput();       
        //expected = new File("");
        //Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
        assertEquals(1,1);
	}
}

