package lixco.com.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import trong.lixco.com.util.AbstractConverter;


@FacesConverter(value = "textAreaConverter")
public class TextAreaConverter implements Converter{

	@Override
	public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
		try{
			System.out.println(value+"aaaaa");
		}catch(Exception e){
		}
		return null;
	}

	@Override
	public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object value) {
		try{
			System.out.println(value+"ssss");
		}catch(Exception e){
		}
		return null;
	}

}
