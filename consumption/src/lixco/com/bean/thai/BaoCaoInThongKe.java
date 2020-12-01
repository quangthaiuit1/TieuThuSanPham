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

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;
import org.joda.time.LocalDate;
import org.omnifaces.cdi.ViewScoped;

import lixco.com.bean.thai.staticentity.Notification;
import lixco.com.entity.City;
import lixco.com.entity.Customer;
import lixco.com.entity.thai.CustomerNonEntity;
import lixco.com.entity.thai.DoanhThuKhachHang;
import lixco.com.entity.thai.SanLuongCuaKhachHang;
import lixco.com.entity.thai.SanPham;
import lixco.com.entity.thai.ThanhPhoSanPham;
import lixco.com.entity.thai.ThongKeTieuThu;
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

	@Inject
	private CityServiceThai CITY_SERVICE_THAI;
	@Inject
	private CustomerServiceThai CUSTOMER_SERVICE_THAI;
	@Inject
	private InvoiceServiceThai invoiceServiceThai;

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

			List<ThongKeTieuThu> list1 = invoiceServiceThai.getListReportThongKeTieuThu2(fromMonth, year);
			List<ThongKeTieuThu> list2 = invoiceServiceThai.getListReportThongKeTieuThu2(compareFromMonth, compareYear);

			// handle list -> report
			for (int i = 0; i < list1.size(); i++) {
				for (int j = 0; j < list2.size(); j++) {
					if (list1.get(i).getLoaiSanPhamId() == list2.get(j).getLoaiSanPhamId()
							&& list1.get(j).getLoaiXuatNhapId() == list2.get(j).getLoaiXuatNhapId()) {
						list1.get(i).setSlKySosanh(list2.get(j).getSlKyBaocao());
						list1.get(i).setDoanhthuKySosanh(list2.get(j).getDoanhthuKyBaocao());
					}
				}
			}

			// handle print pdf
			if (!list1.isEmpty()) {
				// report
				String reportPath = FacesContext.getCurrentInstance().getExternalContext()
						.getRealPath("/resources/reports/thai/inthongke/thongketieuthu.jasper");
				// check neu list rong~
				JRDataSource beanDataSource = new JRBeanCollectionDataSource(list1);
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
		} catch (

		Exception e) {
			e.printStackTrace();
		}
	}

	public void baoCaoThongKeSanLuongPDF() {
		try {
			// list de gan qua report
			List<ThongKeTieuThu> tktieuthus = handleDataThongKeTieuThu();
			sapxep(tktieuthus);
			// handle print pdf
			if (!tktieuthus.isEmpty()) {
				// report
				String reportPath = FacesContext.getCurrentInstance().getExternalContext()
						.getRealPath("/resources/reports/thai/inthongke/bcthongkesanxuat.jasper");
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// thong ke ma xn
	public void baoCaoThongKeMaXNPDF() {
		try {
			// list de gan qua report
			List<ThongKeTieuThu> tktieuthus = handleDataThongKeTieuThu();
			sapxep(tktieuthus);
			// handle print pdf
			if (!tktieuthus.isEmpty()) {
				// report
				String reportPath = FacesContext.getCurrentInstance().getExternalContext()
						.getRealPath("/resources/reports/thai/inthongke/thongke_maxn.jasper");
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
						.getRealPath("/resources/reports/thai/inthongke/bcthongkesanxuat.jasper");
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

	// thong ke tieu thu cua khach hang
	public void baoCaoThongKeTTKHPDF() {
		try {
			// list de gan qua report
			List<DoanhThuKhachHang> tktieuthus = handleDoanhThuKhachHang();
			// handle print pdf
			if (!tktieuthus.isEmpty()) {
				// report
				String reportPath = FacesContext.getCurrentInstance().getExternalContext()
						.getRealPath("/resources/reports/thai/inthongke/thongketieuthukhachhang.jasper");
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// thong ke san luong cua khach hang
	public void baoCaoThongKeSLKHPDF() {
		try {
			// list de gan qua report
			List<SanLuongCuaKhachHang> sls = handleSanLuongCuaKhachHang();
			// handle print pdf
			if (!sls.isEmpty()) {
				// report
				String reportPath = FacesContext.getCurrentInstance().getExternalContext()
						.getRealPath("/resources/reports/thai/inthongke/thongkesanluong.jasper");
				JRDataSource beanDataSource = new JRBeanCollectionDataSource(sls);
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void xuatPDF() {
		if (reportSelected.equals("TKTT")) {
			baoCaoThongKeTieuThuPDF();
			return;
		}
		if (reportSelected.equals("TKTTMAXN")) {
			baoCaoThongKeMaXNPDF();
		}
		if (reportSelected.equals("TKSX")) {
			baoCaoThongKeSanXuatPDF();
			return;
		}
		if (reportSelected.equals("TKSLCKH")) {
			baoCaoThongKeSLKHPDF();
			return;
		}
		if (reportSelected.equals("TKTTCKH")) {
			baoCaoThongKeTTKHPDF();
			return;
		}
		if (reportSelected.equals("TKTTTV")) {
			baoCaoThongKeMaXNPDF();
			return;
		}
		if (reportSelected.equals("TKDSNPP")) {
			baoCaoThongKeMaXNPDF();
			return;
		}
		if (reportSelected.equals("PTSL")) {
			baoCaoThongKeMaXNPDF();
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
		temp.setLoaiXuatNhap("BÁN HÀNG HORECA");
		temp.setLoaiSanPham("BỘT GIẶT");
		temp.setSlKyBaocao(5757);
		temp.setSlKySosanh(6514);
		temp.setDoanhthuKyBaocao(64480628);
		temp.setDoanhthuKySosanh(80310105);
		tktts.add(temp);
		temp = new ThongKeTieuThu();
		temp.setLoaiSanPham("BỘT GIẶT");
		temp.setLoaiXuatNhap("BÁN HÀNG HORECA (CTY)");
		temp.setSlKyBaocao(25745);
		temp.setSlKySosanh(15442);
		temp.setDoanhthuKyBaocao(420324127);
		temp.setDoanhthuKySosanh(222091481);
		tktts.add(temp);
		temp = new ThongKeTieuThu();
		temp.setLoaiSanPham("BỘT GIẶT");
		temp.setLoaiXuatNhap("BÁN HÀNG ONLINE");
		temp.setSlKyBaocao(25745);
		temp.setSlKySosanh(15442);
		temp.setDoanhthuKyBaocao(420324127);
		temp.setDoanhthuKySosanh(222091481);
		tktts.add(temp);
		temp = new ThongKeTieuThu();
		temp.setLoaiSanPham("BỘT GIẶT");
		temp.setLoaiXuatNhap("BÁN NỘI ĐỊA (SIÊU THỊ)");
		temp.setSlKyBaocao(25745);
		temp.setSlKySosanh(15442);
		temp.setDoanhthuKyBaocao(420324127);
		temp.setDoanhthuKySosanh(222091481);
		tktts.add(temp);
		temp = new ThongKeTieuThu();
		temp.setLoaiSanPham("BỘT GIẶT");
		temp.setLoaiXuatNhap("XUẤT KHẨU (LIX)");
		temp.setSlKyBaocao(243);
		temp.setSlKySosanh(486);
		temp.setDoanhthuKyBaocao(5873);
		temp.setDoanhthuKySosanh(8519);
		tktts.add(temp);

		temp = new ThongKeTieuThu();
		temp.setLoaiXuatNhap("HÀNG TRẢ LẠI (ND)");
		temp.setLoaiSanPham("CHẤT TẨY RỬA");
		temp.setSlKyBaocao(1268);
		temp.setSlKySosanh(1520);
		temp.setDoanhthuKyBaocao(8652);
		temp.setDoanhthuKySosanh(5555);
		tktts.add(temp);
		temp = new ThongKeTieuThu();
		temp.setLoaiSanPham("CHẤT TẨY RỬA");
		temp.setLoaiXuatNhap("BÁN HÀNG HORECA (CTY)");
		temp.setSlKyBaocao(274);
		temp.setSlKySosanh(526);
		temp.setDoanhthuKyBaocao(5707193);
		temp.setDoanhthuKySosanh(10867096);
		tktts.add(temp);

		temp = new ThongKeTieuThu();
		temp.setLoaiSanPham("BỘT GIẶT");
		temp.setLoaiXuatNhap("XUẤT HÀNG TRƯNG BÀY");
		temp.setSlKyBaocao(4255);
		temp.setSlKySosanh(4558);
		temp.setDoanhthuKyBaocao(5873);
		temp.setDoanhthuKySosanh(8519);
		tktts.add(temp);
		temp = new ThongKeTieuThu();
		temp.setLoaiXuatNhap("BÁN HÀNG ONLINE");
		temp.setLoaiSanPham("CHẤT TẨY RỬA");
		temp.setSlKyBaocao(732);
		temp.setSlKySosanh(480);
		temp.setDoanhthuKyBaocao(11391348);
		temp.setDoanhthuKySosanh(7488000);
		tktts.add(temp);

		temp = new ThongKeTieuThu();
		temp.setLoaiSanPham("CHẤT TẨY RỬA");
		temp.setLoaiXuatNhap("BÁN NỘI ĐỊA (SIÊU THỊ)");
		temp.setSlKyBaocao(36432);
		temp.setSlKySosanh(5472);
		temp.setDoanhthuKyBaocao(782053308);
		temp.setDoanhthuKySosanh(78610116);
		tktts.add(temp);
		temp = new ThongKeTieuThu();
		temp.setLoaiSanPham("CHẤT TẨY RỬA");
		temp.setLoaiXuatNhap("XUẤT GIỚI THIỆU SP");
		temp.setSlKyBaocao(44);
		temp.setSlKySosanh(71);
		temp.setDoanhthuKyBaocao(836000);
		temp.setDoanhthuKySosanh(1134825);
		tktts.add(temp);
		temp = new ThongKeTieuThu();
		temp.setLoaiSanPham("CHẤT TẨY RỬA");
		temp.setLoaiXuatNhap("BÁN NĐỊA");
		temp.setSlKyBaocao(57237);
		temp.setSlKySosanh(61731);
		temp.setDoanhthuKyBaocao(730362535);
		temp.setDoanhthuKySosanh(817804146);
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

	public List<SanLuongCuaKhachHang> handleSanLuongCuaKhachHang() {
		List<SanLuongCuaKhachHang> sls = new ArrayList<>();
		SanLuongCuaKhachHang sl1 = new SanLuongCuaKhachHang();
		sl1.setTenKhachHang("AEON BÌNH DƯƠNG");
		sl1.setLoaiSanPham("NƯỚC GIẶT");
		sl1.setKyBaoCao(0.0);
		sl1.setKySoSanh(3318);
		sl1.setTangTruong(-100);
		sls.add(sl1);

		sl1 = new SanLuongCuaKhachHang();
		sl1.setTenKhachHang("AEON BÌNH DƯƠNG");
		sl1.setLoaiSanPham("NƯỚC LÀM MỀM VẢI");
		sl1.setKyBaoCao(0.0);
		sl1.setKySoSanh(3578);
		sl1.setTangTruong(-100);
		sls.add(sl1);

		sl1 = new SanLuongCuaKhachHang();
		sl1.setTenKhachHang("AEON BÌNH DƯƠNG");
		sl1.setLoaiSanPham("NƯỚC RỬA CHÉN");
		sl1.setKyBaoCao(0.0);
		sl1.setKySoSanh(6418);
		sl1.setTangTruong(-100);
		sls.add(sl1);

		sl1 = new SanLuongCuaKhachHang();
		sl1.setTenKhachHang("AEON TÂN PHÚ");
		sl1.setLoaiSanPham("NƯỚC RỬA CHÉN");
		sl1.setKyBaoCao(0.0);
		sl1.setKySoSanh(1228);
		sl1.setTangTruong(-100);
		sls.add(sl1);

		sl1 = new SanLuongCuaKhachHang();
		sl1.setTenKhachHang("AEON TÂN PHÚ");
		sl1.setLoaiSanPham("NƯỚC LAU SÀN");
		sl1.setKyBaoCao(0.0);
		sl1.setKySoSanh(3738);
		sl1.setTangTruong(-100);
		sls.add(sl1);
		return sls;
	}

	public List<DoanhThuKhachHang> handleDoanhThuKhachHang() {
		List<DoanhThuKhachHang> dtkhs = new ArrayList<>();
		DoanhThuKhachHang d = new DoanhThuKhachHang("TRUNG TÂM PHÂN PHỐI SÀI GÒN CO.OP", 543636, 354532, 8);
		dtkhs.add(d);
		d = new DoanhThuKhachHang("CÔNG TY TINH CONDOR", 125400, 0, 8);
		dtkhs.add(d);
		d = new DoanhThuKhachHang("CHI NHÁNH CÔNG TY TINH TOYOTA TSUSHO", 477081, 474988, 2);
		dtkhs.add(d);
		d = new DoanhThuKhachHang("AEON BÌNH DƯƠNG", 277081, 374988, -20);
		dtkhs.add(d);
		d = new DoanhThuKhachHang("LOTTE TÂN PHÚ", 357081, 324988, 2);
		dtkhs.add(d);
		d = new DoanhThuKhachHang("CÔNG TY CỔ PHẦN DƯỢC PHẨM PHARMACITY", 477081, 474988, 2);
		dtkhs.add(d);
		d = new DoanhThuKhachHang("PHÂN XƯỞNG CÔNG NGHỆ", 152081, 254988, -30);
		dtkhs.add(d);
		d = new DoanhThuKhachHang("SEVEN SYSTEM VN JSC", 367081, 3274988, 4);
		dtkhs.add(d);
		return dtkhs;
	}
}

