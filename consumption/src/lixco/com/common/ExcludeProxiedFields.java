package lixco.com.common;

import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class ExcludeProxiedFields implements ExclusionStrategy {
	@Override
	public boolean shouldSkipField(FieldAttributes fa) {
		return false;
	}

	@Override
	public boolean shouldSkipClass(Class<?> type) {
		return false;
	}
}
