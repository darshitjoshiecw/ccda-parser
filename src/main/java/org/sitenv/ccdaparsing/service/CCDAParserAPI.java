package org.sitenv.ccdaparsing.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sitenv.ccdaparsing.model.CCDAAdmissionDiagnosis;
import org.sitenv.ccdaparsing.model.CCDAAdvanceDirective;
import org.sitenv.ccdaparsing.model.CCDAAllergy;
import org.sitenv.ccdaparsing.model.CCDAAuthor;
import org.sitenv.ccdaparsing.model.CCDACarePlanSections;
import org.sitenv.ccdaparsing.model.CCDACareTeamMember;
import org.sitenv.ccdaparsing.model.CCDADischargeDiagnosis;
import org.sitenv.ccdaparsing.model.CCDADischargeMedication;
import org.sitenv.ccdaparsing.model.CCDAEncounter;
import org.sitenv.ccdaparsing.model.CCDAFamilyHistory;
import org.sitenv.ccdaparsing.model.CCDAFunctionalStatus;
import org.sitenv.ccdaparsing.model.CCDAGoals;
import org.sitenv.ccdaparsing.model.CCDAHeaderElements;
import org.sitenv.ccdaparsing.model.CCDAHealthConcerns;
import org.sitenv.ccdaparsing.model.CCDAID;
import org.sitenv.ccdaparsing.model.CCDAImmunization;
import org.sitenv.ccdaparsing.model.CCDALabResult;
import org.sitenv.ccdaparsing.model.CCDAMedicalEquipment;
import org.sitenv.ccdaparsing.model.CCDAMedication;
import org.sitenv.ccdaparsing.model.CCDAMentalStatus;
import org.sitenv.ccdaparsing.model.CCDANotes;
import org.sitenv.ccdaparsing.model.CCDANotesActivity;
import org.sitenv.ccdaparsing.model.CCDAPOT;
import org.sitenv.ccdaparsing.model.CCDAPatient;
import org.sitenv.ccdaparsing.model.CCDAProblem;
import org.sitenv.ccdaparsing.model.CCDAProcedure;
import org.sitenv.ccdaparsing.model.CCDARefModel;
import org.sitenv.ccdaparsing.model.CCDASocialHistory;
import org.sitenv.ccdaparsing.model.CCDAVitalSigns;
import org.sitenv.ccdaparsing.model.UsrhSubType;
import org.sitenv.ccdaparsing.processing.AdvanceDirectiveProcesser;
import org.sitenv.ccdaparsing.processing.AuthorParser;
import org.sitenv.ccdaparsing.processing.CCDAHeaderParser;
import org.sitenv.ccdaparsing.processing.CarePlanSectionsParser;
import org.sitenv.ccdaparsing.processing.CareTeamMemberProcessor;
import org.sitenv.ccdaparsing.processing.EncounterDiagnosesProcessor;
import org.sitenv.ccdaparsing.processing.FamilyHistoryProcessor;
import org.sitenv.ccdaparsing.processing.FunctionalStatusProcessor;
import org.sitenv.ccdaparsing.processing.GoalsProcessor;
import org.sitenv.ccdaparsing.processing.HealthConcernsProcessor;
import org.sitenv.ccdaparsing.processing.ImmunizationProcessor;
import org.sitenv.ccdaparsing.processing.LaboratoryResultsProcessor;
import org.sitenv.ccdaparsing.processing.LaboratoryTestProcessor;
import org.sitenv.ccdaparsing.processing.MediactionAllergiesProcessor;
import org.sitenv.ccdaparsing.processing.MedicalEquipmentProcessor;
import org.sitenv.ccdaparsing.processing.MedicationProcessor;
import org.sitenv.ccdaparsing.processing.MentalStatusProcessor;
import org.sitenv.ccdaparsing.processing.NotesParser;
import org.sitenv.ccdaparsing.processing.POTProcessor;
import org.sitenv.ccdaparsing.processing.PatientProcessor;
import org.sitenv.ccdaparsing.processing.ProblemProcessor;
import org.sitenv.ccdaparsing.processing.ProcedureProcessor;
import org.sitenv.ccdaparsing.processing.SmokingStatusProcessor;
import org.sitenv.ccdaparsing.processing.UDIProcessor;
import org.sitenv.ccdaparsing.processing.UsrhSubTypeProcessor;
import org.sitenv.ccdaparsing.processing.VitalSignProcessor;
import org.sitenv.ccdaparsing.util.PositionalXMLReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class CCDAParserAPI {

	private static final Logger logger = LogManager.getLogger(CCDAParserAPI.class);
	
	private static XPath xPath = XPathFactory.newInstance().newXPath();
	
	//private static String filePath = "C:/Projects/Dragon/CCDAParser/170.315_b1_toc_amb_ccd_r21_sample1_v1.xml";
	
	@Autowired
	PatientProcessor patientProcessor;
	
	@Autowired
	EncounterDiagnosesProcessor encounterDiagnosesProcessor;
	
	@Autowired
	ProblemProcessor problemProcessor;
	
	@Autowired
	MedicationProcessor medicationProcessor;
	
	@Autowired
	MediactionAllergiesProcessor mediactionAllergiesProcessor;
	
	@Autowired
	SmokingStatusProcessor smokingStatusProcessor;
	
	@Autowired
	LaboratoryTestProcessor laboratoryTestProcessor;
	
	@Autowired
	LaboratoryResultsProcessor laboratoryResultsProcessor;
	
	@Autowired
	VitalSignProcessor vitalSignProcessor;
	
	@Autowired
	ProcedureProcessor procedureProcessor;
	
	@Autowired
	CareTeamMemberProcessor careTeamMemberProcessor;
	
	@Autowired
	ImmunizationProcessor immunizationProcessor;
	
	@Autowired
	UDIProcessor uDIProcessor;
	
	@Autowired
	POTProcessor pOTProcessor;
	
	@Autowired
	GoalsProcessor goalsProcessor;
	
	@Autowired
	HealthConcernsProcessor healthConcernsProcessor;
	
	@Autowired
	UsrhSubTypeProcessor usrhSubTypeProcessor;

	@Autowired
	FamilyHistoryProcessor familyHistoryProcessor;

	@Autowired
	MedicalEquipmentProcessor medicalEquipmentProcessor;

	@Autowired
	AdvanceDirectiveProcesser advanceDirectiveProcesser;

	@Autowired
	FunctionalStatusProcessor functionalStatusProcessor;

	@Autowired
	MentalStatusProcessor mentalStatusProcessor;

	@Autowired
	CarePlanSectionsParser carePlanSectionsParser;

	@Autowired
	CCDAHeaderParser ccdaHeaderParser;

	@Autowired
	AuthorParser authorParser;

	@Autowired
	NotesParser notesParser;

	public CCDARefModel parseCCDA2_1(InputStream inputStream) {
		return parseCCDA2_1(inputStream,false);
	}
	public CCDARefModel parseCCDA2_1(InputStream inputStream, boolean partialParsingSupported) {
		
		CCDARefModel refModel = new CCDARefModel();
		Future<CCDAPatient> patient=null;
		Future<CCDAEncounter> encounters=null;
		Future<CCDAProblem> problems=null;
		Future<CCDAMedication> medications=null;
		Future<CCDAAllergy> allergies=null;
		Future<CCDASocialHistory> smokingStatus=null;
		Future<CCDALabResult> labTests=null;
		Future<CCDALabResult> labResults=null;
		Future<CCDAVitalSigns> vitals=null;
		Future<CCDAProcedure> procedures=null;
		Future<CCDACareTeamMember> careTeamMembers=null;
		Future<CCDAImmunization> immunizations=null;
		Future<CCDAPOT> pot=null;
		Future<CCDAGoals> goals=null;
		Future<CCDAHealthConcerns> healthConcerns=null;
		Future<CCDAFamilyHistory> familyHistory=null;
		Future<CCDAMedicalEquipment> medicalEquipments=null;
		Future<CCDAAdvanceDirective> advanceDirective=null;
		Future<CCDAFunctionalStatus> functionalStatus=null;
		Future<CCDAMentalStatus> mentalStatus=null;
		Future<UsrhSubType> usrhSubType=null;
		Future<CCDAAdmissionDiagnosis> admissionDiagnosisFuture=null;
		Future<CCDADischargeDiagnosis> dischargeDiagnosisFuture = null;
		Future<CCDACareTeamMember> careTeamMemberFuture = null;
		Future<CCDACarePlanSections> carePlanSectionsFuture = null;
		Future<CCDADischargeMedication> dischargeMedicationFuture = null;
		Future<CCDAHeaderElements> headerElementsFuture = null;
		Future<ArrayList<CCDAAuthor>> authorFromHeaderFuture = null;
		Future<ArrayList<CCDAAuthor>> authorWithLinkedRefFuture = null;
		Future<ArrayList<CCDANotes>> noteDetailListFuture = null;
		Future<ArrayList<CCDANotesActivity>> notesActivityListFuture = null;
		ArrayList<CCDAID> idList = new ArrayList<>();
		logger.info("Parsing CCDA document");
    	long startTime = System.currentTimeMillis();
    	logger.info("All section parsing Start time:"+ startTime);
		long maxWaitTime = 300000;
		long minWaitTime = 5000;
		boolean isTimeOut = false;
		
	    try {
			Document doc = PositionalXMLReader.readXML(inputStream);
			if(doc.getDocumentElement()!= null && doc.getDocumentElement().getChildNodes().getLength()>1)
			{
				refModel.setDocTemplateId(patientProcessor.retrieveDocTemplateId(xPath, doc));
				refModel.setEncompassingEncounter(patientProcessor.retrieveEncompassingEncounter(xPath, doc));
				admissionDiagnosisFuture = encounterDiagnosesProcessor.retrieveAdmissionDiagnosisDetails(doc);
				dischargeDiagnosisFuture = encounterDiagnosesProcessor.retrieveDischargeDiagnosisDetails(doc);
				patient=patientProcessor.retrievePatientDetails(xPath, doc);
				encounters = encounterDiagnosesProcessor.retrieveEncounterDetails(xPath, doc);
				problems = problemProcessor.retrieveProblemDetails(xPath, doc);
				medications = medicationProcessor.retrieveMedicationDetails(xPath, doc);
				allergies = mediactionAllergiesProcessor.retrieveAllergiesDetails(xPath, doc);
				smokingStatus = smokingStatusProcessor.retrieveSmokingStatusDetails(xPath, doc);
				labTests = laboratoryTestProcessor.retrieveLabTests(xPath, doc);
				labResults = laboratoryResultsProcessor.retrieveLabResults(xPath, doc);
				vitals = vitalSignProcessor.retrieveVitalSigns(xPath, doc);
				procedures = procedureProcessor.retrievePrcedureDetails(xPath, doc);
				careTeamMembers = careTeamMemberProcessor.retrieveCTMDetails(xPath, doc);
				careTeamMemberFuture = careTeamMemberProcessor.retrieveCareTeamSectionDetails(doc);
				carePlanSectionsFuture = carePlanSectionsParser.getSuggestedSections(doc);
				dischargeMedicationFuture = medicationProcessor.retrieveDischargeMedicationDetails(xPath, doc);
				headerElementsFuture = ccdaHeaderParser.getHeaderElements(doc, false);
				authorFromHeaderFuture = authorParser.retrieveAuthorsFromHeader(doc);
				authorWithLinkedRefFuture = authorParser.retrieveAuthorsWithLinkedReferenceData(doc);
				noteDetailListFuture = notesParser.retrieveNotesDetails(doc);
				notesActivityListFuture = notesParser.retrieveNotesActivities(doc);

				immunizations = immunizationProcessor.retrieveImmunizationDetails(xPath, doc);
				pot = pOTProcessor.retrievePOTDetails(xPath, doc);
				goals = goalsProcessor.retrieveGoalsDetails(xPath, doc);
				healthConcerns = healthConcernsProcessor.retrieveHealthConcernDetails(xPath, doc);
				usrhSubType = usrhSubTypeProcessor.retrieveUsrhSubTypeDetails(xPath, doc);
				familyHistory = familyHistoryProcessor.retrieveFamilyHistoryDetails(xPath, doc);
				medicalEquipments = medicalEquipmentProcessor.retrieveMedicalEquipment(xPath, doc);
				advanceDirective = advanceDirectiveProcesser.retrieveAdvanceDirectiveDetails(xPath, doc);
				functionalStatus = functionalStatusProcessor.retrieveFunctionalStatusDetails(xPath, doc);
				mentalStatus = mentalStatusProcessor.retrieveMentalStatusDetails(xPath, doc);

				if(patient!=null){
					try{
						refModel.setPatient(patient.get(maxWaitTime, TimeUnit.MILLISECONDS));
					}catch (Exception e) {
						if(!partialParsingSupported)
							isTimeOut = true;
						logger.error("Parsing Patient data section failed",e);
						refModel.addToErrorSections("Patient data");
					}
				}
				
				if(encounters!=null){
					try{
						refModel.setEncounter(encounters.get(isTimeOut?minWaitTime:maxWaitTime, TimeUnit.MILLISECONDS));
						if(refModel.getEncounter()!=null){
							idList.addAll(refModel.getEncounter().getIdLIst());
						}
					}catch (Exception e) {
						if(!partialParsingSupported)
							isTimeOut = true;
						logger.error("Parsing encounters section failed",e);
						refModel.addToErrorSections("Encounters");
					}
				}

				if (admissionDiagnosisFuture != null) {
					try {
						refModel.setAdmissionDiagnosis(admissionDiagnosisFuture.get(isTimeOut?minWaitTime:maxWaitTime, TimeUnit.MILLISECONDS));
					} catch (ExecutionException | InterruptedException | TimeoutException e) {
						if(!partialParsingSupported)
							isTimeOut = true;
						logger.error("Parsing Admission Diagnosis section failed",e);
						refModel.addToErrorSections("Admission Diagnosis");
					}
				}

				if (dischargeDiagnosisFuture != null) {
					try {
						refModel.setDischargeDiagnosis(dischargeDiagnosisFuture.get(isTimeOut?minWaitTime:maxWaitTime, TimeUnit.MILLISECONDS));
					} catch (ExecutionException | InterruptedException | TimeoutException e) {
						if(!partialParsingSupported)
							isTimeOut = true;
						logger.error("Parsing Discharge Diagnosis section failed",e);
						refModel.addToErrorSections("Discharge Diagnosis");
					}
				}

				if (careTeamMemberFuture != null) {
					try{
						refModel.setCareTeamSectionMembers(careTeamMemberFuture.get(isTimeOut?minWaitTime:maxWaitTime, TimeUnit.MILLISECONDS));
					} catch (ExecutionException | InterruptedException | TimeoutException e) {
						if(!partialParsingSupported)
							isTimeOut = true;
						logger.error("Parsing Care Team Section Member section failed",e);
						refModel.addToErrorSections("Care Team Section Member");
					}
				}

				if (carePlanSectionsFuture != null) {
					try {
						refModel.setCarePlanSections(carePlanSectionsFuture.get(isTimeOut?minWaitTime:maxWaitTime, TimeUnit.MILLISECONDS));
					} catch (ExecutionException | InterruptedException | TimeoutException e) {
						if(!partialParsingSupported)
							isTimeOut = true;
						logger.error("Parsing Care Team Sections section failed",e);
						refModel.addToErrorSections("Care Team Sections");
					}
				}

				if (dischargeMedicationFuture != null) {
					try {
						refModel.setDischargeMedication(dischargeMedicationFuture.get(isTimeOut?minWaitTime:maxWaitTime, TimeUnit.MILLISECONDS));
					} catch (ExecutionException | InterruptedException | TimeoutException e) {
						if(!partialParsingSupported)
							isTimeOut = true;
						logger.error("Parsing Discharge Medication section failed",e);
						refModel.addToErrorSections("Discharge Medication");
					}
				}

				if (headerElementsFuture != null) {
					try {
						refModel.setHeader(headerElementsFuture.get(isTimeOut?minWaitTime:maxWaitTime, TimeUnit.MILLISECONDS));
					} catch (ExecutionException | InterruptedException | TimeoutException e) {
						if(!partialParsingSupported)
							isTimeOut = true;
						logger.error("Parsing Header section failed",e);
						refModel.addToErrorSections("Header");
					}
				}

				if (authorFromHeaderFuture != null) {
					try {
						refModel.setAuthorsFromHeader(authorFromHeaderFuture.get(isTimeOut?minWaitTime:maxWaitTime, TimeUnit.MILLISECONDS));
					} catch (ExecutionException | InterruptedException | TimeoutException e) {
						if(!partialParsingSupported)
							isTimeOut = true;
						logger.error("Parsing Author From Header section failed",e);
						refModel.addToErrorSections("Author from Header");
					}
				}

				if (authorWithLinkedRefFuture != null) {
					try {
						refModel.setAuthorsWithLinkedReferenceData(authorWithLinkedRefFuture.get(isTimeOut?minWaitTime:maxWaitTime, TimeUnit.MILLISECONDS));
					} catch (ExecutionException | InterruptedException | TimeoutException e) {
						if(!partialParsingSupported)
							isTimeOut = true;
						logger.error("Parsing with Linked Reference section failed",e);
						refModel.addToErrorSections("Author with Linked Reference");
					}
				}

				if (noteDetailListFuture != null) {
					try {
						refModel.setNotes(noteDetailListFuture.get(isTimeOut?minWaitTime:maxWaitTime, TimeUnit.MILLISECONDS));
					} catch (ExecutionException | InterruptedException | TimeoutException e) {
						if(!partialParsingSupported)
							isTimeOut = true;
						logger.error("Parsing Note details section failed",e);
						refModel.addToErrorSections("Note details");
					}
				}

				if (notesActivityListFuture != null) {
					try {
						refModel.setNotesEntries(notesActivityListFuture.get(isTimeOut?minWaitTime:maxWaitTime, TimeUnit.MILLISECONDS));
					}  catch (ExecutionException | InterruptedException | TimeoutException e) {
						if(!partialParsingSupported)
							isTimeOut = true;
						logger.error("Parsing Notes Activity section failed",e);
						refModel.addToErrorSections("Notes Activity");
					}
				}
				
				if(problems!=null){
					try{
						refModel.setProblem(problems.get(isTimeOut?minWaitTime:maxWaitTime, TimeUnit.MILLISECONDS));
						if(refModel.getProblem()!=null){
							idList.addAll(refModel.getProblem().getIdList());
						}
					}catch (Exception e) {
						if(!partialParsingSupported)
							isTimeOut = true;
						logger.error("Parsing problem section failed",e);
						refModel.addToErrorSections("Problems");
					}
				}
				
				if(medications!=null){
					try{
						refModel.setMedication(medications.get(isTimeOut?minWaitTime:maxWaitTime, TimeUnit.MILLISECONDS));
						if(refModel.getMedication()!=null){
							idList.addAll(refModel.getMedication().getIdList());
						}
					}catch (Exception e) {
						if(!partialParsingSupported)
							isTimeOut = true;
						logger.error("Parsing medications section failed",e);
						refModel.addToErrorSections("Medications");
					}
				}
				
				if(allergies!=null){
					try{
						refModel.setAllergy(allergies.get(isTimeOut?minWaitTime:maxWaitTime, TimeUnit.MILLISECONDS));
						if(refModel.getAllergy()!=null){
							idList.addAll(refModel.getAllergy().getIdList());
						}
					}catch (Exception e) {
						if(!partialParsingSupported)
							isTimeOut = true;
						logger.error("Parsing allergies section failed",e);
						refModel.addToErrorSections("Allergies");
					}
				}
				
				if(smokingStatus!=null){
					try{
						refModel.setSmokingStatus(smokingStatus.get(isTimeOut?minWaitTime:maxWaitTime, TimeUnit.MILLISECONDS));
						if(refModel.getSmokingStatus()!=null){
							idList.addAll(refModel.getSmokingStatus().getIdList());
						}
					}catch (Exception e) {
						if(!partialParsingSupported)
							isTimeOut = true;
						logger.error("Parsing Smoking status section failed",e);
						refModel.addToErrorSections("Smoking status");
					}
				}
				
				if(labTests!=null){
					try{
						refModel.setLabTests(labTests.get(isTimeOut?minWaitTime:maxWaitTime, TimeUnit.MILLISECONDS));
						if(refModel.getLabTests()!=null){
							idList.addAll(refModel.getLabTests().getIdList());
						}
					}catch (Exception e) {
						if(!partialParsingSupported)
							isTimeOut = true;
						logger.error("Parsing Lab Tests section failed",e);
						refModel.addToErrorSections("Lab Tests");
					}
				}
				
				if(labResults!=null){
					try{
						refModel.setLabResults(labResults.get(isTimeOut?minWaitTime:maxWaitTime, TimeUnit.MILLISECONDS));
						if(refModel.getLabResults()!=null){
							idList.addAll(refModel.getLabResults().getIdList());
						}
					}catch (Exception e) {
						if(!partialParsingSupported)
							isTimeOut = true;
						logger.error("Parsing Lab Results section failed",e);
						refModel.addToErrorSections("Lab Results");
					}
				}
				
				if(vitals!=null){
					try{
						refModel.setVitalSigns(vitals.get(isTimeOut?minWaitTime:maxWaitTime, TimeUnit.MILLISECONDS));
						if(refModel.getVitalSigns()!=null){
							idList.addAll(refModel.getVitalSigns().getIdList());
						}
					}catch (Exception e) {
						if(!partialParsingSupported)
							isTimeOut = true;
						logger.error("Parsing Vitals section failed",e);
						refModel.addToErrorSections("Vitals");
					}
				}
				
				if(procedures!=null){
					try{
						refModel.setProcedure(procedures.get(isTimeOut?minWaitTime:maxWaitTime, TimeUnit.MILLISECONDS));
						if(refModel.getProcedure()!=null){
							idList.addAll(refModel.getProcedure().getIdList());
						}
					}catch (Exception e) {
						if(!partialParsingSupported)
							isTimeOut = true;
						logger.error("Parsing procedures section failed",e);
						refModel.addToErrorSections("Procedures");
					}
				}
				
				if(careTeamMembers!=null){
					try{
						refModel.setMembers(careTeamMembers.get(isTimeOut?minWaitTime:maxWaitTime, TimeUnit.MILLISECONDS));
					}catch (Exception e) {
						if(!partialParsingSupported)
							isTimeOut = true;
						logger.error("Parsing Care team members section failed",e);
						refModel.addToErrorSections("Care team members");
					}
				}
				
				if(immunizations!=null){
					try{
						refModel.setImmunization(immunizations.get(isTimeOut?minWaitTime:maxWaitTime, TimeUnit.MILLISECONDS));
						if(refModel.getImmunization()!=null){
							idList.addAll(refModel.getImmunization().getIdList());
						}
					}catch (Exception e) {
						if(!partialParsingSupported)
							isTimeOut = true;
						logger.error("Parsing immunizations section failed",e);
						refModel.addToErrorSections("Immunizations");
					}
				}
				refModel.setUdi(uDIProcessor.retrieveUDIDetails(refModel.getProcedure()));
				
				if(pot!=null){
					try{
						refModel.setPlanOfTreatment(pot.get(isTimeOut?minWaitTime:maxWaitTime, TimeUnit.MILLISECONDS));
						if(refModel.getPlanOfTreatment()!=null){
							idList.addAll(refModel.getPlanOfTreatment().getIdList());
						}
					}catch (Exception e) {
						if(!partialParsingSupported)
							isTimeOut = true;
						logger.error("Parsing Plan of treatment section failed",e);
						refModel.addToErrorSections("Plan of treatment");
					}
				}
				
				if(goals!=null){
					try{
						refModel.setGoals(goals.get(isTimeOut?minWaitTime:maxWaitTime, TimeUnit.MILLISECONDS));
					}catch (Exception e) {
						if(!partialParsingSupported)
							isTimeOut = true;
						logger.error("Parsing goals section failed",e);
						refModel.addToErrorSections("Goals");
					}
				}
				
				if(healthConcerns!=null){
					try{
						refModel.setHcs(healthConcerns.get(isTimeOut?minWaitTime:maxWaitTime, TimeUnit.MILLISECONDS));
					}catch (Exception e) {
						if(!partialParsingSupported)
							isTimeOut = true;
						logger.error("Parsing Health concerns section failed",e);
						refModel.addToErrorSections("Health concerns");
					}
				}
				
				if(usrhSubType!=null){
					try{
						refModel.setUsrhSubType(usrhSubType.get(isTimeOut?minWaitTime:maxWaitTime, TimeUnit.MILLISECONDS));
					}catch (Exception e) {
						if(!partialParsingSupported)
							isTimeOut = true;
						logger.error("Parsing Usrh sub type section failed",e);
						refModel.addToErrorSections("Usrh sub type");
					}
				}

				if(familyHistory!=null){
					try{
						refModel.setFamilyHistory(familyHistory.get(isTimeOut?minWaitTime:maxWaitTime, TimeUnit.MILLISECONDS));
						if(refModel.getFamilyHistory()!=null){
							idList.addAll(refModel.getFamilyHistory().getIdList());
						}
					}catch (Exception e) {
						if(!partialParsingSupported)
							isTimeOut = true;
						logger.error("Parsing Family History section failed",e);
						refModel.addToErrorSections("Family History");
					}
				}
				if(medicalEquipments!=null){
					try {
						refModel.setMedicalEquipment(medicalEquipments.get(isTimeOut?minWaitTime:maxWaitTime, TimeUnit.MILLISECONDS));
					} catch (InterruptedException | ExecutionException | TimeoutException e) {
						if(!partialParsingSupported)
							isTimeOut = true;
						logger.error("Parsing Medical equipment section failed",e);
						refModel.addToErrorSections("Medical equipment");
					}
					if(refModel.getMedicalEquipment()!=null){
						idList.addAll(refModel.getMedicalEquipment().getIds());
					}
				}

				if(advanceDirective!=null){
					try{
						refModel.setAdvanceDirective(advanceDirective.get(isTimeOut?minWaitTime:maxWaitTime, TimeUnit.MILLISECONDS));
						if(refModel.getAdvanceDirective()!=null){
							idList.addAll(refModel.getAdvanceDirective().getIdList());
						}
					}catch (Exception e) {
						if(!partialParsingSupported)
							isTimeOut = true;
						logger.error("Parsing Advance directive section failed",e);
						refModel.addToErrorSections("Advance directive");
					}
				}

				if(functionalStatus!=null){
					try{
						refModel.setFunctionalStatus(functionalStatus.get(isTimeOut?minWaitTime:maxWaitTime, TimeUnit.MILLISECONDS));
						if(refModel.getFunctionalStatus()!=null){
							idList.addAll(refModel.getFunctionalStatus().getIdList());
						}
					}catch (Exception e) {
						if(!partialParsingSupported)
							isTimeOut = true;
						logger.error("Parsing functional status section failed",e);
						refModel.addToErrorSections("Functional status");
					}
				}

				if(mentalStatus!=null){
					try{
						refModel.setMentalStatus(mentalStatus.get(isTimeOut?minWaitTime:maxWaitTime, TimeUnit.MILLISECONDS));
						if(refModel.getMentalStatus()!=null){
							idList.addAll(refModel.getMentalStatus().getIdList());
						}
					}catch (Exception e) {
						if(!partialParsingSupported)
							isTimeOut = true;
						logger.error("Parsing mental status section failed",e);
						refModel.addToErrorSections("Mental status");
					}
				}
				
				refModel.setIdList(idList);
			}
			else
			{
				refModel.setEmpty(true);
			}
			
			logger.info("Parsing CCDA document completed");
		    }
	    	catch (FileNotFoundException fnfException) 
	    	{
	    		logger.error(fnfException);
			}
	    	catch(XPathExpressionException xpeException)
			{
	    		logger.error(xpeException);
			}
	    	catch (IOException ioException) 
	    	{
	    		logger.error(ioException);
			}
	    	catch (SAXException saxException) 
	    	{
	    		logger.info("Parsing CCDA document failed");
	    		logger.error(saxException);
	    		refModel.setEmpty(true);
			}
	    	catch (NullPointerException npException) 
	    	{
	    		logger.error(npException);
			}
	        catch(TransformerException te)
	    	{
	            logger.error(te);
	    	}
	    logger.info("All Section End time:"+ (System.currentTimeMillis() - startTime));
	    
	    return refModel;
	
	}
}
