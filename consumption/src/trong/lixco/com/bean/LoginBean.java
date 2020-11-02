package trong.lixco.com.bean;


import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jboss.logging.Logger;


import trong.lixco.com.account.servicepublics.Account;
import trong.lixco.com.account.servicepublics.AccountServicePublic;
import trong.lixco.com.account.servicepublics.AccountServicePublicProxy;
import trong.lixco.com.account.servicepublics.MemberServicePublic;
import trong.lixco.com.account.servicepublics.MemberServicePublicProxy;
import trong.lixco.com.service.AccountCustomService;
import trong.lixco.com.util.Notify;


@ManagedBean
@SessionScoped
public class LoginBean extends AbstractBean {

	private static final long serialVersionUID = 1L;
	private String nameDB;
	private String username;
	private String password;
	private Notify notify;
	@Inject
	private Logger logger;
	@Inject
	private PathServerAccount pathServerAccount;
	@Inject
	private AccountCustomService accountCustomService;
	@Override
	protected void initItem() {
	}

	public void login() {
		notify = new Notify(FacesContext.getCurrentInstance());
		MemberServicePublic memberService = new MemberServicePublicProxy();
		try {
			if (username != null && !"".equals(username) && password != null) {
				List<Object[]> listInfoAccount = accountCustomService.getAcount(username, password);
				if (!listInfoAccount.isEmpty()) {
					for (int i = 0; i < listInfoAccount.size(); i++) {
						Object[] ob = listInfoAccount.get(i);
						
						
//						Member member = memberService.findId(Long.parseLong(ob[3].toString()));
//						if (member != null) {
							AccountServicePublic accountServicePublic = new AccountServicePublicProxy();
							Account account = accountServicePublic.find_User_Pass(username,password);
//							Account account = new Account();
//							account.setUserName(username);
//							account.setPassword(password);
//							account.setMember(member);
//							account.setId(Long.parseLong(ob[2].toString()));
							FacesContext facesContext = FacesContext.getCurrentInstance();
							HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
							HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext()
									.getResponse();
							HttpSession session = request.getSession();
//							CookieHelper cookieHelper = new CookieHelper();
//							cookieHelper.setCookie("account_id", String.valueOf(account_id), 60 * 60 * 24 * 365 * 10);
							session.setAttribute("account", account);
							response.sendRedirect(request.getContextPath()+"/nhanvien/nhanvienxemluong.jsf");
//						}

						
					}
				}else {
					notify.message("Tài khoản hoặc mật khẩu không chính xác!");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		// String body="user="+Decode.toString()
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

	public String getNameDB() {
		return nameDB;
	}

	public void setNameDB(String nameDB) {
		this.nameDB = nameDB;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
