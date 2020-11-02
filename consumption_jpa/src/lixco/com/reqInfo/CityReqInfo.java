package lixco.com.reqInfo;

import lixco.com.entity.City;

public class CityReqInfo {
	private City city=null;

	public CityReqInfo() {
	}

	public CityReqInfo(City city) {
		this.city = city;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}
	
}
