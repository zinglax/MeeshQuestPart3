package cmsc420.testing;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import cmsc420.meeshquest.part3.MeeshQuest;

public class StudentTests {

	private File xmlOutput = new File("test1.xml");
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
		final MeeshQuest m = new MeeshQuest("pm1_insert_delete2.xml");
        m.processInput();       
        expected = new File("part3.RobertBaxter.pm1_insert_delete.output.xml");
        Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
	}
	
	@Test
	public void peterEnnsInsertDelete() throws IOException {
		final MeeshQuest m = new MeeshQuest("part3.PeterEnns.InsertDeleteInterspersed.input.xml");
        m.processInput();       
        expected = new File("part3.PeterEnns.InsertDeleteInterspersed.output.xml");
        Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
	}
	
	@Test
	public void peterEnnsSmallInsertDelete() throws IOException {
		final MeeshQuest m = new MeeshQuest("part3.PeterEnns.SmallPM1RandomInsertDelete.input.xml");
        m.processInput();       
        expected = new File("part3.PeterEnns.SmallPM1RandomInsertDelete.output.xml");
        Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
	}
	
	@Test
	public void peterEnnsLargeInsertDelete() throws IOException {
		final MeeshQuest m = new MeeshQuest("part3.PeterEnns.LargePM1RandomInsertDelete.input.xml");
        m.processInput();       
        expected = new File("part3.PeterEnns.LargePM1RandomInsertDelete.output.xml");
        Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
	}
	
	@Test
	public void leDelete() throws IOException {
		final MeeshQuest m = new MeeshQuest("LeDelete.xml");
        m.processInput();       
        expected = new File("part3.ZehaoSui.delete6.output.xml");
        Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
	}
	
	@Test
	public void insert_delete() throws IOException {
		final MeeshQuest m = new MeeshQuest("pm1_insert_delete.xml");
        m.processInput();       
        expected = new File("part3.MichaelConstantine.pm1_insert_delete.output.xml");
        Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
	}
	
	@Test
	public void megaRange() throws IOException {
		final MeeshQuest m = new MeeshQuest("MegaRangeTest.xml");
        m.processInput();       
        expected = new File("part3.Kyle.MegaRangeTest.output.xml");
        Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
	}
	
}
