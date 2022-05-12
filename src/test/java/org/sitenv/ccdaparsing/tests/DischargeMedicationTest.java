package org.sitenv.ccdaparsing.tests;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sitenv.ccdaparsing.model.CCDADischargeMedication;
import org.sitenv.ccdaparsing.processing.MedicationProcessor;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.File;

public class DischargeMedicationTest {
	
	private static String CCDA_DOC = "src/test/resources/170.315_b1_toc_amb_ccd_r21_sample1_v1.xml";
	private static CCDADischargeMedication dischargeMedication;
	private static MedicationProcessor medicationProcessor = new MedicationProcessor();
	
	@BeforeClass
	public static void setUp() throws Exception {
		// removed fields to ensure no side effects with DocumentRoot
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new File(CCDA_DOC));
		XPath xPath =  XPathFactory.newInstance().newXPath();
		dischargeMedication = medicationProcessor.retrieveDischargeMedicationDetails(xPath, doc).get();
	}

	@Test
	public void testDischargeMedication() throws Exception{
		Assert.assertNotNull(dischargeMedication);
	}

	@Test
	public void testDischargeMedicationTemplateId(){
		Assert.assertEquals("Discharge Medication templateId test case failed","2.16.840.1.113883.10.20.22.2.11",dischargeMedication.getTemplateIds().get(0).getRootValue());
		Assert.assertEquals("Discharge Medication templateId test case failed","2015-08-01",dischargeMedication.getTemplateIds().get(1).getExtValue());
	}
	@Test
	public void testDischargeMedicationSectionCode(){
		Assert.assertEquals("Discharge Medication section code test case failed","10183-2",dischargeMedication.getSectionCode().getCode());
		Assert.assertEquals("Discharge Medication section code test case failed","2.16.840.1.113883.6.1",dischargeMedication.getSectionCode().getCodeSystem());
		Assert.assertEquals("Discharge Medication section code test case failed","LOINC",dischargeMedication.getSectionCode().getCodeSystemName());
		Assert.assertEquals("Discharge Medication section code test case failed","HOSPITAL DISCHARGE MEDICATIONS",dischargeMedication.getSectionCode().getDisplayName());
	}

	@Test
	public void testDischargeMedicationMedActivities(){
		Assert.assertEquals("Discharge Medication med activities test case failed","2.16.840.1.113883.10.20.22.4.16",dischargeMedication.getMedActivities().get(0).getTemplateIds().get(1).getRootValue());
		Assert.assertEquals("Discharge Medication med activities test case failed","2014-06-09",dischargeMedication.getMedActivities().get(0).getTemplateIds().get(1).getExtValue());
		Assert.assertEquals("Discharge Medication med activities test case failed",true,dischargeMedication.getMedActivities().get(0).getDuration().getLowPresent());
		Assert.assertEquals("Discharge Medication med activities test case failed","20150622",dischargeMedication.getMedActivities().get(0).getDuration().getLow().getValue());
		Assert.assertEquals("Discharge Medication med activities test case failed",true,dischargeMedication.getMedActivities().get(0).getDuration().getHighPresent());
		Assert.assertEquals("Discharge Medication med activities test case failed","20150701045900+0000",dischargeMedication.getMedActivities().get(0).getDuration().getHigh().getValue());
		Assert.assertEquals("Discharge Medication med activities test case failed","C38291",dischargeMedication.getMedActivities().get(0).getRouteCode().getCode());
		Assert.assertEquals("Discharge Medication med activities test case failed","2.16.840.1.113883.3.26.1.1",dischargeMedication.getMedActivities().get(0).getRouteCode().getCodeSystem());
		Assert.assertEquals("Discharge Medication med activities test case failed","NCI Thesaurus",dischargeMedication.getMedActivities().get(0).getRouteCode().getCodeSystemName());
		Assert.assertEquals("Discharge Medication med activities test case failed","Injection",dischargeMedication.getMedActivities().get(0).getRouteCode().getDisplayName());
		Assert.assertEquals("Discharge Medication med activities test case failed","1",dischargeMedication.getMedActivities().get(0).getDoseQuantity().getValue());
		Assert.assertEquals("Discharge Medication med activities test case failed","U",dischargeMedication.getMedActivities().get(0).getDoseQuantity().getUnits());
		Assert.assertEquals("Discharge Medication med activities test case failed","PQ",dischargeMedication.getMedActivities().get(0).getDoseQuantity().getXsiType());
		Assert.assertEquals("Discharge Medication med activities test case failed","2.16.840.1.113883.10.20.22.4.23",dischargeMedication.getMedActivities().get(0).getConsumable().getTemplateIds().get(1).getRootValue());
		Assert.assertEquals("Discharge Medication med activities test case failed","2014-06-09",dischargeMedication.getMedActivities().get(0).getConsumable().getTemplateIds().get(1).getExtValue());
		Assert.assertEquals("Discharge Medication med activities test case failed","731241",dischargeMedication.getMedActivities().get(0).getConsumable().getMedcode().getCode());
		Assert.assertEquals("Discharge Medication med activities test case failed","2.16.840.1.113883.6.88",dischargeMedication.getMedActivities().get(0).getConsumable().getMedcode().getCodeSystem());
		Assert.assertEquals("Discharge Medication med activities test case failed","RxNorm",dischargeMedication.getMedActivities().get(0).getConsumable().getMedcode().getCodeSystemName());
		Assert.assertEquals("Discharge Medication med activities test case failed","20201201172516-0600",dischargeMedication.getMedActivities().get(0).getAuthor().getTime().getValue());
		Assert.assertEquals("Discharge Medication med activities test case failed","2.16.840.1.113883.10.20.22.4.119",dischargeMedication.getMedActivities().get(0).getAuthor().getTemplateIds().get(0).getRootValue());
		Assert.assertEquals("Discharge Medication med activities test case failed","2019-10-01",dischargeMedication.getMedActivities().get(0).getAuthor().getTemplateIds().get(1).getExtValue());
		Assert.assertEquals("Discharge Medication med activities test case failed","1.2.840.114350.1.13.11511.1.7.1.1133",dischargeMedication.getMedActivities().get(0).getAuthor().getAuthorIds().get(0).getRootValue());
		Assert.assertEquals("Discharge Medication med activities test case failed","187338597",dischargeMedication.getMedActivities().get(0).getAuthor().getAuthorIds().get(0).getExtValue());
	}

}


