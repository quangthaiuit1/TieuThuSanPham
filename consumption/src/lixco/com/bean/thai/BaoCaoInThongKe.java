package lixco.com.bean.thai;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;

import org.jboss.logging.Logger;
import org.joda.time.LocalDate;

import lixco.com.entity.City;
import lixco.com.entity.Customer;
import lixco.com.service.thai.CityServiceThai;
import lixco.com.service.thai.CustomerServiceThai;
import trong.lixco.com.bean.AbstractBean;

@javax.faces.bean.ManagedBean
@ViewScoped
public class BaoCaoInThongKe extends AbstractBean {

	private static final long serialVersionUID = 1L;
	private Date startDate;
	private Date endDate;
	private int week;
	private int yearOfWeek;
	private int fromMonth;
	private int toMonth;
	private int compareFromMonth;
	private int compareToMonth;
	private int year;
	private int compareYear;
	private List<City> allCity;
	private City citySelected;
	private List<Customer> customersByCity;
	private List<Customer> selectedsCustomer;
	private String reportSelected;

	@EJB
	private CityServiceThai CITY_SERVICE_THAI;
	@EJB
	private CustomerServiceThai CUSTOMER_SERVICE_THAI;

	@Override
	protected void initItem() {
		Calendar cal = Calendar.getInstance();
		fromMonth = cal.get(Calendar.MONTH);
		toMonth = cal.get(Calendar.MONTH);
		compareFromMonth = cal.get(Calendar.MONTH);
		compareToMonth = cal.get(Calendar.MONTH);
		year = cal.get(Calendar.YEAR);
		compareYear = cal.get(Calendar.YEAR);
		allCity = CITY_SERVICE_THAI.findAll();
	}

	public void ajax_setDate() {
		try {
			if (week <= 53 && week > 0) {
				LocalDate lc = new LocalDate();
				startDate = lc.withWeekOfWeekyear(week).withYear(yearOfWeek).dayOfWeek().withMinimumValue().toDate();
				endDate = lc.withWeekOfWeekyear(week).withYear(yearOfWeek).dayOfWeek().withMaximumValue().toDate();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void ajaxHandleShowCustomerByCity() {
		try {
			customersByCity = CUSTOMER_SERVICE_THAI.findByCity(citySelected.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Logger getLogger() {
		return null;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getWeek() {
		return week;
	}

	public void setWeek(int week) {
		this.week = week;
	}

	public int getYearOfWeek() {
		return yearOfWeek;
	}

	public void setYearOfWeek(int yearOfWeek) {
		this.yearOfWeek = yearOfWeek;
	}

	public int getFromMonth() {
		return fromMonth;
	}

	public void setFromMonth(int fromMonth) {
		this.fromMonth = fromMonth;
	}

	public int getToMonth() {
		return toMonth;
	}

	public void setToMonth(int toMonth) {
		this.toMonth = toMonth;
	}

	public int getCompareFromMonth() {
		return compareFromMonth;
	}

	public void setCompareFromMonth(int compareFromMonth) {
		this.compareFromMonth = compareFromMonth;
	}

	public int getCompareToMonth() {
		return compareToMonth;
	}

	public void setCompareToMonth(int compareToMonth) {
		this.compareToMonth = compareToMonth;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getCompareYear() {
		return compareYear;
	}

	public void setCompareYear(int compareYear) {
		this.compareYear = compareYear;
	}

	public List<City> getAllCity() {
		return allCity;
	}

	public void setAllCity(List<City> allCity) {
		this.allCity = allCity;
	}

	public City getCitySelected() {
		return citySelected;
	}

	public void setCitySelected(City citySelected) {
		this.citySelected = citySelected;
	}

	public List<Customer> getCustomersByCity() {
		return customersByCity;
	}

	public void setCustomersByCity(List<Customer> customersByCity) {
		this.customersByCity = customersByCity;
	}

	public List<Customer> getSelectedsCustomer() {
		return selectedsCustomer;
	}

	public void setSelectedsCustomer(List<Customer> selectedsCustomer) {
		this.selectedsCustomer = selectedsCustomer;
	}

	public String getReportSelected() {
		return reportSelected;
	}

	public void setReportSelected(String reportSelected) {
		this.reportSelected = reportSelected;
	}
}
