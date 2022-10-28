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
import org.sitenv.ccdaparsing.util.CustomReadXml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class CCDAParserAPI {

	private static final Logger logger = LogManager.getLogger(CCDAParserAPI.class);
	
	private static XPath xPath = XPathFactory.newInstance().newXPath();

	private boolean isAnySectionPresent = false;
	private String EMPTY = "empty";

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

	private String xml;

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public CCDARefModel parseCCDA2_1() {
		return parseCCDA2_1(false);
	}
	public CCDARefModel parseCCDA2_1(boolean partialParsingSupported) {
		return parseCCDA2_1(partialParsingSupported,null);
	}
	public CCDARefModel parseCCDA2_1(boolean partialParsingSupported, Set<String> sectionsList) {

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

			long ccdaParseStartTime = System.currentTimeMillis();

			//admissionDiagnosisFuture = encounterDiagnosesProcessor.retrieveAdmissionDiagnosisDetails(doc);
			//dischargeDiagnosisFuture = encounterDiagnosesProcessor.retrieveDischargeDiagnosisDetails(doc);
			patient = isSectionEnabled(sectionsList, "PATIENT DATA") ? preparePatient(refModel) : null;
			encounters = isSectionEnabled(sectionsList, "ENCOUNTER") ? prepareEncounters(new String[]{"2.16.840.1.113883.10.20.22.2.22", "2.16.840.1.113883.10.20.22.2.22.1"}) : null;
			problems = isSectionEnabled(sectionsList, "CONDITION") ? prepareProblems(new String[]{"2.16.840.1.113883.10.20.22.2.5", "2.16.840.1.113883.10.20.22.2.5.1"}) : null;
			medications = isSectionEnabled(sectionsList, "MEDICATIONREQUEST") ? prepareMedications(new String[]{"2.16.840.1.113883.10.20.22.2.1", "2.16.840.1.113883.10.20.22.2.1.1"}) : null;
			allergies = isSectionEnabled(sectionsList, "ALLERGYINTOLERANCE") ? prepareAllergies(new String[]{"2.16.840.1.113883.10.20.22.2.6", "2.16.840.1.113883.10.20.22.2.6.1"}) : null;
			smokingStatus = isSectionEnabled(sectionsList, "SMOKINGSTATUS") ? prepareSmokingStatus(new String[]{"2.16.840.1.113883.10.20.22.2.17", EMPTY}) : null;
			labTests = isSectionEnabled(sectionsList, "LAB TESTS") ? prepareLabTests(new String[]{"2.16.840.1.113883.10.20.22.2.3.1", EMPTY}) : null;
			labResults = isSectionEnabled(sectionsList, "LABRESULTS") ? prepareLabResults(new String[]{"2.16.840.1.113883.10.20.22.2.3", "2.16.840.1.113883.10.20.22.2.3.1"}) : null;
			vitals = isSectionEnabled(sectionsList, "VITALS") ? prepareVitals(new String[] {"2.16.840.1.113883.10.20.22.2.4", "2.16.840.1.113883.10.20.22.2.4.1"}) : null;
			procedures = isSectionEnabled(sectionsList, "PROCEDURE") ? prepareProcedures(new String[] {"2.16.840.1.113883.10.20.22.2.7", "2.16.840.1.113883.10.20.22.2.7.1"}) : null;
			careTeamMembers = isSectionEnabled(sectionsList, "CARETEAM") ? prepareCareTeamMembers(new String[] {"2.16.840.1.113883.10.20.22.2.500", EMPTY}) : null;
			carePlanSectionsFuture = isSectionEnabled(sectionsList, "CAREPLAN") ? prepareCarePlan(new String[] {"2.16.840.1.113883.10.20.21.2.3", EMPTY}) : null;
			//dischargeMedicationFuture = medicationProcessor.retrieveDischargeMedicationDetails(xPath, doc);
			headerElementsFuture = isSectionEnabled(sectionsList, "HEADER ELEMENTS") ? prepareHeaderElements() : null;
			//authorFromHeaderFuture = authorParser.retrieveAuthorsFromHeader(doc);
			//authorWithLinkedRefFuture = authorParser.retrieveAuthorsWithLinkedReferenceData(doc);
			noteDetailListFuture = isSectionEnabled(sectionsList, "NOTES") ? prepareNotes(new String[] {"2.16.840.1.113883.10.20.22.2.65", EMPTY}) : null;
			notesActivityListFuture = isSectionEnabled(sectionsList, "NOTES ACTIVITY") ? prepareNotesActivity(new String[] {"2.16.840.1.113883.10.20.22.4.202", EMPTY}) : null;
			immunizations = isSectionEnabled(sectionsList, "IMMUNIZATION") ? prepareImmunizations(new String[] {"2.16.840.1.113883.10.20.22.2.2", "2.16.840.1.113883.10.20.22.2.2.1"}) : null;
			pot = isSectionEnabled(sectionsList, "PLAN OF TREATMENT") ? preparePOT(new String[] {"2.16.840.1.113883.10.20.22.2.10", EMPTY}) : null;
			goals = isSectionEnabled(sectionsList, "GOAL") ? prepareGoals(new String[] {"2.16.840.1.113883.10.20.22.2.60", EMPTY}) : null;
			healthConcerns = isSectionEnabled(sectionsList, "HEALTH CONCERNS") ? prepareHealthConcern(new String[] {"2.16.840.1.113883.10.20.22.2.58", EMPTY}) : null;
			//usrhSubType = isSectionEnabled("USRH SUB TYPE") ? prepareUsrhSubType()usrhSubTypeProcessor.retrieveUsrhSubTypeDetails(xPath, doc) : null; //complex condition
			familyHistory = isSectionEnabled(sectionsList, "FAMILY HISTORY") ? prepareFamilyHistory(new String[] {"2.16.840.1.113883.10.20.22.2.15", EMPTY}) : null;
			medicalEquipments = isSectionEnabled(sectionsList, "MEDICAL EQUIPMENTS") ? prepareMedicalEquipments(new String[] {"2.16.840.1.113883.10.20.22.2.23", EMPTY}) : null;
			advanceDirective = isSectionEnabled(sectionsList, "ADVANCE DIRECTIVE") ? prepareAdvanceDirective(new String[] {"2.16.840.1.113883.10.20.22.2.21", "2.16.840.1.113883.10.20.22.2.21.1"}) : null;
			functionalStatus = prepareFunctionalStatus(new String[] {"2.16.840.1.113883.10.20.22.2.14", EMPTY});
			mentalStatus = prepareMentalStatus(new String[] {"2.16.840.1.113883.10.20.22.2.56", EMPTY});

			if (patient != null) {
				try {
					long nStartTime = System.currentTimeMillis();
					refModel.setPatient(patient.get(maxWaitTime, TimeUnit.MILLISECONDS));
					long endTime = System.currentTimeMillis();
					long nTimeTaken = (endTime - nStartTime) / 1000; // time in seconds
					logger.info("patient: " + nTimeTaken);
				} catch (ExecutionException | InterruptedException | TimeoutException e) {
					if (!partialParsingSupported)
						isTimeOut = true;
					logger.error("Parsing Patient data section failed", e);
					refModel.addToErrorSections("PATIENT DATA");
				} catch (Exception e) {
					logger.error("Parsing Patient data section failed", e);
					refModel.addToErrorSections("PATIENT DATA");
				}
			}

			if (encounters != null) {
				try {
					long nStartTime = System.currentTimeMillis();
					refModel.setEncounter(encounters.get(isTimeOut ? minWaitTime : maxWaitTime, TimeUnit.MILLISECONDS));
					long endTime = System.currentTimeMillis();
					long nTimeTaken = (endTime - nStartTime) / 1000; // time in seconds
					logger.info("encounters: " + nTimeTaken);
					if (refModel.getEncounter() != null && refModel.getEncounter().getIdLIst() != null) {
						idList.addAll(refModel.getEncounter().getIdLIst());
					}
				} catch (ExecutionException | InterruptedException | TimeoutException e) {
					if (!partialParsingSupported)
						isTimeOut = true;
					logger.error("Parsing encounters section failed", e);
					refModel.addToErrorSections("ENCOUNTER");
				} catch (Exception e) {
					logger.error("Parsing encounters section failed", e);
					refModel.addToErrorSections("ENCOUNTER");
				}
			}

			if (admissionDiagnosisFuture != null) {
				try {
					long nStartTime = System.currentTimeMillis();
					refModel.setAdmissionDiagnosis(admissionDiagnosisFuture.get(isTimeOut ? minWaitTime : maxWaitTime, TimeUnit.MILLISECONDS));
					long endTime = System.currentTimeMillis();
					long nTimeTaken = (endTime - nStartTime) / 1000; // time in seconds
					logger.info("admissionDiagnosisFuture: " + nTimeTaken);
				} catch (ExecutionException | InterruptedException | TimeoutException e) {
					if (!partialParsingSupported)
						isTimeOut = true;
					logger.error("Parsing Admission Diagnosis section failed", e);
					refModel.addToErrorSections("Admission Diagnosis");
				} catch (Exception e) {
					logger.error("Parsing Admission Diagnosis section failed", e);
					refModel.addToErrorSections("Admission Diagnosis");
				}
			}

			if (dischargeDiagnosisFuture != null) {
				try {
					long nStartTime = System.currentTimeMillis();
					refModel.setDischargeDiagnosis(dischargeDiagnosisFuture.get(isTimeOut ? minWaitTime : maxWaitTime, TimeUnit.MILLISECONDS));
					long endTime = System.currentTimeMillis();
					long nTimeTaken = (endTime - nStartTime) / 1000;
					logger.info("dischargeDiagnosisFuture: " + nTimeTaken);
				} catch (ExecutionException | InterruptedException | TimeoutException e) {
					if (!partialParsingSupported)
						isTimeOut = true;
					logger.error("Parsing Discharge Diagnosis section failed", e);
					refModel.addToErrorSections("Discharge Diagnosis");
				} catch (Exception e) {
					logger.error("Parsing Discharge Diagnosis section failed", e);
					refModel.addToErrorSections("Discharge Diagnosis");
				}
			}

			if (carePlanSectionsFuture != null) {
				try {
					long nStartTime = System.currentTimeMillis();
					refModel.setCarePlanSections(carePlanSectionsFuture.get(isTimeOut ? minWaitTime : maxWaitTime, TimeUnit.MILLISECONDS));
					long endTime = System.currentTimeMillis();
					long nTimeTaken = (endTime - nStartTime) / 1000;
					logger.info("carePlanSectionsFuture: " + nTimeTaken);
				} catch (ExecutionException | InterruptedException | TimeoutException e) {
					if (!partialParsingSupported)
						isTimeOut = true;
					logger.error("Parsing Care Plan Sections section failed", e);
					refModel.addToErrorSections("CAREPLAN");
				} catch (Exception e) {
					logger.error("Parsing Care Plan Sections section failed", e);
					refModel.addToErrorSections("CAREPLAN");
				}
			}

			if (dischargeMedicationFuture != null) {
				try {
					long nStartTime = System.currentTimeMillis();
					refModel.setDischargeMedication(dischargeMedicationFuture.get(isTimeOut ? minWaitTime : maxWaitTime, TimeUnit.MILLISECONDS));
					long endTime = System.currentTimeMillis();
					long nTimeTaken = (endTime - nStartTime) / 1000;
					logger.info("dischargeMedicationFuture: " + nTimeTaken);
				} catch (ExecutionException | InterruptedException | TimeoutException e) {
					if (!partialParsingSupported)
						isTimeOut = true;
					logger.error("Parsing Discharge Medication section failed", e);
					refModel.addToErrorSections("Discharge Medication");
				} catch (Exception e) {
					logger.error("Parsing Discharge Medication section failed", e);
					refModel.addToErrorSections("Discharge Medication");
				}
			}

			if (headerElementsFuture != null) {
				try {
					long nStartTime = System.currentTimeMillis();
					refModel.setHeader(headerElementsFuture.get(isTimeOut ? minWaitTime : maxWaitTime, TimeUnit.MILLISECONDS));
					long endTime = System.currentTimeMillis();
					long nTimeTaken = (endTime - nStartTime) / 1000;
					logger.info("headerElementsFuture: " + nTimeTaken);
				} catch (ExecutionException | InterruptedException | TimeoutException e) {
					if (!partialParsingSupported)
						isTimeOut = true;
					logger.error("Parsing Header section failed", e);
					refModel.addToErrorSections("Header");
				} catch (Exception e) {
					logger.error("Parsing Header section failed", e);
					refModel.addToErrorSections("Header");
				}
			}

			if (authorFromHeaderFuture != null) {
				try {
					long nStartTime = System.currentTimeMillis();
					refModel.setAuthorsFromHeader(authorFromHeaderFuture.get(isTimeOut ? minWaitTime : maxWaitTime, TimeUnit.MILLISECONDS));
					long endTime = System.currentTimeMillis();
					long nTimeTaken = (endTime - nStartTime) / 1000;
					logger.info("authorFromHeaderFuture: " + nTimeTaken);
				} catch (ExecutionException | InterruptedException | TimeoutException e) {
					if (!partialParsingSupported)
						isTimeOut = true;
					logger.error("Parsing Author From Header section failed", e);
					refModel.addToErrorSections("Author from Header");
				} catch (Exception e) {
					logger.error("Parsing Author From Header section failed", e);
					refModel.addToErrorSections("Author from Header");
				}
			}

			if (authorWithLinkedRefFuture != null) {
				try {
					long nStartTime = System.currentTimeMillis();
					refModel.setAuthorsWithLinkedReferenceData(authorWithLinkedRefFuture.get(isTimeOut ? minWaitTime : maxWaitTime, TimeUnit.MILLISECONDS));
					long endTime = System.currentTimeMillis();
					long nTimeTaken = (endTime - nStartTime) / 1000;
					logger.info("authorWithLinkedRefFuture: " + nTimeTaken);
				} catch (ExecutionException | InterruptedException | TimeoutException e) {
					if (!partialParsingSupported)
						isTimeOut = true;
					logger.error("Parsing with Linked Reference section failed", e);
					refModel.addToErrorSections("Author with Linked Reference");
				} catch (Exception e) {
					logger.error("Parsing with Linked Reference section failed", e);
					refModel.addToErrorSections("Author with Linked Reference");
				}
			}

			if (noteDetailListFuture != null) {
				try {
					long nStartTime = System.currentTimeMillis();
					refModel.setNotes(noteDetailListFuture.get(isTimeOut ? minWaitTime : maxWaitTime, TimeUnit.MILLISECONDS));
					long endTime = System.currentTimeMillis();
					long nTimeTaken = (endTime - nStartTime) / 1000;
					logger.info("noteDetailListFuture: " + nTimeTaken);
				} catch (ExecutionException | InterruptedException | TimeoutException e) {
					if (!partialParsingSupported)
						isTimeOut = true;
					logger.error("Parsing Note details section failed", e);
					refModel.addToErrorSections("NOTES");
				} catch (Exception e) {
					logger.error("Parsing Note details section failed", e);
					refModel.addToErrorSections("NOTES");
				}
			}

			if (notesActivityListFuture != null) {
				try {
					long nStartTime = System.currentTimeMillis();
					refModel.setNotesEntries(notesActivityListFuture.get(isTimeOut ? minWaitTime : maxWaitTime, TimeUnit.MILLISECONDS));
					long endTime = System.currentTimeMillis();
					long nTimeTaken = (endTime - nStartTime) / 1000;
					logger.info("notesActivityListFuture: " + nTimeTaken);
				} catch (ExecutionException | InterruptedException | TimeoutException e) {
					if (!partialParsingSupported)
						isTimeOut = true;
					logger.error("Parsing Notes Activity section failed", e);
					refModel.addToErrorSections("Notes Activity");
				} catch (Exception e) {
					logger.error("Parsing Notes Activity section failed", e);
					refModel.addToErrorSections("Notes Activity");
				}
			}

			if (allergies != null) {
				try {
					long nStartTime = System.currentTimeMillis();
					refModel.setAllergy(allergies.get(isTimeOut ? minWaitTime : maxWaitTime, TimeUnit.MILLISECONDS));
					long endTime = System.currentTimeMillis();
					long nTimeTaken = (endTime - nStartTime) / 1000;
					logger.info("allergies: " + nTimeTaken);
					if (refModel.getAllergy() != null && refModel.getAllergy().getIdList() != null) {
						idList.addAll(refModel.getAllergy().getIdList());
					}
				} catch (ExecutionException | InterruptedException | TimeoutException e) {
					if (!partialParsingSupported)
						isTimeOut = true;
					logger.error("Parsing allergies section failed", e);
					refModel.addToErrorSections("ALLERGYINTOLERANCE");
				} catch (Exception e) {
					logger.error("Parsing allergies section failed", e);
					refModel.addToErrorSections("ALLERGYINTOLERANCE");
				}
			}

			if (smokingStatus != null) {
				try {
					long nStartTime = System.currentTimeMillis();
					refModel.setSmokingStatus(smokingStatus.get(isTimeOut ? minWaitTime : maxWaitTime, TimeUnit.MILLISECONDS));
					long endTime = System.currentTimeMillis();
					long nTimeTaken = (endTime - nStartTime) / 1000;
					logger.info("smokingStatus: " + nTimeTaken);
					if (refModel.getSmokingStatus() != null && refModel.getSmokingStatus().getIdList() != null) {
						idList.addAll(refModel.getSmokingStatus().getIdList());
					}
				} catch (ExecutionException | InterruptedException | TimeoutException e) {
					if (!partialParsingSupported)
						isTimeOut = true;
					logger.error("Parsing Smoking status section failed", e);
					refModel.addToErrorSections("SMOKINGSTATUS");
				} catch (Exception e) {
					logger.error("Parsing Smoking status section failed", e);
					refModel.addToErrorSections("SMOKINGSTATUS");
				}
			}

			if (labTests != null) {
				try {
					long nStartTime = System.currentTimeMillis();
					refModel.setLabTests(labTests.get(isTimeOut ? minWaitTime : maxWaitTime, TimeUnit.MILLISECONDS));
					long endTime = System.currentTimeMillis();
					long nTimeTaken = (endTime - nStartTime) / 1000;
					logger.info("labTests: " + nTimeTaken);
					if (refModel.getLabTests() != null && refModel.getLabTests().getIdList() != null) {
						idList.addAll(refModel.getLabTests().getIdList());
					}
				} catch (ExecutionException | InterruptedException | TimeoutException e) {
					if (!partialParsingSupported)
						isTimeOut = true;
					logger.error("Parsing Lab Tests section failed", e);
					refModel.addToErrorSections("Lab Tests");
				} catch (Exception e) {
					logger.error("Parsing Lab Tests section failed", e);
					refModel.addToErrorSections("Lab Tests");
				}
			}

			if (vitals != null) {
				try {
					long nStartTime = System.currentTimeMillis();
					refModel.setVitalSigns(vitals.get(isTimeOut ? minWaitTime : maxWaitTime, TimeUnit.MILLISECONDS));
					long endTime = System.currentTimeMillis();
					long nTimeTaken = (endTime - nStartTime) / 1000;
					logger.info("vitals: " + nTimeTaken);
					if (refModel.getVitalSigns() != null && refModel.getVitalSigns().getIdList() != null) {
						idList.addAll(refModel.getVitalSigns().getIdList());
					}
				} catch (ExecutionException | InterruptedException | TimeoutException e) {
					if (!partialParsingSupported)
						isTimeOut = true;
					logger.error("Parsing Vitals section failed", e);
					refModel.addToErrorSections("VITALS");
				} catch (Exception e) {
					logger.error("Parsing Vitals section failed", e);
					refModel.addToErrorSections("VITALS");
				}
			}

			if (careTeamMembers != null) {
				try {
					long nStartTime = System.currentTimeMillis();
					refModel.setMembers(careTeamMembers.get(isTimeOut ? minWaitTime : maxWaitTime, TimeUnit.MILLISECONDS));
					long endTime = System.currentTimeMillis();
					long nTimeTaken = (endTime - nStartTime) / 1000;
					logger.info("careTeamMembers: " + nTimeTaken);
				} catch (ExecutionException | InterruptedException | TimeoutException e) {
					if (!partialParsingSupported)
						isTimeOut = true;
					logger.error("Parsing Care team members section failed", e);
					refModel.addToErrorSections("CARETEAM");
				} catch (Exception e) {
					logger.error("Parsing Care team members section failed", e);
					refModel.addToErrorSections("CARETEAM");
				}
			}

			if (immunizations != null) {
				try {
					long nStartTime = System.currentTimeMillis();
					refModel.setImmunization(immunizations.get(isTimeOut ? minWaitTime : maxWaitTime, TimeUnit.MILLISECONDS));
					long endTime = System.currentTimeMillis();
					long nTimeTaken = (endTime - nStartTime) / 1000;
					logger.info("immunizations: " + nTimeTaken);
					if (refModel.getImmunization() != null && refModel.getImmunization().getIdList() != null) {
						idList.addAll(refModel.getImmunization().getIdList());
					}
				} catch (ExecutionException | InterruptedException | TimeoutException e) {
					if (!partialParsingSupported)
						isTimeOut = true;
					logger.error("Parsing immunizations section failed", e);
					refModel.addToErrorSections("IMMUNIZATION");
				} catch (Exception e) {
					logger.error("Parsing immunizations section failed", e);
					refModel.addToErrorSections("IMMUNIZATION");
				}
			}
			refModel.setUdi(uDIProcessor.retrieveUDIDetails(refModel.getProcedure()));

			if (pot != null) {
				try {
					long nStartTime = System.currentTimeMillis();
					refModel.setPlanOfTreatment(pot.get(isTimeOut ? minWaitTime : maxWaitTime, TimeUnit.MILLISECONDS));
					long endTime = System.currentTimeMillis();
					long nTimeTaken = (endTime - nStartTime) / 1000;
					logger.info("pot: " + nTimeTaken);
					if (refModel.getPlanOfTreatment() != null && refModel.getPlanOfTreatment().getIdList() != null) {
						idList.addAll(refModel.getPlanOfTreatment().getIdList());
					}
				} catch (ExecutionException | InterruptedException | TimeoutException e) {
					if (!partialParsingSupported)
						isTimeOut = true;
					logger.error("Parsing Plan of treatment section failed", e);
					refModel.addToErrorSections("Plan of treatment");
				} catch (Exception e) {
					logger.error("Parsing Plan of treatment section failed", e);
					refModel.addToErrorSections("Plan of treatment");
				}
			}

			if (goals != null) {
				try {
					long nStartTime = System.currentTimeMillis();
					refModel.setGoals(goals.get(isTimeOut ? minWaitTime : maxWaitTime, TimeUnit.MILLISECONDS));
					long endTime = System.currentTimeMillis();
					long nTimeTaken = (endTime - nStartTime) / 1000;
					logger.info("goals: " + nTimeTaken);
				} catch (ExecutionException | InterruptedException | TimeoutException e) {
					if (!partialParsingSupported)
						isTimeOut = true;
					logger.error("Parsing goals section failed", e);
					refModel.addToErrorSections("GOAL");
				} catch (Exception e) {
					logger.error("Parsing goals section failed", e);
					refModel.addToErrorSections("GOAL");
				}
			}

			if (healthConcerns != null) {
				try {
					long nStartTime = System.currentTimeMillis();
					refModel.setHcs(healthConcerns.get(isTimeOut ? minWaitTime : maxWaitTime, TimeUnit.MILLISECONDS));
					long endTime = System.currentTimeMillis();
					long nTimeTaken = (endTime - nStartTime) / 1000;
					logger.info("healthConcerns: " + nTimeTaken);
				} catch (ExecutionException | InterruptedException | TimeoutException e) {
					if (!partialParsingSupported)
						isTimeOut = true;
					logger.error("Parsing Health concerns section failed", e);
					refModel.addToErrorSections("Health concerns");
				} catch (Exception e) {
					logger.error("Parsing Health concerns section failed", e);
					refModel.addToErrorSections("Health concerns");
				}
			}

			if (usrhSubType != null) {
				try {
					long nStartTime = System.currentTimeMillis();
					refModel.setUsrhSubType(usrhSubType.get(isTimeOut ? minWaitTime : maxWaitTime, TimeUnit.MILLISECONDS));
					long endTime = System.currentTimeMillis();
					long nTimeTaken = (endTime - nStartTime) / 1000;
					logger.info("usrhSubType: " + nTimeTaken);
				} catch (ExecutionException | InterruptedException | TimeoutException e) {
					if (!partialParsingSupported)
						isTimeOut = true;
					logger.error("Parsing Usrh sub type section failed", e);
					refModel.addToErrorSections("Usrh sub type");
				} catch (Exception e) {
					logger.error("Parsing Usrh sub type section failed", e);
					refModel.addToErrorSections("Usrh sub type");
				}
			}

			if (familyHistory != null) {
				try {
					long nStartTime = System.currentTimeMillis();
					refModel.setFamilyHistory(familyHistory.get(isTimeOut ? minWaitTime : maxWaitTime, TimeUnit.MILLISECONDS));
					long endTime = System.currentTimeMillis();
					long nTimeTaken = (endTime - nStartTime) / 1000;
					logger.info("familyHistory: " + nTimeTaken);
					if (refModel.getFamilyHistory() != null && refModel.getFamilyHistory().getIdList() != null) {
						idList.addAll(refModel.getFamilyHistory().getIdList());
					}
				} catch (ExecutionException | InterruptedException | TimeoutException e) {
					if (!partialParsingSupported)
						isTimeOut = true;
					logger.error("Parsing Family History section failed", e);
					refModel.addToErrorSections("FAMILY HISTORY");
				} catch (Exception e) {
					logger.error("Parsing Family History section failed", e);
					refModel.addToErrorSections("FAMILY HISTORY");
				}
			}
			if (medicalEquipments != null) {
				try {
					long nStartTime = System.currentTimeMillis();
					refModel.setMedicalEquipment(medicalEquipments.get(isTimeOut ? minWaitTime : maxWaitTime, TimeUnit.MILLISECONDS));
					long endTime = System.currentTimeMillis();
					long nTimeTaken = (endTime - nStartTime) / 1000;
					logger.info("medicalEquipments: " + nTimeTaken);
					if (refModel.getMedicalEquipment() != null && refModel.getMedicalEquipment().getIds() != null) {
						idList.addAll(refModel.getMedicalEquipment().getIds());
					}
				} catch (InterruptedException | ExecutionException | TimeoutException e) {
					if (!partialParsingSupported)
						isTimeOut = true;
					logger.error("Parsing Medical equipment section failed", e);
					refModel.addToErrorSections("MEDICAL EQUIPMENTS");
				} catch (Exception e) {
					logger.error("Parsing Medical equipment section failed", e);
					refModel.addToErrorSections("MEDICAL EQUIPMENTS");
				}

			}

			if (advanceDirective != null) {
				try {
					long nStartTime = System.currentTimeMillis();
					refModel.setAdvanceDirective(advanceDirective.get(isTimeOut ? minWaitTime : maxWaitTime, TimeUnit.MILLISECONDS));
					long endTime = System.currentTimeMillis();
					long nTimeTaken = (endTime - nStartTime) / 1000;
					logger.info("advanceDirective: " + nTimeTaken);
					if (refModel.getAdvanceDirective() != null && refModel.getAdvanceDirective().getIdList() != null) {
						idList.addAll(refModel.getAdvanceDirective().getIdList());
					}
				} catch (InterruptedException | ExecutionException | TimeoutException e) {
					if (!partialParsingSupported)
						isTimeOut = true;
					logger.error("Parsing Advance directive section failed", e);
					refModel.addToErrorSections("ADVANCE DIRECTIVE");
				} catch (Exception e) {
					logger.error("Parsing Advance directive section failed", e);
					refModel.addToErrorSections("ADVANCE DIRECTIVE");
				}
			}

			if (functionalStatus != null) {
				try {
					long nStartTime = System.currentTimeMillis();
					refModel.setFunctionalStatus(functionalStatus.get(isTimeOut ? minWaitTime : maxWaitTime, TimeUnit.MILLISECONDS));
					long endTime = System.currentTimeMillis();
					long nTimeTaken = (endTime - nStartTime) / 1000;
					logger.info("functionalStatus: " + nTimeTaken);
					if (refModel.getFunctionalStatus() != null && refModel.getFunctionalStatus().getIdList() != null) {
						idList.addAll(refModel.getFunctionalStatus().getIdList());
					}
				} catch (InterruptedException | ExecutionException | TimeoutException e) {
					if (!partialParsingSupported)
						isTimeOut = true;
					logger.error("Parsing functional status section failed", e);
					refModel.addToErrorSections("Functional status");
				} catch (Exception e) {
					logger.error("Parsing functional status section failed", e);
					refModel.addToErrorSections("Functional status");
				}
			}

			if (mentalStatus != null) {
				try {
					long nStartTime = System.currentTimeMillis();
					refModel.setMentalStatus(mentalStatus.get(isTimeOut ? minWaitTime : maxWaitTime, TimeUnit.MILLISECONDS));
					long endTime = System.currentTimeMillis();
					long nTimeTaken = (endTime - nStartTime) / 1000;
					logger.info("mentalStatus: " + nTimeTaken);
					if (refModel.getMentalStatus() != null && refModel.getMentalStatus().getIdList() != null) {
						idList.addAll(refModel.getMentalStatus().getIdList());
					}
				} catch (InterruptedException | ExecutionException | TimeoutException e) {
					if (!partialParsingSupported)
						isTimeOut = true;
					logger.error("Parsing mental status section failed", e);
					refModel.addToErrorSections("Mental status");
				} catch (Exception e) {
					logger.error("Parsing mental status section failed", e);
					refModel.addToErrorSections("Mental status");
				}
			}

			if (problems != null) {
				try {
					long nStartTime = System.currentTimeMillis();
					refModel.setProblem(problems.get(isTimeOut ? minWaitTime : maxWaitTime, TimeUnit.MILLISECONDS));
					long endTime = System.currentTimeMillis();
					long nTimeTaken = (endTime - nStartTime) / 1000;
					logger.info("problems: " + nTimeTaken);
					if (refModel.getProblem() != null && refModel.getProblem().getIdList() != null) {
						idList.addAll(refModel.getProblem().getIdList());
					}
				} catch (ExecutionException | InterruptedException | TimeoutException e) {
					if (!partialParsingSupported)
						isTimeOut = true;
					logger.error("Parsing problem section failed", e);
					refModel.addToErrorSections("CONDITION");
				} catch (Exception e) {
					logger.error("Parsing problem section failed", e);
					refModel.addToErrorSections("CONDITION");
				}
			}

			if (medications != null) {
				try {
					long nStartTime = System.currentTimeMillis();
					refModel.setMedication(medications.get(isTimeOut ? minWaitTime : maxWaitTime, TimeUnit.MILLISECONDS));
					long endTime = System.currentTimeMillis();
					long nTimeTaken = (endTime - nStartTime) / 1000;
					logger.info("medications: " + nTimeTaken);
					if (refModel.getMedication() != null && refModel.getMedication().getIdList() != null) {
						idList.addAll(refModel.getMedication().getIdList());
					}
				} catch (ExecutionException | InterruptedException | TimeoutException e) {
					if (!partialParsingSupported)
						isTimeOut = true;
					logger.error("Parsing medications section failed", e);
					refModel.addToErrorSections("MEDICATIONREQUEST");
				} catch (Exception e) {
					logger.error("Parsing medications section failed", e);
					refModel.addToErrorSections("MEDICATIONREQUEST");
				}
			}

			if (procedures != null) {
				try {
					long nStartTime = System.currentTimeMillis();
					refModel.setProcedure(procedures.get(isTimeOut ? minWaitTime : maxWaitTime, TimeUnit.MILLISECONDS));
					long endTime = System.currentTimeMillis();
					long nTimeTaken = (endTime - nStartTime) / 1000;
					logger.info("procedures: " + nTimeTaken);
					if (refModel.getProcedure() != null && refModel.getProcedure().getIdList() != null) {
						idList.addAll(refModel.getProcedure().getIdList());
					}
				} catch (ExecutionException | InterruptedException | TimeoutException e) {
					if (!partialParsingSupported)
						isTimeOut = true;
					logger.error("Parsing procedures section failed", e);
					refModel.addToErrorSections("PROCEDURE");
				} catch (Exception e) {
					logger.error("Parsing procedures section failed", e);
					refModel.addToErrorSections("PROCEDURE");
				}
			}

			if (labResults != null) {
				try {
					long nStartTime = System.currentTimeMillis();
					refModel.setLabResults(labResults.get(isTimeOut ? minWaitTime : maxWaitTime, TimeUnit.MILLISECONDS));
					long endTime = System.currentTimeMillis();
					long nTimeTaken = (endTime - nStartTime) / 1000;
					logger.info("labResults: " + nTimeTaken);
					if (refModel.getLabResults() != null && refModel.getLabResults().getIdList() != null) {
						idList.addAll(refModel.getLabResults().getIdList());
					}
				} catch (ExecutionException | InterruptedException | TimeoutException e) {
					if (!partialParsingSupported)
						isTimeOut = true;
					logger.error("Parsing Lab Results section failed", e);
					refModel.addToErrorSections("LABRESULTS");
				} catch (Exception e) {
					logger.error("Parsing Lab Results section failed", e);
					refModel.addToErrorSections("LABRESULTS");
				}
			}

			long ccdaParseEndTime = System.currentTimeMillis();
			long fullCCDAParseTime = (ccdaParseEndTime - ccdaParseStartTime) / 1000; // time in seconds
			logger.info("fullCCDAParseTime : " + fullCCDAParseTime);

			refModel.setIdList(idList);

			if (!isAnySectionPresent) {
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

	private boolean isSectionEnabled (Set<String> sectionsList, String currentSection){
		if(CollectionUtils.isEmpty(sectionsList)){
			return true;
		}
		return sectionsList.contains(currentSection);
	}

	private Future<ArrayList<CCDANotesActivity>> prepareNotesActivity(String[] templateIds) throws IOException, SAXException, XPathExpressionException, TransformerException {
		ArrayList<CCDANotesActivity> notesActivityListFuture = null;
		Document doc = CustomReadXml.readXML(xml, templateIds);
		if(doc.getDocumentElement() != null && doc.getDocumentElement().getChildNodes().getLength() > 1) {
			isAnySectionPresent = true;
			notesActivityListFuture = notesParser.retrieveNotesActivities(doc);
		}
		doc = null;
		return new AsyncResult<>(notesActivityListFuture);
	}

	private Future<CCDAHealthConcerns> prepareHealthConcern(String[] templateIds) throws IOException, SAXException, XPathExpressionException, TransformerException {
		CCDAHealthConcerns healthConcerns=null;
		Document doc = CustomReadXml.readXML(xml, templateIds);
		if(doc.getDocumentElement() != null && doc.getDocumentElement().getChildNodes().getLength() > 1) {
			isAnySectionPresent = true;
			healthConcerns = healthConcernsProcessor.retrieveHealthConcernDetails(xPath, doc);
		}
		doc = null;
		return new AsyncResult<>(healthConcerns);
	}

	private Future<CCDAGoals> prepareGoals(String[] templateIds) throws IOException, SAXException, XPathExpressionException, TransformerException {
		CCDAGoals goals=null;
		Document doc = CustomReadXml.readXML(xml, templateIds);
		if(doc.getDocumentElement() != null && doc.getDocumentElement().getChildNodes().getLength() > 1) {
			isAnySectionPresent = true;
			goals = goalsProcessor.retrieveGoalsDetails(xPath, doc);
		}
		doc = null;
		return new AsyncResult<>(goals);
	}

	private Future<CCDAHeaderElements> prepareHeaderElements() throws IOException, SAXException, XPathExpressionException {
		CCDAHeaderElements headerElementsFuture = null;
		Document doc = CustomReadXml.readXML(xml, new String[]{EMPTY, EMPTY});
		if(doc.getDocumentElement() != null && doc.getDocumentElement().getChildNodes().getLength() > 1) {
			isAnySectionPresent = true;
			headerElementsFuture = ccdaHeaderParser.getHeaderElements(doc, false);
		}
		doc = null;
		return new AsyncResult<>(headerElementsFuture);
	}

	private Future<CCDALabResult> prepareLabTests(String[] templateIds) throws IOException, SAXException, XPathExpressionException, TransformerException {
		CCDALabResult labTests = null;
		Document doc = CustomReadXml.readXML(xml, templateIds);
		if(doc.getDocumentElement() != null && doc.getDocumentElement().getChildNodes().getLength() > 1) {
			isAnySectionPresent = true;
			labTests = laboratoryTestProcessor.retrieveLabTests(xPath, doc);
		}
		doc = null;
		return new AsyncResult<>(labTests);
	}

	@Async()
	Future<CCDAMentalStatus> prepareMentalStatus(String[] templateIds) throws IOException, SAXException, XPathExpressionException, TransformerException {
		CCDAMentalStatus mentalStatus = null;
		Document doc = CustomReadXml.readXML(xml, templateIds);
		if(doc.getDocumentElement() != null && doc.getDocumentElement().getChildNodes().getLength() > 1) {
			isAnySectionPresent = true;
			mentalStatus = mentalStatusProcessor.retrieveMentalStatusDetails(xPath, doc);
		}
		doc = null;
		return new AsyncResult<>(mentalStatus);
	}

	@Async()
	Future<CCDAFunctionalStatus> prepareFunctionalStatus(String[] templateIds) throws IOException, SAXException, XPathExpressionException, TransformerException {
		CCDAFunctionalStatus functionalStatus = null;
		Document doc = CustomReadXml.readXML(xml, templateIds);
		if(doc.getDocumentElement() != null && doc.getDocumentElement().getChildNodes().getLength() > 1) {
			isAnySectionPresent = true;
			functionalStatus = functionalStatusProcessor.retrieveFunctionalStatusDetails(xPath, doc);
		}
		doc = null;
		return new AsyncResult<>(functionalStatus);
	}

	@Async()
	Future<ArrayList<CCDANotes>> prepareNotes(String[] templateIds) throws IOException, SAXException, XPathExpressionException, TransformerException {
		ArrayList<CCDANotes> noteDetailListFuture = null;
		Document doc = CustomReadXml.readXML(xml, templateIds);
		if(doc.getDocumentElement() != null && doc.getDocumentElement().getChildNodes().getLength() > 1) {
			isAnySectionPresent = true;
			noteDetailListFuture = notesParser.retrieveNotesDetails(doc);
		}
		doc = null;
		return new AsyncResult<>(noteDetailListFuture);
	}

	@Async()
	Future<CCDACarePlanSections> prepareCarePlan(String[] templateIds) throws IOException, SAXException, XPathExpressionException, TransformerException {
		CCDACarePlanSections carePlanSectionsFuture = null;
		Document doc = CustomReadXml.readXML(xml, templateIds);
		if(doc.getDocumentElement() != null && doc.getDocumentElement().getChildNodes().getLength() > 1) {
			isAnySectionPresent = true;
			carePlanSectionsFuture = carePlanSectionsParser.getSuggestedSections(doc);
		}
		doc = null;
		return new AsyncResult<>(carePlanSectionsFuture);
	}

	@Async()
	Future<CCDAAdvanceDirective> prepareAdvanceDirective(String[] templateIds) throws IOException, SAXException, XPathExpressionException, TransformerException {
		CCDAAdvanceDirective advanceDirective = null;
		Document doc = CustomReadXml.readXML(xml, templateIds);
		if(doc.getDocumentElement() != null && doc.getDocumentElement().getChildNodes().getLength() > 1) {
			isAnySectionPresent = true;
			advanceDirective = advanceDirectiveProcesser.retrieveAdvanceDirectiveDetails(xPath, doc);
		}
		doc = null;
		return new AsyncResult<>(advanceDirective);
	}

	@Async()
	Future<CCDAMedicalEquipment> prepareMedicalEquipments(String[] templateIds) throws IOException, SAXException, XPathExpressionException, TransformerException {
		CCDAMedicalEquipment medicalEquipments = null;
		Document doc = CustomReadXml.readXML(xml, templateIds);
		if(doc.getDocumentElement() != null && doc.getDocumentElement().getChildNodes().getLength() > 1) {
			isAnySectionPresent = true;
			medicalEquipments = medicalEquipmentProcessor.retrieveMedicalEquipment(xPath, doc);
		}
		doc = null;
		return new AsyncResult<>(medicalEquipments);
	}

	@Async()
	Future<CCDAFamilyHistory> prepareFamilyHistory(String[] templateIds) throws IOException, SAXException, XPathExpressionException, TransformerException {
		CCDAFamilyHistory familyHistory = null;
		Document doc = CustomReadXml.readXML(xml, templateIds);
		if(doc.getDocumentElement() != null && doc.getDocumentElement().getChildNodes().getLength() > 1) {
			isAnySectionPresent = true;
			familyHistory = familyHistoryProcessor.retrieveFamilyHistoryDetails(xPath, doc);
		}
		doc = null;
		return new AsyncResult<>(familyHistory);
	}

	@Async()
	Future<CCDAPOT> preparePOT(String[] templateIds) throws IOException, SAXException, XPathExpressionException, TransformerException {
		CCDAPOT pot = null;
		Document doc = CustomReadXml.readXML(xml, templateIds);
		if(doc.getDocumentElement() != null && doc.getDocumentElement().getChildNodes().getLength() > 1) {
			isAnySectionPresent = true;
			pot = pOTProcessor.retrievePOTDetails(xPath, doc);
		}
		doc = null;
		return new AsyncResult<>(pot);
	}

	@Async()
	Future<CCDAImmunization> prepareImmunizations(String[] templateIds) throws IOException, SAXException, XPathExpressionException, TransformerException {
		CCDAImmunization immunizations = null;
		Document doc = CustomReadXml.readXML(xml, templateIds);
		if(doc.getDocumentElement() != null && doc.getDocumentElement().getChildNodes().getLength() > 1) {
			isAnySectionPresent = true;
			immunizations = immunizationProcessor.retrieveImmunizationDetails(xPath, doc);
		}
		doc = null;
		return new AsyncResult<>(immunizations);
	}

	@Async()
	Future<CCDACareTeamMember> prepareCareTeamMembers(String[] templateIds) throws IOException, SAXException, XPathExpressionException, TransformerException {
		CCDACareTeamMember careTeamMembers = null;
		Document doc = CustomReadXml.readXML(xml, templateIds);
		if(doc.getDocumentElement() != null && doc.getDocumentElement().getChildNodes().getLength() > 1) {
			isAnySectionPresent = true;
			careTeamMembers = careTeamMemberProcessor.retrieveCTMDetails(xPath, doc);
		}
		doc = null;
		return new AsyncResult<>(careTeamMembers);
	}

	@Async()
	Future<CCDAProcedure> prepareProcedures(String[] templateIds) throws IOException, SAXException, XPathExpressionException, TransformerException {
		CCDAProcedure procedures = null;
		Document doc = CustomReadXml.readXML(xml, templateIds);
		if(doc.getDocumentElement() != null && doc.getDocumentElement().getChildNodes().getLength() > 1) {
			isAnySectionPresent = true;
			procedures = procedureProcessor.retrievePrcedureDetails(xPath, doc);
		}
		doc = null;
		return new AsyncResult<>(procedures);
	}

	@Async()
	Future<CCDAVitalSigns> prepareVitals(String[] templateIds) throws IOException, SAXException, XPathExpressionException, TransformerException {
		CCDAVitalSigns vitals = null;
		Document doc = CustomReadXml.readXML(xml, templateIds);
		if(doc.getDocumentElement() != null && doc.getDocumentElement().getChildNodes().getLength() > 1) {
			isAnySectionPresent = true;
			vitals = vitalSignProcessor.retrieveVitalSigns(xPath, doc);
		}
		doc = null;
		return new AsyncResult<>(vitals);
	}

	@Async()
	Future<CCDASocialHistory> prepareSmokingStatus(String[] templateIds) throws IOException, SAXException, XPathExpressionException, TransformerException {
		CCDASocialHistory smokingStatus = null;
		Document doc = CustomReadXml.readXML(xml, templateIds);
		if(doc.getDocumentElement() != null && doc.getDocumentElement().getChildNodes().getLength() > 1) {
			isAnySectionPresent = true;
			smokingStatus = smokingStatusProcessor.retrieveSmokingStatusDetails(xPath, doc);
		}
		doc = null;
		return new AsyncResult<>(smokingStatus);
	}

	@Async()
	Future<CCDAAllergy> prepareAllergies(String[] templateIds) throws IOException, SAXException, XPathExpressionException, TransformerException {
		CCDAAllergy allergies = null;
		Document doc = CustomReadXml.readXML(xml, templateIds);
		if(doc.getDocumentElement() != null && doc.getDocumentElement().getChildNodes().getLength() > 1) {
			isAnySectionPresent = true;
			allergies = mediactionAllergiesProcessor.retrieveAllergiesDetails(xPath, doc);
		}
		doc = null;
		return new AsyncResult<>(allergies);
	}

	@Async()
	Future<CCDAProblem> prepareProblems(String[] templateIds) throws IOException, SAXException, XPathExpressionException, TransformerException {
		CCDAProblem problems = null;
		Document doc = CustomReadXml.readXML(xml, templateIds);
		if(doc.getDocumentElement() != null && doc.getDocumentElement().getChildNodes().getLength() > 1) {
			isAnySectionPresent = true;
			problems = problemProcessor.retrieveProblemDetails(xPath, doc);
		}
		doc = null;
		return new AsyncResult<>(problems);
	}

	@Async()
	Future<CCDAEncounter> prepareEncounters(String[] templateIds) throws IOException, SAXException, XPathExpressionException, TransformerException {
		CCDAEncounter encounters = null;
		Document doc = CustomReadXml.readXML(xml, templateIds);
		if(doc.getDocumentElement() != null && doc.getDocumentElement().getChildNodes().getLength() > 1) {
			isAnySectionPresent = true;
			encounters = encounterDiagnosesProcessor.retrieveEncounterDetails(xPath, doc);
		}
		doc = null;
		return new AsyncResult<>(encounters);
	}

	@Async()
	Future<CCDAPatient> preparePatient(CCDARefModel refModel) throws IOException, SAXException, XPathExpressionException, TransformerException {
		CCDAPatient patient = null;
		Document doc = CustomReadXml.readXML(xml, new String[]{EMPTY, EMPTY});
		if(doc.getDocumentElement() != null && doc.getDocumentElement().getChildNodes().getLength() > 1) {
			isAnySectionPresent = true;
			refModel.setDocTemplateId(patientProcessor.retrieveDocTemplateId(xPath, doc));
			refModel.setEncompassingEncounter(patientProcessor.retrieveEncompassingEncounter(xPath, doc));
			patient = patientProcessor.retrievePatientDetails(xPath, doc);
		}
		doc = null;
		return new AsyncResult<>(patient);
	}

	@Async()
	Future<CCDALabResult> prepareLabResults(String[] templateIds) throws XPathExpressionException, TransformerException, IOException, SAXException {
		CCDALabResult labResults = null;
		Document doc = CustomReadXml.readXML(xml, templateIds);
		if(doc.getDocumentElement() != null && doc.getDocumentElement().getChildNodes().getLength() > 1) {
			isAnySectionPresent = true;
			labResults = laboratoryResultsProcessor.retrieveLabResults(xPath, doc);
		}
		doc = null;
		return new AsyncResult<>(labResults);
	}

	@Async()
	Future<CCDAMedication> prepareMedications(String[] templateIds) throws XPathExpressionException, TransformerException, IOException, SAXException {
		CCDAMedication medications = null;
		Document doc = CustomReadXml.readXML(xml, templateIds);
		if(doc.getDocumentElement() != null && doc.getDocumentElement().getChildNodes().getLength() > 1) {
			isAnySectionPresent = true;
			medications = medicationProcessor.retrieveMedicationDetails(xPath, doc);
		}
		doc = null;
		return new AsyncResult<>(medications);
	}
}
