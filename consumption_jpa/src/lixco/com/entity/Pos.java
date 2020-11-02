package lixco.com.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="pos")
public class Pos implements Serializable,Cloneable{
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String pos_code;//mã vị trí
	private String pos_name;//tên vị trí
	private double quantity_pallet;//số lượng pallet tối tiểu đặt tại mỗi vị trí
	private String barcode;//mã vạch
	@ManyToOne(fetch=FetchType.LAZY)
	private Floor floor;
	private int row_stack;
	private int floorb;
	@ManyToOne(fetch=FetchType.LAZY)
	private Warehouse warehouse;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getPos_code() {
		return pos_code;
	}
	public void setPos_code(String pos_code) {
		this.pos_code = pos_code;
	}
	public String getPos_name() {
		return pos_name;
	}
	public void setPos_name(String pos_name) {
		this.pos_name = pos_name;
	}
	public Floor getFloor() {
		return floor;
	}
	public void setFloor(Floor floor) {
		this.floor = floor;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public int getRow_stack() {
		return row_stack;
	}
	public void setRow_stack(int row_stack) {
		this.row_stack = row_stack;
	}
	public int getFloorb() {
		return floorb;
	}
	public void setFloorb(int floorb) {
		this.floorb = floorb;
	}
	public Warehouse getWarehouse() {
		return warehouse;
	}
	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}
	public double getQuantity_pallet() {
		return quantity_pallet;
	}
	public void setQuantity_pallet(double quantity_pallet) {
		this.quantity_pallet = quantity_pallet;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pos other = (Pos) obj;
		if (id != other.id)
			return false;
		return true;
	}
	@Override
	public Pos clone() throws CloneNotSupportedException {
		return (Pos)super.clone();
	}
	
}
