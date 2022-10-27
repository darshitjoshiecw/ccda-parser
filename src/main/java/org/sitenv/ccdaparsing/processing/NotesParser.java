package org.sitenv.ccdaparsing.processing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sitenv.ccdaparsing.model.CCDANotes;
import org.sitenv.ccdaparsing.model.CCDANotesActivity;
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
public class NotesParser {

private static Logger log = LogManager.getLogger(NotesParser.class.getName());
	
	public void parse(Document doc, CCDARefModel model, boolean curesUpdate) throws XPathExpressionException {
    	
    	//model.setNotes(retrieveNotesDetails(doc));
    	
    	//model.setNotesEntries(retrieveNotesActivities(doc));
    	
    	if(model.getNotesEntries() != null)
    		log.info(" Notes Activity Entries found : " + model.getNotesEntries().size());
    	
	}

	@Async()
	public Future<ArrayList<CCDANotesActivity>> retrieveNotesActivities(Document doc) throws XPathExpressionException {
		
		ArrayList<CCDANotesActivity> notesActivities = null;
		NodeList notesActivityNodes = (NodeList) CCDAConstants.NOTES_ACTIVITY_EXPRESSION.evaluate(doc, XPathConstants.NODESET);
		
		if( !ParserUtilities.isNodeListEmpty(notesActivityNodes)) {
		
			log.info(" Found Notes Activities Entries ");
			
			
			// Grab the notes activities
			notesActivities = ParserUtilities.readNotesActivity(notesActivityNodes, null);
				
		}
		
		return new AsyncResult<>(notesActivities);
	}

	@Async()
	public Future<ArrayList<CCDANotes>> retrieveNotesDetails(Document doc) throws XPathExpressionException {
		
		ArrayList<CCDANotes> notes = null;
		NodeList sectionNodes = (NodeList) CCDAConstants.NOTES_EXPRESSION.evaluate(doc, XPathConstants.NODESET);
		
		if( !ParserUtilities.isNodeListEmpty(sectionNodes)) {		
		
			notes = new ArrayList<CCDANotes>();
			log.info(" Found Notes sections ");
			
			for(int i = 0; i < sectionNodes.getLength(); i++) {

				Element elem = ApplicationUtil.getCloneNode((Element) sectionNodes.item(i));
				
				CCDANotes note = new CCDANotes();
				
				note.setSectionTemplateId(ParserUtilities.readTemplateIdList((NodeList) CCDAConstants.REL_TEMPLATE_ID_EXP.
						evaluate(elem, XPathConstants.NODESET)));
				
				note.setSectionCode(ParserUtilities.readCode((Element) CCDAConstants.REL_CODE_EXP.
					evaluate(elem, XPathConstants.NODE)));
				
				// Grab the notes activities
				note.setNotesActivity(ParserUtilities.readNotesActivity((NodeList) CCDAConstants.REL_NOTES_ACTIVITY_EXPRESSION.
						evaluate(elem, XPathConstants.NODESET), note));
				
				note.setAuthor(ParserUtilities.readAuthor((Element) CCDAConstants.REL_AUTHOR_EXP.
						evaluate(elem, XPathConstants.NODE)));
				
				notes.add(note);
			}
			
		}
		
		return new AsyncResult<>(notes);
	}
	
	
}
