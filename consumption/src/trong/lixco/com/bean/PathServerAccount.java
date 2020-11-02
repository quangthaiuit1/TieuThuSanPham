package trong.lixco.com.bean;


import java.rmi.RemoteException;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import trong.lixco.com.account.servicepublics.Account;
import trong.lixco.com.account.servicepublics.AccountServicePublic;
import trong.lixco.com.account.servicepublics.AccountServicePublicProxy;
import trong.lixco.com.entity.AccountDatabase;
import trong.lixco.com.service.AccountDatabaseService;
import trong.lixco.com.util.StaticPath;


@ManagedBean
@SessionScoped
public class PathServerAccount {
	@Inject
	private AccountDatabaseService accountDatabaseService;
	private AccountServicePublic accountServicePublic;
	private String path;
	private String pathlocal;

	@PostConstruct
	public void init() {
		

		AccountDatabase accdb = accountDatabaseService.findByName("hethong");
		if (checkAddressLocal()) {
			path = accdb.getAddress();
		} else {
			path = accdb.getAddressPublic();
		}
		StaticPath.setPath(path);

		AccountDatabase accdblocal = accountDatabaseService.findByName("consumption");
		if (checkAddressLocal()) {
			pathlocal = accdblocal.getAddress();
		} else {
			pathlocal = accdblocal.getAddressPublic();
		}
		StaticPath.setPathLocal(pathlocal);
		accountServicePublic = new AccountServicePublicProxy();

	}

	public boolean checkAddressLocal() {
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
				.getRequest();
		try {

			String ipAddress = request.getHeader("X-FORWARDED-FOR");// ip
			if (ipAddress == null) {
				ipAddress = request.getRemoteAddr();
				boolean temp = ipAddress.contains("192.168.");
				if (temp == false) {
					temp = ipAddress.contains("127.0.0.1");
				}
				return temp;
			} else {
				return false;
			}
		} catch (Exception e) {
			return true;
		}
	}

	public Account getAccount(long id) {
		try {
			return accountServicePublic.findById(id);
		} catch (RemoteException e) {
			return null;
		}
	}

	public String getPath() {
		return path;
	}

	public String getPathRefresh() {
		AccountDatabase accdb = accountDatabaseService.findByName("hethong");
		if (checkAddressLocal()) {
			path = accdb.getAddress();
		} else {
			path = accdb.getAddressPublic();
		}
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPathlocal() {
		return pathlocal;
	}

	public void setPathlocal(String pathlocal) {
		this.pathlocal = pathlocal;
	}

}
