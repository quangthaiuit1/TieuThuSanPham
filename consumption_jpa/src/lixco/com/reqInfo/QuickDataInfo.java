package lixco.com.reqInfo;

public class QuickDataInfo {
	private String row;
	private String floor;
	private String pos;
	
	public QuickDataInfo() {
	}
	public QuickDataInfo(String row, String floor, String pos) {
		this.row = row;
		this.floor = floor;
		this.pos = pos;
	}
	public String getRow() {
		return row;
	}
	public void setRow(String row) {
		this.row = row;
	}
	public String getFloor() {
		return floor;
	}
	public void setFloor(String floor) {
		this.floor = floor;
	}
	public String getPos() {
		return pos;
	}
	public void setPos(String pos) {
		this.pos = pos;
	}
	
}
