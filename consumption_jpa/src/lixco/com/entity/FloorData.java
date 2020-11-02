package lixco.com.entity;

import java.util.List;

public class FloorData {
	private int floor;
	private List<Pos> listPos;
	public int getFloor() {
		return floor;
	}
	public void setFloor(int floor) {
		this.floor = floor;
	}
	public List<Pos> getListPos() {
		return listPos;
	}
	public void setListPos(List<Pos> listPos) {
		this.listPos = listPos;
	}
	public FloorData(int floor, List<Pos> listPos) {
		this.floor = floor;
		this.listPos = listPos;
	}
	public FloorData() {
	}
	
}
