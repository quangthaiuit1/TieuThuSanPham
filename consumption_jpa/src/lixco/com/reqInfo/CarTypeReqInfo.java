package lixco.com.reqInfo;

import lixco.com.entity.CarType;

public class CarTypeReqInfo {
	private CarType car_type=null;

	public CarTypeReqInfo(CarType car_type) {
		this.car_type = car_type;
	}

	public CarTypeReqInfo() {
	}

	public CarType getCar_type() {
		return car_type;
	}

	public void setCar_type(CarType car_type) {
		this.car_type = car_type;
	}
	
}
