package lixco.com.common;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieHelper {
	  private static CookieHelper instance=null;
	  private static final Lock createLock_ = new ReentrantLock();
	  public static CookieHelper getInstance() {
			if (instance == null) {
				createLock_.lock();
				try {
					if (instance == null) {
						instance = new CookieHelper();
					}
				} finally {
					createLock_.unlock();
				}

			}
			return instance;
	}
	  public void setCookie(String name, String value, int expiry) throws UnsupportedEncodingException {
	    FacesContext facesContext = FacesContext.getCurrentInstance();
	    HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
	    HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
	    request.setCharacterEncoding("UTF-8");
	    response.setContentType("application/json; charset=utf-8");
	    value=encode(value);
	    Cookie cookie = null;
	    Cookie[] userCookies = request.getCookies();
	    if (userCookies != null && userCookies.length > 0 ) {
	        for (int i = 0; i < userCookies.length; i++) {
	            if (userCookies[i].getName().equals(name)) {
	                cookie = userCookies[i];
	                break;
	            }
	        }
	    }
	    if (cookie != null) {
	        cookie.setValue(value);
	    } else {
	        cookie = new Cookie(name, value);
	    }
	    cookie.setPath(request.getContextPath());
	    cookie.setMaxAge(expiry);
	    response.addCookie(cookie);
	  }

	  public Cookie getCookie(String name) throws IOException {
	    FacesContext facesContext = FacesContext.getCurrentInstance();
	    HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
	    request.setCharacterEncoding("UTF-8");
	    Cookie cookie = null;
	    Cookie[] userCookies = request.getCookies();
	    if (userCookies != null && userCookies.length > 0 ) {
	        for (int i = 0; i < userCookies.length; i++) {
	            if (userCookies[i].getName().equals(name)) {
	                cookie = userCookies[i];
//	                cookie.setValue(decode(cookie.getValue()));
	                return cookie;
	            }
	        }
	    }
	    return null;
	  }
	  public  String encode(String str){
		  try{
			  int incrementor = 0;
		      int dataLength = str.length();
		      StringBuffer tempBuffer = new StringBuffer();
		      while (incrementor < dataLength) {
		            char charecterAt = str.charAt(incrementor);
		            if (charecterAt == '%') {
		                tempBuffer.append("<percentage>");
		            } else if (charecterAt == '+') {
		                tempBuffer.append("<plus>");
		            } else {
		                tempBuffer.append(charecterAt);
		            }
		            incrementor++;
		       }
		      String result= URLEncoder.encode(tempBuffer.toString(),"UTF-8");
		      return result;
		  }catch(Exception e){
			  e.printStackTrace();
		  }
		  return "";
	  }
	  public  String decode(String str){
		  try{
			  String result=java.net.URLDecoder.decode(str,"UTF-8");
			  result = result.replaceAll("<percentage>", "%");
			  result = result.replaceAll("<plus>", "+");
			  return result;
		  }catch(Exception e){
			  e.printStackTrace();
		  }
		  return "";
	  }
}
