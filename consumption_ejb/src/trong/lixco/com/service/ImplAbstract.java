package trong.lixco.com.service;


import java.util.List;

public interface ImplAbstract<T> {
	
	T findById(long id);
	
	T create(T device);
	
	T update(T device);
	
	boolean delete(T device);
	
	List<T> findAll();

}
