package test;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import sources.Investigator;

public class TestInvestigator {
	Investigator instance;
	String inFileName;
	String outFileName;
	
	@Before 
	public void setUp() {
		 inFileName = "src\\resource\\investigatorInput.txt";
		 outFileName = "src\\resource\\investigatorOutput.txt";
		 instance = new Investigator();
	}
	
	@Test
	public void investigate() {
		int numOfGroups = instance.investigate(inFileName, outFileName);
		assertTrue(numOfGroups == 5);
	}
}
