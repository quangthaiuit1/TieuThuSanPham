package lixco.com.reqInfo;

import lixco.com.entity.Product;

public class WrapGoodsReceiptNoteDetailFile {
	private Product product=null;
	private double quantity;
	private String batch_code;
	public WrapGoodsReceiptNoteDetailFile() {
	}
	public WrapGoodsReceiptNoteDetailFile(Product product, double quantity,String batch_code) {
		this.product = product;
		this.quantity = quantity;
		this.batch_code=batch_code;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	public String getBatch_code() {
		return batch_code;
	}
	public void setBatch_code(String batch_code) {
		this.batch_code = batch_code;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((batch_code == null) ? 0 : batch_code.hashCode());
		result = prime * result + ((product == null) ? 0 : product.hashCode());
		long temp;
		temp = Double.doubleToLongBits(quantity);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WrapGoodsReceiptNoteDetailFile other = (WrapGoodsReceiptNoteDetailFile) obj;
		if (batch_code == null) {
			if (other.batch_code != null)
				return false;
		} else if (!batch_code.equals(other.batch_code))
			return false;
		if (product == null) {
			if (other.product != null)
				return false;
		} else if (!product.equals(other.product))
			return false;
		if (Double.doubleToLongBits(quantity) != Double.doubleToLongBits(other.quantity))
			return false;
		return true;
	}
	
	
}
