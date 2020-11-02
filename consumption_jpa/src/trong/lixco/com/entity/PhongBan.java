package trong.lixco.com.entity;


import javax.persistence.Entity;

@Entity
public class PhongBan extends AbstractEntity{
	private String codePBNew;
	private String pass;
	private String namePB;
	public String getCodePBNew() {
		return codePBNew;
	}
	public void setCodePBNew(String codePBNew) {
		this.codePBNew = codePBNew;
	}
	
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
	public String getNamePB() {
		return namePB;
	}
	public void setNamePB(String namePB) {
		this.namePB = namePB;
	}
	
	
}
