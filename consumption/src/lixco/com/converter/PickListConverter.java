package lixco.com.converter;

import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import lixco.com.entity.Customer;
import lixco.com.interfaces.ICustomerService;
import lixco.com.reqInfo.CustomerReqInfo;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

@FacesConverter(value = "pickListConverter")
public class PickListConverter  implements Converter{
	@Inject
	private ICustomerService customerService;
	@Override
	public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
		try{
			CustomerReqInfo t=new CustomerReqInfo();
			customerService.selectById(Long.parseLong(value),t);
			return t.getCustomer();
		}catch(Exception e){
		}
		return null;

	}

	@Override
	public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object value) {
		try{
			String id=((Customer)value).getId()+"";
			return id;
		}catch(Exception e){
		}
		return null;
	}

}
