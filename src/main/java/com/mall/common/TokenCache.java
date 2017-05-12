package com.mall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 保存用户Token的有效期
 * Created by Administrator on 2017-5-1.
 */
public class TokenCache {
    public static final String TOKEN_PREFIX = "token_";
    private  static final Logger logger = LoggerFactory.getLogger(TokenCache.class);

    /**
     * guava本地缓存 初始化缓存1000，最大缓存项10000（LLU），最大过期时间 12H
     */
    private  static LoadingCache<String,String> localCache = CacheBuilder.newBuilder()
            .initialCapacity(1000).maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS)
            .build(new CacheLoader<String, String>() {
                //默认的数据加载实现，当调用get取值时，如果key没有对应值，就调用此方法进行加载 ，返回"null"是避免出现空指针
                @Override
                public String load(String s) throws Exception {
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
            if("null".equals(value)){
                return null;
            }
            return value;
        }catch (Exception e){
            logger.error("localCache get error",e);
        }
        return null;
    }
}
