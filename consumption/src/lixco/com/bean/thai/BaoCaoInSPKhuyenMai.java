package lixco.com.bean.thai;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;
import org.joda.time.LocalDate;
import org.omnifaces.cdi.ViewScoped;

import lixco.com.bean.thai.staticentity.Notification;
import lixco.com.entity.City;
import lixco.com.entity.Customer;
import lixco.com.entity.thai.ChenhLechKhuyenMaiTest;
import lixco.com.entity.thai.ChiPhiHoaHongTest;
import lixco.com.entity.thai.CustomerNonEntity;
import lixco.com.entity.thai.SanPham;
import lixco.com.entity.thai.TKSPThang1;
import lixco.com.entity.thai.TKSPThang1Test;
import lixco.com.entity.thai.ThanhPhoSanPham;
import lixco.com.entity.thai.ThongKeTieuThuTheoVung;
import lixco.com.service.thai.CityServiceThai;
import lixco.com.service.thai.CustomerServiceThai;
import lixco.com.service.thai.InvoiceServiceThai;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import trong.lixco.com.bean.AbstractBean;

@Named
@ViewScoped
public class BaoCaoInSPKhuyenMai extends AbstractBean {

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

	@Inject
	private CityServiceThai CITY_SERVICE_THAI;
	@Inject
	private CustomerServiceThai CUSTOMER_SERVICE_THAI;
	@Inject
	private InvoiceServiceThai INVOICE_SERVICE_THAI;

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

	public void xuatPDF() {
		if (reportSelected.equals("CPHH")) {
			baoCaoChiPhiHoaHongPDF();
			return;
		}
		if (reportSelected.equals("THXKM")) {
			baoCaoTongHopXuatKhuyenMaiPDF();
			return;
		}
		if (reportSelected.equals("KTKM")) {
			baoCaoKiemTraChenhLechKhuyenMaiPDF();
			return;
		}
	}

	private void baoCaoChiPhiHoaHongPDF() {
		try {
			List<ChiPhiHoaHongTest> cphhs = handleDataChiPhiHoaHong();

//			cphhs.sort(Comparator.comparing(TKSPThang1::getLoaiSanPham).thenComparing(TKSPThang1::getSanPham));
			// tinh thanh tien va thue GTTG
			for (int i = 0; i < cphhs.size(); i++) {
				cphhs.get(i).setSlbdvaslctrdl(cphhs.get(i).getSlbg() + cphhs.get(i).getSlctrdl());
				cphhs.get(i).setTongdoanhthubgvactrdl(cphhs.get(i).getDtbgtruocvat() + cphhs.get(i).getDtctrdltruocvat());
			}
			// handle print pdf
			if (!cphhs.isEmpty()) {
				// report
				String reportPath = FacesContext.getCurrentInstance().getExternalContext()
						.getRealPath("/resources/reports/thai/insanphamkhuyenmai/chiphihoahong.jasper");
				// check neu list rong~
				if (!cphhs.isEmpty()) {
					JRDataSource beanDataSource = new JRBeanCollectionDataSource(cphhs);
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

	private void baoCaoTongHopXuatKhuyenMaiPDF() {
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
						.getRealPath("/resources/reports/thai/insanphamkhuyenmai/tonghopkhuyenmai.jasper");
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
	
	private void baoCaoKiemTraChenhLechKhuyenMaiPDF() {
		try {
			List<ChenhLechKhuyenMaiTest> clkm = handleDataKiemTraChenhLech();

			clkm.sort(Comparator.comparing(ChenhLechKhuyenMaiTest::getSodh_daco));


			// handle print pdf
			if (!clkm.isEmpty()) {
				// report
				String reportPath = FacesContext.getCurrentInstance().getExternalContext()
						.getRealPath("/resources/reports/thai/insanphamkhuyenmai/ktchenhlechkm.jasper");
				// check neu list rong~
				if (!clkm.isEmpty()) {
					JRDataSource beanDataSource = new JRBeanCollectionDataSource(clkm);
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
		public List<ChiPhiHoaHongTest> handleDataChiPhiHoaHong() {
			List<ChiPhiHoaHongTest> tktts = new ArrayList<>();
			ChiPhiHoaHongTest temp = new ChiPhiHoaHongTest();
			// temp.setLoaiXuatNhap("BÁN HÀNG HORECA");
			temp.setTenkhachhang("BIG C AN LẠC");
			temp.setSlbg(334);
			temp.setSlctrdl(4335);
			temp.setDtbgtruocvat(7231);
			temp.setDtctrdltruocvat(4216);
			temp.setNodauthang(16000);
			temp.setNocuoithang(1375);
			tktts.add(temp);

			temp = new ChiPhiHoaHongTest();
			// temp.setLoaiXuatNhap("BÁN HÀNG HORECA");
			temp.setTenkhachhang("BIG C AN PHÚ");
			temp.setSlbg(334);
			temp.setSlctrdl(4335);
			temp.setDtbgtruocvat(7231);
			temp.setDtctrdltruocvat(1376);
			temp.setNodauthang(1600);
			temp.setNocuoithang(6777);
			tktts.add(temp);
			
			temp = new ChiPhiHoaHongTest();
			// temp.setLoaiXuatNhap("BÁN HÀNG HORECA");
			temp.setTenkhachhang("BIG C BÌNH DƯƠNG");
			temp.setSlbg(334);
			temp.setSlctrdl(4335);
			temp.setDtbgtruocvat(71321);
			temp.setDtctrdltruocvat(43876);
			temp.setNodauthang(4300);
			temp.setNocuoithang(6177);
			tktts.add(temp);
			
			temp = new ChiPhiHoaHongTest();
			// temp.setLoaiXuatNhap("BÁN HÀNG HORECA");
			temp.setTenkhachhang("BIG C CẦN THƠ");
			temp.setSlbg(334);
			temp.setSlctrdl(4335);
			temp.setDtbgtruocvat(7231);
			temp.setDtctrdltruocvat(4876);
			temp.setNodauthang(1600);
			temp.setNocuoithang(6177);
			tktts.add(temp);

			temp = new ChiPhiHoaHongTest();
			// temp.setLoaiXuatNhap("BÁN HÀNG HORECA");
			temp.setTenkhachhang("BIG C DĨ AN");
			temp.setSlbg(334);
			temp.setSlctrdl(4335);
			temp.setDtbgtruocvat(7231);
			temp.setDtctrdltruocvat(4876);
			temp.setNodauthang(4800);
			temp.setNocuoithang(84777);
			tktts.add(temp);
			
			temp = new ChiPhiHoaHongTest();
			// temp.setLoaiXuatNhap("BÁN HÀNG HORECA");
			temp.setTenkhachhang("BIG C ĐÀ LẠT");
			temp.setSlbg(334);
			temp.setSlctrdl(4335);
			temp.setDtbgtruocvat(7221);
			temp.setDtctrdltruocvat(4276);
			temp.setNodauthang(1687);
			temp.setNocuoithang(6135);
			tktts.add(temp);
			
			return tktts;
		}

	// handle du lieu mau thong ke tieu thu
	public List<ChenhLechKhuyenMaiTest> handleDataKiemTraChenhLech() {
		List<ChenhLechKhuyenMaiTest> tktts = new ArrayList<>();
		ChenhLechKhuyenMaiTest temp = new ChenhLechKhuyenMaiTest();
		// temp.setLoaiXuatNhap("BÁN HÀNG HORECA");
		Date currentDate = new Date();
		temp.setNgay(currentDate);
		temp.setTenkhachhang("TRUNG TÂM PHÂN PHỐI SÀI GÒN COOP");
		temp.setSodh_daco("2020-06/00394");
		temp.setMasp_daco("NTD01");
		temp.setSoluong_daco(84);
		tktts.add(temp);

		temp = new ChenhLechKhuyenMaiTest();
		// temp.setLoaiXuatNhap("BÁN HÀNG HORECA");
		temp.setNgay(currentDate);
		temp.setTenkhachhang("TRUNG TÂM PHÂN PHỐI BÌNH DƯƠNG/ KHO VỆ TINH");
		temp.setSodh_daco("2020-06/00394");
		temp.setMasp_daco("N402");
		temp.setSoluong_daco(504);
		tktts.add(temp);
		
		temp = new ChenhLechKhuyenMaiTest();
		// temp.setLoaiXuatNhap("BÁN HÀNG HORECA");
		temp.setNgay(currentDate);
		temp.setTenkhachhang("TRUNG TÂM PHÂN PHỐI BÌNH DƯƠNG/ KHO VỆ TINH");
		temp.setSodh_daco("2020-06/00395");
		temp.setMasp_daco("NTD06");
		temp.setSoluong_daco(252);
		tktts.add(temp);
		
		temp = new ChenhLechKhuyenMaiTest();
		// temp.setLoaiXuatNhap("BÁN HÀNG HORECA");
		temp.setNgay(currentDate);
		temp.setTenkhachhang("TRUNG TÂM PHÂN PHỐI SÀI GÒN COOP");
		temp.setSodh_daco("2020-06/00622");
		temp.setMasp_daco("NTD01");
		temp.setSoluong_daco(1920);
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
