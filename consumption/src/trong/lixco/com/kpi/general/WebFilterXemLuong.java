package trong.lixco.com.kpi.general;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import trong.lixco.com.account.servicepublics.Account;

@javax.servlet.annotation.WebFilter("/nhanvien/*")
public class WebFilterXemLuong implements Filter{
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException,
			IOException {
		req.setCharacterEncoding("UTF-8");
		res.setCharacterEncoding("UTF-8");
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		try {
			HttpSession session = request.getSession();
			String db = null;
			Account account = null;
			if (session.getAttribute("account") != null)
				account = (Account) session.getAttribute("account");
			
			if (account == null) {
				
				// Chua co tai khoan, database dang nhap
				response.sendRedirect(request.getContextPath()+"/login/login.jsf");

			} else {
			
					chain.doFilter(req, res);
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public String getURL(HttpServletRequest req) {

		String scheme = req.getScheme(); // http
		String serverName = req.getServerName(); // hostname.com
		int serverPort = req.getServerPort(); // 80
		String contextPath = req.getContextPath(); // /mywebapp
		String servletPath = req.getServletPath(); // /servlet/MyServlet
		String pathInfo = req.getPathInfo(); // /a/b;c=123
		String queryString = req.getQueryString(); // d=789

		// Reconstruct original requesting URL
		StringBuilder url = new StringBuilder();
		url.append(scheme).append("://").append(serverName);

		if (serverPort != 80 && serverPort != 443) {
			url.append(":").append(serverPort);
		}

		url.append(contextPath).append(servletPath);

		if (pathInfo != null) {
			url.append(pathInfo);
		}
		if (queryString != null) {
			url.append("?").append(queryString);
		}
		return url.toString();
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

}
