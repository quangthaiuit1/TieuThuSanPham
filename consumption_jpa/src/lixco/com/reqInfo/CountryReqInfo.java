package lixco.com.reqInfo;

import lixco.com.entity.Country;

public class CountryReqInfo {
	private Country country=null;

	public CountryReqInfo() {
	}

	public CountryReqInfo(Country country) {
		super();
		this.country = country;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}
	
}
