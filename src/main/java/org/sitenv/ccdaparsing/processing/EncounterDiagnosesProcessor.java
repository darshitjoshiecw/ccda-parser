package org.sitenv.ccdaparsing.processing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sitenv.ccdaparsing.model.CCDAAdmissionDiagnosis;
import org.sitenv.ccdaparsing.model.CCDADischargeDiagnosis;
import org.sitenv.ccdaparsing.model.CCDAEncounter;
import org.sitenv.ccdaparsing.model.CCDAEncounterActivity;
import org.sitenv.ccdaparsing.model.CCDAEncounterDiagnosis;
import org.sitenv.ccdaparsing.model.CCDAID;
import org.sitenv.ccdaparsing.model.CCDAProblemObs;
import org.sitenv.ccdaparsing.model.CCDAServiceDeliveryLoc;
import org.sitenv.ccdaparsing.util.ApplicationConstants;
import org.sitenv.ccdaparsing.util.ApplicationUtil;
import org.sitenv.ccdaparsing.util.ParserUtilities;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

@Service
public class EncounterDiagnosesProcessor {
	
	private final Logger logger = LogManager.getLogger(EncounterDiagnosesProcessor.class);
	
	@Async()
	public Future<CCDAEncounter> retrieveEncounterDetails(XPath xPath , Document doc) throws XPathExpressionException,TransformerException
	{
		long startTime = System.currentTimeMillis();
    	logger.info("encounter parsing Start time:"+ startTime);

		Element sectionElement = ApplicationUtil.getCloneNode((Element) xPath.compile(ApplicationConstants.ENCOUNTER_EXPRESSION).evaluate(doc, XPathConstants.NODE));
		CCDAEncounter encounters = null;
		List<CCDAID> idLIst = new ArrayList<>();
		if(sectionElement != null)
		{
			encounters = new CCDAEncounter();
			if(ApplicationUtil.checkForNullFlavourNI(sectionElement))
			{
				encounters.setSectionNullFlavourWithNI(true);
				return new AsyncResult<CCDAEncounter>(encounters);
			}
			encounters.setTemplateId(ApplicationUtil.readTemplateIdList((NodeList) xPath.compile("./templateId[not(@nullFlavor)]").
					evaluate(sectionElement, XPathConstants.NODESET)));
			encounters.setSectionCode(ApplicationUtil.readCode((Element) xPath.compile("./code[not(@nullFlavor)]").
					evaluate(sectionElement, XPathConstants.NODE)));
			encounters.setEncActivities(readEncounterActivity((NodeList) xPath.compile("./entry/encounter[not(@nullFlavor)]").
					evaluate(sectionElement, XPathConstants.NODESET), xPath,idLIst));
			encounters.setAuthor(ParserUtilities.readAuthor((Element) CCDAConstants.REL_AUTHOR_EXP.
					evaluate(sectionElement, XPathConstants.NODE)));
			encounters.setNotesActivity(ParserUtilities.readNotesActivity((NodeList) CCDAConstants.REL_NOTES_ACTIVITY_EXPRESSION.
					evaluate(sectionElement, XPathConstants.NODESET), null));
			sectionElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			encounters.setLineNumber(sectionElement.getUserData("lineNumber") + " - " + sectionElement.getUserData("endLineNumber") );
			encounters.setXmlString(ApplicationUtil.nodeToString((Node)sectionElement));
			
			Element textElement = (Element) xPath.compile("./text[not(@nullFlavor)]").evaluate(sectionElement, XPathConstants.NODE);
			
			if(textElement!=null)
			{
			
				encounters.getReferenceLinks().addAll((ApplicationUtil.readSectionTextReferences((NodeList) xPath.compile(".//*[not(@nullFlavor) and @ID]").
					evaluate(textElement, XPathConstants.NODESET))));
			}
			encounters.setIdLIst(idLIst);
		}
		logger.info("encounter parsing End time:"+ (System.currentTimeMillis() - startTime));
		return new AsyncResult<CCDAEncounter>(encounters);
	}
	
	
	public ArrayList<CCDAEncounterActivity> readEncounterActivity(NodeList encounterActivityNodeList , XPath xPath,List<CCDAID> idList) throws XPathExpressionException,TransformerException
	{
		ArrayList<CCDAEncounterActivity> encounterActivityList = new ArrayList<>();
		CCDAEncounterActivity encounterActivity;
		for (int i = 0; i < encounterActivityNodeList.getLength(); i++) {

			Element encounterActivityElement = ApplicationUtil.getCloneNode((Element) encounterActivityNodeList.item(i));
			
			if(encounterActivityElement != null)
			{
				encounterActivity = new CCDAEncounterActivity();
				encounterActivity.setTemplateId(ApplicationUtil.readTemplateIdList((NodeList) xPath.compile("./templateId[not(@nullFlavor)]").
						evaluate(encounterActivityElement, XPathConstants.NODESET)));
				
				encounterActivity.setEncounterTypeCode(ApplicationUtil.readCode((Element) xPath.compile("./code[not(@nullFlavor)]").
										evaluate(encounterActivityElement, XPathConstants.NODE)));
				
				if(ApplicationUtil.readID((Element) xPath.compile("./id[not(@nullFlavor)]").
								evaluate(encounterActivityElement, XPathConstants.NODE),"encounterActivity") != null)
				{
				
					idList.add(ApplicationUtil.readID((Element) xPath.compile("./id[not(@nullFlavor)]").
								evaluate(encounterActivityElement, XPathConstants.NODE),"encounterActivity"));
				}
				
				encounterActivityElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
				encounterActivity.setLineNumber(encounterActivityElement.getUserData("lineNumber") + " - " + encounterActivityElement.getUserData("endLineNumber") );
				encounterActivity.setXmlString(ApplicationUtil.nodeToString((Node)encounterActivityElement));
				
				
				encounterActivity.setReferenceText(ApplicationUtil.readTextReference((Element) xPath.compile(ApplicationConstants.REFERENCE_TEXT_EXPRESSION).
															evaluate(encounterActivityElement, XPathConstants.NODE)));
				
				encounterActivity.setEffectiveTime(ApplicationUtil.readEffectivetime((Element) xPath.compile("./effectiveTime[not(@nullFlavor)]").
															evaluate(encounterActivityElement, XPathConstants.NODE),xPath));
				
				encounterActivity.setSdLocs(readServiceDeliveryLocators((NodeList) xPath.compile("./participant/participantRole[not(@nullFlavor)]").
																evaluate(encounterActivityElement, XPathConstants.NODESET), xPath, idList));
				
				NodeList encounterDiagnosisNodeList = (NodeList) xPath.compile("./entryRelationship/act[not(@nullFlavor)]").
								evaluate(encounterActivityElement, XPathConstants.NODESET);
				
				encounterActivity.setDiagnoses(readEncounterDiagnosis(encounterDiagnosisNodeList,xPath,idList));
				
				NodeList indicationNodeList = (NodeList) xPath.compile("./entryRelationship/observation[not(@nullFlavor)]").
								evaluate(encounterActivityElement, XPathConstants.NODESET);
				
				encounterActivity.setIndications(readProblemObservation(indicationNodeList, xPath,idList));

				encounterActivity.setNotesActivity(ParserUtilities.readNotesActivity((NodeList) CCDAConstants.REL_ENTRY_REL_NOTES_ACTIVITY_EXPRESSION
								.evaluate(encounterActivityElement, XPathConstants.NODESET), null));

				encounterActivity.setAuthor(ParserUtilities.readAuthor((Element) CCDAConstants.REL_AUTHOR_EXP.
						evaluate(encounterActivityElement, XPathConstants.NODE)));

				encounterActivityList.add(encounterActivity);
			}
		}
		return encounterActivityList;
	}
	
	public ArrayList<CCDAEncounterDiagnosis> readEncounterDiagnosis(NodeList encounterDiagnosisNodeList, XPath xPath,List<CCDAID> idList) throws XPathExpressionException,TransformerException
	{
		ArrayList<CCDAEncounterDiagnosis> encounterDiagnosisList = null;
		if(encounterDiagnosisNodeList.getLength() > 0)
		{
			encounterDiagnosisList = new ArrayList<>();
		}
		CCDAEncounterDiagnosis encounterDiagnosis;
		for (int i = 0; i < encounterDiagnosisNodeList.getLength(); i++) {

			Element encounterDiagnosisElement = ApplicationUtil.getCloneNode((Element) encounterDiagnosisNodeList.item(i));
			encounterDiagnosis = new CCDAEncounterDiagnosis();
			encounterDiagnosisElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			encounterDiagnosis.setLineNumber(encounterDiagnosisElement.getUserData("lineNumber") + " - " + encounterDiagnosisElement.getUserData("endLineNumber") );
			encounterDiagnosis.setXmlString(ApplicationUtil.nodeToString((Node)encounterDiagnosisElement));
			encounterDiagnosis.setTemplateId(ApplicationUtil.readTemplateIdList((NodeList) xPath.compile("./templateId[not(@nullFlavor)]").
					evaluate(encounterDiagnosisElement, XPathConstants.NODESET)));
			
			encounterDiagnosis.setReferenceText(ApplicationUtil.readTextReference((Element) xPath.compile(ApplicationConstants.REFERENCE_TEXT_EXPRESSION).
					evaluate(encounterDiagnosisElement, XPathConstants.NODE)));

			
			if(ApplicationUtil.readID((Element) xPath.compile("./id[not(@nullFlavor)]").
					evaluate(encounterDiagnosisElement, XPathConstants.NODE),"encounterDiagnosis")!= null)
			{
			
				idList.add(ApplicationUtil.readID((Element) xPath.compile("./id[not(@nullFlavor)]").
					evaluate(encounterDiagnosisElement, XPathConstants.NODE),"encounterDiagnosis"));
			}
			
			encounterDiagnosis.setEntryCode(ApplicationUtil.readCode((Element) xPath.compile("./code[not(@nullFlavor)]").
									evaluate(encounterDiagnosisElement, XPathConstants.NODE)));
			
			NodeList problemObservationNodeList = (NodeList) xPath.compile("./entryRelationship/observation[not(@nullFlavor)]").
										evaluate(encounterDiagnosisElement, XPathConstants.NODESET);
			encounterDiagnosis.setProblemObs(readProblemObservation(problemObservationNodeList,xPath,idList));
			encounterDiagnosis.setAuthor(ParserUtilities.readAuthor((Element) CCDAConstants.REL_AUTHOR_EXP.
					evaluate(encounterDiagnosisElement, XPathConstants.NODE)));

			encounterDiagnosisList.add(encounterDiagnosis);
		}
		
		return encounterDiagnosisList;
	}
	
	public ArrayList<CCDAServiceDeliveryLoc> readServiceDeliveryLocators(NodeList serviceDeliveryLocNodeList, XPath xPath,List<CCDAID> idList) throws XPathExpressionException,TransformerException
	{
		ArrayList<CCDAServiceDeliveryLoc> serviceDeliveryLocsList = null;
		if(serviceDeliveryLocNodeList.getLength() > 0)
		{
			serviceDeliveryLocsList = new ArrayList<>();
		}
		CCDAServiceDeliveryLoc serviceDeliveryLoc;
		for (int i = 0; i < serviceDeliveryLocNodeList.getLength(); i++) {
			
			serviceDeliveryLoc = new CCDAServiceDeliveryLoc();

			Element serviceDeliveryLocElement = ApplicationUtil.getCloneNode((Element) serviceDeliveryLocNodeList.item(i));
			serviceDeliveryLoc.setTemplateId(ApplicationUtil.readTemplateIdList((NodeList) xPath.compile("./templateId[not(@nullFlavor)]").
											evaluate(serviceDeliveryLocElement, XPathConstants.NODESET)));
			
			serviceDeliveryLoc.setLocationCode(ApplicationUtil.readCode((Element) xPath.compile("./code[not(@nullFlavor)]").
											evaluate(serviceDeliveryLocElement, XPathConstants.NODE)));
			
			/*if(ApplicationUtil.readID((Element) xPath.compile("./id[not(@nullFlavor)]").
					evaluate(serviceDeliveryLocElement, XPathConstants.NODE),"encounterServiceDeliveryLocation")!= null)
			{
			
				idList.add(ApplicationUtil.readID((Element) xPath.compile("./id[not(@nullFlavor)]").
					evaluate(serviceDeliveryLocElement, XPathConstants.NODE),"encounterServiceDeliveryLocation"));
			}*/
			
			serviceDeliveryLoc.setName(ApplicationUtil.readTextContent((Element) xPath.compile("./playingEntity/name[not(@nullFlavor)]").
					evaluate(serviceDeliveryLocElement, XPathConstants.NODE)));
			
			serviceDeliveryLoc.setTelecom(ApplicationUtil.readDataElementList((NodeList) xPath.compile("./telecom[not(@nullFlavor)]").
											evaluate(serviceDeliveryLocElement, XPathConstants.NODESET)));
			serviceDeliveryLoc.setAddress(ApplicationUtil.readAddressList((NodeList) xPath.compile("./addr[not(@nullFlavor)]").
											evaluate(serviceDeliveryLocElement, XPathConstants.NODESET), xPath));
			
			serviceDeliveryLocsList.add(serviceDeliveryLoc);
		}
		
		return serviceDeliveryLocsList;
	}

	public ArrayList<CCDAProblemObs> readProblemObservation(NodeList problemObservationNodeList) throws XPathExpressionException, TransformerException {
		ArrayList<CCDAProblemObs> problemObservationList = null;
		if(problemObservationNodeList.getLength() > 0)
		{
			problemObservationList = new ArrayList<>();
		}
		CCDAProblemObs problemObservation;
		for (int i = 0; i < problemObservationNodeList.getLength(); i++) {

			logger.info(" Adding Problem Observation as part of encounter ");
			problemObservation = new CCDAProblemObs();

			Element problemObservationElement = ApplicationUtil.getCloneNode((Element) problemObservationNodeList.item(i));
			problemObservation.setTemplateId(ParserUtilities.readTemplateIdList((NodeList) CCDAConstants.REL_TEMPLATE_ID_EXP.
					evaluate(problemObservationElement, XPathConstants.NODESET)));

			problemObservation.setProblemType(ParserUtilities.readCode((Element) CCDAConstants.REL_CODE_EXP.
					evaluate(problemObservationElement, XPathConstants.NODE)));

			problemObservation.setTranslationProblemType(ParserUtilities.readCodeList((NodeList) CCDAConstants.REL_CODE_TRANS_EXP.
					evaluate(problemObservationElement, XPathConstants.NODESET)));

			problemObservation.setEffTime(ApplicationUtil.readEffectivetime((Element) CCDAConstants.REL_EFF_TIME_EXP.
					evaluate(problemObservationElement, XPathConstants.NODE),CCDAConstants.CCDAXPATH));

			problemObservation.setProblemCode(ParserUtilities.readCodeWithTranslation((Element) CCDAConstants.REL_VAL__WITH_TRANS_EXP.
					evaluate(problemObservationElement, XPathConstants.NODE)));

			problemObservation.setAuthor(ParserUtilities.readAuthor((Element) CCDAConstants.REL_AUTHOR_EXP.
					evaluate(problemObservationElement, XPathConstants.NODE)));

			problemObservationList.add(problemObservation);
		}

		return problemObservationList;
	}

	public ArrayList<CCDAProblemObs> readProblemObservation(NodeList problemObservationNodeList, XPath xPath,List<CCDAID> idList) throws XPathExpressionException,TransformerException
	{
		ArrayList<CCDAProblemObs> problemObservationList = null;
		if(problemObservationNodeList.getLength() > 0)
		{
			problemObservationList = new ArrayList<>();
		}
		CCDAProblemObs problemObservation;
		for (int i = 0; i < problemObservationNodeList.getLength(); i++) {
			
			problemObservation = new CCDAProblemObs();

			Element problemObservationElement = ApplicationUtil.getCloneNode((Element) problemObservationNodeList.item(i));
			problemObservationElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			problemObservation.setLineNumber(problemObservationElement.getUserData("lineNumber") + " - " + problemObservationElement.getUserData("endLineNumber") );
			problemObservation.setXmlString(ApplicationUtil.nodeToString((Node)problemObservationElement));
			problemObservation.setTemplateId(ApplicationUtil.readTemplateIdList((NodeList) xPath.compile("./templateId[not(@nullFlavor)]").
					evaluate(problemObservationElement, XPathConstants.NODESET)));
			
			problemObservation.setProblemType(ApplicationUtil.readCode((Element) xPath.compile("./code[not(@nullFlavor)]").
									evaluate(problemObservationElement, XPathConstants.NODE)));
			
			problemObservation.setReferenceText(ApplicationUtil.readTextReference((Element) xPath.compile(ApplicationConstants.REFERENCE_TEXT_EXPRESSION).
					evaluate(problemObservationElement, XPathConstants.NODE)));
			
			if(ApplicationUtil.readID((Element) xPath.compile("./id[not(@nullFlavor)]").
					evaluate(problemObservationElement, XPathConstants.NODE),"encounterServiceDeliveryLocation")!= null)
			{
				idList.add(ApplicationUtil.readID((Element) xPath.compile("./id[not(@nullFlavor)]").
					evaluate(problemObservationElement, XPathConstants.NODE),"encounterServiceDeliveryLocation"));
			}
			
			problemObservation.setTranslationProblemType(ApplicationUtil.readCodeList((NodeList) xPath.compile("./code/translation[not(@nullFlavor)]").
									evaluate(problemObservationElement, XPathConstants.NODESET)));
			
			problemObservation.setEffTime(ApplicationUtil.readEffectivetime((Element) xPath.compile("./effectiveTime[not(@nullFlavor)]").
										evaluate(problemObservationElement, XPathConstants.NODE), xPath));
			
			problemObservation.setProblemCode(ApplicationUtil.readCode((Element) xPath.compile("./value[not(@nullFlavor)]").
					evaluate(problemObservationElement, XPathConstants.NODE)));
			
			problemObservationList.add(problemObservation);
		}
		
		return problemObservationList;
	}

	@Async()
	public Future<CCDAAdmissionDiagnosis> retrieveAdmissionDiagnosisDetails(Document doc) throws XPathExpressionException, TransformerException {
		Element sectionElement = ApplicationUtil.getCloneNode((Element) CCDAConstants.ADMISSION_DIAG_EXP.evaluate(doc, XPathConstants.NODE));
		CCDAAdmissionDiagnosis admDiag = null;

		if(sectionElement != null)
		{
			logger.info(" Adding Admission Diagnosis ");
			admDiag = new CCDAAdmissionDiagnosis();

			//Get Template Ids
			admDiag.setTemplateId(ParserUtilities.readTemplateIdList((NodeList) CCDAConstants.REL_TEMPLATE_ID_EXP.
					evaluate(sectionElement, XPathConstants.NODESET)));

			// Get Section Code
			admDiag.setSectionCode(ParserUtilities.readCode((Element) CCDAConstants.REL_CODE_EXP.
					evaluate(sectionElement, XPathConstants.NODE)));

			// Get Entries
			admDiag.setDiagnosis(readHospitalAdmissionDiagnosis((NodeList) CCDAConstants.REL_HOSPITAL_ADMISSION_DIAG_EXP.
					evaluate(sectionElement, XPathConstants.NODESET)));

			admDiag.setAuthor(ParserUtilities.readAuthor((Element) CCDAConstants.REL_AUTHOR_EXP.
					evaluate(sectionElement, XPathConstants.NODE)));
		}

		return new AsyncResult<CCDAAdmissionDiagnosis>(admDiag);
	}

	public ArrayList<CCDAProblemObs> readHospitalAdmissionDiagnosis(NodeList hospitalAdmDiag) throws XPathExpressionException, TransformerException {
		ArrayList<CCDAProblemObs> encDiagList = new ArrayList<CCDAProblemObs>();

		for (int i = 0; i < hospitalAdmDiag.getLength(); i++) {

			logger.info("Found Hospital Admission Diagnosis");
			Element hosAdmDiag = ApplicationUtil.getCloneNode((Element) hospitalAdmDiag.item(i));

			NodeList problemObservationNodeList = (NodeList) CCDAConstants.REL_ENTRY_RELSHIP_OBS_EXP.
					evaluate(hosAdmDiag, XPathConstants.NODESET);

			logger.info("Read Problem Observations ");
			encDiagList.addAll(readProblemObservation(problemObservationNodeList));

		}

		logger.info(" Size of Admission Diagnosis Problem Observations : " + encDiagList.size());
		return encDiagList;
	}

	@Async()
	public Future<CCDADischargeDiagnosis> retrieveDischargeDiagnosisDetails(Document doc) throws XPathExpressionException, TransformerException {
		Element sectionElement = ApplicationUtil.getCloneNode((Element) CCDAConstants.DISCHARGE_DIAG_EXP.evaluate(doc, XPathConstants.NODE));
		CCDADischargeDiagnosis dischargeDiag = null;

		if(sectionElement != null)
		{
			logger.info(" Adding Discharge Diagnosis ");
			dischargeDiag = new CCDADischargeDiagnosis();

			//Get Template Ids
			dischargeDiag.setTemplateId(ParserUtilities.readTemplateIdList((NodeList) CCDAConstants.REL_TEMPLATE_ID_EXP.
					evaluate(sectionElement, XPathConstants.NODESET)));

			// Get Section Code
			dischargeDiag.setSectionCode(ParserUtilities.readCode((Element) CCDAConstants.REL_CODE_EXP.
					evaluate(sectionElement, XPathConstants.NODE)));

			// Get Entries
			dischargeDiag.setDiagnosis(readHospitalDischargeDiagnosis((NodeList) CCDAConstants.REL_HOSPITAL_DISCHARGE_DIAG_EXP.
					evaluate(sectionElement, XPathConstants.NODESET)));

			dischargeDiag.setAuthor(ParserUtilities.readAuthor((Element) CCDAConstants.REL_AUTHOR_EXP.
					evaluate(sectionElement, XPathConstants.NODE)));
		}

		return new AsyncResult<CCDADischargeDiagnosis>(dischargeDiag);
	}

	public ArrayList<CCDAProblemObs> readHospitalDischargeDiagnosis(NodeList hospitalDischargeDiag) throws XPathExpressionException, TransformerException {
		ArrayList<CCDAProblemObs> encDiagList = new ArrayList<CCDAProblemObs>();

		for (int i = 0; i < hospitalDischargeDiag.getLength(); i++) {

			logger.info("Found Hospital Discharge Diagnosis");
			Element hosAdmDiag = ApplicationUtil.getCloneNode((Element) hospitalDischargeDiag.item(i));

			NodeList problemObservationNodeList = (NodeList) CCDAConstants.REL_ENTRY_RELSHIP_OBS_EXP.
					evaluate(hosAdmDiag, XPathConstants.NODESET);

			logger.info("Read Problem Observations ");
			encDiagList.addAll(readProblemObservation(problemObservationNodeList));

		}

		logger.info(" Size of Discharge Diagnosis Problem Observations : " + encDiagList.size());
		return encDiagList;
	}
}
