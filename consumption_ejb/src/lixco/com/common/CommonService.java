package lixco.com.common;

import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


public class CommonService
{
//  private static final Gson _gson = new Gson();
  private static final Gson _gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).registerTypeAdapter(Date.class, new DateSerializer()).create();
  
  public CommonService() {}
  
  public static String FormatResponse(int error, String msg) {
    if ((error == 0) && (msg.equals(""))) {
      msg = "No error";
    }
    JsonObject json = new JsonObject();
    json.addProperty("err", Integer.valueOf(error));
    json.addProperty("msg", msg);
    
    return _gson.toJson(json);
  }
  
  public static String FormatResponse(int error, String msg, String data)
  {
    if ((error == 0) && (msg.equals(""))) {
      msg = "No error";
    }
    JsonObject json = new JsonObject();
    json.addProperty("err", Integer.valueOf(error));
    json.addProperty("msg", msg);
    json.addProperty("dt", data);
    
    return _gson.toJson(json);
  }
  
  public static String FormatResponse(int error, String msg, Object objData)
  {
    if ((error == 0) && (msg.equals(""))) {
      msg = "No error";
    }
    
    JsonObject json = new JsonObject();
    json.addProperty("err", Integer.valueOf(error));
    json.addProperty("msg", msg);
    json.add("dt", JsonParserUtil.getGson().toJsonTree(objData));
    
    return _gson.toJson(json);
  }
  
  public static String FormatResponse(int error, String msg, String objName, Object objData)
  {
    if ((error == 0) && (msg.equals(""))) {
      msg = "No error";
    }
    
    JsonObject json = new JsonObject();
    json.addProperty("err", Integer.valueOf(error));
    json.addProperty("msg", msg);
    JsonObject jsonParent = new JsonObject();
    jsonParent.add(objName, JsonParserUtil.getGson().toJsonTree(objData));
    json.add("dt", jsonParent);
    
    return _gson.toJson(json);
  }
  



  public static String FormatResponse(String objName, Object objData)
  {
    JsonObject json = new JsonObject();
    JsonObject jsonParent = new JsonObject();
    jsonParent.add(objName, JsonParserUtil.getGson().toJsonTree(objData));
    return _gson.toJson(jsonParent);
  }
  
  public static String FormatResponse(int error, String msg, JsonElement jsonEle) {
    if ((error == 0) && (msg.equals(""))) {
      msg = "No error";
    }
    JsonObject json = new JsonObject();
    json.addProperty("err", Integer.valueOf(error));
    json.addProperty("msg", msg);
    json.add("dt", jsonEle);
    return _gson.toJson(json);
  }

}
