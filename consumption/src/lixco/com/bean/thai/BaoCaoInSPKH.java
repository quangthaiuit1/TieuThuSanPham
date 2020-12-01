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
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;
import org.joda.time.LocalDate;
import org.omnifaces.cdi.ViewScoped;

import lixco.com.bean.thai.staticentity.DateUtil;
import lixco.com.bean.thai.staticentity.Notification;
import lixco.com.entity.City;
import lixco.com.entity.Customer;
import lixco.com.entity.CustomerTypes;
import lixco.com.entity.thai.CustomerNonEntity;
import lixco.com.entity.thai.SanPham;
import lixco.com.entity.thai.TKSPThang1;
import lixco.com.entity.thai.TKSPThang1Test;
import lixco.com.entity.thai.ThanhPhoSanPham;
import lixco.com.entity.thai.ThongKeTieuThu;
import lixco.com.entity.thai.ThongKeTieuThu2;
import lixco.com.entity.thai.ThongKeTieuThuTheoVung;
import lixco.com.service.thai.CityServiceThai;
import lixco.com.service.thai.CustomerServiceThai;
import lixco.com.service.thai.CustomerTypeServiceThai;
import lixco.com.service.thai.InvoiceServiceThai;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import trong.lixco.com.bean.AbstractBean;

@Named
@ViewScoped
public class BaoCaoInSPKH extends AbstractBean {

	private static final long serialVersionUID = 1L;
	private Date startDate;
	private Date endDate;
	private int week;
	private int yearOfWeek;
	private int compareFromMonth;
	private int year;
	private int compareYear;
	private List<City> allCity;
	private City citySelected;
	private List<CustomerNonEntity> customersByCity;
	private List<CustomerTypes> customerTypes;
	private CustomerTypes customerTypeSelected;
	// private List<Customer> customersByCity;
	private List<Customer> selectedsCustomer;
	private String reportSelected;

	private Date fromDate;
	private Date toDate;

	@Inject
	private CityServiceThai CITY_SERVICE_THAI;
	@Inject
	private CustomerServiceThai CUSTOMER_SERVICE_THAI;
	@Inject
	private InvoiceServiceThai INVOICE_SERVICE_THAI;

	@Inject
	private CustomerTypeServiceThai CUSTOMER_TYPE_SERVICE_THAI;

	@Override
	protected void initItem() {
		Calendar cal = Calendar.getInstance();
		compareFromMonth = cal.get(Calendar.MONTH) + 1;
		Date currentDate00 = new Date();
		// currentDate00 = DateUtil.SET_HHMMSS_00(currentDate00);
		currentDate00 = DateUtil.DATE_WITHOUT_TIME(currentDate00);
		fromDate = currentDate00;
		toDate = currentDate00;
		year = cal.get(Calendar.YEAR);
		compareYear = cal.get(Calendar.YEAR);
		allCity = CITY_SERVICE_THAI.findAll();
		customerTypes = CUSTOMER_TYPE_SERVICE_THAI.findAll();
	}

	public void ajaxChangeMonth() {

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

	public void ajaxHandleShowCustomerByType() {
		try {
			customersByCity = CUSTOMER_SERVICE_THAI.findByCity(citySelected.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void xuatPDF() {
		if (reportSelected.equals("XTSP")) {
			baoCaoTKXuatTheoSPPDF();
			return;
		}
		if (reportSelected.equals("DTKH")) {
			baoCaoTKXuatTheoSP2PDF();
			return;
		}
	}

	private void baoCaoTKXuatTheoSP2PDF() {
		try {
			// list de gan qua report
			// List<TKSPThang1Test> tktieuthus = handleDataTKSPThang1();
			// sapxep(tktieuthus);
			// sapxepSPThang2(tktieuthus);

			List<String> customers = new ArrayList<>();
			customers.add("1080");
			// list de gan qua report
			List<TKSPThang1> tktieuthus = INVOICE_SERVICE_THAI.findByCustomer2(customers);
			// sapxep(tktieuthus);

			tktieuthus.sort(Comparator.comparing(TKSPThang1::getLoaiSanPham).thenComparing(TKSPThang1::getSanPham));
			// tinh thanh tien va thue GTTG
			for (int i = 0; i < tktieuthus.size(); i++) {
				tktieuthus.get(i).setThanhTien(tktieuthus.get(i).getSoLuong() * tktieuthus.get(i).getDonGia());
				tktieuthus.get(i).setThueTGGT(tktieuthus.get(i).getThanhTien() * tktieuthus.get(i).getHeSoThue());
			}
			// handle print pdf
			if (!tktieuthus.isEmpty()) {
				// report
				String reportPath = FacesContext.getCurrentInstance().getExternalContext()
						.getRealPath("/resources/reports/thai/insanpham/tongket_sp_thang_2.jasper");
				// check neu list rong~
				if (!tktieuthus.isEmpty()) {
					JRDataSource beanDataSource = new JRBeanCollectionDataSource(tktieuthus);
					Map<String, Object> importParam = new HashMap<String, Object>();
					String image = FacesContext.getCurrentInstance().getExternalContext()
							.getRealPath("/resources/gfx/lixco_logo.png");
					importParam.put("logo", image);
					importParam.put("month", compareFromMonth);
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

	private void baoCaoTKXuatTheoSPPDF() {
		try {
			List<String> customers = new ArrayList<>();
			customers.add("1080");
			// list de gan qua report
			List<TKSPThang1> tktieuthus = INVOICE_SERVICE_THAI.findByCustomer(customers);
			// sapxep(tktieuthus);

			tktieuthus.sort(Comparator.comparing(TKSPThang1::getLoaiSanPham));

			for (int i = 0; i < tktieuthus.size(); i++) {
				tktieuthus.get(i).setThanhTien(tktieuthus.get(i).getSoLuong() * tktieuthus.get(i).getDonGia());
				tktieuthus.get(i).setThueTGGT(tktieuthus.get(i).getThanhTien() * tktieuthus.get(i).getHeSoThue());
				tktieuthus.get(i).setTongTien(tktieuthus.get(i).getThanhTien() + tktieuthus.get(i).getThueTGGT());
			}

			// handle print pdf
			if (!tktieuthus.isEmpty()) {
				// report
				String reportPath = FacesContext.getCurrentInstance().getExternalContext()
						.getRealPath("/resources/reports/thai/insanpham/tongket_sp_thang_1.jasper");
				// check neu list rong~
				if (!tktieuthus.isEmpty()) {
					JRDataSource beanDataSource = new JRBeanCollectionDataSource(tktieuthus);
					Map<String, Object> importParam = new HashMap<String, Object>();
					String image = FacesContext.getCurrentInstance().getExternalContext()
							.getRealPath("/resources/gfx/lixco_logo.png");
					importParam.put("logo", image);
					importParam.put("month", compareFromMonth);
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

	public int getCompareFromMonth() {
		return compareFromMonth;
	}

	public void setCompareFromMonth(int compareFromMonth) {
		this.compareFromMonth = compareFromMonth;
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

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public List<CustomerTypes> getCustomerTypes() {
		return customerTypes;
	}

	public void setCustomerTypes(List<CustomerTypes> customerTypes) {
		this.customerTypes = customerTypes;
	}

	public CustomerTypes getCustomerTypeSelected() {
		return customerTypeSelected;
	}

	public void setCustomerTypeSelected(CustomerTypes customerTypeSelected) {
		this.customerTypeSelected = customerTypeSelected;
	}

	// handle du lieu mau thong ke tieu thu
	public List<TKSPThang1Test> handleDataTKSPThang1() {
		List<TKSPThang1Test> tktts = new ArrayList<>();
		TKSPThang1Test temp = new TKSPThang1Test();
		// temp.setLoaiXuatNhap("BÁN HÀNG HORECA");
		temp.setLoaiSanPham("BỘT GIẶT");
		temp.setLoaiXuatNhap("BÁN NỘI ĐỊA");
		temp.setLoaiXuatNhapId(1);
		temp.setLoaiSanPhamId(1);
		temp.setSanPham("BG CO-OP 6KG/03");
		temp.setSanPhamId(1);
		temp.setSoLuong(5757);
		// temp.setSlKySosanh(6514);
		temp.setThanhTien(64480628);
		temp.setThueTGGT(64480628 * 0.1);
		tktts.add(temp);

		temp = new TKSPThang1Test();
		// temp.setLoaiXuatNhap("BÁN HÀNG HORECA");
		temp.setLoaiSanPham("BỘT GIẶT");
		temp.setLoaiSanPhamId(1);
		temp.setLoaiXuatNhap("XUẤT KHẨU (LIX)");
		temp.setLoaiXuatNhapId(2);
		temp.setSanPham("BG CO-OP 900G/12");
		temp.setSanPhamId(2);
		temp.setSoLuong(6514);
		// temp.setSlKySosanh(6514);
		temp.setThanhTien(5380628);
		temp.setThueTGGT(5380628 * 0.1);
		tktts.add(temp);

		temp = new TKSPThang1Test();
		// temp.setLoaiXuatNhap("BÁN HÀNG HORECA");
		temp.setLoaiSanPham("NƯỚC RỬA TAY");
		temp.setLoaiXuatNhap("BÁN NỘI ĐỊA");
		temp.setLoaiXuatNhapId(1);
		temp.setLoaiSanPhamId(2);
		temp.setSanPham("DD RỬA TAY KHÔ ON1 BAMBOO 500ML");
		temp.setSanPhamId(3);
		temp.setSoLuong(2414);
		// temp.setSlKySosanh(6514);
		temp.setThanhTien(12580628);
		temp.setThueTGGT(12580628 * 0.1);
		tktts.add(temp);

		temp = new TKSPThang1Test();
		// temp.setLoaiXuatNhap("BÁN HÀNG HORECA");
		temp.setLoaiSanPham("NƯỚC RỬA TAY");
		temp.setLoaiSanPhamId(2);
		temp.setLoaiXuatNhap("BÁN NỘI ĐỊA");
		temp.setLoaiXuatNhapId(1);
		temp.setSanPham("DD RỬA TAY KHÔ ON1 FRESH SAKURA");
		temp.setSanPhamId(4);
		temp.setSoLuong(2157);
		temp.setThanhTien(26280628);
		temp.setThueTGGT(26280628 * 0.1);
		tktts.add(temp);

		temp = new TKSPThang1Test();
		// temp.setLoaiXuatNhap("BÁN HÀNG HORECA");
		temp.setLoaiSanPham("NƯỚC LAU KÍNH");
		temp.setLoaiSanPhamId(3);
		temp.setLoaiXuatNhap("BÁN HÀNG ONLINE");
		temp.setLoaiXuatNhapId(3);
		temp.setSanPham("NLK LOTTE 5L/C03");
		temp.setSanPhamId(5);
		temp.setSoLuong(3213);
		// temp.setSlKySosanh(6514);
		temp.setThanhTien(78580628);
		temp.setThueTGGT(78580628 * 0.1);
		tktts.add(temp);

		temp = new TKSPThang1Test();
		// temp.setLoaiXuatNhap("BÁN HÀNG HORECA");
		temp.setLoaiSanPham("NƯỚC LAU KÍNH");
		temp.setLoaiSanPhamId(3);
		temp.setLoaiXuatNhap("BÁN HÀNG ONLINE");
		temp.setLoaiXuatNhapId(3);
		temp.setSanPham("NLK IZI HOME 650L/C03");
		temp.setSanPhamId(6);
		temp.setSoLuong(6557);
		// temp.setSlKySosanh(6514);
		temp.setThanhTien(24380628);
		temp.setThueTGGT(24380628 * 0.1);
		tktts.add(temp);
		// temp = new TKSPThang1();
		// // temp.setLoaiXuatNhap("BÁN HÀNG HORECA");
		// temp.setLoaiSanPham("BỘT GIẶT");
		// temp.setLoaiSanPhamId(1);
		// temp.setSanPham("BG CO-OP 6KG/03");
		// temp.setSoLuong(5757);
		// // temp.setSlKySosanh(6514);
		// temp.setThanhTien(64480628);
		// temp.setThueTGGT(64480628 * 0.1);
		// tktts.add(temp);
		//
		// temp = new TKSPThang1();
		// // temp.setLoaiXuatNhap("BÁN HÀNG HORECA");
		// temp.setLoaiSanPham("BỘT GIẶT");
		// temp.setLoaiSanPhamId(1);
		// temp.setSanPham("BG CO-OP 6KG/03");
		// temp.setSoLuong(5757);
		// // temp.setSlKySosanh(6514);
		// temp.setThanhTien(64480628);
		// temp.setThueTGGT(64480628 * 0.1);
		// tktts.add(temp);
		// temp = new TKSPThang1();
		// // temp.setLoaiXuatNhap("BÁN HÀNG HORECA");
		// temp.setLoaiSanPham("BỘT GIẶT");
		// temp.setLoaiSanPhamId(1);
		// temp.setSanPham("BG CO-OP 6KG/03");
		// temp.setSoLuong(5757);
		// // temp.setSlKySosanh(6514);
		// temp.setThanhTien(64480628);
		// temp.setThueTGGT(64480628 * 0.1);
		// tktts.add(temp);
		// temp = new TKSPThang1();
		// // temp.setLoaiXuatNhap("BÁN HÀNG HORECA");
		// temp.setLoaiSanPham("BỘT GIẶT");
		// temp.setLoaiSanPhamId(1);
		// temp.setSanPham("BG CO-OP 6KG/03");
		// temp.setSoLuong(5757);
		// // temp.setSlKySosanh(6514);
		// temp.setThanhTien(64480628);
		// temp.setThueTGGT(64480628 * 0.1);
		// tktts.add(temp);
		// temp = new TKSPThang1();
		// // temp.setLoaiXuatNhap("BÁN HÀNG HORECA");
		// temp.setLoaiSanPham("BỘT GIẶT");
		// temp.setLoaiSanPhamId(1);
		// temp.setSanPham("BG CO-OP 6KG/03");
		// temp.setSoLuong(5757);
		// // temp.setSlKySosanh(6514);
		// temp.setThanhTien(64480628);
		// temp.setThueTGGT(64480628 * 0.1);
		// tktts.add(temp);
		// temp = new TKSPThang1();
		// // temp.setLoaiXuatNhap("BÁN HÀNG HORECA");
		// temp.setLoaiSanPham("BỘT GIẶT");
		// temp.setLoaiSanPhamId(1);
		// temp.setSanPham("BG CO-OP 6KG/03");
		// temp.setSoLuong(5757);
		// // temp.setSlKySosanh(6514);
		// temp.setThanhTien(64480628);
		// temp.setThueTGGT(64480628 * 0.1);
		// tktts.add(temp);

		temp = new TKSPThang1Test();
		// temp.setLoaiXuatNhap("BÁN HÀNG HORECA");
		temp.setLoaiSanPham("BỘT GIẶT");
		temp.setLoaiSanPhamId(1);
		temp.setLoaiXuatNhap("XUẤT KHẨU (LIX)");
		temp.setLoaiXuatNhapId(2);
		temp.setSanPham("BG CO-OP 500G/12");
		temp.setSanPhamId(2);
		temp.setSoLuong(2514);
		// temp.setSlKySosanh(6514);
		temp.setThanhTien(3380628);
		temp.setThueTGGT(3380628 * 0.1);
		tktts.add(temp);

		temp = new TKSPThang1Test();
		// temp.setLoaiXuatNhap("BÁN HÀNG HORECA");
		temp.setLoaiSanPham("NƯỚC RỬA TAY");
		temp.setLoaiSanPhamId(2);
		temp.setSanPham("DD RỬA TAY KHÔ ON1 FRESH SAKURA");
		temp.setSanPhamId(7);
		temp.setLoaiXuatNhap("BÁN ONLINE");
		temp.setLoaiXuatNhapId(3);
		temp.setSoLuong(2157);
		// temp.setSlKySosanh(6514);
		temp.setThanhTien(33280628);
		temp.setThueTGGT(33280628 * 0.1);
		tktts.add(temp);

		return tktts;
	}

	public List<ThongKeTieuThuTheoVung> handleDataThongKeKhuVuc() {
		List<ThongKeTieuThuTheoVung> tktts = new ArrayList<>();
		List<SanPham> sanphams = new ArrayList<>();
		List<ThanhPhoSanPham> sanphamtheotp = new ArrayList<>();
		SanPham s = new SanPham();
		s.setKhuvuc("CHÂU Á");
		s.setThanhpho("BRUNEI");
		s.setTensanpham("NƯỚC RỬA CHÉN");
		s.setKybaocao(3000);
		s.setKysosanh(203769);
		sanphams.add(s);
		s = new SanPham();
		s.setKhuvuc("CHÂU Á");
		s.setThanhpho("BRUNEI");
		s.setTensanpham("CÁC LOẠI SP KHÁC");
		s.setKybaocao(2500);
		s.setKysosanh(3200);
		sanphams.add(s);
		s = new SanPham();
		s.setKhuvuc("CHÂU ÂU");
		s.setThanhpho("LUÂN ĐÔN");
		s.setTensanpham("BỘT GIẶT");
		s.setKybaocao(1200);
		s.setKysosanh(1500);
		sanphams.add(s);
		s = new SanPham();
		s.setKhuvuc("CHÂU Á");
		s.setThanhpho("MIANMA");
		s.setTensanpham("BỘT GIẶT");
		s.setKybaocao(3500);
		s.setKysosanh(1200);
		sanphams.add(s);
		s = new SanPham();
		s.setKhuvuc("CHÂU ÂU");
		s.setThanhpho("PARIS");
		s.setTensanpham("BỘT GIẶT");
		s.setKybaocao(1800);
		s.setKysosanh(3800);
		sanphams.add(s);
		s = new SanPham();
		s.setKhuvuc("CHÂU ÂU");
		s.setThanhpho("PARIS");
		s.setTensanpham("NƯỚC RỬA CHÉN");
		s.setKybaocao(1500);
		s.setKysosanh(3200);
		sanphams.add(s);

		boolean isFirst = false;
		for (int i = 0; i < sanphams.size(); i++) {
			if (!isFirst) {
				ThanhPhoSanPham tp = new ThanhPhoSanPham();
				tp.setThanhpho(sanphams.get(i).getThanhpho());
				List<SanPham> sp = new ArrayList<>();
				sp.add(sanphams.get(i));
				tp.setSanphams(sp);
				ThongKeTieuThuTheoVung tktt = new ThongKeTieuThuTheoVung();
				tktt.setKhuvuc(sanphams.get(i).getKhuvuc());
				List<ThanhPhoSanPham> tps = new ArrayList<>();
				tps.add(tp);
				tktt.setSanphamtheotp(tps);
				tktts.add(tktt);
				isFirst = true;
			} else {

			}
		}
		return null;
	}
}
