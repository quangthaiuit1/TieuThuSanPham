package lixco.com.reqInfo;

import lixco.com.entity.Car;

public class CarReqInfo {
	private Car car=null;

	public CarReqInfo() {
	}
	public CarReqInfo(Car car) {
		this.car = car;
	}


	public Car getCar() {
		return car;
	}

	public void setCar(Car car) {
		this.car = car;
	}
	
}
