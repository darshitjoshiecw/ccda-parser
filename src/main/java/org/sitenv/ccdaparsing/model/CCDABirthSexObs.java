package org.sitenv.ccdaparsing.model;

import java.util.ArrayList;

public class CCDABirthSexObs {

	private ArrayList<CCDAII>					templateIds;
	private CCDACode                            birthSexObsCode;
	private CCDACode							sexCode;
	private CCDAEffTime							observationTime;
	
	private CCDAAuthor	author;

	public CCDABirthSexObs() {
		templateIds = new ArrayList<CCDAII>();
	}

	public ArrayList<CCDAII> getTemplateIds() {
		return templateIds;
	}

	public void setTemplateIds(ArrayList<CCDAII> ids) {
		
		if(ids != null)
			this.templateIds = ids;
	}

	public CCDACode getBirthSexObsCode() {
		return birthSexObsCode;
	}

	public void setBirthSexObsCode(CCDACode birthSexObsCode) {
		this.birthSexObsCode = birthSexObsCode;
	}

	public CCDACode getSexCode() {
		return sexCode;
	}

	public void setSexCode(CCDACode sexCode) {
		this.sexCode = sexCode;
	}

	public CCDAEffTime getObservationTime() {
		return observationTime;
	}

	public void setObservationTime(CCDAEffTime observationTime) {
		this.observationTime = observationTime;
	}

	public CCDAAuthor getAuthor() {
		return author;
	}

	public void setAuthor(CCDAAuthor author) {
		this.author = author;
	}
	
	
}
