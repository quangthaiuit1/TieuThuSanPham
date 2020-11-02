package lixco.com.common;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;



public class JsonParserUtil {

	public JsonParserUtil() {
	}

	private static final Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer())
			.registerTypeAdapter(Date.class, new DateSerializer()).registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY).
			registerTypeAdapterFactory(PersistentCollectionCheckingTypeAdapter.FACTORY).create();

	public static Gson getGson() {
		return gson;
	}

	public static String parseStringValue(String data, String elementName) {
		JsonParser jsonParser = new JsonParser();
		JsonElement jsonEle = jsonParser.parse(data);
		return jsonEle.getAsJsonObject().get(elementName).getAsString();

	}

	public static String parseStringValue(String data, String objectName, String elementName) {
		JsonParser jsonParser = new JsonParser();
		JsonElement jsonEle = jsonParser.parse(data);
		return jsonEle.getAsJsonObject().getAsJsonObject(objectName).get(elementName).getAsString();
	}

	public static JsonArray parseStringValueArray(String data, String objectName, String elementName) {
		JsonParser jsonParser = new JsonParser();
		JsonElement jsonEle = jsonParser.parse(data);
		JsonObject j = jsonEle.getAsJsonObject().getAsJsonObject(objectName);
		JsonArray a = j.get(elementName).getAsJsonArray();
		return a;
	}

	public static <T> void convertJsonArrayToList(String data, String objectName, String elementName, List<T> result,
			Class<T> l) throws UnsupportedEncodingException {
		JsonParser jsonParser = new JsonParser();
		JsonElement jsonEle = jsonParser.parse(data);
		JsonObject j = jsonEle.getAsJsonObject().getAsJsonObject(objectName);
		JsonArray a = j.get(elementName).getAsJsonArray();
		if (a != null) {
			for (int i = 0; i < a.size(); i++) {
				JsonObject jobj = a.get(i).getAsJsonObject();
				result.add(gson.fromJson(jobj, l));
			}
		}
	}

	public static HolderParser getValueString(JsonElement json, String elementName,StringBuilder messages) {
		HolderParser holder = new HolderParser();
		try {
			JsonElement j = json.getAsJsonObject().get(elementName);
			if (j !=null) {
				String r = j.isJsonNull() ? null : j.getAsString();
				holder.setErr(0);
				holder.setValue((Object)r);
			} else {
				holder.setErr(-2);
				holder.setValue(null);
				if (messages != null)
					messages.append(elementName + " not exists ");
			}
		} catch (Exception e) {
			holder.setErr(-1);
			holder.setValue(null);
			if (messages != null)
				messages.append(elementName + " error code ");
		}
		return holder;
	}
	public static HolderParser getValueObject(JsonElement json,String elementName,Class<?> T,StringBuilder messages){
		HolderParser holder = new HolderParser();
		try{
			if(json.getAsJsonObject().has(elementName)){
				holder.setErr(0);
				holder.setValue(gson.fromJson(json.getAsJsonObject().get(elementName), T));
			}else{
				holder.setErr(-2);
				holder.setValue(null);
				if(messages !=null)
					messages.append(elementName + " not exists ");
			}
		}catch(Exception e){
			holder.setErr(-1);
			holder.setValue(null);
			if (messages != null)
				messages.append(elementName + " error code ");
		}
		return holder;
	}

	public static HolderParser getValueNumber(JsonElement json, String elementName, StringBuilder message) {
		// holder container result
		HolderParser holder = new HolderParser();
		try {
			JsonElement j = json.getAsJsonObject().get(elementName);
			if (j != null) {
				Number r = j.isJsonNull() ? 0 : j.getAsNumber();
				holder.setErr(0);
				holder.setValue(r);
			} else {
				holder.setErr(-2);
				holder.setValue(0);
				if (message != null)
					message.append(elementName + " not exists ");
			}
		} catch (Exception e) {
			holder.setErr(-1);
			holder.setValue(0);
			if (message != null)
				message.append(elementName + " error code ");
		}
		return holder;
	}
	public static HolderParser getValueList(JsonElement json,String elementName,StringBuilder messages,Type type ){
		// holder container result
		HolderParser holder = new HolderParser();
		try{
			JsonElement j=json.getAsJsonObject().getAsJsonArray(elementName);
			if(j != null){
				List l=JsonParserUtil.getGson().fromJson(j,type );
				holder.setErr(0);
				holder.setValue((Object)l);
			}else{
				holder.setErr(-2);
				holder.setValue(null);
				if (messages != null)
					messages.append(elementName + " not exists ");
			}
		}catch(Exception e){
			holder.setErr(-1);
			if (messages != null)
				messages.append(elementName + " error code ");
		}
		return holder;
	}
}
