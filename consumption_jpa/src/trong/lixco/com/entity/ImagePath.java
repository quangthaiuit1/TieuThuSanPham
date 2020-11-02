package trong.lixco.com.entity;


import javax.persistence.Entity;

@Entity
public class ImagePath extends AbstractEntity{
	private String path_img;

	public String getPath_img() {
		return path_img;
	}

	public void setPath_img(String path_img) {
		this.path_img = path_img;
	}
}
