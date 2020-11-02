package lixco.com.reqInfo;

public class Message {
	private String user_message;
	private String internal_message;
	private int code;
	public Message() {
	}
	public String getUser_message() {
		return user_message;
	}
	public void setUser_message(String user_message) {
		this.user_message = user_message;
	}
	public String getInternal_message() {
		return internal_message;
	}
	public void setInternal_message(String internal_message) {
		this.internal_message = internal_message;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
}
