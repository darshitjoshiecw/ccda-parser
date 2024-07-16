package org.sitenv.ccdaparsing.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class CCDAII extends CCDADataElement{
	
	private String  rootValue;
	private String  extValue;

	public Boolean isPartOf(ArrayList<CCDAII> list) {

		for( CCDAII item : list) {

			//this.log();
			//item.log();

			// Both Root and Extensions are present.
			if( (rootValue != null) && (item.getRootValue() != null) &&
					(extValue != null)  && (item.getExtValue() != null) &&
					(rootValue.equalsIgnoreCase(item.getRootValue())) &&
					(extValue.equalsIgnoreCase(item.getExtValue()))) {
				return true;
			}
			// Only Root value are present
			else if( (rootValue != null) && (item.getRootValue() != null) &&
					(extValue == null)  && (item.getExtValue() == null) &&
					(rootValue.equalsIgnoreCase(item.getRootValue()))) {
				return true;
			}

			// continue through the list
		}

		//if we never hit the postive case
		return false;
	}

	public void log() {

		/*log.info(" *** Intance Identifier *** ");

		log.info(" Root : " + rootValue);
		log.info(" Extension : " + extValue);*/

	}

	public String getRootValue() {
		return rootValue;
	}

	public void setRootValue(String rootValue) {
		this.rootValue = rootValue;
	}

	public String getExtValue() {
		return extValue;
	}

	public void setExtValue(String extValue) {
		this.extValue = extValue;
	}

	public CCDAII()
	{
	}
	
	public CCDAII(String rootValue) {
		this.rootValue = rootValue;
	}
	
	public CCDAII(String rootValue, String extValue) {
		this(rootValue);
		this.extValue = extValue;
	}
	
	@Override
	public boolean equals(Object obj) {

		if (obj == this) { 
			return true; 
		} 
		if (obj == null || obj.getClass() != this.getClass()) { 
			return false; 
		} 
		CCDAII obj2 = (CCDAII) obj; 
		return  (this.rootValue == obj2.getRootValue() || (this.rootValue != null && this.rootValue.equals(obj2.getRootValue()))) && 
				(this.extValue == obj2.getExtValue() || (this.extValue != null && this.extValue.equals(obj2.getExtValue())));
		
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getClass().getSimpleName() + System.lineSeparator());
		sb.append("rootValue: " + rootValue + System.lineSeparator());
		sb.append("extValue:  " + extValue);
		return sb.toString();
	}
}
