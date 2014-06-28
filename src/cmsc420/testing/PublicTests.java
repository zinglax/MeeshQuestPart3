package cmsc420.testing;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import cmsc420.meeshquest.part3.MeeshQuest;

public class PublicTests {
	
	private File xmlOutput = new File("test1.xml");
	private File expected;

	@Test
	public void publicPortal() throws IOException {
		final MeeshQuest m = new MeeshQuest("part3.public.portal.input.xml");
        m.processInput();       
        expected = new File("part3.public.portal.output.xml");
        Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
	}
	
	@Test
	public void publicPrimary() throws IOException {
		final MeeshQuest m = new MeeshQuest("part3.public.primary.input.xml");
        m.processInput();       
        expected = new File("part3.public.primary.output.xml");
        Assert.assertEquals(FileUtils.readLines(expected), FileUtils.readLines(xmlOutput));
	}

}
