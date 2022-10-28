package org.sitenv.ccdaparsing.tests;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sitenv.ccdaparsing.model.CCDACode;
import org.sitenv.ccdaparsing.model.CCDANotes;
import org.sitenv.ccdaparsing.processing.NotesParser;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;

public class NotesTest {

    private static String CCDA_DOC = "src/test/resources/ModRef_CareTeam_Rplc_Auth_Par_With_Prov_Site3300.xml";
    private static ArrayList<CCDANotes> notes;
    private static NotesParser notesParser = new NotesParser();

    @BeforeClass
    public static void setUp() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(CCDA_DOC));
        notes = notesParser.retrieveNotesDetails(doc);
    }

    @Test
    public void testNotesSectionTemplate() {
        Assert.assertEquals("CCDANotes section template comparison test case failed", "2.16.840.1.113883.10.20.22.2.65", notes.get(0).getSectionTemplateId().get(0).getRootValue());
        Assert.assertEquals("CCDANotes section template comparison test case failed", "2016-11-01", notes.get(0).getSectionTemplateId().get(0).getExtValue());
    }

    @Test
    public void testNotesSection() {
        Assert.assertEquals("CCDANotes section template comparison test case failed", "2.16.840.1.113883.10.20.22.2.65", notes.get(0).getSectionTemplateId().get(0).getRootValue());
        Assert.assertEquals("CCDANotes section template comparison test case failed", "2016-11-01", notes.get(0).getSectionTemplateId().get(0).getExtValue());
        Assert.assertEquals("CCDANotes section code comparison test case failed", "11488-4", notes.get(0).getSectionCode().getCode());
        Assert.assertEquals("CCDANotes section code comparison test case failed", "2.16.840.1.113883.6.1", notes.get(0).getSectionCode().getCodeSystem());
        Assert.assertEquals("CCDANotes section code comparison test case failed", "LOINC", notes.get(0).getSectionCode().getCodeSystemName());
        Assert.assertEquals("CCDANotes section code comparison test case failed", "Consultation Note", notes.get(0).getSectionCode().getDisplayName());
    }

    @Test
    public void testNotesActivityTemplate() {
        Assert.assertEquals("CCDANotes notes activity template comparison test case failed", "2.16.840.1.113883.10.20.22.4.202", notes.get(0).getNotesActivity().get(0).getTemplateId().get(0).getRootValue());
        Assert.assertEquals("CCDANotes notes activity template comparison test case failed", "2016-11-01", notes.get(0).getNotesActivity().get(0).getTemplateId().get(0).getExtValue());
    }

    @Test
    public void testNotesActivityActivityCode() {
        Assert.assertEquals("CCDANotes notes activity activityCode comparison test case failed", "34109-9", notes.get(0).getNotesActivity().get(0).getActivityCode().getCode());
        Assert.assertEquals("CCDANotes notes activity activityCode comparison test case failed", "2.16.840.1.113883.6.1", notes.get(0).getNotesActivity().get(0).getActivityCode().getCodeSystem());
        Assert.assertEquals("CCDANotes notes activity activityCode comparison test case failed", "LOINC", notes.get(0).getNotesActivity().get(0).getActivityCode().getCodeSystemName());
        Assert.assertEquals("CCDANotes notes activity activityCode comparison test case failed", "Note", notes.get(0).getNotesActivity().get(0).getActivityCode().getDisplayName());
        Assert.assertEquals("CCDANotes notes activity activityCode comparison test case failed", "34109-9", notes.get(0).getNotesActivity().get(0).getActivityCode().getCode());
        Assert.assertEquals("CCDANotes notes activity activityCode comparison test case failed", "11488-4", notes.get(0).getNotesActivity().get(0).getActivityCode().getTranslations().get(0).getCode());
        Assert.assertEquals("CCDANotes notes activity activityCode comparison test case failed", "2.16.840.1.113883.6.1", notes.get(0).getNotesActivity().get(0).getActivityCode().getTranslations().get(0).getCodeSystem());
        Assert.assertEquals("CCDANotes notes activity activityCode comparison test case failed", "LOINC", notes.get(0).getNotesActivity().get(0).getActivityCode().getTranslations().get(0).getCodeSystemName());
        Assert.assertEquals("CCDANotes notes activity activityCode comparison test case failed", "20200622", notes.get(0).getNotesActivity().get(0).getEffTime().getValue());
        Assert.assertEquals("CCDANotes notes activity activityCode comparison test case failed", "Consultation Note", notes.get(0).getNotesActivity().get(0).getActivityCode().getTranslations().get(0).getDisplayName());
    }

    @Test
    public void testNotesActivityAuthor() {
        Assert.assertEquals("CCDANotes notes activity author comparison test case failed", "20200622", notes.get(0).getNotesActivity().get(0).getEffTime().getValue());
        Assert.assertEquals("CCDANotes notes activity author comparison test case failed", "202006221100-0500", notes.get(0).getNotesActivity().get(0).getAuthor().getTime().getValue());
        Assert.assertEquals("CCDANotes notes activity author comparison test case failed", "2.16.840.1.113883.10.20.22.4.119", notes.get(0).getNotesActivity().get(0).getAuthor().getTemplateIds().get(0).getRootValue());
        Assert.assertEquals("CCDANotes notes activity author comparison test case failed", "2.16.840.1.113883.4.6", notes.get(0).getNotesActivity().get(0).getAuthor().getAuthorIds().get(0).getRootValue());
        Assert.assertEquals("CCDANotes notes activity author comparison test case failed", "111111", notes.get(0).getNotesActivity().get(0).getAuthor().getAuthorIds().get(0).getExtValue());
        Assert.assertEquals("CCDANotes notes activity author comparison test case failed", "2.16.840.1.113883.19.5", notes.get(0).getNotesActivity().get(0).getAuthor().getRepOrgIds().get(0).getRootValue());
        Assert.assertEquals("CCDANotes notes activity author comparison test case failed", "Neighborhood Physicians Practice", notes.get(0).getNotesActivity().get(0).getAuthor().getOrgName().getValue());
    }

    @Test
    public void testNotesActivityParent() {
        Assert.assertEquals("CCDANotes notes activity parent comparison test case failed", "2.16.840.1.113883.10.20.22.2.65", notes.get(0).getNotesActivity().get(0).getParent().getSectionTemplateId().get(0).getRootValue());
        Assert.assertEquals("CCDANotes notes activity parent comparison test case failed", "2016-11-01", notes.get(0).getNotesActivity().get(0).getParent().getSectionTemplateId().get(0).getExtValue());
        Assert.assertEquals("CCDANotes notes activity parent comparison test case failed", "11488-4", notes.get(0).getNotesActivity().get(0).getParent().getSectionCode().getCode());
        Assert.assertEquals("CCDANotes notes activity parent comparison test case failed", "2.16.840.1.113883.6.1", notes.get(0).getNotesActivity().get(0).getParent().getSectionCode().getCodeSystem());
        Assert.assertEquals("CCDANotes notes activity parent comparison test case failed", "LOINC", notes.get(0).getNotesActivity().get(0).getParent().getSectionCode().getCodeSystemName());
        Assert.assertEquals("CCDANotes notes activity parent comparison test case failed", "Consultation Note", notes.get(0).getNotesActivity().get(0).getParent().getSectionCode().getDisplayName());
    }

    @Test
    public void testNotesActivityStatus() {
        Assert.assertEquals("CCDANotes notes activity status comparison test case failed", "completed", ((CCDACode) notes.get(0).getNotesActivity().get(0).getStatusCode()).getCode());
    }
}
