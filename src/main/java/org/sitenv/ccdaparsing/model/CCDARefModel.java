package org.sitenv.ccdaparsing.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sitenv.ccdaparsing.dto.enums.SeverityLevel;
import org.sitenv.ccdaparsing.processing.CCDAConstants;

import java.util.ArrayList;

public class CCDARefModel {

    private static Logger log = LogManager.getLogger(CCDARefModel.class.getName());

    private CCDAII docTemplateId;
    private CCDAPatient patient;
    private CCDACareTeamMember members;
    private CCDACareTeamMember careTeamSectionMembers;
    private CCDACarePlanSections carePlanSections;
    private CCDAEncounter encounter;
    private CCDAAdmissionDiagnosis admissionDiagnosis;
    private CCDADischargeDiagnosis dischargeDiagnosis;
    private CCDAEncompassingEncounter encompassingEncounter;
    private CCDAAllergy allergy;
    private CCDAMedication medication;
    private CCDADischargeMedication dischargeMedication;
    private CCDAImmunization immunization;
    private CCDALabResult labResults;
    private CCDALabResult labTests;
    private CCDAProcedure procedure;
    private CCDASocialHistory smokingStatus;
    private CCDAVitalSigns vitalSigns;
    private CCDAProblem problem;
    private CCDAPOT planOfTreatment;
    private CCDAGoals goals;
    private CCDAFamilyHistory familyHistory;
    private CCDAAdvanceDirective advanceDirective;
    private CCDAMedicalEquipment medicalEquipment;
    private CCDAFunctionalStatus functionalStatus;
    private CCDAMentalStatus mentalStatus;
    private CCDAHealthConcerns hcs;
    private ArrayList<CCDAUDI> udi;
    private CCDAHeaderElements header;
    private ArrayList<CCDAII> ccdTemplates;
    private ArrayList<CCDAII> dsTemplates;
    private ArrayList<CCDAII> rnTemplates;
    private ArrayList<CCDAII> cpTemplates;
    private ArrayList<CCDAID> idList;
    private UsrhSubType usrhSubType;
    private boolean isEmpty;
    private SeverityLevel severityLevel;

    private ArrayList<CCDAAuthor> authorsFromHeader; // header-level only
    private ArrayList<CCDAAuthor> authorsWithLinkedReferenceData;

    // Cures Update changes for section level notes
    private ArrayList<CCDANotes> notes;
    private ArrayList<CCDANotesActivity> notesEntries;

    public ArrayList<CCDANotes> getNotes() {
        return notes;
    }

    public void setNotes(ArrayList<CCDANotes> notes) {
        this.notes = notes;
    }

    public ArrayList<CCDANotesActivity> getNotesEntries() {
        return notesEntries;
    }

    public void setNotesEntries(ArrayList<CCDANotesActivity> notesEntries) {
        this.notesEntries = notesEntries;
    }

    public ArrayList<CCDAAuthor> getAuthorsWithLinkedReferenceData() {
        return authorsWithLinkedReferenceData;
    }

    public void setAuthorsWithLinkedReferenceData(ArrayList<CCDAAuthor> authorsWithLinkedReferenceData) {
        this.authorsWithLinkedReferenceData = authorsWithLinkedReferenceData;
    }

    public ArrayList<CCDAAuthor> getAuthorsFromHeader() {
        return authorsFromHeader;
    }

    public void setAuthorsFromHeader(ArrayList<CCDAAuthor> authorsFromHeader) {
        this.authorsFromHeader = authorsFromHeader;
    }

    public CCDARefModel() {
        this(SeverityLevel.INFO);
    }

    public CCDARefModel(SeverityLevel severityLevel) {
        this.severityLevel = severityLevel;
        udi = new ArrayList<CCDAUDI>();
		/*notes = new ArrayList<CCDANotes>();
		notesEntries = new ArrayList<CCDANotesActivity>();
		authorsFromHeader = new ArrayList<CCDAAuthor>();
		authorsWithLinkedReferenceData = new ArrayList<CCDAAuthor>();*/

        ccdTemplates = new ArrayList<CCDAII>();
        ccdTemplates.add(new CCDAII(CCDAConstants.US_REALM_TEMPLATE, CCDAConstants.CCDA_2015_AUG_EXT));
        ccdTemplates.add(new CCDAII(CCDAConstants.CCD_TEMPLATE, CCDAConstants.CCDA_2015_AUG_EXT));

        dsTemplates = new ArrayList<CCDAII>();
        dsTemplates.add(new CCDAII(CCDAConstants.US_REALM_TEMPLATE, CCDAConstants.CCDA_2015_AUG_EXT));
        dsTemplates.add(new CCDAII(CCDAConstants.DS_TEMPLATE, CCDAConstants.CCDA_2015_AUG_EXT));

        rnTemplates = new ArrayList<CCDAII>();
        rnTemplates.add(new CCDAII(CCDAConstants.US_REALM_TEMPLATE, CCDAConstants.CCDA_2015_AUG_EXT));
        rnTemplates.add(new CCDAII(CCDAConstants.RN_TEMPLATE, CCDAConstants.CCDA_2015_AUG_EXT));

        cpTemplates = new ArrayList<CCDAII>();
        cpTemplates.add(new CCDAII(CCDAConstants.US_REALM_TEMPLATE, CCDAConstants.CCDA_2015_AUG_EXT));
        cpTemplates.add(new CCDAII(CCDAConstants.CP_TEMPLATE, CCDAConstants.CCDA_2015_AUG_EXT));
    }

	/*public ArrayList<ContentValidationResult> compare(String validationObjective, CCDARefModel submittedCCDA, boolean curesUpdate) {

		ArrayList<ContentValidationResult> results = new ArrayList<ContentValidationResult>();

		if (doesObjectiveRequireCCDS(validationObjective)) {
			log.info(" Performing CCDS checks ");
			compareCCDS(validationObjective, submittedCCDA, results, curesUpdate);
		} else if (doesObjectiveRequireCIRI(validationObjective)) {
			log.info(" Performing CIRI checks ");
			performCIRIValidation(validationObjective, submittedCCDA, results, curesUpdate);
		} else if (doesObjectiveRequireCarePlan(validationObjective)) {
			log.info(" Performing Care Plan checks ");
			performCarePlanValidation(validationObjective, submittedCCDA, results, curesUpdate);
		} else if (doesObjectiveRequireDS4P(validationObjective)) {
			log.info(" Performing DS4P checks ");
			performDS4PValidation(validationObjective, submittedCCDA, results, curesUpdate);
		} else {
			log.info(" Not Performing any content validation checks ");
		}

		log.info(" Compare non CCDS Structured Data ");
		compareNonCCDSStructuredData(validationObjective, submittedCCDA, results, curesUpdate);

		validateDocElements(validationObjective, submittedCCDA, results, curesUpdate);

		log.info(" Total Number of Content Validation Issues " + results.size());
		return results;
	}

	public void validateDocElements(String valObj, CCDARefModel submittedCCDA, ArrayList<ContentValidationResult> results, boolean curesUpdate) {
		if (valObj.equalsIgnoreCase("170.315_b1_ToC_Amb") ||
				valObj.equalsIgnoreCase("170.315_b4_CCDS_Amb")) {
			// Validate it is one of CCD/RN
			String elementName = "Clinical Document Header ";
			ArrayList<ContentValidationResult> ccdResults = new ArrayList<ContentValidationResult>();
			ArrayList<ContentValidationResult> rnResults = new ArrayList<ContentValidationResult>();

			ParserUtilities.compareTemplateIds(ccdTemplates, submittedCCDA.getHeader().getDocTemplates(), ccdResults, elementName);
			ParserUtilities.compareTemplateIds(rnTemplates, submittedCCDA.getHeader().getDocTemplates(), rnResults, elementName);

			if ((ccdResults.size() == 0) ||
					(rnResults.size() == 0)) {
				// Doc Type requirement is met.
				return;
			} else {
				//Add the Errors to Result.
				ContentValidationResult rs = new ContentValidationResult("The scenario requires the submitted document type to be either a Continuity of Care Document, or a Referral Note, but the submitted C-CDA does not contain the relevant template Ids.", ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0");
				results.add(rs);
				log.info(" Scenario requires correct document type, but submitted CCDA does not have the right document type.");
			}
		} else if (valObj.equalsIgnoreCase("170.315_b1_ToC_Inp") ||
				valObj.equalsIgnoreCase("170.315_b4_CCDS_Inp")) {
			// Validate it is one of CCD/RN/DS
			String elementName = "Clinical Document Header ";
			ArrayList<ContentValidationResult> ccdResults = new ArrayList<ContentValidationResult>();
			ArrayList<ContentValidationResult> rnResults = new ArrayList<ContentValidationResult>();
			ArrayList<ContentValidationResult> dsResults = new ArrayList<ContentValidationResult>();

			ParserUtilities.compareTemplateIds(ccdTemplates, submittedCCDA.getHeader().getDocTemplates(), ccdResults, elementName);
			ParserUtilities.compareTemplateIds(rnTemplates, submittedCCDA.getHeader().getDocTemplates(), rnResults, elementName);
			ParserUtilities.compareTemplateIds(dsTemplates, submittedCCDA.getHeader().getDocTemplates(), dsResults, elementName);

			if ((ccdResults.size() == 0) ||
					(rnResults.size() == 0) ||
					(dsResults.size() == 0)) {
				// Doc Type requirement is met.
				return;
			} else {
				//Add the Errors to Result.
				ContentValidationResult rs = new ContentValidationResult("The scenario requires the submitted document type to be either a Continuity of Care Document, or a Referral Note or a Discharge Summary, but the submitted C-CDA does not contain the relevant template Ids.", ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0");
				results.add(rs);
				log.info(" Scenario requires correct document type, but submitted CCDA does not have the right document type.");
			}
		} else if (valObj.equalsIgnoreCase("170.315_b2_CIRI_Amb") ||
				valObj.equalsIgnoreCase("170.315_b2_CIRI_Inp") ||
				valObj.equalsIgnoreCase("170.315_b6_DE_Amb") ||
				valObj.equalsIgnoreCase("170.315_b6_DE_Inp") ||
				//	valObj.equalsIgnoreCase("170.315_b7_DS4P_Amb") ||
				//	valObj.equalsIgnoreCase("170.315_b7_DS4P_Inp") ||
				//	valObj.equalsIgnoreCase("170.315_b8_DS4P_Amb") ||
				//	valObj.equalsIgnoreCase("170.315_b8_DS4P_Inp") ||
				valObj.equalsIgnoreCase("170.315_e1_VDT_Amb") ||
				valObj.equalsIgnoreCase("170.315_e1_VDT_Inp"))
		//	valObj.equalsIgnoreCase("170.315_g9_APIAccess_Amb") ||
		//	valObj.equalsIgnoreCase("170.315_g9_APIAccess_Inp") )
		{
			// Validate for CCD
			String elementName = "Clinical Document Header ";
			ArrayList<ContentValidationResult> ccdResults = new ArrayList<ContentValidationResult>();

			ParserUtilities.compareTemplateIds(ccdTemplates, submittedCCDA.getHeader().getDocTemplates(), ccdResults, elementName);

			if ((ccdResults.size() == 0)) {
				// Doc Type requirement is met.
				return;
			} else {
				//Add the Errors to Result.
				ContentValidationResult rs = new ContentValidationResult("The scenario requires the submitted document type to be a Continuity of Care Document, but the submitted C-CDA does not contain the relevant template Ids.", ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0");
				results.add(rs);
				log.info(" Scenario requires correct document type, but submitted CCDA does not have the right document type.");
			}
		} else if (valObj.equalsIgnoreCase("170.315_b9_CP_Amb") ||
				valObj.equalsIgnoreCase("170.315_b9_CP_Inp")) {
			// Validate for CP
			String elementName = "Clinical Document Header ";
			ArrayList<ContentValidationResult> cpResults = new ArrayList<ContentValidationResult>();

			ParserUtilities.compareTemplateIds(cpTemplates, submittedCCDA.getHeader().getDocTemplates(), cpResults, elementName);

			if ((cpResults.size() == 0)) {
				// Doc Type requirement is met.
				return;
			} else {
				//Add the Errors to Result.
				ContentValidationResult rs = new ContentValidationResult("The scenario requires the submitted document type to be a Care Plan Document, but the submitted C-CDA does not contain the relevant template Ids.", ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0");
				results.add(rs);
				log.info(" Scenario requires correct document type, but submitted CCDA does not have the right document type.");
			}
		} else {
			return;
		}
	}

	public void compareCCDS(String validationObjective, CCDARefModel submittedCCDA,
							ArrayList<ContentValidationResult> results, boolean curesUpdate) {

		log.info("Comparing Patient Data ");
		comparePatients(submittedCCDA, results, curesUpdate);

		log.info("Comparing Social History Smoking Status ");
		validateSmokingStatus(submittedCCDA, results, curesUpdate);

		log.info("Validating Social History Birth Sex ");
		validateBirthSex(submittedCCDA, results, curesUpdate);

		log.info("Comparing Problems ");
		compareProblems(validationObjective, submittedCCDA, results, curesUpdate);

		log.info("Comparing Allergies ");
		compareAllergies(validationObjective, submittedCCDA, results, curesUpdate);

		log.info("Comparing Medications ");
		compareMedications(validationObjective, submittedCCDA, results, curesUpdate);

		log.info("Comparing Lab Results ");
		compareLabResults(validationObjective, submittedCCDA, results, curesUpdate);

		log.info("Comparing Vital Signs ");
		compareVitalObs(validationObjective, submittedCCDA, results, curesUpdate);

		log.info("Comparing Procedures ");
		compareProcedures(validationObjective, submittedCCDA, results, curesUpdate);

		log.info("Comparing Udis ");
		compareUdis(validationObjective, submittedCCDA, results, curesUpdate);

		log.info("Comparing Immunizations ");
		compareImmunizations(validationObjective, submittedCCDA, results, curesUpdate);


		if (curesUpdate) {
			ArrayList<CCDAAuthor> submittedAuthorsWithLinkedReferenceData = null;
			submittedAuthorsWithLinkedReferenceData = submittedCCDA.getAuthorsWithLinkedReferenceData() != null
					? submittedCCDA.getAuthorsWithLinkedReferenceData()
					: null;
			logSubmittedAuthorsWithLinkedReferenceData(submittedAuthorsWithLinkedReferenceData);

			log.info(" Comparing Notes ");
			compareNotesActivities(validationObjective, submittedCCDA, results, curesUpdate,
					submittedAuthorsWithLinkedReferenceData);

			log.info(" Comparing Author ");
			compareAuthorEntries(validationObjective, submittedCCDA, results, curesUpdate,
					submittedAuthorsWithLinkedReferenceData);

			log.info(" Comparing Care Team ");
			compareCareTeamMembers(validationObjective, submittedCCDA, results, curesUpdate);
		}

		log.info("Finished comparison, returning results");

	}

	private void compareProblems(String validationObjective, CCDARefModel submittedCCDA, ArrayList<ContentValidationResult> results, boolean curesUpdate) {

		if ((this.getProblem() != null) && (submittedCCDA.getProblem() != null)) {
			log.info("Start Problem Comparison ");
			this.problem.compare(submittedCCDA.getProblem(), results);
		} else if ((this.getProblem() != null) && (submittedCCDA.getProblem() == null)) {
			// handle the case where the problem section does not exist in the submitted CCDA
			ContentValidationResult rs = new ContentValidationResult("The scenario requires data related to patient's problems, but the submitted C-CDA does not contain problem data.", ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0");
			results.add(rs);
			log.info(" Scenario requires problems but submitted document does not contain problems section");
		} else if ((this.getProblem() == null) && (submittedCCDA.getProblem() != null)) {

			ContentValidationResult rs = new ContentValidationResult("The scenario does not require data related to patient's problems, but the submitted C-CDA does contain problem data.", ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0");
			results.add(rs);
			log.info("Model does not have problems for comparison ");
		} else {

			log.info("Model and Submitted CCDA do not have problems for comparison ");
		}

	}

	private void compareAllergies(String validationObjective, CCDARefModel submittedCCDA, ArrayList<ContentValidationResult> results, boolean curesUpdate) {

		if ((this.getAllergy() != null) && (submittedCCDA.getAllergy() != null)) {
			log.info("Start Allergy Comparison ");
			this.allergy.compare(submittedCCDA.getAllergy(), results, submittedCCDA);
		} else if ((this.getAllergy() != null) && (submittedCCDA.getAllergy() == null)) {
			// handle the case where the allergy section does not exist in the submitted CCDA
			ContentValidationResult rs = new ContentValidationResult("The scenario requires data related to patient's allergies, but the submitted C-CDA does not contain allergy data.", ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0");
			results.add(rs);
			log.info(" Scenario requires allergies but submitted document does not contain allergies section");
		} else if ((this.getAllergy() == null) && (submittedCCDA.getAllergy() != null)) {

			ContentValidationResult rs = new ContentValidationResult("The scenario does not require data related to patient's allergies, but the submitted C-CDA does contain allergy data.", ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0");
			results.add(rs);
			log.info("Model does not have allergies for comparison ");
		} else {

			log.info("Model and Submitted CCDA do not have allergies for comparison ");
		}

	}

	public HashMap<String, CCDAMedicationActivity> getAllMedActivities() {

		HashMap<String, CCDAMedicationActivity> activities = new HashMap<String, CCDAMedicationActivity>();

		if (medication != null) {
			
			*//*
			for(Map.Entry<String, CCDAMedicationActivity> ent: medication.getMedActivitiesMap().entrySet()) {
				log.info("Adding " + ent.getKey());
				activities.put(ent.getKey(), ent.getValue());		
			}*//*

			activities.putAll(medication.getMedActivitiesMap());
			log.info(" Activities Size = " + activities.size());
		}

		if (dischargeMedication != null) {
			
			*//*
			for(Map.Entry<String, CCDAMedicationActivity> ent: dischargeMedication.getMedActivitiesMap().entrySet()) {
				log.info("Adding " + ent.getKey());
				activities.put(ent.getKey(), ent.getValue());			
			}*//*

			activities.putAll(dischargeMedication.getMedActivitiesMap());
			log.info("Activities Size = " + activities.size());
		}

		log.info("Final Med Activities Size = " + activities.size());
		return activities;
	}

	public HashMap<String, CCDAImmunizationActivity> getAllImmunizations() {

		HashMap<String, CCDAImmunizationActivity> activities = new HashMap<String, CCDAImmunizationActivity>();

		if (immunization != null) {

			activities.putAll(immunization.getImmunizationActivitiesMap());
			log.info(" Activities Size = " + activities.size());
		}

		return activities;
	}

	public void compareMedications(String validationObjective, CCDARefModel submittedCCDA, ArrayList<ContentValidationResult> results, boolean curesUpdate) {

		log.info("Retrieving Medication Activities for comparison ");
		HashMap<String, CCDAMedicationActivity> refActivities = this.getAllMedActivities();
		HashMap<String, CCDAMedicationActivity> subActivities = submittedCCDA.getAllMedActivities();

		if ((refActivities != null && refActivities.size() > 0) &&
				(subActivities != null && subActivities.size() > 0)) {

			log.info("Medication Activities in both models ");
			CCDAMedicationActivity.compareMedicationActivityData(refActivities, subActivities, results);

		} else if ((refActivities != null && refActivities.size() > 0) &&
				(subActivities == null || subActivities.size() == 0)) {

			// handle the case where the allergy section does not exist in the submitted CCDA
			ContentValidationResult rs = new ContentValidationResult("The scenario requires data related to patient's medications, but the submitted C-CDA does not contain medication data.", ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0");
			results.add(rs);
			log.info(" Scenario requires medications but submitted document does not contain medication data");

		} else if ((refActivities == null || refActivities.size() == 0) &&
				(subActivities != null && subActivities.size() > 0)) {

			ContentValidationResult rs = new ContentValidationResult("The scenario does not require data related to patient's medications, but the submitted C-CDA does contain medication data.", ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0");
			results.add(rs);
			log.info("Model does not have medications for comparison ");

		} else {

			log.info("Model and Submitted CCDA do not have medications for comparison ");
		}
	}

	public void compareImmunizations(String validationObjective, CCDARefModel submittedCCDA, ArrayList<ContentValidationResult> results, boolean curesUpdate) {

		log.info("Retrieving Immunization Activities for comparison ");
		HashMap<String, CCDAImmunizationActivity> refActivities = this.getAllImmunizations();
		HashMap<String, CCDAImmunizationActivity> subActivities = submittedCCDA.getAllImmunizations();

		if ((refActivities != null && refActivities.size() > 0) &&
				(subActivities != null && subActivities.size() > 0)) {

			log.info("Immunization Activities in both models ");
			CCDAImmunizationActivity.compareImmunizationActivityData(refActivities, subActivities, results);

		} else if ((refActivities != null && refActivities.size() > 0) &&
				(subActivities == null || subActivities.size() == 0)) {

			// handle the case where the allergy section does not exist in the submitted CCDA
			ContentValidationResult rs = new ContentValidationResult("The scenario requires data related to patient's immunizations, but the submitted C-CDA does not contain immunization data.", ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0");
			results.add(rs);
			log.info(" Scenario requires immunizations but submitted document does not contain immunization data");

		} else if ((refActivities == null || refActivities.size() == 0) &&
				(subActivities != null && subActivities.size() > 0)) {

			ContentValidationResult rs = new ContentValidationResult("The scenario does not require data related to patient's immunizations, but the submitted C-CDA does contain immunization data.", ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0");
			results.add(rs);
			log.info("Model does not have immunization for comparison ");

		} else {

			log.info("Model and Submitted CCDA do not have immunization for comparison ");
		}
	}*/

    public CCDAHeaderElements getHeader() {
        return header;
    }

    public void setHeader(CCDAHeaderElements header) {
        this.header = header;
    }

    public CCDADischargeMedication getDischargeMedication() {
        return dischargeMedication;
    }

    public void setDischargeMedication(CCDADischargeMedication dischargeMedication) {
        this.dischargeMedication = dischargeMedication;
    }

    public CCDACarePlanSections getCarePlanSections() {
        return carePlanSections;
    }

    public void setCarePlanSections(CCDACarePlanSections carePlanSections) {
        this.carePlanSections = carePlanSections;
    }

    public CCDACareTeamMember getCareTeamSectionMembers() {
        return careTeamSectionMembers;
    }

    public void setCareTeamSectionMembers(CCDACareTeamMember careTeamSectionMembers) {
        this.careTeamSectionMembers = careTeamSectionMembers;
    }

    public CCDADischargeDiagnosis getDischargeDiagnosis() {
        return dischargeDiagnosis;
    }

    public void setDischargeDiagnosis(CCDADischargeDiagnosis dischargeDiagnosis) {
        this.dischargeDiagnosis = dischargeDiagnosis;
    }

    public CCDAAdmissionDiagnosis getAdmissionDiagnosis() {
        return admissionDiagnosis;
    }

    public void setAdmissionDiagnosis(CCDAAdmissionDiagnosis admissionDiagnosis) {
        this.admissionDiagnosis = admissionDiagnosis;
    }

    private ArrayList<String> errorSections = new ArrayList<>();

    public CCDAII getDocTemplateId() {
        return docTemplateId;
    }

    public void setDocTemplateId(CCDAII docTemplateId) {
        this.docTemplateId = docTemplateId;
    }

    public CCDAPatient getPatient() {
        return patient;
    }

    public void setPatient(CCDAPatient patient) {
        this.patient = patient;
    }

    public CCDACareTeamMember getMembers() {
        return members;
    }

    public void setMembers(CCDACareTeamMember members) {
        this.members = members;
    }

    public CCDAEncounter getEncounter() {
        return encounter;
    }

    public void setEncounter(CCDAEncounter encounter) {
        this.encounter = encounter;
    }

    public CCDAAllergy getAllergy() {
        return allergy;
    }

    public void setAllergy(CCDAAllergy allergy) {
        this.allergy = allergy;
    }

    public CCDAMedication getMedication() {
        return medication;
    }

    public void setMedication(CCDAMedication medication) {
        this.medication = medication;
    }

    public CCDAImmunization getImmunization() {
        return immunization;
    }

    public void setImmunization(CCDAImmunization immunization) {
        this.immunization = immunization;
    }

    public CCDALabResult getLabResults() {
        return labResults;
    }

    public void setLabResults(CCDALabResult labResults) {
        this.labResults = labResults;
    }

    public CCDALabResult getLabTests() {
        return labTests;
    }

    public void setLabTests(CCDALabResult labTests) {
        this.labTests = labTests;
    }

    public CCDAProcedure getProcedure() {
        return procedure;
    }

    public void setProcedure(CCDAProcedure procedure) {
        this.procedure = procedure;
    }

    public CCDASocialHistory getSmokingStatus() {
        return smokingStatus;
    }

    public void setSmokingStatus(CCDASocialHistory smokingStatus) {
        this.smokingStatus = smokingStatus;
    }

    public CCDAVitalSigns getVitalSigns() {
        return vitalSigns;
    }

    public void setVitalSigns(CCDAVitalSigns vitalSigns) {
        this.vitalSigns = vitalSigns;
    }

    public CCDAProblem getProblem() {
        return problem;
    }

    public void setProblem(CCDAProblem problem) {
        this.problem = problem;
    }

    public CCDAPOT getPlanOfTreatment() {
        return planOfTreatment;
    }

    public void setPlanOfTreatment(CCDAPOT planOfTreatment) {
        this.planOfTreatment = planOfTreatment;
    }

    public CCDAGoals getGoals() {
        return goals;
    }

    public void setGoals(CCDAGoals goals) {
        this.goals = goals;
    }

    public void setFamilyHistory(CCDAFamilyHistory familyHistory) {
        this.familyHistory = familyHistory;
    }

    public CCDAFamilyHistory getFamilyHistory() {
        return this.familyHistory;
    }

    public void setMedicalEquipment(CCDAMedicalEquipment medicalEquipment) {
        this.medicalEquipment = medicalEquipment;
    }

    public CCDAMedicalEquipment getMedicalEquipment() {
        return medicalEquipment;
    }

    public void setAdvanceDirective(CCDAAdvanceDirective advanceDirective) {
        this.advanceDirective = advanceDirective;
    }

    public CCDAAdvanceDirective getAdvanceDirective() {
        return this.advanceDirective;
    }

    public void setFunctionalStatus(CCDAFunctionalStatus functionalStatus) {
        this.functionalStatus = functionalStatus;
    }

    public CCDAFunctionalStatus getFunctionalStatus() {
        return this.functionalStatus;
    }

    public void setMentalStatus(CCDAMentalStatus mentalStatus) {
        this.mentalStatus = mentalStatus;
    }

    public CCDAMentalStatus getMentalStatus() {
        return this.mentalStatus;
    }

    public CCDAHealthConcerns getHcs() {
        return hcs;
    }

    public void setHcs(CCDAHealthConcerns hcs) {
        this.hcs = hcs;
    }

    public ArrayList<CCDAUDI> getUdi() {
        return udi;
    }

    public void setUdi(ArrayList<CCDAUDI> udi) {
        this.udi = udi;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean isEmpty) {
        this.isEmpty = isEmpty;
    }

    public ArrayList<CCDAID> getIdList() {
        return idList;
    }

    public void setIdList(ArrayList<CCDAID> idList) {
        this.idList = idList;
    }

    public UsrhSubType getUsrhSubType() {
        return usrhSubType;
    }

    public void setUsrhSubType(UsrhSubType usrhSubType) {
        this.usrhSubType = usrhSubType;
    }

    public CCDAEncompassingEncounter getEncompassingEncounter() {
        return encompassingEncounter;
    }

    public void setEncompassingEncounter(CCDAEncompassingEncounter encompassingEncounter) {
        this.encompassingEncounter = encompassingEncounter;
    }

    public boolean warningsPermitted() {
        return severityLevel == SeverityLevel.WARNING || severityLevel == SeverityLevel.INFO;
    }

    public boolean infoPermitted() {
        return severityLevel == SeverityLevel.INFO;
    }

    public String getSeverityLevelName() {
        return severityLevel.name();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((allergy == null) ? 0 : allergy.hashCode());
        result = prime * result
                + ((encounter == null) ? 0 : encounter.hashCode());
        result = prime * result + ((goals == null) ? 0 : goals.hashCode());
        result = prime * result + ((hcs == null) ? 0 : hcs.hashCode());
        result = prime * result
                + ((immunization == null) ? 0 : immunization.hashCode());
        result = prime * result
                + ((labResults == null) ? 0 : labResults.hashCode());
        result = prime * result
                + ((labTests == null) ? 0 : labTests.hashCode());
        result = prime * result
                + ((medication == null) ? 0 : medication.hashCode());
        result = prime * result + ((members == null) ? 0 : members.hashCode());
        result = prime * result + ((patient == null) ? 0 : patient.hashCode());
        result = prime * result
                + ((planOfTreatment == null) ? 0 : planOfTreatment.hashCode());
        result = prime * result + ((problem == null) ? 0 : problem.hashCode());
        result = prime * result
                + ((procedure == null) ? 0 : procedure.hashCode());
        result = prime * result
                + ((smokingStatus == null) ? 0 : smokingStatus.hashCode());
        result = prime * result + ((udi == null) ? 0 : udi.hashCode());
        result = prime * result
                + ((vitalSigns == null) ? 0 : vitalSigns.hashCode());
        result = prime * result
                + ((usrhSubType == null) ? 0 : usrhSubType.hashCode());
        result = prime * result
                + ((familyHistory == null) ? 0 : familyHistory.hashCode());
        result = prime * result
                + ((medicalEquipment == null) ? 0 : medicalEquipment.hashCode());
        result = prime * result
                + ((advanceDirective == null) ? 0 : advanceDirective.hashCode());
        result = prime * result
                + ((functionalStatus == null) ? 0 : functionalStatus.hashCode());
        result = prime * result
                + ((mentalStatus == null) ? 0 : mentalStatus.hashCode());
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
        CCDARefModel other = (CCDARefModel) obj;
        if (allergy == null) {
            if (other.allergy != null)
                return false;
        } else if (!allergy.equals(other.allergy))
            return false;
        if (encounter == null) {
            if (other.encounter != null)
                return false;
        } else if (!encounter.equals(other.encounter))
            return false;
        if (goals == null) {
            if (other.goals != null)
                return false;
        } else if (!goals.equals(other.goals))
            return false;
        if (hcs == null) {
            if (other.hcs != null)
                return false;
        } else if (!hcs.equals(other.hcs))
            return false;
        if (immunization == null) {
            if (other.immunization != null)
                return false;
        } else if (!immunization.equals(other.immunization))
            return false;
        if (labResults == null) {
            if (other.labResults != null)
                return false;
        } else if (!labResults.equals(other.labResults))
            return false;
        if (labTests == null) {
            if (other.labTests != null)
                return false;
        } else if (!labTests.equals(other.labTests))
            return false;
        if (medication == null) {
            if (other.medication != null)
                return false;
        } else if (!medication.equals(other.medication))
            return false;
        if (members == null) {
            if (other.members != null)
                return false;
        } else if (!members.equals(other.members))
            return false;
        if (patient == null) {
            if (other.patient != null)
                return false;
        } else if (!patient.equals(other.patient))
            return false;
        if (planOfTreatment == null) {
            if (other.planOfTreatment != null)
                return false;
        } else if (!planOfTreatment.equals(other.planOfTreatment))
            return false;
        if (problem == null) {
            if (other.problem != null)
                return false;
        } else if (!problem.equals(other.problem))
            return false;
        if (procedure == null) {
            if (other.procedure != null)
                return false;
        } else if (!procedure.equals(other.procedure))
            return false;
        if (smokingStatus == null) {
            if (other.smokingStatus != null)
                return false;
        } else if (!smokingStatus.equals(other.smokingStatus))
            return false;
        if (udi == null) {
            if (other.udi != null)
                return false;
        } else if (!udi.equals(other.udi))
            return false;
        if (vitalSigns == null) {
            if (other.vitalSigns != null)
                return false;
        } else if (!vitalSigns.equals(other.vitalSigns))
            return false;
        if (usrhSubType == null) {
            if (other.usrhSubType != null)
                return false;
        } else if (!usrhSubType.equals(other.usrhSubType))
            return false;
        if (familyHistory == null) {
            if (other.familyHistory != null)
                return false;
        } else if (!familyHistory.equals(other.familyHistory))
            return false;
        if (medicalEquipment == null) {
            if (other.medicalEquipment != null)
                return false;
        } else if (!medicalEquipment.equals(other.medicalEquipment))
            return false;
        if (advanceDirective == null) {
            if (other.advanceDirective != null)
                return false;
        } else if (!advanceDirective.equals(other.advanceDirective))
            return false;
        if (functionalStatus == null) {
            if (other.functionalStatus != null)
                return false;
        } else if (!functionalStatus.equals(other.functionalStatus))
            return false;
        if (mentalStatus == null) {
            if (other.mentalStatus != null)
                return false;
        } else if (!mentalStatus.equals(other.mentalStatus))
            return false;
        return true;
    }

    public ArrayList<String> getErrorSections() {
        return errorSections;
    }

    public void setErrorSections(ArrayList<String> errorSections) {
        this.errorSections = errorSections;
    }

    public void addToErrorSections(String sectionName) {
        if (null == this.errorSections)
            this.errorSections = new ArrayList<String>();

        errorSections.add(sectionName);
    }
/*
	public Boolean doesObjectiveRequireCCDS(String valObj) {

		if (valObj.equalsIgnoreCase("170.315_b1_ToC_Amb") ||
				valObj.equalsIgnoreCase("170.315_b1_ToC_Inp") ||
				valObj.equalsIgnoreCase("170.315_b4_CCDS_Amb") ||
				valObj.equalsIgnoreCase("170.315_b4_CCDS_Inp") ||
				valObj.equalsIgnoreCase("170.315_b6_DE_Amb") ||
				valObj.equalsIgnoreCase("170.315_b6_DE_Inp") ||
				valObj.equalsIgnoreCase("170.315_e1_VDT_Amb") ||
				valObj.equalsIgnoreCase("170.315_e1_VDT_Inp") ||
				valObj.equalsIgnoreCase("170.315_g9_APIAccess_Amb") ||
				valObj.equalsIgnoreCase("170.315_g9_APIAccess_Inp"))
			return true;
		else
			return false;
	}

	public Boolean doesObjectiveRequireCIRI(String valObj) {

		if (valObj.equalsIgnoreCase("170.315_b2_CIRI_Amb") ||
				valObj.equalsIgnoreCase("170.315_b2_CIRI_Inp"))
			return true;
		else
			return false;
	}

	public Boolean doesObjectiveRequireCarePlan(String valObj) {

		if (valObj.equalsIgnoreCase("170.315_b9_CP_Amb") ||
				valObj.equalsIgnoreCase("170.315_b9_CP_Inp"))
			return true;
		else
			return false;
	}

	public Boolean doesObjectiveRequireDS4P(String valObj) {

		if (valObj.equalsIgnoreCase("170.315_b7_DS4P_Amb") ||
				valObj.equalsIgnoreCase("170.315_b7_DS4P_Inp"))
			return true;
		else
			return false;
	}

	public void log() {

		if (patient != null)
			patient.log();
		else
			log.info(" No Patient Data in the model ");

		if (encounter != null)
			encounter.log();
		else
			log.info("No Encounter data in the model");

		if (problem != null)
			problem.log();
		else
			log.info("No Problem data in the model");

		if (medication != null)
			medication.log();
		else
			log.info("No Medication data in the model");

		if (dischargeMedication != null)
			dischargeMedication.log();
		else
			log.info("No Discharge Medication data in the model");

		if (allergy != null)
			allergy.log();
		else
			log.info("No Allergy data in the model");

		if (immunization != null)
			immunization.log();
		else
			log.info("No Immunization data in the model");

		if (labResults != null)
			labResults.log();
		else
			log.info("No Lab Results data in the model");

		if (labTests != null)
			labTests.log();
		else
			log.info("No Lab Tests data in the model");

		if (procedure != null)
			procedure.log();
		else
			log.info("No Procedure data in the model");

		if (socialHistory != null)
			socialHistory.log();
		else
			log.info("No Smoking Status in the model");

		if (vitalSigns != null)
			vitalSigns.log();
		else
			log.info("No Vital Signs data in the model");

		if (goals != null)
			goals.log();
		else
			log.info("No Goals data in the model");

		if (planOfTreatment != null)
			planOfTreatment.log();
		else
			log.info("No Plan of Treatment data in the model");


		if (hcs != null)
			hcs.log();
		else
			log.info("No Health Concerns data in the model");

		if (members != null)
			members.log();
		else
			log.info("No Care Team Members data in the model");

		if (carePlanSections != null)
			carePlanSections.log();
		else
			log.info("No Care Plan Sections data (" + CCDACarePlanSections.REQUIRED_SECTIONS + ")"
					+ " available");

		for (int j = 0; j < udi.size(); j++) {
			udi.get(j).log();

		}

		for (int k = 0; k < notes.size(); k++) {

			notes.get(k).log();
		}

		if (medEquipments != null)
			medEquipments.log();
		else
			log.info(" Medical Equipment Data is null ");


		if (admissionDiagnosis != null)
			admissionDiagnosis.log();

		if (dischargeDiagnosis != null)
			dischargeDiagnosis.log();

		if (header != null)
			header.log();
	}

	public void performCIRIValidation(String validationObjective, CCDARefModel submittedCCDA, ArrayList<ContentValidationResult> results, boolean curesUpdate) {
		log.info("Comparing Patient Data ");
		comparePatients(submittedCCDA, results, curesUpdate);

		log.info("Comparing Problems ");
		compareProblems(validationObjective, submittedCCDA, results, curesUpdate);

		log.info("Comparing Allergies ");
		compareAllergies(validationObjective, submittedCCDA, results, curesUpdate);

		log.info("Comparing Medications ");
		compareMedications(validationObjective, submittedCCDA, results, curesUpdate);

		log.info("Finished comparison , returning results");

	}

	public void performCarePlanValidation(String validationObjective, CCDARefModel submittedCCDA, ArrayList<ContentValidationResult> results, boolean curesUpdate) {
		log.info("Comparing Patient Data ");
		comparePatients(submittedCCDA, results, curesUpdate);

		log.info("Comparing CarePlan Sections");
		compareCarePlanSections(submittedCCDA, results);

		log.info("Finished comparison , returning results");

	}

	public void performDS4PValidation(String validationObjective, CCDARefModel submittedCCDA, ArrayList<ContentValidationResult> results, boolean curesUpdate) {
		log.info("Comparing Patient Data ");
		comparePatients(submittedCCDA, results, curesUpdate);

		log.info("Finished comparison , returning results");

	}

	private void comparePatients(CCDARefModel submittedCCDA, ArrayList<ContentValidationResult> results, boolean curesUpdate) {

		if ((patient != null) && (submittedCCDA.getPatient() != null)) {
			this.patient.compare(submittedCCDA.getPatient(), results, submittedCCDA, curesUpdate);
		} else if ((patient == null) && (submittedCCDA.getPatient() != null)) {
			ContentValidationResult rs = new ContentValidationResult("The scenario does not require patient data, but submitted file does have patient data", enums.ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0");
			results.add(rs);
		} else if ((patient != null) && (submittedCCDA.getPatient() == null)) {
			ContentValidationResult rs = new ContentValidationResult("The scenario requires patient data, but submitted file does have patient data", enums.ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0");
			results.add(rs);
		} else {
			log.info("Both the Ref Model and the Submitted Patient Data are null ");
		}
	}

	private void compareCarePlanSections(CCDARefModel submittedCCDA, ArrayList<ContentValidationResult> results) {
		if (carePlanSections != null && submittedCCDA.getCarePlanSections() != null) {
			this.carePlanSections.compare(submittedCCDA.getCarePlanSections(), results, submittedCCDA);
		} else {
			log.error("An unexpected programmatic error has occurred where either "
					+ "carePlanSections is null or submittedCCDA.getCarePlanSections() is null, "
					+ "when both should have been initialized by CarePlanSectionsParser");
		}
	}*/
}
