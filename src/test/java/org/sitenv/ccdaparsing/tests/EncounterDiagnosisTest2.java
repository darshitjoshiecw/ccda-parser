package org.sitenv.ccdaparsing.tests;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sitenv.ccdaparsing.model.CCDAEncounter;
import org.sitenv.ccdaparsing.processing.EncounterDiagnosesProcessor;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.File;

public class EncounterDiagnosisTest2 {
	
	private  static String CCDA_DOC = "src/test/resources/ModRef_AddAuthors_b1TocAmbCcdR21Aample1V13.xml";
	private  static CCDAEncounter encounter;
	private static EncounterDiagnosesProcessor encounterDiagnosesProcessor = new EncounterDiagnosesProcessor();

	@BeforeClass
	public  static void setUp() throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new File(CCDA_DOC));
		XPath xPath =  XPathFactory.newInstance().newXPath();
		encounter = encounterDiagnosesProcessor.retrieveEncounterDetails(xPath, doc).get();
	}

	@Test
	public void testEncounterAuthors(){
		Assert.assertEquals("CCDAEncounter Author time comparison test case failed", "199805011145-0800",encounter.getAuthor().getTime().getValue());
		Assert.assertEquals("CCDAEncounter Author ext value comparison test case failed", "555555555",encounter.getAuthor().getAuthorIds().get(0).getExtValue());
	}

	@Test
	public void testCCDAEncounterActivityAuthors(){
		Assert.assertEquals("CCDAEncounterActivity Author time comparison test case failed", "199805011145-0800",encounter.getEncActivities().get(0).getAuthor().getTime().getValue());
		Assert.assertEquals("CCDAEncounterActivity Author root value comparison test case failed", "1.1.1.1.1.1.1.2",encounter.getEncActivities().get(0).getAuthor().getAuthorIds().get(0).getRootValue());
	}

	@Test
	public void testCCDAEncounterDiagnosisAuthors(){
		Assert.assertEquals("CCDAEncounterDiagnosis Author time comparison test case failed", "199805011145-0800",encounter.getEncActivities().get(0).getDiagnoses().get(0).getAuthor().getTime().getValue());
		Assert.assertEquals("CCDAEncounterDiagnosis Author root value comparison test case failed", "1.1.1.1.1.1.1.2",encounter.getEncActivities().get(0).getDiagnoses().get(0).getAuthor().getAuthorIds().get(0).getRootValue());
		Assert.assertEquals("CCDAEncounterDiagnosis Author ext value comparison test case failed", "555555555",encounter.getEncActivities().get(0).getDiagnoses().get(0).getAuthor().getAuthorIds().get(0).getExtValue());
	}
}
