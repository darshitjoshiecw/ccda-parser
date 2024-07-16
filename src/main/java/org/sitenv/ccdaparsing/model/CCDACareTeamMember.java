package org.sitenv.ccdaparsing.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class CCDACareTeamMember extends CCDAXmlSnippet {

	private static Logger log = LogManager.getLogger(CCDACareTeamMember.class.getName());

	private ArrayList<CCDAParticipant> members;

	private ArrayList<CCDAII>       		sectionTemplateId;
	private CCDACode sectionCode;
	private CCDACode statusCode;
	private CCDAEffTime careTeamEffectiveTime;
	private ArrayList<CCDACareTeamMemberAct> memberActs;
	private CCDAAuthor author;
	private String careTeamName;
	private CCDADataElement referenceText;

	public void log() {

		for (int i = 0; i < members.size(); i++) {
			members.get(i).log();
		}

		if(author != null)
			author.log();

		for(int j = 0; j < memberActs.size(); j++) {
			memberActs.get(j).log();
		}
	}

	public CCDACareTeamMember()
	{
		members = new ArrayList<CCDAParticipant>();
		memberActs = new ArrayList<CCDACareTeamMemberAct>();
	}

	public CCDAEffTime getCareTeamEffectiveTime() {
		return careTeamEffectiveTime;
	}

	public void setCareTeamEffectiveTime(CCDAEffTime careTeamEffectiveTime) {
		this.careTeamEffectiveTime = careTeamEffectiveTime;
	}

	public CCDACode getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(CCDACode statusCode) {
		this.statusCode = statusCode;
	}

	public ArrayList<CCDAII> getSectionTemplateId() {
		return sectionTemplateId;
	}

	public void setSectionTemplateId(ArrayList<CCDAII> sectionTemplateId) {
		this.sectionTemplateId = sectionTemplateId;
	}

	public CCDACode getSectionCode() {
		return sectionCode;
	}

	public void setSectionCode(CCDACode sectionCode) {
		this.sectionCode = sectionCode;
	}

	public ArrayList<CCDACareTeamMemberAct> getMemberActs() {
		return memberActs;
	}

	public void setCareTeamName(String careTeamName) {
		this.careTeamName = careTeamName;
	}

	public String getCareTeamName() {
		return careTeamName;
	}

	public CCDADataElement getReferenceText() {
		return referenceText;
	}

	public void setReferenceText(CCDADataElement referenceText) {
		this.referenceText = referenceText;
	}

	public void setMemberActs(ArrayList<CCDACareTeamMemberAct> memberActs) {
		this.memberActs = memberActs;
	}

	public CCDAAuthor getAuthor() {
		return author;
	}

	public void setAuthor(CCDAAuthor author) {
		this.author = author;
	}

	public ArrayList<CCDAParticipant> getMembers() {
		return members;
	}

	public void setMembers(ArrayList<CCDAParticipant> members) {
		this.members = members;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((members == null) ? 0 : members.hashCode());
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
		CCDACareTeamMember other = (CCDACareTeamMember) obj;
		if (members == null) {
			if (other.members != null)
				return false;
		} else if (!members.equals(other.members))
			return false;
		return true;
	}
}
