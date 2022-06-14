package org.sitenv.ccdaparsing.processing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sitenv.ccdaparsing.model.CCDACareTeamMember;
import org.sitenv.ccdaparsing.model.CCDACareTeamMemberAct;
import org.sitenv.ccdaparsing.model.CCDACode;
import org.sitenv.ccdaparsing.model.CCDADataElement;
import org.sitenv.ccdaparsing.model.CCDAParticipant;
import org.sitenv.ccdaparsing.util.ApplicationConstants;
import org.sitenv.ccdaparsing.util.ApplicationUtil;
import org.sitenv.ccdaparsing.util.ParserUtilities;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.concurrent.Future;

@Service
public class CareTeamMemberProcessor {

	private static final Logger logger = LogManager.getLogger(CareTeamMemberProcessor.class);

	@Async()
	public Future<CCDACareTeamMember> retrieveCTMDetails(XPath xPath , Document doc) throws XPathExpressionException,TransformerException
	{
		long startTime = System.currentTimeMillis();
    	logger.info("care team members parsing Start time:"+ startTime);
    	
		NodeList performerNodeList = (NodeList) xPath.compile(ApplicationConstants.CTM_EXPRESSION).evaluate(doc, XPathConstants.NODESET);
		CCDACareTeamMember careTeamMember = new CCDACareTeamMember();
		ArrayList<CCDAParticipant> participantList = new ArrayList<>();
		CCDAParticipant participant ;
		for (int i = 0; i < performerNodeList.getLength(); i++) {
			
			participant = new CCDAParticipant();
			Element performerElement = (Element) performerNodeList.item(i);
			
			participant.setAddress(ApplicationUtil.readAddress((Element) xPath.compile("./assignedEntity/addr[not(@nullFlavor)]").
					evaluate(performerElement, XPathConstants.NODE), xPath));
			
			readName((Element) xPath.compile("./assignedEntity/assignedPerson/name[not(@nullFlavor)]").
	    				evaluate(performerElement, XPathConstants.NODE), participant , xPath);
			
			participant.setTelecom(ApplicationUtil.readDataElement((Element) xPath.compile("./assignedEntity/telecom[not(@nullFlavor)]").
					evaluate(performerElement, XPathConstants.NODE)));
			participantList.add(participant);
		}
		careTeamMember.setMembers(participantList);
		CCDACareTeamMember ctMember = retrieveCareTeamSectionDetails(doc, careTeamMember);
		logger.info("care team members parsing End time:"+ (System.currentTimeMillis() - startTime));
		
		return new AsyncResult<CCDACareTeamMember>(ctMember);
	}

	public CCDACareTeamMember retrieveCareTeamSectionDetails(Document doc, CCDACareTeamMember careTeamMember) throws XPathExpressionException, TransformerException 
	{
		Element sectionElement = (Element) CCDAConstants.CARE_TEAM_SECTION_EXPRESSION.evaluate(doc, XPathConstants.NODE);

		if(sectionElement != null)
		{
			logger.info(" Found Care Team Member section ");
			careTeamMember.setSectionTemplateId(ParserUtilities.readTemplateIdList((NodeList) CCDAConstants.REL_TEMPLATE_ID_EXP.
					evaluate(sectionElement, XPathConstants.NODESET)));

			careTeamMember.setSectionCode(ParserUtilities.readCode((Element) CCDAConstants.REL_CODE_EXP.
					evaluate(sectionElement, XPathConstants.NODE)));

			// Add Member Acts
			careTeamMember.setMemberActs(readMemberActs((NodeList) CCDAConstants.REL_CARE_TEAM_ORG_EXPRESSION.
					evaluate(sectionElement, XPathConstants.NODESET)));

			readDataFromOrganization((NodeList) CCDAConstants.REL_CARE_TEAM_ORG_EXPRESSION.
					evaluate(sectionElement, XPathConstants.NODESET), careTeamMember);

			careTeamMember.setCareTeamName(readCareTeamName(careTeamMember.getReferenceText(), sectionElement));

			careTeamMember.setAuthor(ParserUtilities.readAuthor((Element) CCDAConstants.REL_AUTHOR_EXP.
					evaluate(sectionElement, XPathConstants.NODE)));
		}

		return careTeamMember;
	}

	public ArrayList<CCDACareTeamMemberAct> readMemberActs(NodeList orgList) throws XPathExpressionException, TransformerException 
	{
		ArrayList<CCDACareTeamMemberAct> careTeamMembers = new ArrayList<>();

		if(orgList != null) {

			for (int i = 0; i < orgList.getLength(); i++) {

				logger.info("Found Organizer ");

				Element orgElement = (Element) orgList.item(i);

				// Parse the Members
				retrieveMembers((NodeList) CCDAConstants.REL_CARE_TEAM_MEMBER_ACT_EXPRESSION.
						evaluate(orgElement, XPathConstants.NODESET), careTeamMembers);

			}
		}
		return careTeamMembers;
	}

	public void readDataFromOrganization(NodeList orgList, CCDACareTeamMember careTeamMember) throws XPathExpressionException, TransformerException {
		if (orgList != null && orgList.getLength() > 0) {
			logger.info("Found Organizer ");
			Element orgElement = (Element) orgList.item(0);

			// Parse the Members status
			careTeamMember.setStatusCode(ParserUtilities.readCode((Element) CCDAConstants.REL_STATUS_CODE_EXP.
					evaluate(orgElement, XPathConstants.NODE)));

			careTeamMember.setCareTeamEffectiveTime(ApplicationUtil.readEffectivetime((Element) CCDAConstants.REL_EFF_TIME_EXP.
					evaluate(orgElement, XPathConstants.NODE), CCDAConstants.CCDAXPATH));

			careTeamMember.setReferenceText(ParserUtilities.readDataElement((Element) CCDAConstants.REL_CARE_TEAM_ORG_REF.
					evaluate(orgElement, XPathConstants.NODE)));

		}
	}

	public void retrieveMembers(NodeList memberActNodes, ArrayList<CCDACareTeamMemberAct> careTeamMembers) throws XPathExpressionException, TransformerException {
		if (memberActNodes != null) {
			for (int i = 0; i < memberActNodes.getLength(); i++) {

				logger.info(" Found Member Act Node ");
				CCDACareTeamMemberAct mact = new CCDACareTeamMemberAct();

				Element memberActElement = (Element) memberActNodes.item(i);

				mact.setTemplateIds(ParserUtilities.readTemplateIdList((NodeList) CCDAConstants.REL_TEMPLATE_ID_EXP.
						evaluate(memberActElement, XPathConstants.NODESET)));

				mact.setMemberActCode(ParserUtilities.readCode((Element) CCDAConstants.REL_CODE_EXP.
						evaluate(memberActElement, XPathConstants.NODE)));

				mact.setStatusCode(ParserUtilities.readCode((Element) CCDAConstants.REL_STATUS_CODE_EXP.
						evaluate(memberActElement, XPathConstants.NODE)));

				mact.setEffectiveTime(ApplicationUtil.readEffectivetime((Element) CCDAConstants.REL_EFF_TIME_EXP.
						evaluate(memberActElement, XPathConstants.NODE), CCDAConstants.CCDAXPATH));

				mact.setAuthor(ParserUtilities.readAuthor((Element) CCDAConstants.REL_AUTHOR_EXP.
						evaluate(memberActElement, XPathConstants.NODE)));

				Element performerElement = (Element) CCDAConstants.REL_PERFORMER_EXP.evaluate(memberActElement,XPathConstants.NODE);

				if(performerElement != null)
				{
					logger.info(" Found Perfomer ");
					CCDAParticipant pp = new CCDAParticipant();
					readName((Element) CCDAConstants.REL_ASSN_ENTITY_PERSON_NAME.
							evaluate(performerElement, XPathConstants.NODE), pp, XPathFactory.newInstance().newXPath());

					pp.setParticipantRole(ApplicationUtil.readCode((Element) CCDAConstants.REL_FUNCTION_CODE_EXP.evaluate(performerElement, XPathConstants.NODE)));

					mact.setPrimaryPerformer(pp);
				}

				NodeList participantNodeList = (NodeList) CCDAConstants.REL_PARTICIPANT_EXP.evaluate(memberActElement,XPathConstants.NODESET);

				if(participantNodeList != null)
				{
					for(int j = 0; j < participantNodeList.getLength(); j++)
					{
						logger.info(" Found Participants ");
						CCDAParticipant opp = new CCDAParticipant();
						readName((Element) CCDAConstants.REL_ASSN_ENTITY_PERSON_NAME.
								evaluate(participantNodeList.item(j), XPathConstants.NODE), opp, XPathFactory.newInstance().newXPath());

						mact.addParticipant(opp);
					}
				}

				careTeamMembers.add(mact);
			}
		}

	}
	
	private void readName(Element nameElement,CCDAParticipant participant,XPath xPath) throws XPathExpressionException
	{
		if(nameElement != null)
		{
			NodeList giveNameNodeList = (NodeList) xPath.compile("./given[not(@nullFlavor)]").
					evaluate(nameElement, XPathConstants.NODESET);
			for (int i = 0; i < giveNameNodeList.getLength(); i++) {
				Element givenNameElement = (Element) giveNameNodeList.item(i);
				if(!ApplicationUtil.isEmpty(givenNameElement.getAttribute("qualifier")))
				{
					participant.setPreviousName(ApplicationUtil.readTextContent(givenNameElement));
				}else if (i == 0) {
					participant.setFirstName(ApplicationUtil.readTextContent(givenNameElement));
				}else {
					participant.setMiddleName(ApplicationUtil.readTextContent(givenNameElement));
				}
			}
			
			participant.setLastName(ApplicationUtil.readTextContent((Element) xPath.compile("./family[not(@nullFlavor)]").
					evaluate(nameElement, XPathConstants.NODE)));
			participant.setSuffix(ApplicationUtil.readTextContent((Element) xPath.compile("./suffix[not(@nullFlavor)]").
					evaluate(nameElement, XPathConstants.NODE)));
		}
	}

	public String readCareTeamName(CCDADataElement referenceTextElement, Element sectionElement)  throws XPathExpressionException, TransformerException{
		try {
			if(referenceTextElement != null && referenceTextElement.getValue() != null) {
				String xml = (ApplicationUtil.nodeToString(sectionElement));
				InputSource is = new InputSource(new StringReader(xml));
				Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
				String id = referenceTextElement.getValue().replace("#", "");
				return ApplicationUtil.getTextByElementId(id, document);
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			logger.error("Parsing Care team section failed", e);
		}
		return null;
	}
}