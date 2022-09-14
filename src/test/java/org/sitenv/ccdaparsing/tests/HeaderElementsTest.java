package org.sitenv.ccdaparsing.tests;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sitenv.ccdaparsing.model.CCDAHeaderElements;
import org.sitenv.ccdaparsing.processing.CCDAHeaderParser;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.File;

public class HeaderElementsTest {

	private  static String CCDA_DOC = "src/test/resources/170.315_b1_toc_amb_ccd_r21_sample1_v1.xml";
	private  static CCDAHeaderElements headerElements;
	private static CCDAHeaderParser headerParser = new CCDAHeaderParser();

	@BeforeClass
	public  static void setUp() throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new File(CCDA_DOC));
		XPath xPath =  XPathFactory.newInstance().newXPath();
		headerElements = headerParser.getHeaderElements(doc,false).get();
	}

	@Test
	public void testHeaderElementsDocTemplates(){
		Assert.assertEquals("CCDAHeaderElements docTemplates root value comparison test case failed", "2.16.840.1.113883.10.20.22.1.1",headerElements.getDocTemplates().get(0).getRootValue());
		Assert.assertEquals("CCDAHeaderElements docTemplates ext value comparison test case failed", "2015-08-01",headerElements.getDocTemplates().get(0).getExtValue());
	}

	@Test
	public void testHeaderElementsDocCodes(){
		Assert.assertEquals("CCDAHeaderElements DocCodes code comparison test case failed", "34133-9",headerElements.getDocCode().getCode());
		Assert.assertEquals("CCDAHeaderElements DocCodes code system comparison test case failed", "2.16.840.1.113883.6.1",headerElements.getDocCode().getCodeSystem());
		Assert.assertEquals("CCDAHeaderElements DocCodes code system name comparison test case failed", "LOINC",headerElements.getDocCode().getCodeSystemName());
		Assert.assertEquals("CCDAHeaderElements DocCodes ext display name comparison test case failed", "Summarization of Episode Note",headerElements.getDocCode().getDisplayName());
	}
}
