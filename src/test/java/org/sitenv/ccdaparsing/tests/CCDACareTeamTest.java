package org.sitenv.ccdaparsing.tests;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sitenv.ccdaparsing.model.CCDACareTeamMember;
import org.sitenv.ccdaparsing.processing.CareTeamMemberProcessor;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class CCDACareTeamTest {

    private static String CCDA_DOC = "src/test/resources/ModRef_CareTeam_Rplc_Auth_Par_With_Prov_Site3300.xml";
    private static CCDACareTeamMember careTeamMember;
    private static CareTeamMemberProcessor careTeamMemberProcessor = new CareTeamMemberProcessor();

    @BeforeClass
    public static void setUp() throws Exception {
        // removed fields to ensure no side effects with DocumentRoot
        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(CCDA_DOC));
        careTeamMember = careTeamMemberProcessor.retrieveCareTeamSectionDetails(doc,new CCDACareTeamMember());
    }

    @Test
    public void testCareTeamSectionAuthor(){
        Assert.assertEquals("", "555566667", careTeamMember.getAuthor().getAuthorIds().get(0).getExtValue());
    }

    @Test
    public void testCareTeamSectionCode(){
        Assert.assertEquals("", "85847-2", careTeamMember.getSectionCode().getCode());
        Assert.assertEquals("", "2.16.840.1.113883.6.1", careTeamMember.getSectionCode().getCodeSystem());
        Assert.assertEquals("", "LOINC", careTeamMember.getSectionCode().getCodeSystemName());
    }

    @Test
    public void testCareTeamSectionTemplateId(){
        Assert.assertEquals("", "2020-07-01", careTeamMember.getSectionTemplateId().get(0).getExtValue());
        Assert.assertEquals("", "2.16.840.1.113883.10.20.22.2.500", careTeamMember.getSectionTemplateId().get(0).getRootValue());
    }

    @Test
    public void testCareTeamMemberActs(){
        Assert.assertEquals("", "20200601", careTeamMember.getMemberActs().get(0).getEffectiveTime().getLow().getValue());
        Assert.assertEquals("", "2.16.840.1.113883.6.1", careTeamMember.getMemberActs().get(0).getMemberActCode().getCodeSystem());
        Assert.assertEquals("", "85847-2", careTeamMember.getMemberActs().get(0).getMemberActCode().getCode());
        Assert.assertEquals("", "active", careTeamMember.getMemberActs().get(0).getStatusCode().getCode());
        Assert.assertEquals("", "Albert",careTeamMember.getMemberActs().get(0).getPrimaryPerformer().getFirstName().getValue());
        Assert.assertEquals("", "2019-07-01", careTeamMember.getMemberActs().get(0).getTemplateIds().get(0).getExtValue());
        Assert.assertEquals("", "2.16.840.1.113883.10.20.22.4.500.1", careTeamMember.getMemberActs().get(0).getTemplateIds().get(0).getRootValue());
        Assert.assertEquals("", "Davis", careTeamMember.getMemberActs().get(0).getPrimaryPerformer().getLastName().getValue());
        Assert.assertEquals("", "JohnParticipant", careTeamMember.getMemberActs().get(0).getOtherCareTeamMembers().get(0).getFirstName().getValue());
        Assert.assertEquals("", "SmithParticipant", careTeamMember.getMemberActs().get(0).getOtherCareTeamMembers().get(0).getLastName().getValue());
    }

    @Test
    public void testCareTeamMember(){
        Assert.assertEquals("", "active", careTeamMember.getStatusCode().getCode());
        Assert.assertEquals("", "20200601", careTeamMember.getCareTeamEffectiveTime().getLow().getValue());
        Assert.assertEquals("", true, careTeamMember.getCareTeamEffectiveTime().getLowPresent());
        Assert.assertEquals("", "#ctm_1", careTeamMember.getReferenceText().getValue());
        Assert.assertEquals("", "Dr Albert Davis", careTeamMember.getCareTeamName());
    }

}
