package org.sitenv.ccdaparsing.tests;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sitenv.ccdaparsing.model.CCDAAuthor;
import org.sitenv.ccdaparsing.model.CCDACode;
import org.sitenv.ccdaparsing.model.CCDAEffTime;
import org.sitenv.ccdaparsing.model.CCDAII;
import org.sitenv.ccdaparsing.model.CCDAProcedure;
import org.sitenv.ccdaparsing.model.CCDAUDI;
import org.sitenv.ccdaparsing.processing.ProcedureProcessor;
import org.sitenv.ccdaparsing.processing.UDIProcessor;
import org.w3c.dom.Document;

public class UDITest {
	
	private static String CCDA_DOC = "src/test/resources/170.315_b1_toc_amb_ccd_r21_sample1_v2.xml";
	private static CCDAProcedure procedures;
	private static ArrayList<CCDAUDI>  patientUDIList;
	private static ArrayList<CCDAUDI>  udiList;
	private static ProcedureProcessor procedureProcessor = new ProcedureProcessor();
	private static UDIProcessor uDIProcessor = new UDIProcessor();
	
	@BeforeClass
	public static void setUp() throws Exception {
		// removed fields to ensure no side effects with DocumentRoot
		DocumentBuilderFactory factory = 
				DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new File(CCDA_DOC));
		XPath xPath =  XPathFactory.newInstance().newXPath();
		procedures = procedureProcessor.retrievePrcedureDetails(xPath, doc).get();
		patientUDIList = uDIProcessor.retrieveUDIDetails(procedures);
		
		udiList = new ArrayList<>();
		CCDAUDI udiOne = new CCDAUDI();
		
		ArrayList<CCDAII> udiTemplateIds = new ArrayList<CCDAII>();
		CCDAII udiTemplateId = new CCDAII();
		udiTemplateId.setRootValue("2.16.840.1.113883.10.20.22.4.37");
		udiTemplateIds.add(udiTemplateId);
		
		udiOne.setTemplateIds(udiTemplateIds);
		
		CCDACode deviceCodeOne = new CCDACode();
		deviceCodeOne.setCode("704708004");
		deviceCodeOne.setCodeSystem("2.16.840.1.113883.6.96");
		deviceCodeOne.setCodeSystemName("SNOMED CT");
		deviceCodeOne.setDisplayName("Cardiac resynchronization therapy implantable pacemaker");

		udiOne.setDeviceCode(deviceCodeOne);
		
		ArrayList<CCDAII> scopingEntityIdList = new ArrayList<CCDAII>();
		CCDAII scopingEntityId = new CCDAII();
		scopingEntityId.setRootValue("2.16.840.1.113883.3.3719");
		scopingEntityIdList.add(scopingEntityId);
		udiOne.setScopingEntityId(scopingEntityIdList);
		
		udiList.add(udiOne);
		udiList.add(udiOne);
	}
	
	
	@Test
	public void testUDI() throws Exception{
		Assert.assertNotNull(patientUDIList);
	}

	@Test
	public void testUdiList(){
		patientUDIList.get(0).getDeviceCode().setXmlString(null);
		patientUDIList.get(0).getDeviceCode().setLineNumber(null);
		patientUDIList.get(0).setLineNumber(null);
		patientUDIList.get(0).setXmlString(null);
		patientUDIList.get(1).getDeviceCode().setXmlString(null);
		patientUDIList.get(1).getDeviceCode().setLineNumber(null);
		patientUDIList.get(1).setLineNumber(null);
		patientUDIList.get(1).setXmlString(null);
		Assert.assertEquals("UDI test case failed",udiList.get(0),patientUDIList.get(0));
		Assert.assertEquals("UDI test case failed",udiList.get(1),patientUDIList.get(1));
	}
	
	@Test
	public void testUdiListTemplateIds(){
		Assert.assertEquals("UDI test case template Id failed",udiList.get(0).getTemplateIds(),patientUDIList.get(0).getTemplateIds());
	}
	
	@Test
	public void testUdiListUDIValue(){
		Assert.assertEquals("UDI test case UDI value failed",udiList.get(0).getUDIValue(),patientUDIList.get(0).getUDIValue());
	}
	
	@Test
	public void testUdiListDeviceCode(){
		patientUDIList.get(0).getDeviceCode().setXmlString(null);
		patientUDIList.get(0).getDeviceCode().setLineNumber(null);
		Assert.assertEquals("UDI test device code value failed",udiList.get(0).getDeviceCode(),patientUDIList.get(0).getDeviceCode());
	}
	
	@Test
	public void testUdiListScopingEntityId(){
		Assert.assertEquals("UDI test device code value failed",udiList.get(0).getScopingEntityId(),patientUDIList.get(0).getScopingEntityId());
	}

	@Test
	public void testCCDAUDIAuthor(){
		CCDAUDI ccdaudi = procedures.getProcActsProcs().get(2).getPatientUDI().get(2);
		Assert.assertEquals("CCDAUDI template root value comparison test case failed","2.16.840.1.113883.10.20.22.4.37", ccdaudi.getTemplateIds().get(0).getRootValue());
		Assert.assertEquals("CCDAUDI UDI root value comparison test case failed","2.16.840.1.113883.3.3719", ccdaudi.getUDIValue().get(0).getRootValue());
		Assert.assertEquals("CCDAUDI UDI ext value comparison test case failed","(01)00643169007222(17)160128(21)BLC200461H", ccdaudi.getUDIValue().get(0).getExtValue());
		Assert.assertEquals("CCDAUDI device code value comparison test case failed","704708004", ccdaudi.getDeviceCode().getCode());
		Assert.assertEquals("CCDAUDI device code system value comparison test case failed","2.16.840.1.113883.6.96", ccdaudi.getDeviceCode().getCodeSystem());
		Assert.assertEquals("CCDAUDI device code system name value comparison test case failed","SNOMED-CT", ccdaudi.getDeviceCode().getCodeSystemName());
		Assert.assertEquals("CCDAUDI device code display name comparison test case failed","Cardiac resynchronization therapy implantable pacemaker", ccdaudi.getDeviceCode().getDisplayName());
		Assert.assertEquals("CCDAUDI scoping entity id comparison test case failed","2.16.840.1.113883.3.3719", ccdaudi.getScopingEntityId().get(0).getRootValue());
		Assert.assertEquals("CCDAUDI Author time value comparison test case failed","199805011145-0800", ccdaudi.getAuthor().getTime().getValue());
		Assert.assertEquals("CCDAUDI Author root value comparison test case failed","1.1.1.1.1.1.1.2", ccdaudi.getAuthor().getAuthorIds().get(0).getRootValue());
		Assert.assertEquals("CCDAUDI Author ext value comparison test case failed","555555555", ccdaudi.getAuthor().getAuthorIds().get(0).getExtValue());
	}
}
