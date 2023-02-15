package org.sitenv.ccdaparsing.processing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sitenv.ccdaparsing.model.CCDAID;
import org.sitenv.ccdaparsing.model.CCDAMedicalEquipment;
import org.sitenv.ccdaparsing.model.CCDAMedicalEquipmentOrg;
import org.sitenv.ccdaparsing.model.CCDANonMedicalSupplyAct;
import org.sitenv.ccdaparsing.model.CCDAUDI;
import org.sitenv.ccdaparsing.util.ApplicationConstants;
import org.sitenv.ccdaparsing.util.ApplicationUtil;
import org.sitenv.ccdaparsing.util.ParserUtilities;
import org.springframework.beans.factory.annotation.Autowired;
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
public class MedicalEquipmentProcessor {

    private static final Logger logger = LogManager.getLogger(MedicalEquipmentProcessor.class);

    @Autowired
    ProcedureProcessor procedureProcessor;

    public CCDAMedicalEquipment retrieveMedicalEquipment(XPath xPath , Document doc) throws XPathExpressionException, TransformerException {
        long startTime = System.currentTimeMillis();
        logger.info("medical equipment parsing Start time:"+ startTime);
        CCDAMedicalEquipment medicalEquipments = null;
        Element sectionElement = ApplicationUtil.getCloneNode((Element) xPath.compile(ApplicationConstants.MEDICAL_EQUIPMENT_EXPRESSION).evaluate(doc, XPathConstants.NODE));
        List<CCDAID> ids = new ArrayList<>();
        if(sectionElement != null){
            medicalEquipments = new CCDAMedicalEquipment();
            sectionElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            medicalEquipments.setLineNumber(sectionElement.getUserData("lineNumber") + " - " + sectionElement.getUserData("endLineNumber"));
            medicalEquipments.setXmlString(ApplicationUtil.nodeToString((Node) sectionElement));
            if(ApplicationUtil.checkForNullFlavourNI(sectionElement)) {
                medicalEquipments.setSectionNullFlavourWithNI(true);
                return medicalEquipments;
            }
            medicalEquipments.setSectionTemplateId(ApplicationUtil.readTemplateIdList((NodeList) xPath.compile(ApplicationConstants.TEMPLATE_ID_EXPRESSION).
                    evaluate(sectionElement, XPathConstants.NODESET)));
            medicalEquipments.setSectionCode(ApplicationUtil.readCode((Element) xPath.compile(ApplicationConstants.CODE_EXPRESSION).
                    evaluate(sectionElement, XPathConstants.NODE)));

            logger.info(" Adding UDIs from Organizers ");
            // Account for UDIs
            medicalEquipments.addUDIs(readUDIsFromOrganizer((NodeList)CCDAConstants.MEDICAL_EQUIPMENT_ORG_EXPRESSION.
                    evaluate(sectionElement, XPathConstants.NODESET)));

            logger.info("Adding UDIs from Procedures Activity Procedures ");
            medicalEquipments.addUDIs(readUDIsFromProcedures((NodeList)CCDAConstants.REL_PROC_ACT_PROC_EXP.
                    evaluate(sectionElement, XPathConstants.NODESET)));

            medicalEquipments.setAuthor(ParserUtilities.readAuthor((Element) CCDAConstants.REL_AUTHOR_EXP.
                    evaluate(sectionElement, XPathConstants.NODE)));

            medicalEquipments.setProcActsProcs(procedureProcessor.readProcedures((NodeList) xPath.compile("./entry/procedure[not(@nullFlavor)]").
                    evaluate(sectionElement, XPathConstants.NODESET), xPath,ids));
            medicalEquipments.setSupplyActivities(readSupplyActivities((NodeList) xPath.compile("./entry/supply[not(@nullFlavor)]").
                    evaluate(sectionElement, XPathConstants.NODESET), xPath,ids));
            medicalEquipments.setOrganizers(readOrganizers((NodeList) xPath.compile("./entry/organizer[not(@nullFlavor)]").
                    evaluate(sectionElement, XPathConstants.NODESET), xPath,ids));
            Element textElement = (Element) xPath.compile("./text[not(@nullFlavor)]").evaluate(sectionElement, XPathConstants.NODE);

            if (textElement != null) {
                medicalEquipments.getReferenceLinks().addAll((ApplicationUtil.readSectionTextReferences((NodeList) xPath.compile(".//*[not(@nullFlavor) and @ID]").
                        evaluate(textElement, XPathConstants.NODESET))));
            }
            medicalEquipments.setIds(ids);
        }
        logger.info("medical equipment parsing End time:"+ (System.currentTimeMillis() - startTime));
        return medicalEquipments;
    }

    public ArrayList<CCDAUDI> readUDIsFromOrganizer(NodeList orgNodeList) throws XPathExpressionException
    {
        ArrayList<CCDAUDI> udisList = null;

        for (int i = 0; i < orgNodeList.getLength(); i++) {

            logger.info(" Reading UDIs from Organizer ");
            Element orgElement = ApplicationUtil.getCloneNode((Element) orgNodeList.item(i));

            readUDIsFromProcedures((NodeList)CCDAConstants.MEDICAL_EQUIPMENT_ORG_PAP_EXPRESSION.
                    evaluate(orgElement, XPathConstants.NODESET));
        }

        return udisList;
    }

    public ArrayList<CCDAUDI> readUDIsFromProcedures(NodeList proceduresNodeList ) throws XPathExpressionException
    {
        ArrayList<CCDAUDI> udis = new ArrayList<CCDAUDI>();
        for (int i = 0; i < proceduresNodeList.getLength(); i++) {

            logger.info("Adding UDIs from procs ");

            Element procedureElement = ApplicationUtil.getCloneNode((Element) proceduresNodeList.item(i));

            if(procedureElement != null) {

                logger.info(" Procedure Not null ");

                NodeList deviceNodeList = (NodeList) CCDAConstants.REL_PROCEDURE_UDI_EXPRESSION.
                        evaluate(procedureElement, XPathConstants.NODESET);

                ArrayList<CCDAUDI> devices = readUDI(deviceNodeList);
                if(devices != null)
                    udis.addAll(devices);
            }


        }

        return udis;
    }
    
    public ArrayList<CCDAUDI> readUDI(NodeList deviceNodeList) throws XPathExpressionException
    {
        ArrayList<CCDAUDI> deviceList =  null;
        if(!ParserUtilities.isNodeListEmpty(deviceNodeList))
        {
            deviceList = new ArrayList<>();
        }
        CCDAUDI device;
        for (int i = 0; i < deviceNodeList.getLength(); i++) {

            logger.info("Adding UDIs");
            device = new CCDAUDI();

            Element deviceElement = ApplicationUtil.getCloneNode((Element) deviceNodeList.item(i));
            device.setTemplateIds(ParserUtilities.readTemplateIdList((NodeList) CCDAConstants.REL_TEMPLATE_ID_EXP.
                    evaluate(deviceElement, XPathConstants.NODESET)));

            device.setUDIValue(ParserUtilities.readTemplateIdList((NodeList) CCDAConstants.REL_ID_EXP.
                    evaluate(deviceElement, XPathConstants.NODESET)));

            device.setDeviceCode(ParserUtilities.readCode((Element) CCDAConstants.REL_PLAYING_DEV_CODE_EXP.
                    evaluate(deviceElement, XPathConstants.NODE)));

            device.setScopingEntityId(ParserUtilities.readTemplateIdList((NodeList) CCDAConstants.REL_SCOPING_ENTITY_ID_EXP.
                    evaluate(deviceElement, XPathConstants.NODESET)));

            device.setAuthor(ParserUtilities.readAuthor((Element) CCDAConstants.REL_AUTHOR_EXP.
                    evaluate(deviceElement, XPathConstants.NODE)));
            deviceList.add(device);

        }

        return deviceList;

    }

    private List<CCDAMedicalEquipmentOrg> readOrganizers(NodeList orgNodes, XPath xPath, List<CCDAID> ids) throws TransformerException, XPathExpressionException {
        if (ApplicationUtil.isNodeListEmpty(orgNodes)) {
            return new ArrayList<>();
        }
        List<CCDAMedicalEquipmentOrg> medicalEquipmentOrgs = null;
        CCDAMedicalEquipmentOrg medicalEquipmentOrg;
        for (int i = 0; i < orgNodes.getLength(); i++) {
            medicalEquipmentOrg = new CCDAMedicalEquipmentOrg();
            Element orgElement = ApplicationUtil.getCloneNode((Element) orgNodes.item(i));
        orgElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            medicalEquipmentOrg.setLineNumber(orgElement.getUserData("lineNumber") + " - " + orgElement.getUserData("endLineNumber") );
            medicalEquipmentOrg.setXmlString(ApplicationUtil.nodeToString((Node)orgElement));

        if(ApplicationUtil.readID((Element) xPath.compile(ApplicationConstants.ID_EXPRESSION).
                evaluate(orgElement, XPathConstants.NODE),"procedure")!= null) {
            ids.add(ApplicationUtil.readID((Element) xPath.compile(ApplicationConstants.ID_EXPRESSION).
                    evaluate(orgElement, XPathConstants.NODE),"procedure"));
        }
            medicalEquipmentOrg.setSectionTemplateId(ApplicationUtil.readTemplateIdList((NodeList) xPath.compile(ApplicationConstants.TEMPLATE_ID_EXPRESSION).
                evaluate(orgElement, XPathConstants.NODESET)));
            medicalEquipmentOrg.setReferenceText(ApplicationUtil.readTextReference((Element) xPath.compile(ApplicationConstants.REFERENCE_TEXT_EXPRESSION).
                evaluate(orgElement, XPathConstants.NODE)));
            medicalEquipmentOrg.setSupplyCode(ApplicationUtil.readCode((Element) xPath.compile(ApplicationConstants.CODE_EXPRESSION).
                evaluate(orgElement, XPathConstants.NODE)));
            medicalEquipmentOrg.setStatus(ApplicationUtil.readCode((Element) xPath.compile(ApplicationConstants.STATUS_EXPRESSION).
                evaluate(orgElement, XPathConstants.NODE)));
            medicalEquipmentOrg.setTimeOfUse(ApplicationUtil.readEffectivetime((Element) xPath.compile(ApplicationConstants.EFFECTIVE_EXPRESSION).
                evaluate(orgElement, XPathConstants.NODE),xPath));
            medicalEquipmentOrgs = new ArrayList<>();
            medicalEquipmentOrgs.add(medicalEquipmentOrg);
        }
        return medicalEquipmentOrgs;
    }

    private List<CCDANonMedicalSupplyAct> readSupplyActivities(NodeList supplyNodes, XPath xPath, List<CCDAID> ids) throws TransformerException, XPathExpressionException {
        if (ApplicationUtil.isNodeListEmpty(supplyNodes)) {
            return new ArrayList<>();
        }
        List<CCDANonMedicalSupplyAct> supplyActs = null;
        CCDANonMedicalSupplyAct supplyAct;
        for (int i = 0; i < supplyNodes.getLength(); i++) {
            supplyAct = new CCDANonMedicalSupplyAct();
            Element supplyElement = ApplicationUtil.getCloneNode((Element) supplyNodes.item(i));
            supplyElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            supplyAct.setLineNumber(supplyElement.getUserData(ApplicationConstants.LINE_NUMBER_KEY_NAME) + " - " + supplyElement.getUserData(ApplicationConstants.END_LINE_NUMBER_KEY_NAME) );
            supplyAct.setXmlString(ApplicationUtil.nodeToString((Node)supplyElement));
            if(ApplicationUtil.readID((Element) xPath.compile(ApplicationConstants.ID_EXPRESSION).
                    evaluate(supplyElement, XPathConstants.NODE),"procedure")!= null) {
                ids.add(ApplicationUtil.readID((Element) xPath.compile(ApplicationConstants.ID_EXPRESSION).
                        evaluate(supplyElement, XPathConstants.NODE),"procedure"));
            }
            supplyAct.setSectionTemplateId(ApplicationUtil.readTemplateIdList((NodeList) xPath.compile(ApplicationConstants.TEMPLATE_ID_EXPRESSION).
                    evaluate(supplyElement, XPathConstants.NODESET)));
            supplyAct.setReferenceText(ApplicationUtil.readTextReference((Element) xPath.compile(ApplicationConstants.REFERENCE_TEXT_EXPRESSION).
                    evaluate(supplyElement, XPathConstants.NODE)));
            supplyAct.setSupplyCode(ApplicationUtil.readCode((Element) xPath.compile(ApplicationConstants.CODE_EXPRESSION).
                    evaluate(supplyElement, XPathConstants.NODE)));
            supplyAct.setStatus(ApplicationUtil.readCode((Element) xPath.compile(ApplicationConstants.STATUS_EXPRESSION).
                    evaluate(supplyElement, XPathConstants.NODE)));
            supplyAct.setTimeOfUse(ApplicationUtil.readEffectivetime((Element) xPath.compile(ApplicationConstants.EFFECTIVE_EXPRESSION).
                    evaluate(supplyElement, XPathConstants.NODE),xPath));
            supplyActs = new ArrayList<>();
            supplyActs.add(supplyAct);
        }
        return supplyActs;
    }
}
