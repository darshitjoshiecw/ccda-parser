package org.sitenv.ccdaparsing.model;

import org.springframework.util.StringUtils;

public class CCDATelecom extends CCDAXmlSnippet {

	private String useAttribute;
	private String valueAttribute;


	public String getUseAttribute() {
		return useAttribute;
	}
	
	public void setUseAttribute(String useAttribute) {
		this.useAttribute = useAttribute;
	}
	
	public String getValueAttribute() {
		return valueAttribute;
	}
	
	public void setValueAttribute(String valueAttribute) {
		this.valueAttribute = valueAttribute;
	}
	
	private String formatTelecomValue(String value) {
		if(!StringUtils.isEmpty(value) && !value.contains("mailto")) {
			return value.replaceAll("[^0-9+]", "");
		}
		return value;
	}
	
	private boolean isTelecomContainsMailValue(String value) {
		return value!=null && value.contains("mailto");
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CCDATelecom other = (CCDATelecom) obj;
		
		if(!isTelecomContainsMailValue(valueAttribute)) {
			if (useAttribute == null) {
				if (other.useAttribute != null)
					return false;
			} else if (!useAttribute.equalsIgnoreCase(other.useAttribute))
				return false;
		}
		
		if (valueAttribute == null) {
			if (other.valueAttribute != null)
				return false;
		} else if (!formatTelecomValue(valueAttribute).equalsIgnoreCase(formatTelecomValue(other.valueAttribute)))
			return false;
		return true;
	}	

}
