package trong.lixco.com.entity;


import javax.persistence.Entity;

@Entity
public class Branch extends AbstractEntity{
	private String code;
	private String name;
	
	public void setId(long id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}

