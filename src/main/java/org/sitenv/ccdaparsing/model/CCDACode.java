package org.sitenv.ccdaparsing.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sitenv.ccdaparsing.dto.ContentValidationResult;
import org.sitenv.ccdaparsing.dto.enums.ContentValidationResultLevel;

import java.util.ArrayList;

public class CCDACode extends CCDADataElement {
	private static Logger log = LogManager.getLogger(CCDAProblemObs.class.getName());
	private String  code;
	private String  codeSystem;
	private String  codeSystemName;
	private String  displayName;
	private String valueSetOid;
	private ArrayList<CCDACode> translations;
	private String nullFlavor;

	public Boolean matches(CCDACode cd, ArrayList<ContentValidationResult> results, String elementName) {

		if( (code != null) && (cd.getCode() != null) &&
				(codeSystem != null) && (cd.getCodeSystem() != null) &&
				(code.equalsIgnoreCase(cd.getCode())) &&
				(codeSystem.equalsIgnoreCase(cd.getCodeSystem()))) {
			return true;
		}
		else
		{
			String error = "The " + elementName + " : Code = " + ((code != null)?code:"None Specified")
					+ " , CodeSystem = " + ((codeSystem != null)?codeSystem:"None Specified")
					+ " do not match the submitted CCDA : code = " + ((cd.getCode() != null)?cd.getCode():"None Specified")
					+ " , and CodeSystem = " + ((cd.getCodeSystem() != null)?cd.getCodeSystem():"None Specified");

			ContentValidationResult rs = new ContentValidationResult(error, ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0" );
			results.add(rs);
			return false;
		}

	}

	public Boolean codeMatches(CCDACode cd, ArrayList<ContentValidationResult> results, String elementName) {

		if( (code != null) && (cd.getCode() != null) &&
				(code.equalsIgnoreCase(cd.getCode())) ) {
			return true;
		}
		else
		{
			String error = "The " + elementName + " : Code = " + ((code != null)?code:"None Specified")
					+ " does not match the submitted CCDA : code = " + ((cd.getCode() != null)?cd.getCode():"None Specified");

			ContentValidationResult rs = new ContentValidationResult(error, ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0" );
			results.add(rs);
			return false;

		}
	}

	public Boolean isCodePresent(CCDACode cd) {

		// Check if the code is present in this element's code
		if( (code != null) && (cd.getCode() != null) &&
				(codeSystem != null) && (cd.getCodeSystem() != null) &&
				(code.equalsIgnoreCase(cd.getCode())) &&
				(codeSystem.equalsIgnoreCase(cd.getCodeSystem()))) {

			log.info(" Code : " + code + " is same as passed in code. ");
			return true;
		}

		// Check if the code is present in this element's translation
		for(CCDACode trans : translations) {

			if( (trans.getCode() != null) && (cd.getCode() != null) &&
					(trans.getCodeSystem() != null) && (cd.getCodeSystem() != null) &&
					(trans.getCode().equalsIgnoreCase(cd.getCode())) &&
					(trans.getCodeSystem().equalsIgnoreCase(cd.getCodeSystem()))) {

				log.info(" Translation Code : " + trans.getCode() + " is same as passed in code.");
				return true;
			}
		}

		log.info(" Passed in Code : " + ((cd.getCode() != null)?cd.getCode():"Null") + " is not present ");
		return false;
	}

	public Boolean codeEquals(CCDACode cd) {

		if( (code != null) && (cd.getCode() != null) &&
				(codeSystem != null) && (cd.getCodeSystem() != null) &&
				(code.equalsIgnoreCase(cd.getCode())) &&
				(codeSystem.equalsIgnoreCase(cd.getCodeSystem()))) {
			return true;
		}
		return false;
	}

	public String getValueSetOid() {
		return valueSetOid;
	}

	public void setValueSetOid(String valueSetOid) {
		this.valueSetOid = valueSetOid;
	}

	public ArrayList<CCDACode> getTranslations() {
		return translations;
	}

	public void setTranslations(ArrayList<CCDACode> translations) {
		this.translations = translations;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCodeSystem() {
		return codeSystem;
	}

	public void setCodeSystem(String codeSystem) {
		this.codeSystem = codeSystem;
	}

	public String getCodeSystemName() {
		return codeSystemName;
	}

	public void setCodeSystemName(String codeSystemName) {
		this.codeSystemName = codeSystemName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public String getNullFlavor() {
		return nullFlavor;
	}

	public void setNullFlavor(String nullFlavor) {
		this.nullFlavor = nullFlavor;
	}

	public CCDACode()
	{
		super();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		CCDACode other = (CCDACode) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (codeSystem == null) {
			if (other.codeSystem != null)
				return false;
		} else if (!codeSystem.equals(other.codeSystem))
			return false;
		if (codeSystemName == null) {
			if (other.codeSystemName != null)
				return false;
		} else if (!codeSystemName.equals(other.codeSystemName))
			return false;
		if (displayName == null) {
			if (other.displayName != null)
				return false;
		} else if (!displayName.equals(other.displayName))
			return false;
		return true;
	}

	public void addTranslation(CCDACode transCode) {
		this.translations.add(transCode);

	}

	public Boolean isTranslationPresent() {

		if(translations != null &&
				translations.size() > 0 )
			return true;
		else
			return false;
	}
	
}
