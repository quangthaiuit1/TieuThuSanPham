package lixco.com.common;


import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;



import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by phuc on 15/07/2019.
 */

public class ApiCallClient extends OkHttpClient{
    private ApiCallClient(){
        this.newBuilder().connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS).build();
    }
    private static class LazyHolder {
        private static final ApiCallClient instance = new ApiCallClient();
    }

    public static ApiCallClient getInstance() {
        return LazyHolder.instance;
    }
    public Call loginApi(String url,String username,String password){
        try{
            this.connectTimeoutMillis();
            RequestBody formBody = new FormBody.Builder(StandardCharsets.UTF_8)
                    .add("user", username)
                    .add("pass", password)
                    .add("brand","HO CHI MINH")
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build();

            Call call = this.newCall(request);
            return call;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public Call getListObjectWithParam(String url,String table,String command,String dataJson){
        try{
            RequestBody formBody = RequestBody.create("cm="+command+"&dt="+escapeJavascript(dataJson),MediaType.parse("application/x-www-form-urlencoded; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(url+table)
                    .post(formBody)
                    .build();
            Call call = this.newCall(request);
            return call;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public Call updateObject(String url,String table,String dataJson){
        try{
            RequestBody formBody = RequestBody.create("cm=update&dt="+escapeJavascript(dataJson),MediaType.parse("application/x-www-form-urlencoded; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(url+table)
                    .post(formBody)
                    .build();
            Call call = this.newCall(request);
            return call;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public Call addObject(String url,String table,String dataJson){
    	try{
    		RequestBody body=RequestBody.create("cm=add&dt="+escapeJavascript(dataJson),MediaType.parse("application/x-www-form-urlencoded; charset=utf-8"));
    		Request request=new Request.Builder()
    				.url(url+table)
    				.post(body)
    				.build();
    		Call call= this.newCall(request);
    		return call;
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return null;
    }
    public Call deleteObject(String url,String table,String dataJson){
    	try{
    		RequestBody formBody=RequestBody.create("cm=delete&dt="+dataJson,MediaType.parse("application/x-www-form-urlencoded; charset=utf-8"));
    		Request request=new Request.Builder()
    				.url(url+table)
    				.post(formBody)
    				.build();
    		Call call= this.newCall(request);
    		return call;
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return null;
    }
    public static String escapeJavascript(String data) {
        try {
        	  data = data.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
              data = data.replaceAll("\\+", "%2B");
              return data;
        } catch (Exception ex) {
        	ex.printStackTrace();
            return null;
        }
    }
//    private static int GET = 1;
//    private static int POST = 2;
//
//    private static final MediaType JSON  = MediaType.parse("application/json; charset=utf-8");
//
//
//    public static JSONObject getJsonFromUrl(String url){
//        return getJson(url,GET,null);
//    }
//
//    public static JSONObject postJsonFromUrl(String url,String jsonString){
//        return getJson(url,POST,jsonString);
//    }
//
//    private static JSONObject getJson(String url,int method,String jsonStringParams){
//        JSONObject json = null;
//        // try parse the string to a JSON object
//        try {
//            String jsonString = makeServiceCall(url, method, jsonStringParams);
//            if (jsonString != null) {
//                json = new JSONObject(jsonString);
//            }
//            return json;
//        } catch (Exception e) {
//            Log.e("ApiCall", "Error parsing data " + e.toString());
//            return json;
//        }
//    }
//
//    private static String makeServiceCall(String url,int method, String jsonStringParams) throws IOException {
//        OkHttpClient client = new OkHttpClient();
//
//        Request request;
//        if (method == POST){
//            RequestBody body = RequestBody.create(JSON, jsonStringParams);
//            request = new Request.Builder()
//                    .url(url)
//                    .post(body)
//                    .build();
//        }else{
//            request = new Request.Builder()
//                    .url(url)
//                    .build();
//        }
//
//        Response response = client.newCall(request).execute();
//        return response.body().string();
//    }
}
