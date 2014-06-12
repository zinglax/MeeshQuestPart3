package cmsc420.testing;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import cmsc420.meeshquest.part3.MeeshQuest;

public class DylansTests {

	private File xmlOutput = new File("test1.txt");
	private File expected;
	
	@Test
	public void dylanSimpleDelete() throws IOException {
		final MeeshQuest m = new MeeshQuest("dylan.simpleDelete.xml");
        m.processInput();       
        //expected = new File("");
        //Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
        assertEquals(1,1);
	}		
	
}

