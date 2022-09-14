package org.sitenv.ccdaparsing.processing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sitenv.ccdaparsing.model.CCDACarePlanSections;
import org.sitenv.ccdaparsing.model.CCDAII;
import org.sitenv.ccdaparsing.util.ParserUtilities;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.util.concurrent.Future;

@Service
public class CarePlanSectionsParser {
	
	private static Logger log = LogManager.getLogger(CarePlanSectionsParser.class.getName());
	
	private static final CCDAII INTERVENTIONS_SECTION_V3 = 
			new CCDAII("2.16.840.1.113883.10.20.21.2.3", "2015-08-01");
	private static final CCDAII HEALTH_STATUS_EVALUATIONS_AND_OUTCOMES_SECTION = 
			new CCDAII("2.16.840.1.113883.10.20.22.2.61"); 
	

	@Async()
	public Future<CCDACarePlanSections> getSuggestedSections(Document doc) throws XPathExpressionException {
		CCDACarePlanSections carePlanSections = new CCDACarePlanSections();
		
		Element interventions = (Element) CCDAConstants.INTERVENTIONS_SECTION_V3_EXP.evaluate(doc, XPathConstants.NODE);				
		if(interventions != null) {
			log.info("interventions tagName: " + interventions.getTagName());
			log.info("Setting: Document HAS Interventions Section (V3) 2.16.840.1.113883.10.20.21.2.3:2015-08-01");
			carePlanSections.setInterventionsSectionV3(true);
			
			carePlanSections.setAuthor(ParserUtilities.readAuthor((Element) CCDAConstants.REL_AUTHOR_EXP.
					evaluate(interventions, XPathConstants.NODE)));
		} else {
			log.info("Document does NOT have Interventions Section (V3) 2.16.840.1.113883.10.20.21.2.3:2015-08-01");
		}
		
		Element healthStatusEvals = (Element) CCDAConstants.HEALTH_STATUS_EVALUATIONS_AND_OUTCOMES_SECTION_EXP
				.evaluate(doc, XPathConstants.NODE);
		if(healthStatusEvals != null) {
			log.info("healthStatusEvals tagName: " + healthStatusEvals.getTagName());
			log.info("Setting: Document HAS Health Status Evaluations and Outcomes Section 2.16.840.1.113883.10.20.22.2.61");
			carePlanSections.setHealthStatusEvaluationsAndOutcomesSection(true);	
			
			carePlanSections.setAuthor(ParserUtilities.readAuthor((Element) CCDAConstants.REL_AUTHOR_EXP.
					evaluate(healthStatusEvals, XPathConstants.NODE)));
		} else {
			log.info("Document does NOT have Health Status Evaluations and Outcomes Section 2.16.840.1.113883.10.20.22.2.61");
		}
		
		return new AsyncResult<>(carePlanSections);
	}
	
}
