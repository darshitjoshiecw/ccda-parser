package org.sitenv.ccdaparsing.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sitenv.ccdaparsing.dto.ContentValidationResult;
import org.sitenv.ccdaparsing.dto.enums.ContentValidationResultLevel;
import org.sitenv.ccdaparsing.util.ParserUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CCDAMedicationActivity extends CCDAXmlSnippet{

	private static Logger log = LogManager.getLogger(CCDAMedicationActivity.class.getName());

	private ArrayList<CCDAII>     				templateIds;
	private CCDAEffTime							duration;
	private CCDAFrequency						frequency;
	private CCDACode							routeCode;
	private CCDACode							approachSiteCode;
	private CCDAPQ								doseQuantity;
	private CCDAPQ								rateQuantity;
	private CCDACode							adminUnitCode;
	private CCDACode							statusCode;
	private CCDAConsumable						consumable;
	private CCDADataElement referenceText;
	private CCDAMedicationSubstanceAdminstration medSubAdmin;
	private CCDAAuthor author;
	private Boolean negationInd;

	public static void compareMedicationActivityData(HashMap<String, CCDAMedicationActivity> refActivities,
													 HashMap<String, CCDAMedicationActivity> subActivities, ArrayList<ContentValidationResult> results) {

		log.info(" Start Comparing Medication Activities ");
		// For each medication Activity in the Ref Model, check if it is present in the subCCDA Med.
		for(Map.Entry<String, CCDAMedicationActivity> ent: refActivities.entrySet()) {

			if(subActivities.containsKey(ent.getKey())) {

				log.info("Comparing Medication Activities ");
				String context = "Medication Activity Entry corresponding to the code " + ent.getKey();
				subActivities.get(ent.getKey()).compare(ent.getValue(), results, context);


			} else {
				// Error
				String error = "The scenario contains Medication Activity data for Medication with code " + ent.getKey() +
						" , however there is no matching data in the submitted CCDA. ";
				ContentValidationResult rs = new ContentValidationResult(error, ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0" );
				results.add(rs);
			}
		}

		// Handle the case where the medication data is not present in the reference,
		if( (refActivities == null || refActivities.size() == 0) && (subActivities != null && subActivities.size() > 0) ) {

			// Error
			String error = "The scenario does not require Medication Activity data " +
					" , however there is medication activity data in the submitted CCDA. ";
			ContentValidationResult rs = new ContentValidationResult(error, ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0" );
			results.add(rs);
		}

	}


	public void compare(CCDAMedicationActivity refMedActivity, ArrayList<ContentValidationResult> results , String context) {

		log.info("Comparing Medication Activity ");

		// Handle Template Ids
		ParserUtilities.compareTemplateIds(refMedActivity.getTemplateIds(), templateIds, results, context);

		// Compare Effective Times
		String elementNameTime = "Medication Duration for " + context;
		//ParserUtilities.compareEffectiveTime(refMedActivity.getDuration(), duration, results, elementNameTime);

		// Compare template Ids
		String consumableElement = "Consumable TemplateIds for " + context;
		ParserUtilities.compareTemplateIds(refMedActivity.getConsumable().getTemplateIds(),
				consumable.getTemplateIds(), results, consumableElement);

		// Compare Med Codes
		String elementNameVal = "Consumable code element: " + context;
		ParserUtilities.compareCode(refMedActivity.getConsumable().getMedcode(), consumable.getMedcode(), results, elementNameVal);
	}

	public Boolean hasSameMedication(CCDAConsumable refConsumable) {

		if(consumable != null &&
				refConsumable != null) {

			return consumable.hasSameMedCode(refConsumable);

		}

		return false;
	}

	public void log() {


		for(int j = 0; j < templateIds.size(); j++) {
			log.info(" Tempalte Id [" + j + "] = " + templateIds.get(j).getRootValue());
			log.info(" Tempalte Id Ext [" + j + "] = " + templateIds.get(j).getExtValue());
		}

		if(routeCode != null)
			log.info("Medication Activity Route Code = " + routeCode.getCode());

		if(approachSiteCode != null)
			log.info("Medication Activity Approach Site Code = " + approachSiteCode.getCode());

		if(adminUnitCode != null)
			log.info("Medication Activity Admin Unit Code = " + adminUnitCode.getCode());

		if(duration != null) {
			duration.log();
		}

		if(frequency != null) {
			frequency.log();
		}

		if(doseQuantity != null){
			doseQuantity.log();
		}

		if(rateQuantity != null){
			rateQuantity.log();
		}

		if(consumable != null) {
			consumable.log();
		}

		if(author != null)
			author.log();
	}

	public ArrayList<CCDAII> getTemplateIds() {
		return templateIds;
	}

	public void setTemplateIds(ArrayList<CCDAII> templateIds) {
		this.templateIds = templateIds;
	}

	public CCDAEffTime getDuration() {
		return duration;
	}

	public void setDuration(CCDAEffTime duration) {
		this.duration = duration;
	}

	public CCDAFrequency getFrequency() {
		return frequency;
	}

	public void setFrequency(CCDAFrequency frequency) {
		this.frequency = frequency;
	}

	public CCDACode getRouteCode() {
		return routeCode;
	}

	public void setRouteCode(CCDACode routeCode) {
		this.routeCode = routeCode;
	}

	public CCDACode getApproachSiteCode() {
		return approachSiteCode;
	}

	public void setApproachSiteCode(CCDACode approachSiteCode) {
		this.approachSiteCode = approachSiteCode;
	}

	public CCDAPQ getDoseQuantity() {
		return doseQuantity;
	}

	public void setDoseQuantity(CCDAPQ doseQuantity) {
		this.doseQuantity = doseQuantity;
	}

	public CCDAPQ getRateQuantity() {
		return rateQuantity;
	}

	public void setRateQuantity(CCDAPQ rateQuantity) {
		this.rateQuantity = rateQuantity;
	}

	public CCDACode getAdminUnitCode() {
		return adminUnitCode;
	}

	public void setAdminUnitCode(CCDACode adminUnitCode) {
		this.adminUnitCode = adminUnitCode;
	}

	public CCDAConsumable getConsumable() {
		return consumable;
	}

	public void setConsumable(CCDAConsumable consumable) {
		this.consumable = consumable;
	}
	
	public CCDADataElement getReferenceText() {
		return referenceText;
	}

	public void setReferenceText(CCDADataElement referenceText) {
		this.referenceText = referenceText;
	}

	public CCDAMedicationSubstanceAdminstration getMedSubAdmin() {
		return medSubAdmin;
	}

	public void setMedSubAdmin(CCDAMedicationSubstanceAdminstration medSubAdmin) {
		this.medSubAdmin = medSubAdmin;
	}
	
	public CCDACode getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(CCDACode statusCode) {
		this.statusCode = statusCode;
	}
	
	public CCDAAuthor getAuthor() {
		return author;
	}

	public void setAuthor(CCDAAuthor author) {
		this.author = author;
	}
	
	public Boolean getNegationInd() {
		return negationInd;
	}

	public void setNegationInd(Boolean negationInd) {
		this.negationInd = negationInd;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((adminUnitCode == null) ? 0 : adminUnitCode.hashCode());
		result = prime
				* result
				+ ((approachSiteCode == null) ? 0 : approachSiteCode.hashCode());
		result = prime * result
				+ ((consumable == null) ? 0 : consumable.hashCode());
		result = prime * result
				+ ((doseQuantity == null) ? 0 : doseQuantity.hashCode());
		result = prime * result
				+ ((duration == null) ? 0 : duration.hashCode());
		result = prime * result
				+ ((frequency == null) ? 0 : frequency.hashCode());
		result = prime * result
				+ ((rateQuantity == null) ? 0 : rateQuantity.hashCode());
		result = prime * result
				+ ((routeCode == null) ? 0 : routeCode.hashCode());
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
		CCDAMedicationActivity other = (CCDAMedicationActivity) obj;
		if (adminUnitCode == null) {
			if (other.adminUnitCode != null)
				return false;
		} else if (!adminUnitCode.equals(other.adminUnitCode))
			return false;
		if (approachSiteCode == null) {
			if (other.approachSiteCode != null)
				return false;
		} else if (!approachSiteCode.equals(other.approachSiteCode))
			return false;
		if (consumable == null) {
			if (other.consumable != null)
				return false;
		} else if (!consumable.equals(other.consumable))
			return false;
		if (doseQuantity == null) {
			if (other.doseQuantity != null)
				return false;
		} else if (!doseQuantity.equals(other.doseQuantity))
			return false;
		if (duration == null) {
			if (other.duration != null)
				return false;
		} else if (!duration.equals(other.duration))
			return false;
		if (frequency == null) {
			if (other.frequency != null)
				return false;
		} else if (!frequency.equals(other.frequency))
			return false;
		if (rateQuantity == null) {
			if (other.rateQuantity != null)
				return false;
		} else if (!rateQuantity.equals(other.rateQuantity))
			return false;
		if (routeCode == null) {
			if (other.routeCode != null)
				return false;
		} else if (!routeCode.equals(other.routeCode))
			return false;
		if (templateIds == null) {
			if (other.templateIds != null)
				return false;
		} else if (!templateIds.equals(other.templateIds))
			return false;
		return true;
	}
	
	

}
