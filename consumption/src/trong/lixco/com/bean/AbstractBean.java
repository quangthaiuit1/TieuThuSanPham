package trong.lixco.com.bean;


import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jboss.logging.Logger;
import org.primefaces.context.RequestContext;

import trong.lixco.com.account.servicepublics.Account;
import trong.lixco.com.account.servicepublics.AccountServicePublic;
import trong.lixco.com.account.servicepublics.AccountServicePublicProxy;
import trong.lixco.com.account.servicepublics.LockTable;
import trong.lixco.com.account.servicepublics.LockTableServicePublic;
import trong.lixco.com.account.servicepublics.LockTableServicePublicProxy;
import trong.lixco.com.account.servicepublics.Program;
import trong.lixco.com.info.PrivateConfig;
import trong.lixco.com.util.UrlPermission;

public abstract class AbstractBean implements Serializable {
	private static final long serialVersionUID = 9154488968915975475L;
	@Inject
	protected AuthorizationManager authorizationManager;
	private Account account;
	private PrivateConfig cf;
	AccountServicePublic accountServicePublic;

	private LockTableServicePublic lockTableServicePublic;

	@PostConstruct
	public void init() {
		lockTableServicePublic = new LockTableServicePublicProxy();
		accountServicePublic = new AccountServicePublicProxy();
		initItem();
	}

	protected abstract void initItem();

	protected abstract Logger getLogger();

	private void getSession() {
		HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
				.getRequest();
		HttpSession session = req.getSession();
		account = (Account) session.getAttribute("account");
		if (account != null)
			try {
				cf = new PrivateConfig(account.getPrivateConfig());
			} catch (Exception e) {
			}
			
	}

	public void writeLogInfo(String message) {
		if (account == null)
			getSession();
		getLogger().info("(" + account.getUserName() + "): " + message);
	}

	public void writeLogError(String message) {
		if (account == null)
			getSession();
		getLogger().error("(" + account.getUserName() + "): " + message);
	}

	public void writeLogWarning(String message) {
		try {
			if (account == null)
				getSession();
			getLogger().fatal("(" + account.getUserName() + "): " + message);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public List<UrlPermission> getUrlPermissions() {
		return authorizationManager.getUrlPermissions();
	}

	// Kiem tra thang da khoa chua
	public boolean lock(Date date) {
		LockTable lt;
		try {
			int month = (date.getMonth() + 1);
			int year = (date.getYear() + 1900);
			Program pr = authorizationManager.getProgram();
			lt = lockTableServicePublic.findMonthYear(month, year, pr);
			if (lt != null) {
				return lt.isLocks();
			} else {
				return false;
			}
		} catch (RemoteException e) {
			return false;
		}
	}

	public boolean allowSave(Date date) {
		UrlPermission up = authorizationManager.getPermission();
		if (up != null) {
			if (up.isSave()) {
				if (date != null) {
					if (lock(date) == false) {
						return true;
					} else {
						return false;
					}
				} else {
					return true;
				}
			} else {
				return up.isSave();
			}
		} else {
			return false;
		}
	}

	public boolean allowUpdate(Date date) {
		UrlPermission up = authorizationManager.getPermission();
		if (up != null) {
			if (up.isUpdate()) {
				if (date != null) {
					if (lock(date) == false) {
						return true;
					} else {
						return false;
					}
				} else {
					return true;
				}
			} else {
				return up.isUpdate();
			}
		} else {
			return false;
		}
	}

	public boolean allowDelete(Date date) {
		UrlPermission up = authorizationManager.getPermission();
		if (up != null) {
			if (up.isDelete()) {
				if (date != null) {
					if (lock(date) == false) {
						return true;
					} else {
						return false;
					}
				} else {
					return true;
				}
			} else {
				return up.isDelete();
			}
		} else {
			return false;
		}
	}

	public Map<String, Object> installConfigPersonReport() {
		Map<String, Object> importParam = new HashMap<String, Object>();
		String image = FacesContext.getCurrentInstance().getExternalContext()
				.getRealPath("/resources/gfx/lixco_logo.png");
		importParam.put("logo", image);
		if (cf == null)
			getSession();
		if (cf != null) {
			if (",".equals(cf.getDecimalSeparator()))
				importParam.put("REPORT_LOCALE", new Locale("vi", "VN"));
			importParam.put("formatNumber", cf.getParttenNumber());
			importParam.put("formatDate", cf.getFormatDate());

		}
		return importParam;

	}

	public int getMax(List<Integer> list) {
		int max = 0;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) > max) {
				max = list.get(i);
			}
		}
		return max;
	}

	public AuthorizationManager getAuthorizationManager() {
		return authorizationManager;
	}

	public void setAuthorizationManager(AuthorizationManager authorizationManager) {
		this.authorizationManager = authorizationManager;
	}

	public PrivateConfig getCf() {
		if (cf == null)
			getSession();
		return cf;
	}

	public void setCf(PrivateConfig cf) {
		this.cf = cf;
	}

	public Account getAccount() {
		HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
				.getRequest();
		HttpSession session = req.getSession();
		account = (Account) session.getAttribute("account");
		String db = (String) session.getAttribute("database");
		try {
			if (account != null) {
				return accountServicePublic.findById(account.getId());
			} else {
				return new Account();
			}
		} catch (Exception e) {
			return null;
		}

	}
	public String getDatabase() {
		HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
				.getRequest();
		HttpSession session = req.getSession();
		String db = (String) session.getAttribute("database");
		return db;

	}

	public void notice(String content) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "ThÃ´ng bÃ¡o!", content);
		RequestContext.getCurrentInstance().showMessageInDialog(message);
	}

	public void noticeError(String content) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ThÃ´ng bÃ¡o!", content);
		RequestContext.getCurrentInstance().showMessageInDialog(message);
	}
}
