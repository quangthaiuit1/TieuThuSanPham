package lixco.com.reqInfo;

import lixco.com.entity.CarOwner;

public class CarOwnerReqInfo {
	private CarOwner car_owner=null;

	public CarOwnerReqInfo() {
	}
	public CarOwnerReqInfo(CarOwner car_owner) {
		this.car_owner = car_owner;
	}
	public CarOwner getCar_owner() {
		return car_owner;
	}

	public void setCar_owner(CarOwner car_owner) {
		this.car_owner = car_owner;
	}
	
}
