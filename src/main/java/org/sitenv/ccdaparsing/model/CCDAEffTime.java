package org.sitenv.ccdaparsing.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sitenv.ccdaparsing.dto.ContentValidationResult;
import org.sitenv.ccdaparsing.dto.enums.ContentValidationResultLevel;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CCDAEffTime extends CCDAXmlSnippet{

	private CCDADataElement low;
	private Boolean         lowPresent;
	private CCDADataElement high;
	private Boolean         highPresent;
	private String value;
	private Boolean         valuePresent;
	private String singleAdministration;
	private boolean singleAdministrationValuePresent;
	private boolean nullFlavour;

	public Boolean hasValidData() {

		if( (lowPresent || highPresent || valuePresent) )
			return true;
		else
			return false;
	}

	public void compare(CCDAEffTime subTime, ArrayList<ContentValidationResult> results, String elementName) {

		String refTime;
		String submittedtime;

		// Compare low time values
		if(lowPresent && subTime.getLowPresent() ) {

			if(low.getValue().length() >= 8)
				refTime = low.getValue().substring(0,8);
			else
				refTime = low.getValue();

			if(subTime.getLow().getValue().length() >= 8)
				submittedtime = subTime.getLow().getValue().substring(0,8);
			else
				submittedtime = subTime.getLow().getValue();

			if(refTime.equalsIgnoreCase(submittedtime) ) {
			}
			else {
				String error = "The " + elementName + " (Effective Time: low time value) is " + low.getValue() + " , but submitted CCDA (Effective Time: low time value) is " + subTime.getLow().getValue() + " which does not match ";
				ContentValidationResult rs = new ContentValidationResult(error, ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0" );
				results.add(rs);
			}


		}
		else if(lowPresent && !subTime.getLowPresent()) {

			String error = "The " + elementName + " (low time value) is required, but submitted CCDA does not contain the (low time value) for " + elementName;
			ContentValidationResult rs = new ContentValidationResult(error, ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0" );
			results.add(rs);
		}
		else if(!lowPresent && subTime.getLowPresent()) {

			String error = "The " + elementName + " (low time value) is not required, but submitted CCDA contains the (low time value) for " + elementName;
			ContentValidationResult rs = new ContentValidationResult(error, ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0" );
			results.add(rs);
		}
		else {
		}

		// Compare High Times values
		if(highPresent && subTime.getHighPresent() ) {

			if(high.getValue().length() >= 8)
				refTime = high.getValue().substring(0,8);
			else
				refTime = high.getValue();

			if(subTime.getHigh().getValue().length() >= 8)
				submittedtime = subTime.getHigh().getValue().substring(0,8);
			else
				submittedtime = subTime.getHigh().getValue();

			if(refTime.equalsIgnoreCase(submittedtime) ) {
			}
			else {
				String error = "The " + elementName + " (Effective Time: High time value) is " + high.getValue() + " , but submitted CCDA (Effective Time: High time value) is " + subTime.getHigh().getValue() + " which does not match ";
				ContentValidationResult rs = new ContentValidationResult(error, ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0" );
				results.add(rs);
			}

		}
		else if(highPresent && !subTime.getHighPresent()) {

			String error = "The " + elementName + " (high time value) is required, but submitted CCDA does not contain the (high time value) for " + elementName;
			ContentValidationResult rs = new ContentValidationResult(error, ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0" );
			results.add(rs);
		}
		else if(!highPresent && subTime.getHighPresent()) {

			String error = "The " + elementName + " (high time value) is not required, but submitted CCDA contains the (high time value) for " + elementName;
			ContentValidationResult rs = new ContentValidationResult(error, ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0" );
			results.add(rs);
		}
		else {
		}

		// Compare Time value element
/*		if(valuePresent && subTime.getValuePresent() ) {

			if(value.getValue().length() >= 8)
				refTime = value.getValue().substring(0,8);
			else 
				refTime = value.getValue();

			if(subTime.getValue().getValue().length() >= 8)
				submittedtime = subTime.getValue().getValue().substring(0,8);
			else 
				submittedtime = subTime.getValue().getValue();

			if(refTime.equalsIgnoreCase(submittedtime) ) {
				log.info("Value Time element matches");
			}
			else {
				String error = "The " + elementName + " (Effective Time: Value ) is " + value.getValue() + " , but submitted CCDA (Effective Time: Value ) is " + subTime.getValue().getValue() + " which does not match ";
				ContentValidationResult rs = new ContentValidationResult(error, ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0" );
				results.add(rs);
			}

		}
		else if(valuePresent && !subTime.getValuePresent()) {

			String error = "The " + elementName + " (value time element ) is required, but submitted CCDA does not contain the (value time element) for " + elementName;
			ContentValidationResult rs = new ContentValidationResult(error, ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0" );
			results.add(rs);
		}
		else if(!valuePresent && subTime.getValuePresent()) {

			String error = "The " + elementName + " (value time element) is not required, but submitted CCDA contains the (value time element) for " + elementName;
			ContentValidationResult rs = new ContentValidationResult(error, ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0" );
			results.add(rs);
		}
		else {
			log.info("Value Time elements absent in both refernce and submitted models ");
		}*/
	}

	public void compareValueElement(CCDAEffTime subTime, ArrayList<ContentValidationResult> results, String elementName) {

		String refTime;
		String submittedtime;

		// Compare Time value element
		if(valuePresent && subTime.getValuePresent() ) {

			if(value.length() >= 8)
				refTime = value.substring(0,8);
			else
				refTime = value;

			if(subTime.getValue().length() >= 8)
				submittedtime = subTime.getValue().substring(0,8);
			else
				submittedtime = subTime.getValue();

			if(refTime.equalsIgnoreCase(submittedtime) ) {
			}
			else {
				String error = "The " + elementName + " ( Time Value ) is : " + value
						+ " , but submitted CCDA ( Time Value ) is : " + subTime.getValue()
						+ " which does not match ";
				ContentValidationResult rs = new ContentValidationResult(error, ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0" );
				results.add(rs);
			}

		}
		else if(valuePresent && !subTime.getValuePresent()) {

			String error = "The " + elementName
					+ " (value time element ) is required, but submitted CCDA does not contain the (value time element) for "
					+ elementName;
			ContentValidationResult rs = new ContentValidationResult(error, ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0" );
			results.add(rs);
		}
		else if(!valuePresent && subTime.getValuePresent()) {

			String error = "The " + elementName
					+ " (value time element) is not required, but submitted CCDA contains the (value time element) for "
					+ elementName;
			ContentValidationResult rs = new ContentValidationResult(error, ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0" );
			results.add(rs);
		}
		else {
		}
	}

	public void compareValueElementWithExactMatchFullPrecision(CCDAEffTime subTime, ArrayList<ContentValidationResult> results, String elementName) {

		String refTime;
		String submittedtime;

		// Compare Time value element
		if(valuePresent && subTime.getValuePresent() ) {


			refTime = value;
			submittedtime = subTime.getValue();

			if(refTime.equalsIgnoreCase(submittedtime) ) {
			}
			else {
				String error = "The " + elementName + " (Effective Time: Value ) is " + value
						+ " , but submitted CCDA (Effective Time: Value ) is " + subTime.getValue()
						+ " which does not match ";
				ContentValidationResult rs = new ContentValidationResult(error, ContentValidationResultLevel.ERROR,
						"/ClinicalDocument", "0");
				results.add(rs);
			}

		}
		else if(valuePresent && !subTime.getValuePresent()) {

			String error = "The " + elementName
					+ " (value time element ) is required, but submitted CCDA does not contain the (value time element) for "
					+ elementName;
			ContentValidationResult rs = new ContentValidationResult(error, ContentValidationResultLevel.ERROR,
					"/ClinicalDocument", "0");
			results.add(rs);
		}
		else if(!valuePresent && subTime.getValuePresent()) {

		}
		else {
		}
	}

	public void validateValueLengthDateTimeAndTimezoneDependingOnPrecision(ArrayList<ContentValidationResult> results,
																		   String localElName, String parentElName, int index, boolean isSub) {
		System.out.println("!!: ENTER validateValueLengthDateTimeAndTimezoneDependingOnPrecision");

		if (valuePresent) {
			final String timeDocType = isSub ? "submitted" : "scenario";
			final String errorPrefix = "The " + timeDocType + " Provenance (Time: Value) ";
			final boolean isDisplayIndex = index > -1 && !parentElName.equalsIgnoreCase("Document Level");

			// validate date only in first 8 chars so we can have more specific errors returned
			// This validation fails for letters, symbols, or being too short. Too long ends up in the next validation.
			// instead of one big RegEx with an or condition and one mixed less-specific error
			String dateOnly8CharTime;
			if (value.length() > 8) {
				// we only have > 8 characters, store only the 1st 8
				dateOnly8CharTime = value.substring(0, 8);
			} else {
				// we only have 8 characters, store them all
				dateOnly8CharTime = value;
			}
			System.out.println("!!: stored dateOnly8CharTime: " + dateOnly8CharTime);
			// validate dateOnly8CharTime with RegEx for 1st 8 chars
//			^[0-9]{8}$
//			^ asserts position at start of a line
//			Match a single character present in the list below [0-9]
//			{8} matches the previous token exactly 8 times
//			0-9 matches a single character in the range between 0 (index 48) and 9 (index 57) (case sensitive)
//			$ asserts position at the end of a line			
			Pattern baseDatePattern = Pattern.compile("^[0-9]{8}$");
			Matcher baseDateMatcher = baseDatePattern.matcher(dateOnly8CharTime);
			if (baseDateMatcher.find()) {
			} else {
				String error = errorPrefix + value + " at " + parentElName + (isDisplayIndex ? " index " + (index + 1) : "")
						+ ", is invalid. Please ensure the value starts with an 8-digit date. "
						+ "The invalid date portion of the value is " + baseDateMatcher + ".";

				ContentValidationResult rs = new ContentValidationResult(error,
						ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0");
				results.add(rs);
			}

			// validate time and time-zone portions specifically
			if (value.length() > 8) {
				String timeAndTimeZone = value.substring(8);
				System.out.println("!!: stored timeAndTimeZone: " + timeAndTimeZone);
				// validate timeAndTimeZone with RegEx for chars after first 8
				// Note: If there is an issue where the base date > 8 chars, that error will show up in the time portion.
				// This is a perfectly reasonable result as the time zone is supposed to start after 8 chars and if it does not it's invalid
//				^([0-9]{4}|[0-9]{6})(-|\+)([0-9]{4})$
//				^ asserts position at start of a line
//				1st Capturing Group ([0-9]|[0-9])
//				1st Alternative [0-9]
//				Match a single character present in the list below [0-9]
//				{4} matches the previous token exactly 4 times
//				0-9 matches a single character in the range between 0 (index 48) and 9 (index 57) (case sensitive)
//				2nd Alternative [0-9]
//				Match a single character present in the list below [0-9]
//				{6} matches the previous token exactly 6 times
//				0-9 matches a single character in the range between 0 (index 48) and 9 (index 57) (case sensitive)
//				2nd Capturing Group (-|\+)
//				1st Alternative -
//				- matches the character - literally (case sensitive)
//				2nd Alternative \+
//				\+ matches the character + literally (case sensitive)
//				3rd Capturing Group ([0-9])
//				Match a single character present in the list below [0-9]
//				{4} matches the previous token exactly 4 times
//				0-9 matches a single character in the range between 0 (index 48) and 9 (index 57) (case sensitive)
//				$ asserts position at the end of a line
				Pattern timeAndTimeZoneDatePattern = Pattern.compile("^([0-9]{4}|[0-9]{6})(-|\\+)([0-9]{4})$");
				Matcher timeAndTimeZoneDateMatcher = timeAndTimeZoneDatePattern.matcher(timeAndTimeZone);
				if (timeAndTimeZoneDateMatcher.find()) {
				} else {
					String error = errorPrefix + value + " at " + parentElName + (isDisplayIndex ? " index " + (index + 1) : "")
							+ " is invalid. Please ensure the time and time-zone starts with a 4 or 6-digit time, "
							+ "followed by a '+' or a '-', and finally, a 4-digit time-zone. "
							+ "The invalid time and time-zone portion of the value is " + timeAndTimeZone + ".";
					ContentValidationResult rs = new ContentValidationResult(error,
							ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0");
					results.add(rs);
				}
			}

		}

	}

	public void log() {

	}
	
	public CCDAEffTime(String value)
	{
		this.value = value;
	}
	
	public CCDADataElement getLow() {
		return low;
	}

	public void setLow(CCDADataElement low) {
		this.low = low;
	}

	public Boolean getLowPresent() {
		return lowPresent;
	}

	public void setLowPresent(Boolean lowPresent) {
		this.lowPresent = lowPresent;
	}

	public CCDADataElement getHigh() {
		return high;
	}

	public void setHigh(CCDADataElement high) {
		this.high = high;
	}

	public Boolean getHighPresent() {
		return highPresent;
	}

	public void setHighPresent(Boolean highPresent) {
		this.highPresent = highPresent;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Boolean getValuePresent() {
		return valuePresent;
	}

	public void setValuePresent(Boolean valuePresent) {
		this.valuePresent = valuePresent;
	}
	
	public String getSingleAdministration() {
		return singleAdministration;
	}

	public void setSingleAdministration(String singleAdministration) {
		this.singleAdministration = singleAdministration;
	}
	
	public boolean isSingleAdministrationValuePresent() {
		return singleAdministrationValuePresent;
	}

	public void setSingleAdministrationValuePresent(
			boolean singleAdministrationValuePresent) {
		this.singleAdministrationValuePresent = singleAdministrationValuePresent;
	}
	
	public boolean isNullFlavour() {
		return nullFlavour;
	}

	public void setNullFlavour(boolean nullFlavour) {
		this.nullFlavour = nullFlavour;
	}

	public CCDAEffTime()
	{
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((high == null) ? 0 : high.hashCode());
		result = prime * result + ((highPresent == null) ? 0 : highPresent.hashCode());
		result = prime * result + ((low == null) ? 0 : low.hashCode());
		result = prime * result + ((lowPresent == null) ? 0 : lowPresent.hashCode());
		result = prime * result + ((singleAdministration == null) ? 0 : singleAdministration.hashCode());
		result = prime * result + (singleAdministrationValuePresent ? 1231 : 1237);
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		result = prime * result + ((valuePresent == null) ? 0 : valuePresent.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		CCDAEffTime other = (CCDAEffTime) obj;
		if (high == null) {
			if (other.high != null)
				return false;
		} else if (!high.equals(other.high))
			return false;
		if (highPresent == null) {
			if (other.highPresent != null)
				return false;
		} else if (!highPresent.equals(other.highPresent))
			return false;
		if (low == null) {
			if (other.low != null)
				return false;
		} else if (!low.equals(other.low))
			return false;
		if (lowPresent == null) {
			if (other.lowPresent != null)
				return false;
		} else if (!lowPresent.equals(other.lowPresent))
			return false;
		if (singleAdministration == null) {
			if (other.singleAdministration != null)
				return false;
		} else if (!singleAdministration.equals(other.singleAdministration))
			return false;
		if (singleAdministrationValuePresent != other.singleAdministrationValuePresent)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		if (valuePresent == null) {
			if (other.valuePresent != null)
				return false;
		} else if (!valuePresent.equals(other.valuePresent))
			return false;
		return true;
	}
	
	

	
}
