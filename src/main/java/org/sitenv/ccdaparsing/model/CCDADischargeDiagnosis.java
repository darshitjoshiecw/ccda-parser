package org.sitenv.ccdaparsing.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class CCDADischargeDiagnosis extends CCDAXmlSnippet {

	private static Logger log = LogManager.getLogger(CCDADischargeDiagnosis.class.getName());

	private ArrayList<CCDAII>    templateId;
	private CCDACode sectionCode;
	private ArrayList<CCDAProblemObs> diagnosis;
	
	private CCDAAuthor author;
	
	public void log() {
		
		if(sectionCode != null)
			log.info("Discharge Diagnosis Section Code = " + sectionCode.getCode());
		
		for(int j = 0; j < templateId.size(); j++) {
			log.info(" Tempalte Id [" + j + "] = " + templateId.get(j).getRootValue());
			log.info(" Tempalte Id Ext [" + j + "] = " + templateId.get(j).getExtValue());
		}
		
		for(int k = 0; k < diagnosis.size(); k++) {
			diagnosis.get(k).log();
		}
		
		if(author != null)
			author.log();
	}
	
	public CCDADischargeDiagnosis() {
		super();
		diagnosis = new ArrayList<CCDAProblemObs>();
		templateId = new ArrayList<CCDAII>();
	}

	public ArrayList<CCDAII> getTemplateId() {
		return templateId;
	}

	public void setTemplateId(ArrayList<CCDAII> templateId) {
		this.templateId = templateId;
	}

	public CCDACode getSectionCode() {
		return sectionCode;
	}

	public void setSectionCode(CCDACode sectionCode) {
		this.sectionCode = sectionCode;
	}

	public ArrayList<CCDAProblemObs> getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(ArrayList<CCDAProblemObs> diagnosis) {
		this.diagnosis = diagnosis;
	}

	public CCDAAuthor getAuthor() {
		return author;
	}

	public void setAuthor(CCDAAuthor author) {
		this.author = author;
	}
	
	
	
}
