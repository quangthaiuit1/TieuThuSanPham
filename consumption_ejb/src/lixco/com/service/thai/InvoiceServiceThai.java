package lixco.com.service.thai;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jboss.logging.Logger;

import lixco.com.entity.IECategories;
import lixco.com.entity.Invoice;
import lixco.com.entity.InvoiceDetail;
import lixco.com.entity.Product;
import lixco.com.entity.ProductType;
import lixco.com.entity.thai.TKSPThang1;
import lixco.com.entity.thai.ThongKeTieuThu;
import lixco.com.reportInfo.IECommercialInvoiceReportInfo;
import trong.lixco.com.service.AbstractService;

@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class InvoiceServiceThai extends AbstractService {
	@Inject
	private EntityManager em;
	@Resource
	private SessionContext ct;
	@Inject
	private Logger logger;

	@Override
	protected Class<Invoice> getEntityClass() {
		return Invoice.class;
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	@Override
	protected SessionContext getUt() {
		return ct;
	}

	public List<ThongKeTieuThu> getListReportThongKeTieuThu2(int month, int year) {
		// select p.product_type_id,pt.name,d.ie_categories_id,ie.content,
		// sum(dt.quantity),sum(dt.total_amount) from invoicedetail as dt
		// inner join invoice as d on dt.invoice_id=d.id
		// inner join product as p on dt.product_id=p.id
		// inner join producttype as pt on p.product_type_id=pt.id
		// inner join iecategories as ie on d.ie_categories_id=ie.id
		// where d.invoice_date >= month()
		// group by p.product_type_id,pt.name,d.ie_categories_id,ie.content
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ThongKeTieuThu> cq = cb.createQuery(ThongKeTieuThu.class);
			Root<InvoiceDetail> root = cq.from(InvoiceDetail.class);
			Join<InvoiceDetail, Invoice> invoice_ = root.join("invoice", JoinType.INNER);
			Join<InvoiceDetail, Product> product_ = root.join("product", JoinType.INNER);
			Join<Product, ProductType> productType_ = product_.join("product_type", JoinType.INNER);
			Join<Invoice, IECategories> iecategories_ = invoice_.join("ie_categories", JoinType.INNER);
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(cb.equal(cb.function("month", Integer.class, invoice_.get("invoice_date")), month));
			predicates.add(cb.equal(cb.function("year", Integer.class, invoice_.get("invoice_date")), year));

			cq.select(cb.construct(ThongKeTieuThu.class, iecategories_.get("content"), iecategories_.get("id"),
					productType_.get("id"), cb.sum(root.get("quantity")), cb.sum(root.get("total_amount")),
					productType_.get("name"))).where(cb.and(predicates.toArray(new Predicate[0])))
					.groupBy(productType_.get("id"), productType_.get("name"), iecategories_.get("id"),
							iecategories_.get("content"));
			TypedQuery<ThongKeTieuThu> query = em.createQuery(cq);
			return query.getResultList();
		} catch (Exception e) {
			logger.error("InvoiceServiceThai.getListReportThongKeTieuThu2:" + e.getMessage(), e);
		}
		return null;
	}

	public List<TKSPThang1> findByCustomer(List<String> tksps) {
		// String sql = "SELECT u.employee_code,u.employee_name FROM user_result
		// as u where u.survey_id = ?1 and u.employee_code = ?2";
		String sql = "SELECT p_type.name as loaiSanPham, p_type.id as loaiSanPhamId, p.product_name as sanPham,"
				+ "p.id as sanPhamId, iv_detail.quantity as soLuong, iv.id as invoiceId, iv_detail.unit_price as donGia, iv.tax_value as heSoThue "
				+ "FROM consumption.invoice as iv inner join consumption.invoicedetail as iv_detail on iv.id= iv_detail.invoice_id "
				+ "inner join consumption.product as p on p.id = iv_detail.product_id "
				+ "inner join consumption.producttype as p_type on p.product_type_id = p_type.id "
				+ "WHERE iv.customer_id in (?1);";
		// convert list to string
		StringBuilder sb = new StringBuilder();
		String prefix = "";
		for (String s : tksps) {
			sb.append(prefix);
			prefix = ",";
			sb.append(s);
		}

		Query query = em.createNativeQuery(sql).setParameter(1, sb.toString());
		// query.setParameter(2, departmentCode);
		// List<TKSPThang1> items = query.getResultList();
		List<TKSPThang1> items = new ArrayList<>();
		List<Object[]> listObj = query.getResultList();
		for (Object[] p : listObj) {
			TKSPThang1 item = new TKSPThang1(Objects.toString(p[0]), Long.parseLong(Objects.toString(p[1])),
					Objects.toString(p[2]), Long.parseLong(Objects.toString(p[3])),
					Double.parseDouble(Objects.toString(p[4])), Long.parseLong(Objects.toString(p[5])),
					Double.parseDouble(Objects.toString(p[6])), Double.parseDouble(Objects.toString(p[7])));
			items.add(item);
		}
		return items;
	}

	// thong ke san pham thang form 2
	public List<TKSPThang1> findByCustomer2(List<String> tksps) {
		// String sql = "SELECT u.employee_code,u.employee_name FROM user_result
		// as u where u.survey_id = ?1 and u.employee_code = ?2";
		String sql = "SELECT p_type.name as loaiSanPham, p_type.id as loaiSanPhamId, p.product_name as sanPham,"
				+ "p.id as sanPhamId, sum(iv_detail.quantity) as soLuong, iv.id as invoiceId, iv_detail.unit_price as donGia, iv.tax_value as heSoThue, "
				+ "loaixuatnhap.content as loaiXuatNhap, loaixuatnhap.id as loaiXuatNhapId "
				+ "FROM consumption.invoice as iv inner join consumption.invoicedetail as iv_detail on iv.id= iv_detail.invoice_id "
				+ "inner join consumption.product as p on p.id = iv_detail.product_id "
				+ "inner join consumption.producttype as p_type on p.product_type_id = p_type.id "
				+ "INNER JOIN consumption.iecategories as loaixuatnhap on iv.ie_categories_id = loaixuatnhap.id "
				+ "WHERE iv.customer_id in (?1) " + "GROUP BY p.id, iv.ie_categories_id;";
		// convert list to string
		StringBuilder sb = new StringBuilder();
		String prefix = "";
		for (String s : tksps) {
			sb.append(prefix);
			prefix = ",";
			sb.append(s);
		}

		Query query = em.createNativeQuery(sql).setParameter(1, sb.toString());
		// query.setParameter(2, departmentCode);
		// List<TKSPThang1> items = query.getResultList();
		List<TKSPThang1> items = new ArrayList<>();
		List<Object[]> listObj = query.getResultList();
		for (Object[] p : listObj) {
			TKSPThang1 item = new TKSPThang1(Objects.toString(p[0]), Long.parseLong(Objects.toString(p[1])),
					Objects.toString(p[2]), Long.parseLong(Objects.toString(p[3])),
					Double.parseDouble(Objects.toString(p[4])), Long.parseLong(Objects.toString(p[5])),
					Double.parseDouble(Objects.toString(p[6])), Double.parseDouble(Objects.toString(p[7])),
					Objects.toString(p[8]), Long.parseLong(Objects.toString(p[9])));
			items.add(item);
		}
		return items;
	}

	// public List<Invoice> findByMonth(int month) {
	// try
	// // primary
	// CriteriaBuilder cb = em.getCriteriaBuilder();
	// CriteriaQuery<Invoice> cq = cb.createQuery(Invoice.class);
	// Root<Invoice> root = cq.from(Invoice.class);
	// List<Predicate> queries = new ArrayList<>();
	//// cb.fun
	// if (month != 0) {
	// queries.add(cb.equal(cb.function("month",Integer.class,root.get("invoice_date")),month));
	// }
	//
	//// Predicate data[] = new Predicate[queries.size()];
	//// for (int i = 0; i < queries.size(); i++) {
	//// data[i] = queries.get(i);
	//// }
	//// Predicate finalPredicate = cb.and(data);
	// // cq.where(finalPredicate);
	//
	// cq.select(root).where(cb.and(queries.toArray(new Predicate[0])));
	// TypedQuery<Invoice> query = em.createQuery(cq);
	// List<Invoice> results = query.getResultList();
	// if (!results.isEmpty()) {
	// return results;
	// } else {
	// return new ArrayList<Invoice>();
	// }
	// }Catch
	// }
}
