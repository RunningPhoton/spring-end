package com.PT.tools;


import com.google.common.cache.CacheLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.TimeUnit;
public class TokenOptions {
    private static Logger logger = LoggerFactory.getLogger(TokenOptions.class);

    public static final String TOKEN_PREFIX = "toke_is_";
    //LRU
    private static LoadingCache<String,String> localCache =
            com.google.common.cache.CacheBuilder.newBuilder().initialCapacity(1000).
                    maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS).
                    build(new CacheLoader<String, String>() {
        @Override
        public String load(String k1) throws Exception {
            return "null";
        }
    });

    public static void setKey(String key,String value){
        localCache.put(key,value);
    }

    public static String getKey(String key){
        String value = null;
        try {
            value = localCache.get(key);
            if ("null".equals(value)){
                return null;
            }
            return value;
        }catch (Exception e){
            logger.error("localCache get error",e);
        }
        return null;
    }
}
