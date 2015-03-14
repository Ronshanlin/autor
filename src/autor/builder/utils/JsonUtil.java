package autor.builder.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * json工具类<br> 
 *
 * @author shanlin
 */
public class JsonUtil {
    private static Gson gson;
    
    static{
        gson = new GsonBuilder().enableComplexMapKeySerialization().serializeNulls()
                .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    }
    
    public static String toJson(Object obj){
        return gson.toJson(obj);
    }
    
    public static <T>  T fromJson(String json, Class<T> classOfT){
        return gson.fromJson(json, classOfT);
    }
}
