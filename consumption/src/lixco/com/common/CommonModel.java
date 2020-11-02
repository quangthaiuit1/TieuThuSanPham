package lixco.com.common;





import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;

/**
 *
 * @author hp
 */
public class CommonModel {

    public static final byte HEADER_HTML = 0;
    public static final byte HEADER_JS = 1;
    public static final byte HEADER_TEXT_PAINT = 2;

    public static void prepareHeader(HttpServletResponse resp, byte type) {
        resp.setCharacterEncoding("utf-8");
        if (type == HEADER_HTML) {
            resp.setContentType("text/html; charset=utf-8");
        } else if (type == HEADER_JS) {
            resp.setContentType("text/javascript; charset=utf-8");
        } else if (type == HEADER_TEXT_PAINT) {
            resp.setContentType("text/plain; charset=utf-8");
        }
        String appName = "phuc.nguyen@vothuong";
        resp.addHeader("Server", appName);
    }

    public static void out(String content, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        out.print(content);
    }

    public static String toJson(int error, String msg, Map data) {
        Map mapdata = new HashMap();
        JSONObject ldata = new JSONObject();
        mapdata.put("err", error);
        mapdata.put("msg", msg);
        mapdata.put("dt", data);
        ldata.putAll(mapdata);
        return ldata.toJSONString();
    }
    public static String toJson(int error,String msg,String data){
        Map mapdata=new HashMap();
        JSONObject ldata=new JSONObject();
        mapdata.put("err",error);
        mapdata.put("msg", msg);
        mapdata.put("dt",data);
        ldata.putAll(mapdata);
        return ldata.toJSONString();
    }
    public static String toJson(int error,String msg){
        Map mapdata=new HashMap();
        JSONObject ldata=new JSONObject();
        mapdata.put("err",error);
        mapdata.put("msg", msg);
        ldata.putAll(mapdata);
        return ldata.toJSONString();
    }
}
