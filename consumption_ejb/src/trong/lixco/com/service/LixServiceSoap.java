package trong.lixco.com.service;
//package hong.lixco.com.service;
//
//import java.io.File;
//import java.io.InputStream;
//import java.nio.file.Files;
//import java.text.SimpleDateFormat;
//import java.time.LocalDateTime;
//import java.time.temporal.WeekFields;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//import java.util.Properties;
//import java.util.stream.Collectors;
//
//import javax.annotation.Resource;
//import javax.ejb.SessionContext;
//import javax.ejb.Stateless;
//import javax.ejb.TransactionAttribute;
//import javax.ejb.TransactionAttributeType;
//import javax.ejb.TransactionManagement;
//import javax.ejb.TransactionManagementType;
//import javax.inject.Inject;
//import javax.jws.WebMethod;
//import javax.jws.WebParam;
//import javax.jws.WebService;
//import javax.jws.soap.SOAPBinding;
//import javax.mail.Authenticator;
//import javax.mail.Message;
//import javax.mail.Multipart;
//import javax.mail.PasswordAuthentication;
//import javax.mail.Session;
//import javax.mail.Transport;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeBodyPart;
//import javax.mail.internet.MimeMessage;
//import javax.mail.internet.MimeMultipart;
//import javax.mail.internet.MimeUtility;
//import javax.persistence.EntityManager;
//import javax.persistence.TypedQuery;
//import javax.persistence.criteria.CriteriaBuilder;
//import javax.persistence.criteria.CriteriaQuery;
//import javax.persistence.criteria.Predicate;
//import javax.persistence.criteria.Root;
//import javax.persistence.criteria.Subquery;
//
//import net.sf.jasperreports.engine.JRDataSource;
//import net.sf.jasperreports.engine.JasperExportManager;
//import net.sf.jasperreports.engine.JasperFillManager;
//import net.sf.jasperreports.engine.JasperPrint;
//import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
//
//import org.apache.commons.codec.binary.Base64;
//import org.apache.commons.io.FileUtils;
//import org.apache.commons.io.filefilter.WildcardFileFilter;
//import org.jboss.logging.Logger;
//
//import trong.lixco.com.ejb.service.dathang.IDNoticeDMSService;
//import trong.lixco.com.entity.AccountLix;
//import trong.lixco.com.entity.AddressShop;
//import trong.lixco.com.entity.CheckingShopLix;
//import trong.lixco.com.entity.CityCus;
//import trong.lixco.com.entity.CoordinatesMember;
//import trong.lixco.com.entity.CountyCus;
//import trong.lixco.com.entity.ExportAgentLix;
//import trong.lixco.com.entity.ExportAgentLixDetail;
//import trong.lixco.com.entity.FollowLocalMember;
//import trong.lixco.com.entity.FollowSaleEveryday;
//import trong.lixco.com.entity.GuildCus;
//import trong.lixco.com.entity.IDNoticeDMS;
//import trong.lixco.com.entity.MailSystemLix;
//import trong.lixco.com.entity.MemberLix;
//import trong.lixco.com.entity.MyDoc;
//import trong.lixco.com.entity.NoticeDMS;
//import trong.lixco.com.entity.OrderDetailLix;
//import trong.lixco.com.entity.OrderLix;
//import trong.lixco.com.entity.ParamInfor;
//import trong.lixco.com.entity.ProductCategoryGroupLix;
//import trong.lixco.com.entity.ProductCategoryLix;
//import trong.lixco.com.entity.ProductLix;
//import trong.lixco.com.entity.ServerMailLix;
//import trong.lixco.com.entity.ShopLix;
//import trong.lixco.com.entity.SystemInfor;
//import trong.lixco.com.entity.TargetLix;
//import trong.lixco.com.entity.WorkFollowDetailLix;
//import trong.lixco.com.entity.WorkFollowLix;
//import trong.lixco.com.service.program.FollowSaleEverydayService;
//import trong.lixco.com.service.program.PathImageShopService;
//import trong.lixco.com.service.program.TargetLixService;
//import trong.lixco.com.service.program.WorkFollowLixService;
//import trong.lixco.com.util.InforTarget;
//import trong.lixco.com.util.OrderReport;
//
//import com.google.gson.Gson;
//
///**
// * Neu xay ra loi khong tim thay thu vien thi copy thu vien .jar vao ear/lib Vi
// * du: Gson-2.8.0
// * 
// * @author vantrong
// *
// */
//
//@Stateless
//@TransactionManagement(TransactionManagementType.CONTAINER)
//@WebService(name = "httplixServiceSoap", targetNamespace = "http://httplixServiceSoap/")
//@SOAPBinding(style = SOAPBinding.Style.DOCUMENT)
//public class LixServiceSoap {
//
//	@Inject
//	private EntityManager em;
//	@Inject
//	private Logger logger;
//	@Resource
//	private SessionContext ct;
//	private double DISTANCE = 1; // 1000 meter: Khoang cach tao don hang
//	static SimpleDateFormat ddmmyyyy;
//	static {
//		ddmmyyyy = new SimpleDateFormat("dd/MM/yyyy");
//	}
//
//	// Kiem tra khoan cach de tao don hang
//	@TransactionAttribute(TransactionAttributeType.REQUIRED)
//	@WebMethod(operationName = "changePassword", action = "changePassword")
//	public boolean changePassword(@WebParam(name = "iduser") long iduser, @WebParam(name = "oldpass") String oldpass,
//			@WebParam(name = "newpass") String newpass) {
//		try {
//			MemberLix mem = em.find(MemberLix.class, iduser);
//			if (mem != null) {
//				String password = mem.getAccountLix().getPassword();
//				if (password.equals(oldpass)) {
//					mem.getAccountLix().setPassword(newpass);
//					em.merge(mem.getAccountLix());
//					return true;
//				} else {
//					return false;
//				}
//			} else {
//				return false;
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			ct.setRollbackOnly();
//			return false;
//		}
//	}
//
//	@WebMethod(operationName = "locationSale", action = "locationSale")
//	public FollowLocalMember locationSale(@WebParam(name = "idsale") long idsale) {
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<Object> cq = cb.createQuery(Object.class);
//		Root<FollowLocalMember> root = cq.from(FollowLocalMember.class);
//		cq.multiselect(cb.max(root.get("id"))).where(cb.equal(root.get("memberLix").get("id"), idsale),
//				cb.equal(root.get("disable"), false));
//		TypedQuery<Object> query = em.createQuery(cq);
//		Object result = query.getSingleResult();
//		if (result != null)
//			return em.find(FollowLocalMember.class, (long) result);
//		return new FollowLocalMember();
//	}
//
//	@WebMethod(operationName = "locationMem", action = "locationMem")
//	public List<FollowLocalMember> locationMem(@WebParam(name = "idsale") long idsale) {
//		Date dates = new Date();
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<FollowLocalMember> cq = cb.createQuery(FollowLocalMember.class);
//		Root<FollowLocalMember> root = cq.from(FollowLocalMember.class);
//
//		cq.select(root).where(cb.equal(root.get("memberLix").get("id"), idsale),
//				cb.equal(cb.function("year", Integer.class, root.get("date")), dates.getYear() + 1900),
//				cb.equal(cb.function("month", Integer.class, root.get("date")), dates.getMonth() + 1),
//				cb.equal(cb.function("day", Integer.class, root.get("date")), dates.getDate()),
//				cb.equal(root.get("memberLix").get("disable"), false));
//
//		TypedQuery<FollowLocalMember> query = em.createQuery(cq);
//		List<FollowLocalMember> resuls = query.getResultList();
//		return resuls;
//	}
//
//	@WebMethod(operationName = "reportOrder", action = "reportOrder")
//	public String reportOrder(@WebParam(name = "orderId") long orderId) {
//		OrderLix orderLix = em.find(OrderLix.class, orderId);
//		JRDataSource beanDataSource = null;
//		Map<String, Object> importParam = new HashMap<String, Object>();
//		SimpleDateFormat sf = new SimpleDateFormat("d/M/yyyy");
//		try {
//			List<OrderDetailLix> orderDetailLixs;
//			String reportPath = "resources/indonhangtheocuahang.jasper";
//			orderDetailLixs = findOrder(orderLix);
//
//			InputStream in = this.getClass().getClassLoader().getResourceAsStream(reportPath);
//			if (orderDetailLixs.size() == 0) {
//				return "";
//			} else {
//				beanDataSource = new JRBeanCollectionDataSource(orderDetailLixs);
//				importParam.put("titleDate", "Ä�Æ N HÃ€NG NGÃ€Y " + sf.format(orderLix.getDate()));
//				importParam.put("datefooter", new Date());
//				JasperPrint jasperPrint = JasperFillManager.fillReport(in, importParam, beanDataSource);
//				byte[] result = JasperExportManager.exportReportToPdf(jasperPrint);
//				return (result != null) ? java.util.Base64.getEncoder().encodeToString(result) : "";
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			return "";
//		}
//
//	}
//
//	@WebMethod(operationName = "reportOrderList", action = "reportOrderList")
//	public String reportOrderList(@WebParam(name = "iduser") long iduser, @WebParam(name = "date") String date,
//			@WebParam(name = "typeEx") String typeEx) {
//
//		JRDataSource beanDataSource = null;
//		Map<String, Object> importParam = new HashMap<String, Object>();
//		SimpleDateFormat sf = new SimpleDateFormat("d/M/yyyy");
//		try {
//
//			Date d = null;
//			try {
//				d = sf.parse(date);
//			} catch (Exception e) {
//				// TODO: handle exception
//			}
//			MemberLix mem = em.find(MemberLix.class, iduser);
//			List<OrderDetailLix> orderDetailLixs;
//			String reportPath = "resources/indonhang.jasper";
//			if ("khachhang".equals(typeEx)) {
//				reportPath = "resources/indonhangtheocuahang.jasper";
//				orderDetailLixs = findCustomSortCus(d, null, null, mem, null);
//			} else {
//				orderDetailLixs = findCustomSortProd(d, null, null, mem, null);
//			}
//
//			InputStream in = this.getClass().getClassLoader().getResourceAsStream(reportPath);
//			if (orderDetailLixs.size() == 0) {
//				return "";
//			} else {
//				beanDataSource = new JRBeanCollectionDataSource(orderDetailLixs);
//				importParam.put("titleDate", "Ä�Æ N HÃ€NG NGÃ€Y " + date);
//				importParam.put("datefooter", new Date());
//				JasperPrint jasperPrint = JasperFillManager.fillReport(in, importParam, beanDataSource);
//				byte[] result = JasperExportManager.exportReportToPdf(jasperPrint);
//				return (result != null) ? java.util.Base64.getEncoder().encodeToString(result) : "";
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			return "";
//		}
//
//	}
//
//	public List<OrderDetailLix> findCustomSortProd(Date date, ProductLix productLix, ShopLix shopLix,
//			MemberLix memberLixSale, MemberLix memberLixMan) {
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
//		List<Predicate> predicatesParent = new LinkedList<Predicate>();
//		Root<OrderDetailLix> root = cq.from(OrderDetailLix.class);
//
//		// Subquery
//		List<Predicate> predicates = new LinkedList<Predicate>();
//		Subquery<OrderLix> sqOne = cq.subquery(OrderLix.class);
//		Root<OrderLix> root2 = sqOne.from(OrderLix.class);
//		// Condition Month,Year
//		Predicate predicateStart = cb.greaterThanOrEqualTo(root2.get("date"), date);
//
//		predicates.add(predicateStart);
//		if (shopLix != null) {
//			Predicate predicateShop = cb.equal(root2.get("shopLix"), shopLix);
//			predicates.add(predicateShop);
//		}
//		if (memberLixSale != null) {
//			installMemberList(memberLixSale.getId());
//			if (memberLixs != null && memberLixs.size() != 0) {
//				Predicate predicateSale = cb.in(root2.get("memberLix")).value(memberLixs);
//				predicates.add(predicateSale);
//			}
//		}
//		if (memberLixMan != null) {
//			installMemberList(memberLixMan.getId());
//			if (memberLixs != null && memberLixs.size() != 0) {
//				Predicate predicateMan = cb.in(root2.get("memberLix")).value(memberLixs);
//				predicates.add(predicateMan);
//			}
//		}
//
//		// query
//		sqOne.where(cb.and(predicates.toArray(new Predicate[0])));
//		sqOne.select(root2);
//
//		Predicate predicateParent = cb.equal(root.get("orderLix"), cb.any(sqOne));
//		predicatesParent.add(predicateParent);
//		if (productLix != null && productLix.getId() != null) {
//			Predicate predicatePro = cb.equal(root.get("productLix"), productLix);
//			predicatesParent.add(predicatePro);
//		}
//
//		List<OrderDetailLix> fcs = new ArrayList<OrderDetailLix>();
//		cq.multiselect(root.get("orderLix"), root.get("productLix"), root.get("quantity"))
//				.where(cb.and(predicatesParent.toArray(new Predicate[0])))
//				.orderBy(cb.asc(root.get("productLix").get("name")),
//						cb.asc(root.get("orderLix").get("shopLix").get("name")));
//
//		List<Object[]> valueArray = em.createQuery(cq).getResultList();
//
//		for (Object[] values : valueArray) {
//			final OrderLix orderLix = (OrderLix) values[0];
//			final ProductLix prd = (ProductLix) values[1];
//			final double quantity = (Double) values[2];
//
//			OrderDetailLix fc = new OrderDetailLix();
//			fc.setOrderLix(orderLix);
//			fc.setProductLix(prd);
//			fc.setQuantity(quantity);
//
//			fcs.add(fc);
//		}
//		return fcs;
//
//	}
//
//	public List<OrderDetailLix> findOrder(OrderLix orderLix) {
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<OrderDetailLix> cq = cb.createQuery(OrderDetailLix.class);
//		Root<OrderDetailLix> root = cq.from(OrderDetailLix.class);
//		cq.select(root).where(cb.equal(root.get("orderLix"), orderLix));
//		return em.createQuery(cq).getResultList();
//	}
//
//	public List<OrderDetailLix> findCustomSortCus(Date date, ProductLix productLix, ShopLix shopLix,
//			MemberLix memberLixSale, MemberLix memberLixMan) {
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
//		List<Predicate> predicatesParent = new LinkedList<Predicate>();
//		Root<OrderDetailLix> root = cq.from(OrderDetailLix.class);
//
//		// Subquery
//		List<Predicate> predicates = new LinkedList<Predicate>();
//		Subquery<OrderLix> sqOne = cq.subquery(OrderLix.class);
//		Root<OrderLix> root2 = sqOne.from(OrderLix.class);
//		// Condition Month,Year
//		Predicate predicateStart = cb.greaterThanOrEqualTo(root2.get("date"), date);
//
//		predicates.add(predicateStart);
//		if (shopLix != null) {
//			Predicate predicateShop = cb.equal(root2.get("shopLix"), shopLix);
//			predicates.add(predicateShop);
//		}
//		if (memberLixSale != null) {
//			installMemberList(memberLixSale.getId());
//			if (memberLixs != null && memberLixs.size() != 0) {
//				Predicate predicateSale = cb.in(root2.get("memberLix")).value(memberLixs);
//				predicates.add(predicateSale);
//			}
//		}
//		if (memberLixMan != null) {
//			installMemberList(memberLixMan.getId());
//			if (memberLixs != null && memberLixs.size() != 0) {
//				Predicate predicateMan = cb.in(root2.get("memberLix")).value(memberLixs);
//				predicates.add(predicateMan);
//			}
//		}
//
//		// query
//		sqOne.where(cb.and(predicates.toArray(new Predicate[0])));
//		sqOne.select(root2);
//
//		Predicate predicateParent = cb.equal(root.get("orderLix"), cb.any(sqOne));
//		predicatesParent.add(predicateParent);
//		if (productLix != null && productLix.getId() != null) {
//			Predicate predicatePro = cb.equal(root.get("productLix"), productLix);
//			predicatesParent.add(predicatePro);
//		}
//
//		List<OrderDetailLix> fcs = new ArrayList<OrderDetailLix>();
//		cq.multiselect(root.get("orderLix"), root.get("productLix"), root.get("quantity"), root.get("note"))
//				.where(cb.and(predicatesParent.toArray(new Predicate[0])))
//				.orderBy(cb.asc(root.get("orderLix").get("shopLix").get("name")),
//						cb.asc(root.get("productLix").get("name")));
//
//		List<Object[]> valueArray = em.createQuery(cq).getResultList();
//
//		for (Object[] values : valueArray) {
//			final OrderLix orderLix = (OrderLix) values[0];
//			final ProductLix prd = (ProductLix) values[1];
//			final double quantity = (Double) values[2];
//			final String note = (String) values[3];
//
//			OrderDetailLix fc = new OrderDetailLix();
//			fc.setOrderLix(orderLix);
//			fc.setProductLix(prd);
//			fc.setQuantity(quantity);
//			fc.setNote(note);
//
//			fcs.add(fc);
//		}
//		return fcs;
//
//	}
//
//	private List<MemberLix> memberLixs2;
//	private void installMemberList(long iduser) {
//		MemberLix memberLix = em.find(MemberLix.class, iduser);
//		memberLixs = new ArrayList<MemberLix>();
//		memberLixs.add(memberLix);
//		showAllMemberChild2(iduser);
//
//	}
//	private void showAllMemberChild2(long idMember) {
//		List<MemberLix> memberLixs = listMemberByMemberParent2(idMember);
//		for (int i = 0; i < memberLixs.size(); i++) {
//			this.memberLixs.add(memberLixs.get(i));
//			showAllMemberChild2(memberLixs.get(i).getId());
//		}
//	}
//
//	private List<MemberLix> listMemberByMemberParent2(long iduser) {
//		// lay danh sach member theo idmembercha
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<MemberLix> cq = cb.createQuery(MemberLix.class);
//		Root<MemberLix> root = cq.from(MemberLix.class);
//		cq.select(root).where(cb.equal(root.get("memParent").get("id"), iduser));
//		TypedQuery<MemberLix> query = em.createQuery(cq);
//		List<MemberLix> results = query.getResultList();
//		return results;
//	}
//
//	private List<MemberLix> memberLixAlls;
//
//	@WebMethod(operationName = "showAllSubMemberOneLevel", action = "showAllSubMemberOneLevel")
//	public List<MemberLix> showAllSubMemberOneLevel(@WebParam(name = "idparent") long idparent) {
//		memberLixAlls = new ArrayList<MemberLix>();
//		showAllSubMemberChildOneLevel(idparent);
//		return memberLixAlls;
//	}
//
//	public void showAllSubMemberChildOneLevel(long idMember) {
//		List<MemberLix> memberLixs = memberByMemberParent(idMember);
//		for (int i = 0; i < memberLixs.size(); i++) {
//			// memberLixs.get(i).setAgentLix(null);
//			this.memberLixAlls.add(memberLixs.get(i));
//		}
//	}
//
//	@WebMethod(operationName = "showAllSubMember", action = "showAllSubMember")
//	public List<MemberLix> showAllSubMember(@WebParam(name = "idparent") long idparent) {
//		memberLixAlls = new ArrayList<MemberLix>();
//		showAllSubMemberChild(idparent);
//		return memberLixAlls;
//	}
//
//	public void showAllSubMemberChild(long idMember) {
//		List<MemberLix> memberLixs = memberByMemberParent(idMember);
//		for (int i = 0; i < memberLixs.size(); i++) {
//			this.memberLixAlls.add(memberLixs.get(i));
//			showAllSubMemberChild(memberLixs.get(i).getId());
//		}
//	}
//
//	public List<MemberLix> memberByMemberParent(long iduser) {
//		// lay danh sach member theo idmembercha
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<MemberLix> cq = cb.createQuery(MemberLix.class);
//		Root<MemberLix> root = cq.from(MemberLix.class);
//		cq.select(root).where(cb.equal(root.get("memParent").get("id"), iduser), cb.equal(root.get("disable"), false));
//		TypedQuery<MemberLix> query = em.createQuery(cq);
//		List<MemberLix> results = query.getResultList();
//		return results;
//	}
//
//	/**
//	 * Ket thuc Danh sach tuyen ban hang theo user 25-09-2017
//	 */
//
//	// Xoa chi tiet tuyen
//	@TransactionAttribute(TransactionAttributeType.REQUIRED)
//	@WebMethod(operationName = "deleteWorkFollowDetailLix", action = "deleteWorkFollowDetailLix")
//	public boolean deleteWorkFollowDetailLix(@WebParam(name = "id") long id) {
//		try {
//			WorkFollowDetailLix wfd = em.find(WorkFollowDetailLix.class, id);
//			em.remove(wfd);
//			em.flush();
//			return true;
//		} catch (Exception e) {
//			return false;
//		}
//	}
//
//	/*
//	 * Dong bo chi tiet tuyen
//	 */
//
//	@Inject
//	private FollowSaleEverydayService followSaleEverydayService;
//
//	@TransactionAttribute(TransactionAttributeType.REQUIRED)
//	@WebMethod(operationName = "syncWFD", action = "syncWFD")
//	public boolean syncWFD(@WebParam(name = "idwfd") long idwfd, @WebParam(name = "check") boolean check,
//			@WebParam(name = "date") String date) {
//		SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy kk:mm");
//		try {
//			WorkFollowDetailLix wfd = em.find(WorkFollowDetailLix.class, idwfd);
//			if (wfd != null) {
//				wfd.setTimeSync(new Date());
//				wfd.setChecking(check);
//				Date da = null;
//				try {
//					da = sf.parse(date);
//				} catch (Exception e) {
//				}
//				if (da != null) {
//					wfd.setDateChecking(da);
//					em.merge(wfd);
//					try {
//						FollowSaleEveryday fls = followSaleEverydayService.findRange(wfd.getWorkFollowLix()
//								.getMemberSale(), da, wfd.getShopLix());
//						if (fls == null) {
//							FollowSaleEveryday fle = new FollowSaleEveryday();
//							fle.setVisit(wfd.isChecking());
//							fle.setDate(sf.parse(date));
//							fle.setMemberLix(wfd.getWorkFollowLix().getMemberSale());
//							fle.setShopLix(wfd.getShopLix());
//							followSaleEverydayService.create(fle);
//						} else {
//							fls.setDate(sf.parse(date));
//							followSaleEverydayService.update(fls);
//						}
//
//					} catch (Exception e) {
//					}
//				}
//
//				em.flush();
//				return true;
//			} else {
//				System.out.println("Khong tÃ¬m thay wfd: " + idwfd);
//				return false;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			ct.setRollbackOnly();
//			return false;
//		}
//	}
//
//	// Xoa tuyen
//	@TransactionAttribute(TransactionAttributeType.REQUIRED)
//	@WebMethod(operationName = "deleteWorkFollowLix", action = "deleteWorkFollowLix")
//	public boolean deleteWorkFollowLix(@WebParam(name = "id") long id) {
//		try {
//			WorkFollowLix wf = em.find(WorkFollowLix.class, id);
//			em.remove(wf);
//			em.flush();
//			return true;
//		} catch (Exception e) {
//			return false;
//		}
//	}
//
//	// Luu/Cap nhat tuyen
//	@TransactionAttribute(TransactionAttributeType.REQUIRED)
//	@WebMethod(operationName = "createUpdateWorkFollowLix", action = "createUpdateWorkFollowLix")
//	public WorkFollowLix createUpdateWorkFollowLix(@WebParam(name = "id") long id,
//			@WebParam(name = "name") String name, @WebParam(name = "note") String note,
//			@WebParam(name = "idsale") long idsale, @WebParam(name = "date") String date,
//			@WebParam(name = "iduser") long iduser, @WebParam(name = "day") String day) {
//
//		try {
//			SimpleDateFormat sf = new SimpleDateFormat("d/M/yyyy");
//			if (id != -1) {
//				WorkFollowLix workFollowLix = em.find(WorkFollowLix.class, id);
//				MemberLix memberSale = em.find(MemberLix.class, idsale);
//				if (workFollowLix != null) {
//					workFollowLix.setName(name);
//					workFollowLix.setNote(note);
//					workFollowLix.setDate(sf.parse(date));
//					workFollowLix.setMemberSale(memberSale);
//					workFollowLix.setDay(day);
//					workFollowLix = em.merge(workFollowLix);
//					return workFollowLix;
//				} else {
//					return null;
//				}
//
//			} else {
//				MemberLix memberSale = em.find(MemberLix.class, idsale);
//				WorkFollowLix workFollowLix = new WorkFollowLix();
//				workFollowLix.setIduse(iduser);
//				workFollowLix.setCode(ajaxSelectWorkFollowLixCode());
//				workFollowLix.setName(name);
//				workFollowLix.setDate(sf.parse(date));
//				workFollowLix.setMemberSale(memberSale);
//				workFollowLix.setNote(note);
//				workFollowLix.setDay(day);
//				em.persist(workFollowLix);
//				em.flush();
//				return workFollowLix;
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			ct.setRollbackOnly();
//			return null;
//		}
//	}
//
//	/*
//	 * @param numberStart la so ky tu dem (vd:001 la 3, 0001 la 4)
//	 */
//	public WorkFollowLix showWorkFollowLixMaxId() {
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<WorkFollowLix> cq = cb.createQuery(WorkFollowLix.class);
//		Root<WorkFollowLix> root = cq.from(WorkFollowLix.class);
//		cq.select(root).orderBy(cb.desc(root.get("id")));
//		TypedQuery<WorkFollowLix> query = em.createQuery(cq).setFirstResult(0);
//		List<WorkFollowLix> results = query.getResultList();
//		if (results.size() > 0) {
//			return results.get(0);
//		} else {
//			return null;
//		}
//	}
//
//	public String ajaxSelectWorkFollowLixCode() {
//		WorkFollowLix workFollowLix = showWorkFollowLixMaxId();
//		if (workFollowLix != null) {
//			String code = workFollowLix.getCode();
//			int temp = Integer.parseInt(code.substring(2, code.length()));
//			String result = "OG" + String.format("%08d", temp + 1);
//			return result;
//		} else {
//			return "OG" + String.format("%08d", 1);
//		}
//
//	}
//
//	// Luu vi tri
//	@TransactionAttribute(TransactionAttributeType.REQUIRED)
//	@WebMethod(operationName = "saveLocationMyLocal", action = "saveLocationMyLocal")
//	public boolean saveLocationMyLocal(@WebParam(name = "idUser") long idUser,
//			@WebParam(name = "longitude") double longitude, @WebParam(name = "latitude") double latitude,
//			@WebParam(name = "pin") String pin) {
//		try {
//			MemberLix memberLix = em.find(MemberLix.class, idUser);
//			if (memberLix != null) {
//
//				CoordinatesMember coordinatesMember = new CoordinatesMember();
//				coordinatesMember.setMlongitude(longitude);
//				coordinatesMember.setMlatitude(latitude);
//				coordinatesMember.setPin(pin);
//				em.persist(coordinatesMember);
//				coordinatesMember = em.merge(coordinatesMember);
//
//				FollowLocalMember followLocalMember = new FollowLocalMember();
//				followLocalMember.setDate(new Date());
//				followLocalMember.setMemberLix(memberLix);
//				followLocalMember.setCoordinatesMember(coordinatesMember);
//				em.persist(followLocalMember);
//				em.flush();
//
//				return true;
//			} else {
//				return false;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			ct.setRollbackOnly();
//			return false;
//		}
//	}
//
//	@WebMethod(operationName = "check_version", action = "check_version")
//	public String check_version() {
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<ParamInfor> cq = cb.createQuery(ParamInfor.class);
//		Root<ParamInfor> root = cq.from(ParamInfor.class);
//		cq.select(root).where(cb.equal(root.get("name"), "timeUpdateFile"));
//		TypedQuery<ParamInfor> query = em.createQuery(cq);
//		List<ParamInfor> results = query.getResultList();
//		if (results.size() != 0) {
//			return results.get(0).getVersion() + "";
//		} else {
//			return "1f";
//		}
//	}
//
//	@WebMethod(operationName = "systeminfor", action = "systeminfor")
//	public SystemInfor systeminfor() {
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<SystemInfor> cq = cb.createQuery(SystemInfor.class);
//		Root<SystemInfor> root = cq.from(SystemInfor.class);
//		cq.select(root);
//		TypedQuery<SystemInfor> query = em.createQuery(cq);
//		List<SystemInfor> results = query.getResultList();
//		if (results.size() != 0) {
//			return results.get(0);
//		} else {
//			return new SystemInfor();
//		}
//	}
//
//	// Theo doi memberlix
//	public FollowLocalMember findfllocaByUser(MemberLix memberLix) {
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<FollowLocalMember> cq = cb.createQuery(FollowLocalMember.class);
//		Root<FollowLocalMember> root = cq.from(FollowLocalMember.class);
//		cq.select(root).where(cb.equal(root.get("memberLix"), memberLix));
//		TypedQuery<FollowLocalMember> query = em.createQuery(cq);
//		List<FollowLocalMember> results = query.getResultList();
//		if (results.size() != 0) {
//			return results.get(0);
//		}
//		return null;
//	}
//
//	/**
//	 * Don hang
//	 */
//	// Xoa chi tiet don hang
//	@TransactionAttribute(TransactionAttributeType.REQUIRED)
//	@WebMethod(operationName = "deleteOrderDetail", action = "deleteOrderDetail")
//	public boolean deleteOrderDetail(@WebParam(name = "idOrderD") long idOrderD) {
//		try {
//			OrderDetailLix orderDetailLix = em.find(OrderDetailLix.class, idOrderD);
//			em.remove(orderDetailLix);
//			em.flush();
//			return true;
//		} catch (Exception e) {
//			return false;
//		}
//	}
//
//	@WebMethod(operationName = "refreshOrder", action = "refreshOrder")
//	public OrderLix refreshOrder(@WebParam(name = "idOrder") long idOrder) {
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<OrderLix> cq = cb.createQuery(OrderLix.class);
//		Root<OrderLix> root = cq.from(OrderLix.class);
//		cq.select(root).where(cb.equal(root.get("id"), idOrder));
//		TypedQuery<OrderLix> query = em.createQuery(cq);
//		List<OrderLix> results = query.getResultList();
//		if (results.size() != 0) {
//			return results.get(0);
//		}
//		return null;
//	}
//
//	// Luu/Cap nhat don hang
//	@TransactionAttribute(TransactionAttributeType.REQUIRED)
//	@WebMethod(operationName = "createUpdateOrderLix", action = "createUpdateOrderLix")
//	public OrderLix createUpdateOrderLix(@WebParam(name = "id") long id, @WebParam(name = "idUser") long idUser,
//			@WebParam(name = "idShop") long idShop, @WebParam(name = "code") String code,
//			@WebParam(name = "date") String date, @WebParam(name = "noteOrder") String noteOrder,
//			@WebParam(name = "checkOut") boolean checkOut, @WebParam(name = "orderDetailLixs") String orderDetailLixs) {
//
//		try {
//			SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy kk:mm");
//			OrderLix orderLix = em.find(OrderLix.class, id);
//			if (orderLix == null) {
//				orderLix = new OrderLix();
//				orderLix.setTimeSync(new Date());
//				orderLix.setCode(code);
//				orderLix.setCheckOut(checkOut);
//				try {
//					orderLix.setDate(sf.parse(date));
//				} catch (Exception e) {
//					orderLix.setDate(new Date());
//				}
//				ShopLix shopLix = em.find(ShopLix.class, idShop);
//				orderLix.setShopLix(shopLix);
//				MemberLix memberLix = em.find(MemberLix.class, idUser);
//				orderLix.setMemberLix(memberLix);
//				orderLix.setNote(noteOrder);
//				em.persist(orderLix);
//				orderLix = em.merge(orderLix);
//
//			} else {
//				orderLix.setTimeSync(new Date());
//				orderLix.setCode(code);
//				orderLix.setCheckOut(checkOut);
//				try {
//					orderLix.setDate(sf.parse(date));
//				} catch (Exception e) {
//					orderLix.setDate(new Date());
//				}
//				ShopLix shopLix = em.find(ShopLix.class, idShop);
//				orderLix.setShopLix(shopLix);
//				MemberLix memberLix = em.find(MemberLix.class, idUser);
//				orderLix.setMemberLix(memberLix);
//				orderLix.setNote(noteOrder);
//				orderLix = em.merge(orderLix);
//			}
//			LixServiceSoapUtil.deleteORDByIDOR(em, orderLix.getId());
//			Gson gson = new Gson();
//			OrderDetailLix[] results = gson.fromJson(orderDetailLixs, OrderDetailLix[].class);
//			if (results != null) {
//				for (int i = 0; i < results.length; i++) {
//					OrderDetailLix item = new OrderDetailLix();
//					if (results[i].getId() != 0)
//						item.setId(results[i].getId());
//					item.setOrderLix(orderLix);
//					ProductLix prod = em.find(ProductLix.class, results[i].getProductLix().getId());
//					item.setProductLix(prod);
//					item.setQuantity(results[i].getQuantity());
//					item.setPrice(results[i].getPrice());
//					item.setTotal(results[i].getTotal());
//					item.setNote(results[i].getNote());
//					if (item.getId() == null) {
//						em.persist(item);
//					} else {
//						em.merge(item);
//					}
//				}
//
//			}
//			em.flush();
//			return orderLix;
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			ct.setRollbackOnly();
//			return null;
//		}
//	}
//
//	// Kiem tra khoan cach de tao don hang
//	@TransactionAttribute(TransactionAttributeType.REQUIRED)
//	@WebMethod(operationName = "checkDistance", action = "checkDistance")
//	public boolean checkDistance(@WebParam(name = "idWFD") long idWFD, @WebParam(name = "idmem") long idmem,
//			@WebParam(name = "longitude") double longitude, @WebParam(name = "latitude") double latitude) {
//		try {
//			WorkFollowDetailLix wfd = em.find(WorkFollowDetailLix.class, idWFD);
//			MemberLix mem = em.find(MemberLix.class, idmem);
//			ShopLix shopLix = wfd.getShopLix();
//			if (shopLix != null) {
//				double distan = calcDistanceLocaltion(shopLix.getAddressShop().getLongitudeShop(), shopLix
//						.getAddressShop().getLatitudeShop(), longitude, latitude);
//				if (distan <= DISTANCE) {
//					CheckingShopLix ck = new CheckingShopLix(wfd, mem);
//					em.persist(ck);
//					WorkFollowDetailLix fld = em.find(WorkFollowDetailLix.class, idWFD);
//					fld.setChecking(true);
//					fld.setDateChecking(new Date());
//					em.merge(fld);
//					em.flush();
//					return true;
//				} else {
//					return false;
//				}
//			} else {
//				return false;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			ct.setRollbackOnly();
//			return false;
//		}
//	}
//
//	// Danh sach workfollow theo user
//	@WebMethod(operationName = "dataOrderDetailLixByOrder", action = "dataOrderDetailLixByOrder")
//	public List<OrderDetailLix> dataOrderDetailLixByOrder(@WebParam(name = "idOrder") long idOrder) {
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<OrderDetailLix> cq = cb.createQuery(OrderDetailLix.class);
//		Root<OrderDetailLix> root = cq.from(OrderDetailLix.class);
//		cq.select(root).where(cb.equal(root.get("orderLix").get("id"), idOrder));
//		TypedQuery<OrderDetailLix> query = em.createQuery(cq);
//		List<OrderDetailLix> results = query.getResultList();
//		return results;
//	}
//
//	/*
//	 * @param numberStart la so ky tu dem (vd:001 la 3, 0001 la 4)
//	 */
//	public OrderLix showOrderLixMaxId() {
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<OrderLix> cq = cb.createQuery(OrderLix.class);
//		Root<OrderLix> root = cq.from(OrderLix.class);
//		cq.select(root).orderBy(cb.desc(root.get("id")));
//		TypedQuery<OrderLix> query = em.createQuery(cq).setFirstResult(0);
//		List<OrderLix> results = query.getResultList();
//		if (results.size() > 0) {
//			return results.get(0);
//		} else {
//			return null;
//		}
//	}
//
//	public String ajaxSelectOrderCode() {
//		OrderLix orderLix = showOrderLixMaxId();
//		if (orderLix != null) {
//			String code = orderLix.getCode();
//			int temp = Integer.parseInt(code.substring(2, code.length()));
//			String result = "DH" + String.format("%08d", temp + 1);
//			return result;
//		} else {
//			return "DH" + String.format("%08d", 1);
//		}
//
//	}
//
//	/**
//	 * Thao tac shop lix 28-08-2017
//	 */
//	// Xoa shoplix
//	@TransactionAttribute(TransactionAttributeType.REQUIRED)
//	@WebMethod(operationName = "deleteShopLix", action = "deleteShopLix")
//	public boolean deleteShopLix(@WebParam(name = "id") long id) {
//		try {
//			ShopLix shopLix = em.find(ShopLix.class, id);
//			em.remove(shopLix);
//			em.flush();
//			return true;
//		} catch (Exception e) {
//			return false;
//		}
//	}
//
//	/*
//	 * @param numberStart la so ky tu dem (vd:001 la 3, 0001 la 4)
//	 */
//	public ShopLix showShopLixMaxId() {
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<ShopLix> cq = cb.createQuery(ShopLix.class);
//		Root<ShopLix> root = cq.from(ShopLix.class);
//		cq.select(root).orderBy(cb.desc(root.get("id")));
//		TypedQuery<ShopLix> query = em.createQuery(cq).setFirstResult(0);
//		List<ShopLix> results = query.getResultList();
//		if (results.size() > 0) {
//			return results.get(0);
//		} else {
//			return null;
//		}
//	}
//
//	public String ajaxSelectCode() {
//		ShopLix shopLix = showShopLixMaxId();
//		if (shopLix != null) {
//			String code = shopLix.getCode();
//			int temp = Integer.parseInt(code.substring(2, code.length()));
//			String result = "SG" + String.format("%08d", temp + 1);
//			return result;
//		} else {
//			return "SG" + String.format("%08d", 1);
//		}
//
//	}
//
//	// Luu/ cap nhat cua hang
//	@TransactionAttribute(TransactionAttributeType.REQUIRED)
//	@WebMethod(operationName = "saveShopLix", action = "saveShopLix")
//	public ShopLix saveShopLix(@WebParam(name = "idUser") long idUser, @WebParam(name = "id") long id,
//			@WebParam(name = "code") String code, @WebParam(name = "name") String name,
//			@WebParam(name = "numberAdd") String numberAdd, @WebParam(name = "note") String note,
//			@WebParam(name = "idAdd") long idAdd, @WebParam(name = "idGuildCus") long idGuildCus,
//			@WebParam(name = "longitude") double longitude, @WebParam(name = "latitude") double latitude,
//			@WebParam(name = "idserver") long idserver, @WebParam(name = "phone") String phone) {
//		try {
//
//			ShopLix shopLix = em.find(ShopLix.class, idserver);
//			if (shopLix != null) {
//				shopLix.setTimeSync(new Date());
//				AddressShop addressShop = shopLix.getAddressShop();
//				GuildCus guildCus = em.find(GuildCus.class, idGuildCus);
//
//				if (addressShop == null) {
//					addressShop = new AddressShop();
//					addressShop.setGuildCus(guildCus);
//					addressShop.setLongitudeShop(longitude);
//					addressShop.setLatitudeShop(latitude);
//					em.persist(addressShop);
//					addressShop = em.merge(addressShop);
//				} else {
//					addressShop.setGuildCus(guildCus);
//					addressShop.setLongitudeShop(longitude);
//					addressShop.setLatitudeShop(latitude);
//					addressShop = em.merge(addressShop);
//				}
//
//				shopLix.setCode(code);
//				shopLix.setName(name);
//				shopLix.setAddressShop(addressShop);
//				shopLix.setNote(note);
//				shopLix.setNumberAdd(numberAdd);
//				shopLix.setPhone(phone);
//				shopLix = em.merge(shopLix);
//				em.flush();
//				return em.find(ShopLix.class, shopLix.getId());
//			} else {
//				MemberLix mem = em.find(MemberLix.class, idUser);
//				shopLix = new ShopLix();
//				shopLix.setTimeSync(new Date());
//
//				GuildCus guildCus = em.find(GuildCus.class, idGuildCus);
//				AddressShop addressShop = new AddressShop();
//				addressShop.setGuildCus(guildCus);
//				addressShop.setLongitudeShop(longitude);
//				addressShop.setLatitudeShop(latitude);
//				em.persist(addressShop);
//				addressShop = em.merge(addressShop);
//				shopLix.setUserCreate(mem);
//				shopLix.setUserManager(mem);
//				shopLix.setCode(code);
//				shopLix.setName(name);
//				shopLix.setAddressShop(addressShop);
//				shopLix.setNote(note);
//				shopLix.setNumberAdd(numberAdd);
//				shopLix.setPhone(phone);
//				em.persist(shopLix);
//				shopLix = em.merge(shopLix);
//				em.flush();
//				return em.find(ShopLix.class, shopLix.getId());
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			ct.setRollbackOnly();
//			return new ShopLix();
//		}
//	}
//
//	// Danh sach dia chi cua hang
//	@WebMethod(operationName = "findAddShoplixByCity", action = "findAddShoplixByCity")
//	public List<AddressShop> findAddShoplixByCity(@WebParam(name = "idcity") long idcity) {
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<AddressShop> cq = cb.createQuery(AddressShop.class);
//		Root<AddressShop> root = cq.from(AddressShop.class);
//		List<Predicate> predicates = new LinkedList<Predicate>();
//		Predicate pr = cb.equal(root.get("guildCus").get("countyCus").get("cityCus").get("id"), idcity);
//		predicates.add(pr);
//
//		cq.select(root).where(predicates.toArray(new Predicate[0]));
//		TypedQuery<AddressShop> query = em.createQuery(cq);
//		List<AddressShop> results = query.getResultList();
//		return results;
//	}
//
//	// Danh sach thanh pho
//	@WebMethod(operationName = "showAllCityCus", action = "showAllCityCus")
//	public List<CityCus> findAll() {
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<CityCus> cq = cb.createQuery(CityCus.class);
//		Root<CityCus> root = cq.from(CityCus.class);
//		cq.select(root);
//		TypedQuery<CityCus> query = em.createQuery(cq);
//		List<CityCus> results = query.getResultList();
//		return results;
//	}
//
//	// Danh sach quan/huyen theo thanh pho
//	@WebMethod(operationName = "findByCityCus", action = "findByCityCus")
//	public List<CountyCus> findByCityCus(@WebParam(name = "idcities") long idcities) {
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<CountyCus> cq = cb.createQuery(CountyCus.class);
//		Root<CountyCus> root = cq.from(CountyCus.class);
//		List<Predicate> predicates = new LinkedList<Predicate>();
//		Predicate predicateStart = cb.in(root.get("cityCus").get("id")).value(idcities);
//		predicates.add(predicateStart);
//		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
//		TypedQuery<CountyCus> query = em.createQuery(cq);
//		List<CountyCus> results = query.getResultList();
//		return results;
//	}
//
//	@WebMethod(operationName = "findGuidByCity", action = "findGuidByCity")
//	public List<GuildCus> findGuidByCity(@WebParam(name = "idcities") long idcities) {
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<GuildCus> cq = cb.createQuery(GuildCus.class);
//		Root<GuildCus> root = cq.from(GuildCus.class);
//		List<Predicate> predicates = new LinkedList<Predicate>();
//		Predicate predicateStart = cb.in(root.get("countyCus").get("cityCus").get("id")).value(idcities);
//		predicates.add(predicateStart);
//		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
//		TypedQuery<GuildCus> query = em.createQuery(cq);
//		List<GuildCus> results = query.getResultList();
//		return results;
//	}
//
//	// Danh sach phuong/xa theo quan/huyen
//	@WebMethod(operationName = "showAllGuildCus", action = "showAllGuildCus")
//	public List<GuildCus> findByGuildCus(@WebParam(name = "idcounty") long idCounty) {
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<GuildCus> cq = cb.createQuery(GuildCus.class);
//		Root<GuildCus> root = cq.from(GuildCus.class);
//		List<Predicate> predicates = new LinkedList<Predicate>();
//		if (idCounty != -1) {
//			Predicate predicateStart = cb.equal(root.get("countyCus").get("id"), idCounty);
//			predicates.add(predicateStart);
//		}
//		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
//		TypedQuery<GuildCus> query = em.createQuery(cq);
//		List<GuildCus> results = query.getResultList();
//		return results;
//	}
//
//	// Danh sach shoplix
//	@WebMethod(operationName = "findShoplixByUser", action = "findShoplixByUser")
//	public List<ShopLix> findShoplixByUser(@WebParam(name = "iduser") long iduser) {
//
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<ShopLix> cq = cb.createQuery(ShopLix.class);
//		Root<ShopLix> root = cq.from(ShopLix.class);
//		List<Predicate> predicates = new LinkedList<Predicate>();
//		if (iduser != -1) {
//			installMemberList(iduser);
//			Predicate pr = cb.in(root.get("userCreate")).value(memberLixs);
//			predicates.add(pr);
//		}
//		cq.select(root).where(predicates.toArray(new Predicate[0]));
//		TypedQuery<ShopLix> query = em.createQuery(cq);
//		List<ShopLix> results = query.getResultList();
//		return results;
//	}
//
//	// Danh sach shoplix theo khu vá»±c
//	@WebMethod(operationName = "findShoplixByCounty", action = "findShoplixByCounty")
//	public List<ShopLix> findShoplixByCounty(@WebParam(name = "idcounty") String idcounty) {
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<ShopLix> cq = cb.createQuery(ShopLix.class);
//		Root<ShopLix> root = cq.from(ShopLix.class);
//		List<Predicate> predicates = new LinkedList<Predicate>();
//		Gson gson = new Gson();
//		List<Long> ids = new ArrayList<Long>();
//		String[] params = gson.fromJson(idcounty, String[].class);
//		for (int i = 0; i < params.length; i++) {
//			ids.add(Long.parseLong(params[i]));
//		}
//		if (ids.size() != 0) {
//			Predicate pr = cb.in(root.get("addressShop").get("guildCus").get("countyCus").get("id")).value(ids);
//			predicates.add(pr);
//
//			cq.select(root).where(predicates.toArray(new Predicate[0]));
//			TypedQuery<ShopLix> query = em.createQuery(cq);
//			List<ShopLix> results = query.getResultList();
//			return results;
//		} else {
//			return new ArrayList<ShopLix>();
//		}
//
//	}
//
//	// Danh sach shoplix theo mem quan ly
//	@WebMethod(operationName = "findShoplixByMemM", action = "findShoplixByMemM")
//	public List<ShopLix> findShoplixByMemM(@WebParam(name = "idmemm") long idmemm) {
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<ShopLix> cq = cb.createQuery(ShopLix.class);
//		Root<ShopLix> root = cq.from(ShopLix.class);
//		List<Predicate> predicates = new LinkedList<Predicate>();
//		Predicate pr = cb.equal(root.get("userManager"), idmemm);
//		predicates.add(pr);
//		cq.select(root).where(predicates.toArray(new Predicate[0]));
//		TypedQuery<ShopLix> query = em.createQuery(cq);
//		List<ShopLix> results = query.getResultList();
//		return results;
//
//	}
//
//	// Danh sach shoplix
//	@WebMethod(operationName = "findAllShoplix", action = "findAllShoplix")
//	public List<ShopLix> findAllShoplix() {
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<ShopLix> cq = cb.createQuery(ShopLix.class);
//		Root<ShopLix> root = cq.from(ShopLix.class);
//		cq.select(root);
//		TypedQuery<ShopLix> query = em.createQuery(cq);
//		List<ShopLix> results = query.getResultList();
//		return results;
//	}
//
//	// Danh sach shoplix
//	@WebMethod(operationName = "findLikeShoplix", action = "findLikeShoplix")
//	public List<ShopLix> findLikeShoplix(@WebParam(name = "textparam") String textparam) {
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<ShopLix> cq = cb.createQuery(ShopLix.class);
//		Root<ShopLix> root = cq.from(ShopLix.class);
//
//		List<Predicate> predicates = new LinkedList<Predicate>();
//
//		Predicate predicateN = cb.like(root.get("name"), "%" + textparam + "%");
//		predicates.add(predicateN);
//		Predicate predicateAdd = cb.like(root.get("numberAdd"), "%" + textparam + "%");
//		predicates.add(predicateAdd);
//
//		Predicate predicateG = cb.like(root.get("addressShop").get("guildCus").get("name"), "%" + textparam + "%");
//		predicates.add(predicateG);
//
//		Predicate predicateC = cb.like(root.get("addressShop").get("guildCus").get("countyCus").get("name"), "%"
//				+ textparam + "%");
//		predicates.add(predicateC);
//
//		Predicate predicateCi = cb.like(
//				root.get("addressShop").get("guildCus").get("countyCus").get("cityCus").get("name"), "%" + textparam
//						+ "%");
//		predicates.add(predicateCi);
//
//		cq.select(root).where(cb.or(predicates.toArray(new Predicate[0])));
//
//		cq.select(root);
//		TypedQuery<ShopLix> query = em.createQuery(cq);
//		List<ShopLix> results = query.getResultList();
//		return results;
//	}
//
//	// Danh sach productLix
//	@WebMethod(operationName = "sendMailInfor", action = "sendMailInfor")
//	public boolean sendMailInfor(@WebParam(name = "emailaddress") String emailaddress) {
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<MemberLix> cq = cb.createQuery(MemberLix.class);
//		Root<MemberLix> root = cq.from(MemberLix.class);
//		cq.select(root).where(cb.like(root.get("email"), emailaddress), cb.equal(root.get("disable"), false));
//		TypedQuery<MemberLix> query = em.createQuery(cq);
//		List<MemberLix> results = query.getResultList();
//		if (results.size() != 0) {
//			MemberLix memberLix = results.get(0);
//			return sendmail(memberLix);
//		}
//		return false;
//	}
//
//	// Danh sach productLix
//	@WebMethod(operationName = "lixcodmslogin", action = "lixcodmslogin")
//	public MemberLix lixcodmslogin(@WebParam(name = "lixcodmsu") String username,
//			@WebParam(name = "lixcodmsp") String password) {
//		System.out.println("Login: " + username);
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<MemberLix> cq = cb.createQuery(MemberLix.class);
//		Root<MemberLix> root = cq.from(MemberLix.class);
//		cq.select(root).where(cb.equal(root.get("accountLix").get("username"), username),
//				cb.equal(root.get("accountLix").get("password"), password), cb.equal(root.get("disable"), false));
//		TypedQuery<MemberLix> query = em.createQuery(cq);
//		List<MemberLix> results = query.getResultList();
//		if (results.size() != 0) {
//			// cap nhat null de dang nhap ben DMS
//			MemberLix mem = results.get(0);
//			if (mem.getAccountLix().getUsername().equals(username)
//					&& mem.getAccountLix().getPassword().equals(password)) {
//				return mem;
//			} else {
//				return null;
//			}
//		} else {
//			return null;
//		}
//
//	}
//
//	// Danh sach productLix
//	@WebMethod(operationName = "showProductLix", action = "showProductLix")
//	public List<ProductLix> showProductLix() {
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<ProductLix> cq = cb.createQuery(ProductLix.class);
//		Root<ProductLix> root = cq.from(ProductLix.class);
//		cq.select(root).where(cb.equal(root.get("disable"), false), cb.equal(root.get("promo"), false));
//		TypedQuery<ProductLix> query = em.createQuery(cq);
//		List<ProductLix> results = query.getResultList();
//		return results;
//	}
//
//	// Danh sach nhom productLix
//	@WebMethod(operationName = "dataProductCategoryGroupLix", action = "dataProductCategoryGroupLix")
//	public List<ProductCategoryGroupLix> dataProductCategoryGroupLix() {
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<ProductCategoryGroupLix> cq = cb.createQuery(ProductCategoryGroupLix.class);
//		Root<ProductCategoryGroupLix> root = cq.from(ProductCategoryGroupLix.class);
//		cq.select(root);
//		TypedQuery<ProductCategoryGroupLix> query = em.createQuery(cq);
//		List<ProductCategoryGroupLix> results = query.getResultList();
//		return results;
//	}
//
//	// Danh sach loai productLix
//	@WebMethod(operationName = "dataProductCategoryLix", action = "dataProductCategoryLix")
//	public List<ProductCategoryLix> dataProductCategoryLix() {
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<ProductCategoryLix> cq = cb.createQuery(ProductCategoryLix.class);
//		Root<ProductCategoryLix> root = cq.from(ProductCategoryLix.class);
//		cq.select(root);
//		TypedQuery<ProductCategoryLix> query = em.createQuery(cq);
//		List<ProductCategoryLix> results = query.getResultList();
//		return results;
//	}
//
//	// Danh sach don hang
//	@WebMethod(operationName = "searchOrder", action = "searchOrder")
//	public List<OrderLix> searchOrder(@WebParam(name = "iduser") long iduser, @WebParam(name = "idsale") long idsale,
//			@WebParam(name = "date") String date) {
//		memberLixs = new ArrayList<MemberLix>();
//		if (idsale != -1) {
//			MemberLix memberLix = em.find(MemberLix.class, idsale);
//			memberLixs.add(memberLix);
//		} else {
//			MemberLix memberLix = em.find(MemberLix.class, iduser);
//			memberLixs.add(memberLix);
//			showAllMemberChild(iduser);
//			for (int i = 0; i < memberLixs.size(); i++) {
//				memberLixs.get(i).setMemSub(null);
//			}
//		}
//		return processOrder(memberLixs, date);
//	}
//
//	// Lay danh sach don hang
//	public List<OrderLix> processOrder(List<MemberLix> memberLixs, String date) {
//		Date d = null;
//		SimpleDateFormat sf = new SimpleDateFormat("d/M/yyyy");
//		try {
//			d = sf.parse(date);
//		} catch (Exception e) {
//		}
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<OrderLix> cq = cb.createQuery(OrderLix.class);
//		Root<OrderLix> root = cq.from(OrderLix.class);
//		List<Predicate> predicates = new LinkedList<Predicate>();
//		if (d != null) {
//			Predicate predicateD = cb.greaterThanOrEqualTo(root.get("date"), d);
//			predicates.add(predicateD);
//		}
//		Predicate predicateS = cb.in(root.get("memberLix")).value(memberLixs);
//		predicates.add(predicateS);
//		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
//		TypedQuery<OrderLix> query = em.createQuery(cq);
//		List<OrderLix> results = query.getResultList();
//		return results;
//	}
//
//	// Danh sach workfollow theo idshop
//	@WebMethod(operationName = "showOrderLixByShopLix", action = "showOrderLixByShopLix")
//	public List<OrderLix> showOrderLixByShopLix(@WebParam(name = "idshop") long idshop,
//			@WebParam(name = "limit") int limit) {
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<OrderLix> cq = cb.createQuery(OrderLix.class);
//		Root<OrderLix> root = cq.from(OrderLix.class);
//		cq.select(root).where(cb.equal(root.get("shopLix").get("id"), idshop)).orderBy(cb.desc(root.get("id")));
//		TypedQuery<OrderLix> query = em.createQuery(cq);
//		if (limit != -1)
//			query.setMaxResults(limit);
//		List<OrderLix> results = query.getResultList();
//		return results;
//	}
//
//	// Danh sach workfollow theo idshop
//	@WebMethod(operationName = "countOrderLixByShopLix", action = "countOrderLixByShopLix")
//	public int countOrderLixByShopLix(@WebParam(name = "idshop") long idshop) {
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
//		Root<OrderLix> root = cq.from(OrderLix.class);
//		cq.multiselect(cb.count(root.get("id"))).where(cb.equal(root.get("shopLix").get("id"), idshop));
//
//		TypedQuery<Long> query = em.createQuery(cq);
//		List<Long> results = query.getResultList();
//		int count = 0;
//		for (Long values : results) {
//			count = Integer.parseInt(values + "");
//			break;
//		}
//		return count;
//	}
//
//	// Danh sach workfollow theo id
//	@WebMethod(operationName = "showWorkFollowDetailByWorkFollow", action = "showWorkFollowDetailByWorkFollow")
//	public List<WorkFollowDetailLix> showWorkFollowDetailByWorkFollow(@WebParam(name = "idworkfollow") long idworkfollow) {
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<WorkFollowDetailLix> cq = cb.createQuery(WorkFollowDetailLix.class);
//		Root<WorkFollowDetailLix> root = cq.from(WorkFollowDetailLix.class);
//		cq.select(root).where(cb.equal(root.get("workFollowLix").get("id"), idworkfollow));
//		TypedQuery<WorkFollowDetailLix> query = em.createQuery(cq);
//		List<WorkFollowDetailLix> results = query.getResultList();
//		return results;
//	}
//
//	/**
//	 * Danh sach fordfollow theo user 12-09-2017
//	 */
//	private List<MemberLix> memberLixs;
//
//	// Danh sach tuyen ban hang theo user
//	@WebMethod(operationName = "searchWorkFollow", action = "searchWorkFollow")
//	public List<WorkFollowLix> searchWorkFollow(@WebParam(name = "idsale") long idsale,
//			@WebParam(name = "name") String name) {
//		memberLixs = new ArrayList<MemberLix>();
//		MemberLix memberLix = em.find(MemberLix.class, idsale);
//		memberLixs.add(memberLix);
//		showAllMemberChild(idsale);
//
//		return processWorkFollowByMemName(memberLixs, name);
//	}
//
//	public void showAllMemberChild(long idMember) {
//		List<MemberLix> memberLixs = listMemberByMemberParent(idMember);
//		for (int i = 0; i < memberLixs.size(); i++) {
//			this.memberLixs.add(memberLixs.get(i));
//			showAllMemberChild(memberLixs.get(i).getId());
//		}
//	}
//
//	public List<MemberLix> listMemberByMemberParent(long iduser) {
//		// lay danh sach member theo idmembercha
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<MemberLix> cq = cb.createQuery(MemberLix.class);
//		Root<MemberLix> root = cq.from(MemberLix.class);
//		cq.select(root).where(cb.equal(root.get("memParent").get("id"), iduser));
//		TypedQuery<MemberLix> query = em.createQuery(cq);
//		List<MemberLix> results = query.getResultList();
//		return results;
//	}
//
//	// Lay danh sach wf theo danh sach nhan vien
//	public List<WorkFollowLix> processWorkFollowByMemName(List<MemberLix> memberLixs, String name) {
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<WorkFollowLix> cq = cb.createQuery(WorkFollowLix.class);
//		Root<WorkFollowLix> root = cq.from(WorkFollowLix.class);
//		List<Predicate> predicates = new LinkedList<Predicate>();
//		Predicate predicateS = cb.in(root.get("memberSale")).value(memberLixs);
//		predicates.add(predicateS);
//		Predicate predicateN = cb.like(root.get("name"), "%" + name + "%");
//		predicates.add(predicateN);
//		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])))
//				.orderBy(cb.asc(root.get("memberSale")), cb.asc(root.get("day")));
//		TypedQuery<WorkFollowLix> query = em.createQuery(cq);
//		List<WorkFollowLix> results = query.getResultList();
//		return results;
//	}
//
//	// kiem tra ket noi den server
//	@WebMethod(operationName = "checkconnect", action = "checkconnect")
//	public boolean checkconnect() {
//		return true;
//	}
//
//	// Danh sach cau hinh mail server
//	@WebMethod(operationName = "configServerMail", action = "configServerMail")
//	public List<ServerMailLix> configServerMail() {
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<ServerMailLix> cq = cb.createQuery(ServerMailLix.class);
//		Root<ServerMailLix> root = cq.from(ServerMailLix.class);
//		cq.select(root);
//		TypedQuery<ServerMailLix> query = em.createQuery(cq);
//		return query.getResultList();
//	}
//
//	// Lay mail admin quan tri he thong
//	@WebMethod(operationName = "getMailSystemLix", action = "getMailSystemLix")
//	public MailSystemLix getMailSystemLix() {
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<MailSystemLix> cq = cb.createQuery(MailSystemLix.class);
//		Root<MailSystemLix> root = cq.from(MailSystemLix.class);
//		cq.select(root);
//		TypedQuery<MailSystemLix> query = em.createQuery(cq);
//		List<MailSystemLix> results = query.getResultList();
//		if (results.size() != 0)
//			return results.get(0);
//		return null;
//	}
//
//	public boolean sendmail(MemberLix memberLix) {
//		try {
//			String email = memberLix.getEmail();
//			AccountLix accountLix = memberLix.getAccountLix();
//			String useAccLix = accountLix.getUsername();
//			String passAccLix = accountLix.getPassword();
//
//			Properties props = System.getProperties();
//			List<ServerMailLix> serverMailLixs = configServerMail();
//			for (int i = 0; i < serverMailLixs.size(); i++) {
//				props.put(serverMailLixs.get(i).getKey(), serverMailLixs.get(i).getValue());
//			}
//
//			Authenticator pa = null;
//			MailSystemLix mailSystemLix = getMailSystemLix();
//			String emailadmin = "trong-nguyenvan@lixco.com";
//			if (mailSystemLix != null) {
//				final String login = mailSystemLix.getEmail();
//				emailadmin = mailSystemLix.getEmail();
//				final String pwd = mailSystemLix.getPass();
//				if (login != null && pwd != null) {
//					props.put("mail.smtp.auth", "true");
//					pa = new Authenticator() {
//						public PasswordAuthentication getPasswordAuthentication() {
//							return new PasswordAuthentication(login, pwd);
//						}
//					};
//				}// else: no authentication
//			}
//			Session session = Session.getInstance(props, pa);
//			// â€” Create a new message â€“
//			Message msg = new MimeMessage(session);
//			msg.setFrom(new InternetAddress(emailadmin));
//
//			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false));
//
//			// â€” Set the subject and body text â€“
//			msg.setSubject(MimeUtility.encodeText("ThÃ´ng tin tÃ i khoáº£n chÆ°Æ¡ng trÃ¬nh Lixco DMS", "utf-8", "B"));
//
//			String text = "ChÃ o " + memberLix.getName();
//			String html = "<h4>" + "ThÃ´ng tin tÃ i khoáº£n" + "</h4><h5>TÃªn Ä‘Äƒng nháº­p: " + useAccLix
//					+ "</h5><h5>Máº­t kháº©u: " + passAccLix + "</h5>";
//
//			Multipart multipart = new MimeMultipart("alternative");
//			MimeBodyPart textPart = new MimeBodyPart();
//			textPart.setText(text, "utf-8");
//
//			MimeBodyPart htmlPart = new MimeBodyPart();
//			htmlPart.setContent(html, "text/html; charset=utf-8");
//
//			multipart.addBodyPart(textPart);
//			multipart.addBodyPart(htmlPart);
//			msg.setContent(multipart);
//			// â€” Set some other header information â€“
//
//			msg.setHeader("X-Mailer", "LOTONtechEmail");
//			msg.setSentDate(new Date());
//			msg.saveChanges();
//
//			// â€” Send the message â€“
//			Transport.send(msg);
//			return true;
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//			return false;
//		}
//	}
//
//	// public boolean sendmail(MemberLix memberLix) {
//	// try {
//	//
//	// String email = memberLix.getEmail();
//	// AccountLix accountLix = memberLix.getAccountLix();
//	// String useAccLix = accountLix.getUsername();
//	// String passAccLix = accountLix.getPassword();
//	//
//	// Properties props = System.getProperties();
//	// props.put("mail.smtp.host", "mail.lixco.com");
//	// props.put("mail.smtp.port", "25");
//	// final String login = "trong-nguyenvan@lixco.com";
//	// final String pwd = "It@2168lix";
//	// Authenticator pa = null;
//	// if (login != null && pwd != null) {
//	// props.put("mail.smtp.auth", "true");
//	// pa = new Authenticator() {
//	// public PasswordAuthentication getPasswordAuthentication() {
//	// return new PasswordAuthentication(login, pwd);
//	// }
//	// };
//	// }// else: no authentication
//	//
//	// Session session = Session.getInstance(props, pa);
//	// // â€” Create a new message â€“
//	// Message msg = new MimeMessage(session);
//	// // â€” Set the FROM and TO fields â€“
//	// msg.setFrom(new InternetAddress("trong-nguyenvan@lixco.com"));
//	// msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email,
//	// false));
//	//
//	// // â€” Set the subject and body text â€“
//	// msg.setSubject(MimeUtility.encodeText("ThÃ´ng tin tÃ i khoáº£n chÆ°Æ¡ng trÃ¬nh Lixco DMS",
//	// "utf-8", "B"));
//	//
//	// String text = "Hello, World";
//	// String html = "<h4>" + "ThÃ´ng tin tÃ i khoáº£n" + "</h4><h5>TÃªn Ä‘Äƒng nháº­p: "
//	// + useAccLix
//	// + "</h5><h5>Máº­t kháº©u: " + passAccLix + "</h5>";
//	//
//	// Multipart multipart = new MimeMultipart("alternative");
//	// MimeBodyPart textPart = new MimeBodyPart();
//	// textPart.setText(text, "utf-8");
//	//
//	// MimeBodyPart htmlPart = new MimeBodyPart();
//	// htmlPart.setContent(html, "text/html; charset=utf-8");
//	//
//	// multipart.addBodyPart(textPart);
//	// multipart.addBodyPart(htmlPart);
//	// msg.setContent(multipart);
//	// // â€” Set some other header information â€“
//	//
//	// msg.setHeader("X-Mailer", "LOTONtechEmail");
//	// msg.setSentDate(new Date());
//	// msg.saveChanges();
//	// // â€” Send the message â€“
//	// Transport.send(msg);
//	// return true;
//	// } catch (Exception e) {
//	// // TODO: handle exception
//	// e.printStackTrace();
//	// return false;
//	// }
//	// }
//	//
//
//	private static double DegreesToRadians(double degrees) {
//		return degrees * Math.PI / 180.0;
//	}
//
//	public static double calcDistanceLocaltion(double longitude1, double latitude1, double longitude2, double latitude2) {
//		double circumference = 40000.0; // Earth's circumference at the equator
//										// in km
//		double distance = 0.0;
//
//		// Calculate radians
//		double latitude1Rad = DegreesToRadians(latitude1);
//		double longitude1Rad = DegreesToRadians(longitude1);
//		double latititude2Rad = DegreesToRadians(latitude2);
//		double longitude2Rad = DegreesToRadians(longitude2);
//
//		double logitudeDiff = Math.abs(longitude1Rad - longitude2Rad);
//
//		if (logitudeDiff > Math.PI) {
//			logitudeDiff = 2.0 * Math.PI - logitudeDiff;
//		}
//
//		double angleCalculation = Math.acos(Math.sin(latititude2Rad) * Math.sin(latitude1Rad)
//				+ Math.cos(latititude2Rad) * Math.cos(latitude1Rad) * Math.cos(logitudeDiff));
//
//		distance = circumference * angleCalculation / (2.0 * Math.PI);
//
//		return distance;
//	}
//
//	@WebMethod(operationName = "showMyDoc", action = "showMyDoc")
//	public List<MyDoc> showMyDoc() {
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<MyDoc> cq = cb.createQuery(MyDoc.class);
//		Root<MyDoc> root = cq.from(MyDoc.class);
//		cq.select(root);
//		List<MyDoc> myDocs = em.createQuery(cq).getResultList();
//		return myDocs;
//	}
//
//	@Inject
//	TargetLixService targetLixService;
//
//	@WebMethod(operationName = "showTarget", action = "showTarget")
//	public List<InforTarget> showTarget(@WebParam(name = "idwf") long idwf, @WebParam(name = "week") int week,
//			@WebParam(name = "year") int year, @WebParam(name = "day") String day) {
//		try {
//			int dayp = 0;
//			if ("Thá»© 2".equals(day)) {
//				dayp = 2;
//			} else if ("Thá»© 3".equals(day)) {
//				dayp = 3;
//			} else if ("Thá»© 4".equals(day)) {
//				dayp = 4;
//			} else if ("Thá»© 5".equals(day)) {
//				dayp = 5;
//			} else if ("Thá»© 6".equals(day)) {
//				dayp = 6;
//			} else if ("Thá»© 7".equals(day)) {
//				dayp = 7;
//			} else if ("Chá»§ nháº­t".equals(day)) {
//				dayp = 0;
//			}
//
//			WeekFields weekFields = WeekFields.of(Locale.getDefault());
//			LocalDateTime ldt = LocalDateTime.now().withYear(year).with(weekFields.weekOfYear(), week)
//					.with(weekFields.dayOfWeek(), dayp);
//
//			LocalDateTime ldt2 = LocalDateTime.now().withYear(year).with(weekFields.weekOfYear(), week)
//					.with(weekFields.dayOfWeek(), dayp);
//
//			Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse(ldt.toLocalDate().toString());
//			Date endDate = new SimpleDateFormat("yyyy-MM-dd").parse(ldt2.toLocalDate().toString());
//
//			WorkFollowLix wf = workFollowLixService.findById(idwf);
//
//			CriteriaBuilder cb = em.getCriteriaBuilder();
//			CriteriaQuery<TargetLix> cq = cb.createQuery(TargetLix.class);
//			Root<TargetLix> root = cq.from(TargetLix.class);
//			List<Predicate> predicates = new LinkedList<Predicate>();
//			Predicate predicateW = cb.equal(root.get("tweek"), week);
//			predicates.add(predicateW);
//			Predicate predicateY = cb.equal(root.get("tyear"), year);
//			predicates.add(predicateY);
//			Predicate predicateWF = cb.equal(root.get("workFollowLix"), wf);
//			predicates.add(predicateWF);
//			cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
//			TypedQuery<TargetLix> query = em.createQuery(cq);
//			List<TargetLix> tgs = query.getResultList();
//
//			List<InforTarget> its = new ArrayList<InforTarget>();
//			for (int i = 0; i < tgs.size(); i++) {
//				if (tgs.get(i).getProductCategoryLix() != null) {
//					InforTarget it = new InforTarget();
//					it.setProductCategoryLix(tgs.get(i).getProductCategoryLix());
//					it.setQuantityTarget(tgs.get(i).getQuantity());
//					its.add(it);
//				}
//
//			}
//			List<ExportAgentLixDetail> exps = findExportFollowSal(startDate, endDate, wf.getMemberSale());
//			for (int i = 0; i < exps.size(); i++) {
//				if (exps.get(i).getProductLix().getProductCategoryLix() != null) {
//					InforTarget it = new InforTarget();
//					it.setProductLix(exps.get(i).getProductLix());
//					it.setProductCategoryLix(exps.get(i).getProductLix().getProductCategoryLix());
//					it.setQuantity(exps.get(i).getQuantity());
//					its.add(it);
//				}
//			}
//			//
//			// for (int i = 0; i < exps.size(); i++) {
//			// boolean status=true;
//			// for (int j = 0; j < its.size(); j++) {
//			// try {
//			// if (its.get(j).getProductCategoryLix()
//			// .equals(exps.get(i).getProductLix().getProductCategoryLix())) {
//			// its.get(j).setProductLix(exps.get(i).getProductLix());
//			// its.get(j).setQuantity(exps.get(i).getQuantity());
//			// status=false;
//			// }
//			// } catch (Exception e) {
//			// }
//			// }
//			// if (status) {
//			// InforTarget it = new InforTarget();
//			// it.setProductCategoryLix(exps.get(i).getProductLix().getProductCategoryLix());
//			// it.setQuantityTarget(exps.get(i).getQuantity());
//			// its.add(it);
//			// }
//			// }
//			Map<String, List<InforTarget>> datas = its.stream().collect(
//					Collectors.groupingBy(p -> p.getProductCategoryLix().getCode(), Collectors.toList()));
//			List<InforTarget> results = new ArrayList<InforTarget>();
//			for (String keyp : datas.keySet()) {
//				List<InforTarget> mapsl = datas.get(keyp);
//				double qtr = 0;
//				for (int i = 0; i < mapsl.size(); i++) {
//					qtr += mapsl.get(i).getQuantity();
//				}
//				for (int i = 0; i < mapsl.size(); i++) {
//					mapsl.get(i).setQuantityTargetResult(qtr);
//				}
//				results.addAll(mapsl);
//			}
//			return results;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new ArrayList<InforTarget>();
//		}
//	}
//
//	// Xuat nha phan phoi
//	public List<ExportAgentLixDetail> findExportFollowSal(Date startDate, Date endDate, MemberLix memberSale) {
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
//		List<Predicate> predicatesParent = new LinkedList<Predicate>();
//		Root<ExportAgentLixDetail> root = cq.from(ExportAgentLixDetail.class);
//		// Subquery
//		List<Predicate> predicates = new LinkedList<Predicate>();
//		Subquery<ExportAgentLix> sqOne = cq.subquery(ExportAgentLix.class);
//		Root<ExportAgentLix> root2 = sqOne.from(ExportAgentLix.class);
//		// Condition Month,Year
//		if (startDate != null) {
//			Predicate predicateYear = cb.equal(cb.function("year", Integer.class, root2.get("date")),
//					(startDate.getYear() + 1900));
//			Predicate predicateMonth = cb.equal(cb.function("month", Integer.class, root2.get("date")),
//					(startDate.getMonth() + 1));
//			Predicate predicateDay = cb
//					.equal(cb.function("day", Integer.class, root2.get("date")), startDate.getDate());
//			predicates.add(predicateYear);
//			predicates.add(predicateMonth);
//			predicates.add(predicateDay);
//		}
//		// Predicate predicateStart = cb.greaterThanOrEqualTo(root2.get("date"),
//		// startDate);
//		// Predicate predicateEnd = cb.lessThanOrEqualTo(root2.get("date"),
//		// endDate);
//
//		// predicates.add(predicateStart);
//		// predicates.add(predicateEnd);
//
//		if (memberSale != null) {
//			Predicate predicateShop = cb.equal(root2.get("memberLix"), memberSale);
//			predicates.add(predicateShop);
//		}
//		// query
//		sqOne.where(cb.and(predicates.toArray(new Predicate[0])));
//		sqOne.select(root2);
//
//		Predicate predicateParent = cb.equal(root.get("exportAgentLix"), cb.any(sqOne));
//		predicatesParent.add(predicateParent);
//
//		List<ExportAgentLixDetail> fcs = new ArrayList<ExportAgentLixDetail>();
//		cq.multiselect(root.get("productLix"), cb.sum(root.get("quantity")))
//				.where(cb.and(predicatesParent.toArray(new Predicate[0]))).groupBy(root.get("productLix"));
//
//		List<Object[]> valueArray = em.createQuery(cq).getResultList();
//		for (Object[] values : valueArray) {
//			final ProductLix prd = (ProductLix) values[0];
//			final double quantity = (Double) values[1];
//			// quy doi ra thung
//			double qthung = (double) Math.round((quantity / prd.getChangeValue()) * 10) / 10;
//			if (qthung != 0) {
//				ExportAgentLixDetail fc = new ExportAgentLixDetail();
//				fc.setProductLix(prd);
//				fc.setQuantity(qthung);
//				fcs.add(fc);
//			}
//		}
//		return fcs;
//
//	}
//
//	// Xuat nha phan phoi bao cao don hang
//	public List<ExportAgentLixDetail> findExportOrderReport(Date startDate, Date endDate, long idsale) {
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
//		List<Predicate> predicatesParent = new LinkedList<Predicate>();
//		Root<ExportAgentLixDetail> root = cq.from(ExportAgentLixDetail.class);
//		// Subquery
//		List<Predicate> predicates = new LinkedList<Predicate>();
//		Subquery<ExportAgentLix> sqOne = cq.subquery(ExportAgentLix.class);
//		Root<ExportAgentLix> root2 = sqOne.from(ExportAgentLix.class);
//		// Condition Month,Year
//		Predicate predicateStart = cb.greaterThanOrEqualTo(root2.get("date"), startDate);
//		Predicate predicateEnd = cb.lessThanOrEqualTo(root2.get("date"), endDate);
//		predicates.add(predicateStart);
//		predicates.add(predicateEnd);
//
//		Predicate predicateShop = cb.equal(root2.get("memberLix").get("id"), idsale);
//		predicates.add(predicateShop);
//		// query
//		sqOne.where(cb.and(predicates.toArray(new Predicate[0])));
//		sqOne.select(root2);
//
//		Predicate predicateParent = cb.equal(root.get("exportAgentLix"), cb.any(sqOne));
//		predicatesParent.add(predicateParent);
//
//		List<ExportAgentLixDetail> fcs = new ArrayList<ExportAgentLixDetail>();
//		cq.multiselect(root.get("productLix"), cb.sum(root.get("quantity")), cb.sum(root.get("total")))
//				.where(cb.and(predicatesParent.toArray(new Predicate[0]))).groupBy(root.get("productLix"));
//
//		List<Object[]> valueArray = em.createQuery(cq).getResultList();
//		for (Object[] values : valueArray) {
//			final ProductLix prd = (ProductLix) values[0];
//			final double quantity = (Double) values[1];
//			final double total = (Double) values[2];
//			ExportAgentLixDetail fc = new ExportAgentLixDetail();
//			fc.setProductLix(prd);
//			fc.setQuantity(quantity);
//			fc.setTotal(total);
//			fcs.add(fc);
//		}
//		return fcs;
//
//	}
//
//	public List<OrderDetailLix> findCustom(Date startDate, Date endDate, long idsale) {
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
//		List<Predicate> predicatesParent = new LinkedList<Predicate>();
//		Root<OrderDetailLix> root = cq.from(OrderDetailLix.class);
//		// Subquery
//		List<Predicate> predicates = new LinkedList<Predicate>();
//		Subquery<OrderLix> sqOne = cq.subquery(OrderLix.class);
//		Root<OrderLix> root2 = sqOne.from(OrderLix.class);
//
//		Predicate predicatesd = cb.greaterThanOrEqualTo(root2.get("date"), startDate);
//		predicates.add(predicatesd);
//		Predicate predicateed = cb.lessThanOrEqualTo(root2.get("date"), endDate);
//		predicates.add(predicateed);
//		Predicate predicateSale = cb.equal(root2.get("memberLix").get("id"), idsale);
//		predicates.add(predicateSale);
//
//		// query
//		sqOne.where(cb.and(predicates.toArray(new Predicate[0])));
//		sqOne.select(root2);
//		Predicate predicateParent = cb.equal(root.get("orderLix"), cb.any(sqOne));
//		predicatesParent.add(predicateParent);
//		List<OrderDetailLix> fcs = new ArrayList<OrderDetailLix>();
//		cq.multiselect(root.get("productLix"), cb.sum(root.get("quantity")))
//				.where(cb.and(predicatesParent.toArray(new Predicate[0]))).groupBy(root.get("productLix"))
//				.orderBy(cb.asc(root.get("productLix").get("name")));
//
//		List<Object[]> valueArray = em.createQuery(cq).getResultList();
//
//		for (Object[] values : valueArray) {
//			final ProductLix prd = (ProductLix) values[0];
//			final double quantity = (Double) values[1];
//
//			OrderDetailLix fc = new OrderDetailLix();
//			fc.setProductLix(prd);
//			fc.setQuantity(quantity);
//
//			fcs.add(fc);
//		}
//		return fcs;
//	}
//
//	@WebMethod(operationName = "loadNotice", action = "loadNotice")
//	public List<NoticeDMS> loadNotice() {
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<NoticeDMS> cq = cb.createQuery(NoticeDMS.class);
//		Root<NoticeDMS> root = cq.from(NoticeDMS.class);
//		cq.select(root).where(cb.equal(root.get("disable"), false)).orderBy(cb.desc(root.get("date")));
//		TypedQuery<NoticeDMS> query = em.createQuery(cq);
//		List<NoticeDMS> results = query.getResultList();
//		return results;
//	}
//
//	@Inject
//	IDNoticeDMSService idNoticeDMSService;
//
//	@TransactionAttribute(TransactionAttributeType.REQUIRED)
//	@WebMethod(operationName = "saveIDNotice", action = "saveIDNotice")
//	public boolean saveIDNotice(@WebParam(name = "idStr") String idStr, @WebParam(name = "idaccount") String idaccount) {
//		try {
//			IDNoticeDMS idNoticeDMS = idNoticeDMSService.findByIdStr(idStr);
//			if (idNoticeDMS == null) {
//				IDNoticeDMS newidnotice = new IDNoticeDMS();
//				newidnotice.setIdStr(idStr);
//				newidnotice.setIdAccount(idaccount);
//				em.persist(newidnotice);
//				em.flush();
//				return true;
//			} else {
//				return false;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			ct.setRollbackOnly();
//			return false;
//		}
//	}
//
//	@WebMethod(operationName = "saveImageShop", action = "saveImageShop")
//	public boolean saveImageShop(@WebParam(name = "name") String name, @WebParam(name = "content") String content,
//			@WebParam(name = "file") String file, @WebParam(name = "latlong") String latlong) {
//		try {
//			File directory = new File(pathImageShopService.findById(1l).getPath(), name + latlong + ".jpg");
//			byte[] base64Bytes = Base64.decodeBase64(file.getBytes());
//			FileUtils.writeByteArrayToFile(directory, base64Bytes);
//			return true;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//
//	}
//
//	public static void main(String[] args) {
//		String a = "589_20190627 092511_La10.6277067Lo108.840365.jpg";
//		System.out.println(a.substring(0, a.indexOf("_")));
//	}
//
//	@Inject
//	PathImageShopService pathImageShopService;
//
//	@WebMethod(operationName = "listNameImageShop", action = "listNameImageShop")
//	public List<String> listNameImageShop(@WebParam(name = "idshop") long idshop) {
//		List<String> results = new ArrayList<String>();
//		try {
//			File directory = new File(pathImageShopService.findById(1l).getPath());
//			Iterator<File> collectors = FileUtils.listFiles(directory, new WildcardFileFilter(idshop + "*"), null)
//					.iterator();
//			while (collectors.hasNext()) {
//				results.add(collectors.next().getName());
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return results;
//	}
//
//	@WebMethod(operationName = "getImageShop", action = "getImageShop")
//	public String getImageShop(@WebParam(name = "name") String name) {
//		try {
//			File directory = new File(pathImageShopService.findById(1l).getPath());
//			File file = null;
//			Iterator<File> collectors = FileUtils.listFiles(directory, new WildcardFileFilter(name + "*"), null)
//					.iterator();
//			while (collectors.hasNext()) {
//				file = collectors.next();
//				break;
//			}
//			if (file != null) {
//				byte[] fileContent = Files.readAllBytes(file.toPath());
//				String img = java.util.Base64.getEncoder().encodeToString(fileContent);
//				return img;
//			} else {
//				return "";
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			return "";
//		}
//	}
//
//	@WebMethod(operationName = "deleteImageShop", action = "deleteImageShop")
//	public boolean deleteImageShop(@WebParam(name = "name") String name) {
//		try {
//			File directory = new File(pathImageShopService.findById(1l).getPath());
//			File file = null;
//			Iterator<File> collectors = FileUtils.listFiles(directory, new WildcardFileFilter(name + "*"), null)
//					.iterator();
//			while (collectors.hasNext()) {
//				file = collectors.next();
//				break;
//			}
//			if (file != null) {
//				if (file.delete()) {
//					return true;
//				} else
//					return false;
//			} else {
//				return false;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//	}
//
//	@WebMethod(operationName = "reportOrderSale", action = "reportOrderSale")
//	public List<OrderReport> reportOrderSale(@WebParam(name = "startDate") String sd,
//			@WebParam(name = "endDate") String ed, @WebParam(name = "idsale") long idsale) {
//		try {
//			List<OrderReport> orderReports = new ArrayList<OrderReport>();
//			Date startDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(sd);
//			Date endDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(ed);
//			List<OrderDetailLix> orderDetailLixs = findCustom(startDate, endDate, idsale);
//			List<ExportAgentLixDetail> exps = findExportOrderReport(startDate, endDate, idsale);
//
//			for (int i = 0; i < orderDetailLixs.size(); i++) {
//				OrderReport od = new OrderReport();
//				od.setProductLix(orderDetailLixs.get(i).getProductLix());
//				od.setQuantityRd(orderDetailLixs.get(i).getQuantity());
//				for (int j = 0; j < exps.size(); j++) {
//					if (orderDetailLixs.get(i).getProductLix().equals(exps.get(j).getProductLix())) {
//						od.setQuantityFinal(exps.get(j).getQuantity());
//						od.setTotal(exps.get(j).getTotal());
//						break;
//					}
//				}
//				orderReports.add(od);
//			}
//			return orderReports;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new ArrayList<OrderReport>();
//		}
//	}
//
//	/*
//	 * Bao cao so don hang tren cua hang dd/MM/yyyy
//	 */
//	@WebMethod(operationName = "orderPerShopwf", action = "orderPerShopwf")
//	public List<OrderPerShopInfo> orderPerShopwf(@WebParam(name = "idgiamsat") long idgiamsat,
//			@WebParam(name = "startDate") String startDate, @WebParam(name = "endDate") String endDate) {
//		memberLixs = new ArrayList<MemberLix>();
//		showAllMemberChild(idgiamsat);
//		Date sd = null, ed = null;
//		try {
//			sd = ddmmyyyy.parse(startDate);
//			ed = ddmmyyyy.parse(endDate);
//		} catch (Exception e) {
//		}
//		List<OrderPerShopInfo> orwf = processDataService.countShopByMem(memberLixs);
//		List<OrderPerShopInfo> orex = processDataService.findExdGroupSale(memberLixs, sd, ed);
//		for (int i = 0; i < orwf.size(); i++) {
//			for (int j = 0; j < orex.size(); j++) {
//				if (orwf.get(i).getMemberLix().equals(orex.get(j).getMemberLix())) {
//					orwf.get(i).setNumberOr(orex.get(j).getNumberOr());
//					orwf.get(i).setTotal(orex.get(j).getTotal());
//				}
//			}
//		}
//		return orwf;
//	}
//
//	/*
//	 * Bao cao san luong ban hang dd/MM/yyyy
//	 */
//	@WebMethod(operationName = "outputProduct", action = "outputProduct")
//	public List<InforTarget> outputProduct(@WebParam(name = "idsale") long idsale,
//			@WebParam(name = "startDate") String startDate, @WebParam(name = "endDate") String endDate) {
//		Date sd = null, ed = null;
//		try {
//			sd = ddmmyyyy.parse(startDate);
//			ed = ddmmyyyy.parse(endDate);
//		} catch (Exception e) {
//		}
//		MemberLix memberLix = em.find(MemberLix.class, idsale);
//		List<Calendar> dateList = DateUtil.getListDateBetweenTwoDate(sd, ed);
//		List<InforTarget> inforTargets = new ArrayList<InforTarget>();
//		for (int j = 0; j < dateList.size(); j++) {
//			inforTargets.addAll(processDataService.showTarget(memberLix, dateList.get(j).get(Calendar.WEEK_OF_YEAR),
//					dateList.get(j).get(Calendar.YEAR), dateList.get(j).get(Calendar.DAY_OF_WEEK)));
//		}
//
//		Map<ProductCategoryLix, List<InforTarget>> datas = inforTargets.stream().collect(
//				Collectors.groupingBy(p -> p.getProductCategoryLix(), Collectors.toList()));
//		List<InforTarget> results = new ArrayList<InforTarget>();
//		for (ProductCategoryLix keyp : datas.keySet()) {
//			InforTarget rs = new InforTarget();
//			rs.setMemberLix(memberLix);
//			rs.setProductCategoryLix(keyp);
//
//			List<InforTarget> mapsl = datas.get(keyp);
//			// double quantity = 0;
//			double quantityTarget = 0;
//			double quantityTargetResult = 0;
//			for (int j = 0; j < mapsl.size(); j++) {
//				// quantity += mapsl.get(j).getQuantity();
//				quantityTarget += mapsl.get(j).getQuantityTarget();
//				quantityTargetResult += mapsl.get(j).getQuantityTargetResult();
//			}
//
//			// rs.setQuantity(quantity);
//			rs.setQuantityTarget(quantityTarget);
//			rs.setQuantityTargetResult(quantityTargetResult);
//			results.add(rs);
//		}
//		return results;
//	}
//
//	/*
//	 * Bao cao scua hang sau N ngay khong co don hang dd/MM/yyyy
//	 */
//	@WebMethod(operationName = "notOutputOrder", action = "notOutputOrder")
//	public List<ShopOrCh> notOutputOrder(@WebParam(name = "idgiamsat") long idgiamsat,
//			@WebParam(name = "date") String date) {
//		memberLixs = new ArrayList<MemberLix>();
//		showAllMemberChild(idgiamsat);
//		Date sd = null;
//		try {
//			sd = ddmmyyyy.parse(date);
//		} catch (Exception e) {
//		}
//		List<ShopOrCh> shopOrChs = new ArrayList<ShopOrCh>();
//		for (int i = 0; i < memberLixs.size(); i++) {
//			shopOrChs.addAll(processDataService.findExportFollowSal(sd, memberLixs.get(i)));
//		}
//		return shopOrChs;
//
//	}
//
//	/*
//	 * Bao cao scua hang sau N ngay khong checking dd/MM/yyyy
//	 */
//	@WebMethod(operationName = "notOutputChecking", action = "notOutputChecking")
//	public List<ShopOrCh> notOutputChecking(@WebParam(name = "idgiamsat") long idgiamsat,
//			@WebParam(name = "date") String date) {
//		memberLixs = new ArrayList<MemberLix>();
//		showAllMemberChild(idgiamsat);
//		Date sd = null;
//		try {
//			sd = ddmmyyyy.parse(date);
//		} catch (Exception e) {
//		}
//		List<ShopOrCh> shopOrChs = new ArrayList<ShopOrCh>();
//		for (int i = 0; i < memberLixs.size(); i++) {
//			shopOrChs.addAll(processDataService.findCheckingFollowSal(sd, memberLixs.get(i)));
//		}
//		return shopOrChs;
//	}
//
//	/*
//	 * Bao cao scua hang sau N ngay khong checking dd/MM/yyyy
//	 */
//	@WebMethod(operationName = "notImageShop", action = "notImageShop")
//	public List<ShopImage> notImageShop(@WebParam(name = "idgiamsat") long idgiamsat) {
//		List<ShopImage> shopImages = new ArrayList<ShopImage>();
//		memberLixs = new ArrayList<MemberLix>();
//		showAllMemberChild(idgiamsat);
//		List<ShopLix> shopLixs = new ArrayList<ShopLix>();
//		for (int i = 0; i < memberLixs.size(); i++) {
//			System.out.println(memberLixs.get(i).getName());
//			shopLixs.addAll(processDataService.findListShop(memberLixs.get(i)));
//		}
//		for (int i = 0; i < shopLixs.size(); i++) {
//			boolean status = processDataService.checkImageShop(shopLixs.get(i).getId());
//			ShopImage sh = new ShopImage(shopLixs.get(i), status);
//			shopImages.add(sh);
//		}
//		return shopImages;
//	}
//}