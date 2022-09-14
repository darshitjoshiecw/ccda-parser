package org.sitenv.ccdaparsing.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sitenv.ccdaparsing.dto.ContentValidationResult;
import org.sitenv.ccdaparsing.dto.enums.ContentValidationResultLevel;
import org.sitenv.ccdaparsing.util.ParserUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CCDANotesActivity extends CCDAXmlSnippet {

	private static Logger log = LogManager.getLogger(CCDANotesActivity.class.getName());
	
	private ArrayList<CCDAII>    			templateId;
	private CCDACode activityCode;
	private CCDADataElement statusCode;
	private CCDADataElement text;
	private CCDAEffTime effTime;
	private CCDAAuthor author;
	
	private CCDANotes parent;
	
	public CCDANotesActivity() { 
		
		templateId = new ArrayList<CCDAII>();
		
	}

	public static void compareNotesActivities(HashMap<String, CCDANotesActivity> refNotes,
			HashMap<String, CCDANotesActivity> subNotes, ArrayList<ContentValidationResult> results,
			ArrayList<CCDAAuthor> submittedAuthorsWithLinkedReferenceData) {

		log.info(" Start Comparing Notes Section level data ");
		
		// For each Notes activity in the Ref Model, check if it is present in the subCCDA Model.
		for(Map.Entry<String, CCDANotesActivity> ent: refNotes.entrySet()) {

			if(subNotes.containsKey(ent.getKey())) {
				log.info("Comparing Notes Activity since the section level matched ");
				String context = "Notes Section corresponding to the code " + ent.getKey();
				subNotes.get(ent.getKey()).compare(ent.getValue(), results, context, false,
						submittedAuthorsWithLinkedReferenceData);
			} else {
				// Error
				String error = "The scenario contains Notes data with code " + ent.getKey() +
						" , however there is no matching data in the submitted CCDA. ";
				ContentValidationResult rs = new ContentValidationResult(error, ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0" );
				results.add(rs);
			}
			
		}
		
	}
	
	public CCDANotes getParent() {
		return parent;
	}
	public void setParent(CCDANotes parent) {
		this.parent = parent;
	}
	public static Logger getLog() {
		return log;
	}
	public static void setLog(Logger log) {
		CCDANotesActivity.log = log;
	}
	public ArrayList<CCDAII> getTemplateId() {
		return templateId;
	}
	public void setTemplateId(ArrayList<CCDAII> templateId) {
		this.templateId = templateId;
	}
	public CCDACode getActivityCode() {
		return activityCode;
	}
	public void setActivityCode(CCDACode activityCode) {
		this.activityCode = activityCode;
	}
	public CCDADataElement getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(CCDADataElement statusCode) {
		this.statusCode = statusCode;
	}
	public CCDADataElement getText() {
		return text;
	}
	public void setText(CCDADataElement text) {
		this.text = text;
	}
	public CCDAEffTime getEffTime() {
		return effTime;
	}
	public void setEffTime(CCDAEffTime effTime) {
		this.effTime = effTime;
	}
	public CCDAAuthor getAuthor() {
		return author;
	}
	public void setAuthor(CCDAAuthor author) {
		this.author = author;
	}
	
	
	public void log() {
		
		log.info("***Notes Activity ***");
		
		if(activityCode != null)
			log.info("Activity Code = " + activityCode.getCode());
		
		if(statusCode != null)
			log.info("Status Code = " + statusCode.getValue());
	
		for(int j = 0; j < templateId.size(); j++) {
			log.info(" Tempalte Id [" + j + "] = " + templateId.get(j).getRootValue());
			log.info(" Tempalte Id Ext [" + j + "] = " + templateId.get(j).getExtValue());
		}
		
		if(effTime != null) 
			//effTime.log();
		
		if(text != null)
			log.info(" Text = " + text.getValue());
		
		if(author != null) {
			author.log();
		}
		
	}
	
	public static HashMap<String, CCDANotesActivity> getAllNotesActivities(ArrayList<CCDANotesActivity> acts) {
		
		HashMap<String, CCDANotesActivity> allacts = new HashMap<String, CCDANotesActivity>();

		for (CCDANotesActivity act : acts) {
			if (act.getActivityCode() != null && act.getActivityCode().getCode() != null) {
				allacts.put(act.getActivityCode().getCode(), act);
			}
		}

		return allacts;
	}
	
	public static void compareNotesActivity(ArrayList<CCDANotesActivity> refNotesActivity, 
			ArrayList<CCDANotesActivity> subNotesActivity, 	ArrayList<ContentValidationResult> results, String context) {
		log.info(" Start Comparing Notes Activity ");
		
		HashMap<String, CCDANotesActivity> refNotesActs = getAllNotesActivities(refNotesActivity);
		HashMap<String, CCDANotesActivity> subNotesActs = getAllNotesActivities(subNotesActivity);

		if ((refNotesActs != null && refNotesActs.size() > 0) && 
			(subNotesActs != null && subNotesActs.size() > 0)) {

			// we have note activities in ref and sub, compare them...
			log.info("Notes present in both models, Size of Ref Notes Activities = " + refNotesActs.size()
					+ " : Size of Sub Notes Activities = " + subNotesActs.size());
			CCDANotesActivity.compareNotesActivityEntryLevel(refNotesActs, subNotesActs, results, context);

		} else if ((refNotesActs != null && refNotesActs.size() > 0)
				&& (subNotesActs == null || subNotesActs.size() == 0)) {

			// handle the case where the Notes section does not exist in the submitted CCD
			log.info(" Scenario requires Notes Activity Entry "
					+ "but submitted document does not contain Notes Activity Entrydata");
			ContentValidationResult rs = new ContentValidationResult(
					"The scenario requires data related to patient's Notes Activity Entry, "
							+ "but the submitted C-CDA does not contain Notes Activity Entry data.",
					ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0");
			results.add(rs);

		} else {
			log.info("Ref Model does not have Notes Activity Entries for comparison ");
		}
	}
	
	public static void compareNotesActivityEntryLevel(HashMap<String, CCDANotesActivity> refNotes,
			HashMap<String, CCDANotesActivity> subNotes, ArrayList<ContentValidationResult> results, String context) {
		log.info(" Start Comparing Notes Activity Entry level data ");

		// For each Notes section in the Ref Model, check if it is present in the subCCDA Model.
		for (Map.Entry<String, CCDANotesActivity> ent : refNotes.entrySet()) {

			if (subNotes.containsKey(ent.getKey())) {
				// Compare
				log.info("Comparing Notes Activity Entry since we found the entry with the code. " + ent.getKey());
				context += ", Notes Activity Entry corresponding to the code " + ent.getKey();
				subNotes.get(ent.getKey()).compare(ent.getValue(), results, context, true);
			} else {
				// Fire Error
				String error = "The scenario contains Notes Activity Entry data with code " + ent.getKey()
						+ " , however there is no matching data in the submitted CCDA. ";
				ContentValidationResult rs = new ContentValidationResult(error, ContentValidationResultLevel.ERROR,
						"/ClinicalDocument", "0");
				results.add(rs);
			}
			
		}
	}

	public void compare(CCDANotesActivity refNote, ArrayList<ContentValidationResult> results, String context,
			boolean parentComparison) {
		compare(refNote, results, context, parentComparison, null);
	}
	
	public void compare(CCDANotesActivity refNote, ArrayList<ContentValidationResult> results, String context,
			boolean parentComparison, ArrayList<CCDAAuthor> submittedAuthorsWithLinkedReferenceData) {
		log.info("Comparing Notes Activity Entry ");
		
		// Handle Template Ids
		ParserUtilities.compareTemplateIds(refNote.getTemplateId(), templateId, results, context);
		
		// Compare Effective Times
		String elementNameTime = "Effective Time for " + context;
		ParserUtilities.compareEffectiveTimeValue(refNote.getEffTime(), getEffTime(), results, elementNameTime);
		
		// Compare Activity Codes 
		String elementNameVal = " Comparing Notes Activity code element for " + context;
		ParserUtilities.compareCode(refNote.getActivityCode(), activityCode, results, elementNameVal);
		
		// Compare Status Codes 
		String elementNameVal1 = " Comparing Notes Activity Status code element for " + context;
		ParserUtilities.compareDataElement(refNote.getStatusCode(), statusCode, results, elementNameVal1);
		
		// Comparing Author for Entry
		String elementVal2 = "Note Activity Author Entry for " + context;
		ParserUtilities.compareAuthor(refNote.getAuthor(), author, results, elementVal2,
				submittedAuthorsWithLinkedReferenceData);
		
		if(parentComparison && (this.parent != null) && (refNote.parent != null)) {
			log.info(" Comparing Parent Notes Section details ");
			
			// Handle Template Ids
			String elementNameVal3 = "Comparing Notes Section tempalte Ids " + context;
			ParserUtilities.compareTemplateIds(refNote.parent.getSectionTemplateId(), parent.getSectionTemplateId(), results, elementNameVal3);
			
			// Compare section Codes 
			elementNameVal3 = "Comparing Notes Section code element for " + context;
			ParserUtilities.compareCode(refNote.parent.getSectionCode(), parent.getSectionCode(), results, elementNameVal3);
		}
		
		// Maybe add check to compare TEXT.
		log.info(" TODO  :  Add Check for Text which only checks null ");
	}
	
	// TODO: May want to send in data like: What section it's in (using code like
	// old impl) so we can identify for the user and properly compare to like-items
	// in the logic. The method which would call this, simply loops through, which is not going to be valid.
	// It's just a proof of concept for now.
	public void compareAuthor(CCDANotesActivity subNoteActivity, ArrayList<ContentValidationResult> results,
			boolean curesUpdate, ArrayList<CCDAAuthor> authorsWithLinkedReferenceData) {
		log.info("Comparing Authors for current Note Activity");
		ArrayList<CCDAAuthor> refAllNoteActivityAuths = new ArrayList<CCDAAuthor>(Arrays.asList(this.getAuthor()));
		ArrayList<CCDAAuthor> subAllNoteActivityAuths = new ArrayList<CCDAAuthor>(Arrays.asList(
				  subNoteActivity != null && subNoteActivity.getAuthor() != null
				? subNoteActivity.getAuthor()
				: null));
		CCDAAuthor.compareAuthors(refAllNoteActivityAuths, subAllNoteActivityAuths, results, "Note Activity",
				authorsWithLinkedReferenceData);
	}	
	
}
