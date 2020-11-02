package lixco.com.common;

import java.io.IOException;
import java.util.Collection;

import org.hibernate.Hibernate;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class PersistentCollectionCheckingTypeAdapter extends TypeAdapter<Collection> {
	public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
		@Override
		public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
			if (Collection.class.isAssignableFrom(type.getRawType())) {
				TypeAdapter delegate = gson.getDelegateAdapter(this, type);

				return (TypeAdapter<T>) new PersistentCollectionCheckingTypeAdapter(delegate);
			}

			return null;
		}
	};

	private final TypeAdapter delegate;

	PersistentCollectionCheckingTypeAdapter(TypeAdapter delegate) {
		this.delegate = delegate;
	}

	@Override
	public void write(JsonWriter out, Collection value) throws IOException {
		if (value == null) {
			out.nullValue();
			return;
		}

		// This catches non-proxied collections AND initialized proxied
		// collections
		if (Hibernate.isInitialized(value)) {
			delegate.write(out, value);
			return;
		}

		// write out null for uninitialized proxied collections
		 out.nullValue();
	}

	@Override
	public Collection read(JsonReader in) throws IOException {
		return (Collection) delegate.read(in);
	}
}
