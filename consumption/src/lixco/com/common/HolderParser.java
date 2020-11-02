package lixco.com.common;

public class HolderParser {
	private int err;
	private Object value;
	
	
	public HolderParser() {
	}
	public HolderParser(int err, Object value) {
		this.err = err;
		this.value = value;
	}
	public int getErr() {
		return err;
	}
	public void setErr(int err) {
		this.err = err;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	
}
