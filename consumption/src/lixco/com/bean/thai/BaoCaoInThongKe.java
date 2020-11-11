package lixco.com.bean.thai;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.jboss.logging.Logger;
import org.joda.time.LocalDate;

import lixco.com.entity.City;
import lixco.com.entity.Customer;
import lixco.com.entity.thai.CustomerNonEntity;
import lixco.com.entity.thai.ThongKeTieuThu;
import lixco.com.service.thai.CityServiceThai;
import lixco.com.service.thai.CustomerServiceThai;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
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
	private List<CustomerNonEntity> customersByCity;
	// private List<Customer> customersByCity;
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

	public void baoCaoThongKeTieuThuPDF() {
		try {
			// list de gan qua report
			List<ThongKeTieuThu> tktieuthus = handleDataThongKeTieuThu();
			sapxep(tktieuthus);
			// handle print pdf
			if (!tktieuthus.isEmpty()) {
				// report
				String reportPath = FacesContext.getCurrentInstance().getExternalContext()
						.getRealPath("/resources/reports/thai/thongketieuthu.jasper");
				// check neu list rong~
				if (!tktieuthus.isEmpty()) {
					JRDataSource beanDataSource = new JRBeanCollectionDataSource(tktieuthus);
					Map<String, Object> importParam = new HashMap<String, Object>();
					String image = FacesContext.getCurrentInstance().getExternalContext()
							.getRealPath("/resources/gfx/lixco_logo.png");
					importParam.put("logo", image);
					importParam.put("month", fromMonth);
					importParam.put("year", year);
					importParam.put("sovoi_month", compareFromMonth);
					importParam.put("sovoi_year", compareYear);
					JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, importParam, beanDataSource);
					FacesContext facesContext = FacesContext.getCurrentInstance();
					OutputStream outputStream;
					outputStream = facesContext.getExternalContext().getResponseOutputStream();
					JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
					facesContext.responseComplete();
				} else {
					Notification.NOTI_ERROR("Không có dữ liệu");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void baoCaoThongKeSanXuatPDF() {
		try {
			// list de gan qua report
			List<ThongKeTieuThu> tktieuthus = handleDataThongKeTieuThu();
			sapxep(tktieuthus);
			// handle print pdf
			if (!tktieuthus.isEmpty()) {
				// report
				String reportPath = FacesContext.getCurrentInstance().getExternalContext()
						.getRealPath("/resources/reports/thai/bcthongkesanxuat.jasper");
				// check neu list rong~
				if (!tktieuthus.isEmpty()) {
					JRDataSource beanDataSource = new JRBeanCollectionDataSource(tktieuthus);
					Map<String, Object> importParam = new HashMap<String, Object>();
					String image = FacesContext.getCurrentInstance().getExternalContext()
							.getRealPath("/resources/gfx/lixco_logo.png");
					importParam.put("logo", image);
					importParam.put("month", fromMonth);
					importParam.put("year", year);
					importParam.put("sovoi_month", compareFromMonth);
					importParam.put("sovoi_year", compareYear);
					JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, importParam, beanDataSource);
					FacesContext facesContext = FacesContext.getCurrentInstance();
					OutputStream outputStream;
					outputStream = facesContext.getExternalContext().getResponseOutputStream();
					JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
					facesContext.responseComplete();
				} else {
					Notification.NOTI_ERROR("Không có dữ liệu");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void xuatPDF() {
		if (reportSelected.equals("TKTT")) {
			baoCaoThongKeTieuThuPDF();
			return;
		}
		if (reportSelected.equals("TKSX")) {
			baoCaoThongKeSanXuatPDF();
			return;
		}
	}

	private List<ThongKeTieuThu> sapxep(List<ThongKeTieuThu> items) {
		try {
			Collections.sort(items, new Comparator<ThongKeTieuThu>() {
				@Override
				public int compare(ThongKeTieuThu sv1, ThongKeTieuThu sv2) {
					try {
						boolean ngay = sv1.getLoaiSanPham().equals(sv2.getLoaiSanPham());
						if (ngay) {
							return -1;
						}
						return 1;
					} catch (Exception e) {
						return -1;
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return items;
	}

	// private List<OrderFoodReport> sapxep(List<OrderFoodReport> items) {
	// try {
	// Collections.sort(items, new Comparator<OrderFoodReport>() {
	// @Override
	// public int compare(OrderFoodReport sv1, OrderFoodReport sv2) {
	// try {
	// boolean ngay =
	// sv1.getRegistrationDate().equals(sv2.getRegistrationDate());
	// if (ngay) {
	// if (sv1.getShift() > sv2.getShift()) {
	// return 1;
	// } else if (sv1.getShift() == sv2.getShift()) {
	// return sv1.getFoodName().compareTo(sv2.getFoodName());
	// } else {
	// return -1;
	// }
	// } else {
	// boolean ngayss =
	// sv1.getRegistrationDate().before(sv2.getRegistrationDate());
	// if (ngayss)
	// return -1;
	// return 1;
	// }
	// } catch (Exception e) {
	// return -1;
	// }
	// }
	// });
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return items;
	// }

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

	// public List<Customer> getCustomersByCity() {
	// return customersByCity;
	// }
	//
	// public void setCustomersByCity(List<Customer> customersByCity) {
	// this.customersByCity = customersByCity;
	// }

	public List<Customer> getSelectedsCustomer() {
		return selectedsCustomer;
	}

	public List<CustomerNonEntity> getCustomersByCity() {
		return customersByCity;
	}

	public void setCustomersByCity(List<CustomerNonEntity> customersByCity) {
		this.customersByCity = customersByCity;
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

	// handle du lieu mau thong ke tieu thu
	public List<ThongKeTieuThu> handleDataThongKeTieuThu() {
		List<ThongKeTieuThu> tktts = new ArrayList<>();
		ThongKeTieuThu temp = new ThongKeTieuThu();
		temp.setProductName("BÁN HÀNG HORECA");
		temp.setLoaiSanPham("BỘT GIẶT");
		temp.setSlKyBaocao(5757);
		temp.setSlKySosanh(6514);
		temp.setDoanhthuKyBaocao(64480628);
		temp.setDoanhthuKySosanh(80310105);
		tktts.add(temp);
		temp = new ThongKeTieuThu();
		temp.setLoaiSanPham("BỘT GIẶT");
		temp.setProductName("BÁN HÀNG HORECA (CTY)");
		temp.setSlKyBaocao(25745);
		temp.setSlKySosanh(15442);
		temp.setDoanhthuKyBaocao(420324127);
		temp.setDoanhthuKySosanh(222091481);
		tktts.add(temp);
		temp = new ThongKeTieuThu();
		temp.setLoaiSanPham("BỘT GIẶT");
		temp.setProductName("BÁN HÀNG ONLINE");
		temp.setSlKyBaocao(25745);
		temp.setSlKySosanh(15442);
		temp.setDoanhthuKyBaocao(420324127);
		temp.setDoanhthuKySosanh(222091481);
		tktts.add(temp);
		temp = new ThongKeTieuThu();
		temp.setLoaiSanPham("BỘT GIẶT");
		temp.setProductName("BÁN NỘI ĐỊA (SIÊU THỊ)");
		temp.setSlKyBaocao(25745);
		temp.setSlKySosanh(15442);
		temp.setDoanhthuKyBaocao(420324127);
		temp.setDoanhthuKySosanh(222091481);
		tktts.add(temp);
		temp = new ThongKeTieuThu();
		temp.setLoaiSanPham("BỘT GIẶT");
		temp.setProductName("XUẤT KHẨU (LIX)");
		temp.setSlKyBaocao(243);
		temp.setSlKySosanh(486);
		temp.setDoanhthuKyBaocao(5873);
		temp.setDoanhthuKySosanh(8519);
		tktts.add(temp);

		temp = new ThongKeTieuThu();
		temp.setProductName("HÀNG TRẢ LẠI (ND)");
		temp.setLoaiSanPham("CHẤT TẨY RỬA");
		temp.setSlKyBaocao(1268);
		temp.setSlKySosanh(1520);
		temp.setDoanhthuKyBaocao(8652);
		temp.setDoanhthuKySosanh(5555);
		tktts.add(temp);
		temp = new ThongKeTieuThu();
		temp.setLoaiSanPham("CHẤT TẨY RỬA");
		temp.setProductName("BÁN HÀNG HORECA (CTY)");
		temp.setSlKyBaocao(274);
		temp.setSlKySosanh(526);
		temp.setDoanhthuKyBaocao(5707193);
		temp.setDoanhthuKySosanh(10867096);
		tktts.add(temp);

		temp = new ThongKeTieuThu();
		temp.setLoaiSanPham("BỘT GIẶT");
		temp.setProductName("XUẤT HÀNG TRƯNG BÀY");
		temp.setSlKyBaocao(4255);
		temp.setSlKySosanh(4558);
		temp.setDoanhthuKyBaocao(5873);
		temp.setDoanhthuKySosanh(8519);
		tktts.add(temp);
		temp = new ThongKeTieuThu();
		temp.setProductName("BÁN HÀNG ONLINE");
		temp.setLoaiSanPham("CHẤT TẨY RỬA");
		temp.setSlKyBaocao(732);
		temp.setSlKySosanh(480);
		temp.setDoanhthuKyBaocao(11391348);
		temp.setDoanhthuKySosanh(7488000);
		tktts.add(temp);

		temp = new ThongKeTieuThu();
		temp.setLoaiSanPham("CHẤT TẨY RỬA");
		temp.setProductName("BÁN NỘI ĐỊA (SIÊU THỊ)");
		temp.setSlKyBaocao(36432);
		temp.setSlKySosanh(5472);
		temp.setDoanhthuKyBaocao(782053308);
		temp.setDoanhthuKySosanh(78610116);
		tktts.add(temp);
		temp = new ThongKeTieuThu();
		temp.setLoaiSanPham("CHẤT TẨY RỬA");
		temp.setProductName("XUẤT GIỚI THIỆU SP");
		temp.setSlKyBaocao(44);
		temp.setSlKySosanh(71);
		temp.setDoanhthuKyBaocao(836000);
		temp.setDoanhthuKySosanh(1134825);
		tktts.add(temp);
		temp = new ThongKeTieuThu();
		temp.setLoaiSanPham("CHẤT TẨY RỬA");
		temp.setProductName("BÁN NĐỊA");
		temp.setSlKyBaocao(57237);
		temp.setSlKySosanh(61731);
		temp.setDoanhthuKyBaocao(730362535);
		temp.setDoanhthuKySosanh(817804146);
		tktts.add(temp);

		return tktts;
	}
}
