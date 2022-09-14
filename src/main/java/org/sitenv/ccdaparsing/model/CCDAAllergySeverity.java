package org.sitenv.ccdaparsing.model;

import java.util.ArrayList;

public class CCDAAllergySeverity extends CCDAXmlSnippet{

	private ArrayList<CCDAII>				templateIds;
	private CCDACode						severity;
	private CCDADataElement referenceText;
	private CCDAAuthor		author;
	
	public ArrayList<CCDAII> getTemplateIds() {
		return templateIds;
	}

	public void setTemplateIds(ArrayList<CCDAII> templateIds) {
		this.templateIds = templateIds;
	}

	public CCDACode getSeverity() {
		return severity;
	}

	public void setSeverity(CCDACode severity) {
		this.severity = severity;
	}
	
	public CCDADataElement getReferenceText() {
		return referenceText;
	}

	public void setReferenceText(CCDADataElement referenceText) {
		this.referenceText = referenceText;
	}

	public CCDAAuthor getAuthor() {
		return author;
	}

	public void setAuthor(CCDAAuthor author) {
		this.author = author;
	}

	public CCDAAllergySeverity()
	{
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((severity == null) ? 0 : severity.hashCode());
		result = prime * result
				+ ((templateIds == null) ? 0 : templateIds.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CCDAAllergySeverity other = (CCDAAllergySeverity) obj;
		if (severity == null) {
			if (other.severity != null)
				return false;
		} else if (!severity.equals(other.severity))
			return false;
		if (templateIds == null) {
			if (other.templateIds != null)
				return false;
		} else if (!templateIds.equals(other.templateIds))
			return false;
		return true;
	}
}
