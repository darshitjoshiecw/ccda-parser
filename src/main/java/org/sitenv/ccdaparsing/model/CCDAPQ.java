package org.sitenv.ccdaparsing.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sitenv.ccdaparsing.dto.ContentValidationResult;
import org.sitenv.ccdaparsing.dto.enums.ContentValidationResultLevel;

import java.util.ArrayList;

public class CCDAPQ extends CCDAXmlSnippet {

	private String  value;
	private String  units;
	private String xsiType;

	public Boolean compare(CCDAPQ subValue, ArrayList<ContentValidationResult> results, String elementName) {

		Boolean exception = false;

		Boolean valueComp = false;
		Boolean unitComp = false;

		//Compare Values by converting to doubles.
		if( (value != null) && (subValue.getValue() != null) ){

			try {
				Double refval = Double.parseDouble(value);
				Double subval = Double.parseDouble(subValue.getValue());

				if(Math.abs(refval-subval) < 0.0001) {
					//log.info(" Values Match with actual values");
					valueComp = true;
				}

			}
			catch(NumberFormatException e) {
				exception = true;
			}

		}
		else if ( (value == null) && (subValue.getValue() == null)) {
			valueComp = true;
		}

		// Compare units accounting for "unity"
		if( (units != null) && (subValue.getUnits() != null) ){

			if(units.equalsIgnoreCase(subValue.getUnits())) {
				unitComp = true;
			}
			else if( (units.equalsIgnoreCase("1") || units.equalsIgnoreCase("")) &&
					(subValue.getUnits().equalsIgnoreCase("1") || subValue.getUnits().equalsIgnoreCase("")) )
			{
				unitComp = true;
			}

		}
		else if( (units == null) && (subValue.getUnits() == null) ) {
			unitComp = true;
		}

		if(!valueComp || !unitComp) {
			String error = "The " + elementName + " : Value PQ - value = " + ((value != null)?value:"None Specified")
					+ " and Units = " + ((units != null)?units:"None Specified")
					+ "  , do not match the submitted CCDA : Value PQ - value = " + ((subValue.getValue() != null)?subValue.getValue():"None Specified")
					+ " , and Units = " + ((subValue.getUnits() != null)?subValue.getUnits():"None Specified") + ".";

			ContentValidationResult rs = new ContentValidationResult(error, ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0" );
			results.add(rs);
			return false;

		}

		return true;

		/*
		if( (value != null) && (subValue.getValue() != null) &&
			(units != null) && (subValue.getUnits() != null) &&
			(value.equalsIgnoreCase(subValue.getValue())) &&
			(units.equalsIgnoreCase(subValue.getUnits()))) {
			return true;
		}
		else
		{
			String error = "The " + elementName + " : Value PQ - value = " + ((value != null)?value:"None Specified")
				       + " and Units = " + ((units != null)?units:"None Specified")
				       + "  , do not match the submitted CCDA : Value PQ - value = " + ((subValue.getValue() != null)?subValue.getValue():"None Specified")
				       + " , and Units = " + ((subValue.getUnits() != null)?subValue.getUnits():"None Specified") + ".";

			ContentValidationResult rs = new ContentValidationResult(error, ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0" );
			results.add(rs);
			return false;
		}*/

	}

	public Boolean compareWithTolerance(CCDAPQ subValue, ArrayList<ContentValidationResult> results, String elementName, Double tolerancePercentage) {

		Boolean exception = false;

		Boolean valueComp = false;
		Boolean unitComp = false;

		//Compare Values by converting to doubles.
		if( (value != null) && (subValue.getValue() != null) ){

			try {
				Double refval = Double.parseDouble(value);
				Double subval = Double.parseDouble(subValue.getValue());

				Double toleranceLimit = refval * tolerancePercentage;

				if(Math.abs(refval-subval) < toleranceLimit) {
					valueComp = true;
				}

			}
			catch(NumberFormatException e) {
				exception = true;
			}

		}
		else if ( (value == null) && (subValue.getValue() == null)) {
			valueComp = true;
		}

		// Compare units accounting for "unity"
		if( (units != null) && (subValue.getUnits() != null) ){

			if(units.equalsIgnoreCase(subValue.getUnits())) {
				unitComp = true;
			}
			else if( (units.equalsIgnoreCase("1") || units.equalsIgnoreCase("")) &&
					(subValue.getUnits().equalsIgnoreCase("1") || subValue.getUnits().equalsIgnoreCase("")) )
			{
				unitComp = true;
			}

		}
		else if( (units == null) && (subValue.getUnits() == null) ) {
			unitComp = true;
		}

		if(!valueComp || !unitComp) {
			String error = "The " + elementName + " : Value PQ - value = " + ((value != null)?value:"None Specified")
					+ " and Units = " + ((units != null)?units:"None Specified")
					+ "  , do not match the submitted CCDA : Value PQ - value = " + ((subValue.getValue() != null)?subValue.getValue():"None Specified")
					+ " , and Units = " + ((subValue.getUnits() != null)?subValue.getUnits():"None Specified") + ".";

			error += " If the CEHRT vendor believes the submitted C-CDA values are accurate, please work with your ATL to verify the values submitted explaining the reason for not matching.";

			ContentValidationResult rs = new ContentValidationResult(error, ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0" );
			results.add(rs);
			return false;

		}

		return true;

	}

	public void log() {

	}

	public String getValue() {
		return value;
	}



	public void setValue(String value) {
		this.value = value;
	}



	public String getUnits() {
		return units;
	}



	public void setUnits(String units) {
		this.units = units;
	}

	public CCDAPQ(String value)
	{
	  this.value = value;
	}
	
	public CCDAPQ(String value, String xsiType)
	{
	  this.value = value;
	  this.xsiType = xsiType;
	}
	
	public String getXsiType() {
		return xsiType;
	}



	public void setXsiType(String xsiType) {
		this.xsiType = xsiType;
	}



	public CCDAPQ()
	{
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((units == null) ? 0 : units.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		result = prime * result + ((xsiType == null) ? 0 : xsiType.hashCode());
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
		CCDAPQ other = (CCDAPQ) obj;
		if (units == null) {
			if (other.units != null)
				return false;
		} else if (!units.equals(other.units))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		if (xsiType == null) {
			if (other.xsiType != null)
				return false;
		} else if (!xsiType.equals(other.xsiType))
			return false;
		return true;
	}

	
}
