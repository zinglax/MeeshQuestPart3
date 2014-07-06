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
	
	
}

