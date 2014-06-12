package cmsc420.testing;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import cmsc420.meeshquest.part3.MeeshQuest;

public class StudentTests {

	private File xmlOutput = new File("test1.txt");
	private File expected;
	
	@Test
	public void danzouInsertDelete() throws IOException {
		final MeeshQuest m = new MeeshQuest("part3.danzou.insertDelete.xml");
        m.processInput();       
        expected = new File("part3.danzou.insertDelete.output.xmld");
        Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
	}
	
	
	@Test
	public void pm1_insert_delete() throws IOException {
		final MeeshQuest m = new MeeshQuest("pm1_insert_delete.xml");
        m.processInput();       
        expected = new File("part3.RobertBaxter.pm1_insert_delete.output.xml");
        Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
	}
}
