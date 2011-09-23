package utils;

import java.util.Arrays;
import play.cache.Cache;
import utils.F.Callable;

import static utils.C.*;

public class CacheUtil {
    
    public static final String SEPARATOR = ".";
    
    public static <T> T get(String key, Class<T> retType, Callable<T> fetch) {
        T obj = (T) Cache.get(key);
        if (obj != null) {
            return obj;
        } else {
            obj = fetch.apply();
            Cache.add(key, obj);
        }
        return obj;
    }
    
    public static String key(String... parts) {
        return eList(Arrays.asList(parts)).mkString(SEPARATOR);
    }
}
