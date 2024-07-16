package org.sitenv.ccdaparsing.processing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sitenv.ccdaparsing.model.CCDAAuthor;
import org.sitenv.ccdaparsing.model.CCDARefModel;
import org.sitenv.ccdaparsing.util.ApplicationUtil;
import org.sitenv.ccdaparsing.util.ParserUtilities;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;
import java.util.concurrent.Future;

@Service
public class AuthorParser {

	private static Logger log = LogManager.getLogger(AuthorParser.class.getName());
	
	public void parse(Document doc, CCDARefModel model, boolean curesUpdate) throws XPathExpressionException {
    	log.info(" *** Parsing Author *** ");
    	//model.setAuthorsFromHeader(retrieveAuthorsFromHeader(doc));
    	// TODO: For performance reasons, consider only running on sub model, not ref models.
    	// I'm not sure a need yet for the linked references on the scenarios, but there may be one.
    	// However, since scenarios are only loaded once, it's not all that important for performance either. 
    	// Subs are loaded every time, but there's only one (and we need that data). That's the current purpose of the call.
    	//model.setAuthorsWithLinkedReferenceData(retrieveAuthorsWithLinkedReferenceData(doc));
	}

	@Async()
	public Future<ArrayList<CCDAAuthor>> retrieveAuthorsFromHeader(Document doc) throws XPathExpressionException {
		ArrayList<CCDAAuthor> auths = new ArrayList<CCDAAuthor>();
		CCDAAuthor auth = null;
		NodeList docAuths = (NodeList)(CCDAConstants.AUTHORS_FROM_HEADER_EXP.evaluate(doc, XPathConstants.NODESET));
		
		for (int i = 0; i < docAuths.getLength(); i++) {
			
			log.info("Parsing Author at document level ");
			Element authElement = ApplicationUtil.getCloneNode((Element) docAuths.item(i));
			
			auth = ParserUtilities.readAuthor(authElement);
			
			if(auth != null) {
				log.info(" Adding header Author ");
				auths.add(auth);
			}				
		}
		
		return new AsyncResult<>(auths);
	}

	@Async()
	public Future<ArrayList<CCDAAuthor>> retrieveAuthorsWithLinkedReferenceData(Document doc)
			throws XPathExpressionException {
		ArrayList<CCDAAuthor> auths = new ArrayList<CCDAAuthor>();
		CCDAAuthor auth = null;
		NodeList bodyAuths = (NodeList) 
				CCDAConstants.AUTHORS_WITH_LINKED_REFERENCE_DATA_EXP.evaluate(doc, XPathConstants.NODESET);

		for (int i = 0; i < bodyAuths.getLength(); i++) {
			log.info(
					"Parsing Authors with linked reference data from the entire document "
					+ "(header, sections, entries, and statements)");
			Element authElement = ApplicationUtil.getCloneNode((Element) bodyAuths.item(i));
			auth = ParserUtilities.readAuthor(authElement);
			if (auth != null) {
				log.info(" Adding author with linked reference data ");
				auths.add(auth);
			}
		}

		return new AsyncResult<>(auths);
	}	

}
