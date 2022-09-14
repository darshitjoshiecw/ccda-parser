package org.sitenv.ccdaparsing.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class CCDAConsumable extends CCDAXmlSnippet {

	private static Logger log = LogManager.getLogger(CCDAConsumable.class.getName());

	ArrayList<CCDAII> 			templateIds;
	CCDACode					medcode;
	ArrayList<CCDACode>			translations;
	CCDADataElement				lotNumberText;
	CCDADataElement				manufacturingOrg;

	public Boolean hasSameMedCode(CCDAConsumable refConsumable) {

		if(medcode != null &&
				refConsumable.getMedcode() != null )
			return medcode.codeEquals(refConsumable.getMedcode());

		return false;
	}

	public void log() {

		if(medcode != null)
			log.info("Consumable Code = " + medcode.getCode());

		for(int l = 0; l < translations.size(); l++) {
			log.info("Consumable Translation Code = " + translations.get(l).getCode());
		}

		for(int j = 0; j < templateIds.size(); j++) {
			log.info(" Tempalte Id [" + j + "] = " + templateIds.get(j).getRootValue());
			log.info(" Tempalte Id Ext [" + j + "] = " + templateIds.get(j).getExtValue());
		}

		if(lotNumberText != null) {
			log.info(" Lot Number = " + lotNumberText.getValue());
		}

		if(manufacturingOrg != null) {
			log.info(" Manufacturing Org Name = " + manufacturingOrg.getValue());
		}

	}

	public ArrayList<CCDACode> getTranslations() {
		return translations;
	}

	public void setTranslations(ArrayList<CCDACode> translations) {
		this.translations = translations;
	}

	public CCDADataElement getLotNumberText() {
		return lotNumberText;
	}

	public void setLotNumberText(CCDADataElement lotNumberText) {
		this.lotNumberText = lotNumberText;
	}

	public CCDADataElement getManufacturingOrg() {
		return manufacturingOrg;
	}

	public void setManufacturingOrg(CCDADataElement manufacturingOrg) {
		this.manufacturingOrg = manufacturingOrg;
	}

	public ArrayList<CCDAII> getTemplateIds() {
		return templateIds;
	}

	public void setTemplateIds(ArrayList<CCDAII> templateIds) {
		this.templateIds = templateIds;
	}

	public CCDACode getMedcode() {
		return medcode;
	}

	public void setMedcode(CCDACode medcode) {
		this.medcode = medcode;
	}

	public CCDAConsumable()
	{

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((lotNumberText == null) ? 0 : lotNumberText.hashCode());
		result = prime
				* result
				+ ((manufacturingOrg == null) ? 0 : manufacturingOrg.hashCode());
		result = prime * result + ((medcode == null) ? 0 : medcode.hashCode());
		result = prime * result
				+ ((templateIds == null) ? 0 : templateIds.hashCode());
		result = prime * result
				+ ((translations == null) ? 0 : translations.hashCode());
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
		CCDAConsumable other = (CCDAConsumable) obj;
		if (lotNumberText == null) {
			if (other.lotNumberText != null)
				return false;
		} else if (!lotNumberText.equals(other.lotNumberText))
			return false;
		if (manufacturingOrg == null) {
			if (other.manufacturingOrg != null)
				return false;
		} else if (!manufacturingOrg.equals(other.manufacturingOrg))
			return false;
		if (medcode == null) {
			if (other.medcode != null)
				return false;
		} else if (!medcode.equals(other.medcode))
			return false;
		if (templateIds == null) {
			if (other.templateIds != null)
				return false;
		} else if (!templateIds.equals(other.templateIds))
			return false;
		if (translations == null) {
			if (other.translations != null)
				return false;
		} else if (!translations.equals(other.translations))
			return false;
		return true;
	}
}
