package lixco.com.common;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionHelper {
	private static SessionHelper instance=null;
	private static final Lock createLock_ = new ReentrantLock();
	private SessionHelper(){}
	public static SessionHelper getInstance(){
		if (instance == null) {
			createLock_.lock();
			try {
				if (instance == null) {
					instance = new SessionHelper();
				}
			} finally {
				createLock_.unlock();
			}

		}
		return instance;
	}
	public <T> T getSession(String name,HttpServletRequest request,Class<T> type) {
		try{
		    HttpSession session = request.getSession();
			return type.cast(session.getAttribute(name));
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
    }
	public <T> void setSession(String name,T value,HttpServletRequest request){
		try{
			
			HttpSession session=request.getSession();
			session.setAttribute(name, value);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
