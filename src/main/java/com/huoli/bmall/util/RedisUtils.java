package com.huoli.bmall.util;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.log4j.Logger;

import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.Tuple;

/**
@author :zhouwenbin
@time   :2016-2-22
@comment:
 **/
public class RedisUtils {
	
	private static Logger logger = Logger.getLogger(RedisUtils.class);

    private RedisUtils() {
    }
    
    private static class SingletonHolder {
        private final static RedisUtils INSTANCE =new RedisUtils();
     }
   
     public static RedisUtils getInstance() {
        return SingletonHolder.INSTANCE;
     }

    // jedis池
    private static JedisPool pool;

    static {
        // 加载redis配置文件
        ResourceBundle bundle = ResourceBundle.getBundle("redis");
        if (bundle == null) {
            logger.error("error", new IllegalArgumentException("[redis.properties] is not found!"));
        }

        // 创建jedis池配置实例
        JedisPoolConfig config = new JedisPoolConfig();
        //设置池配置项值
        try {
            config.setMaxActive(Integer.valueOf(bundle.getString("redis.maxActive")));
            config.setMaxIdle(Integer.valueOf(bundle.getString("redis.maxIdle")));
            config.setMaxWait(Long.valueOf(bundle.getString("redis.maxWait")));
            config.setTestOnBorrow(Boolean.valueOf(bundle.getString("redis.testOnBorrow")));
           // config.setTestOnReturn(Boolean.valueOf(bundle.getString("redis.pool.testOnReturn")));
           
            String host =bundle.getString("redis.host");
            String pass= bundle.getString("redis.pass");
            int port= Integer.parseInt(bundle.getString("redis.port"));
            //根据配置实例化jedis池
            pool = new JedisPool(config, host, port,Protocol.DEFAULT_TIMEOUT,pass,Protocol.DEFAULT_DATABASE);
        } catch (Exception e) {
            logger.error("error", e);
        }
    }

    /**
     * <p>通过key获取储存在redis中的value</p>
     * <p>并释放连接</p>
     *
     * @param key
     * @return 成功返回value 失败返回null
     */
    public String get(String key) {
        Jedis jedis = null;
        String value = null;
        try {
            jedis = pool.getResource();
            value = jedis.get(key);
        } catch (Exception e) {
            pool.returnBrokenResource(jedis);
            e.printStackTrace();
        } finally {
            returnResource(pool, jedis);
        }
        return value;
    }


    /**
     * <p>向redis存入key和value,并释放连接资源</p>
     * <p>如果key已经存在 则覆盖</p>
     *
     * @param key
     * @param value
     * @return 成功 返回OK 失败返回 0
     */
    public void setWithLock(String key, String value) {
        RedisLock lock = new RedisLock(pool);
        if (lock.tryLock(key)) {
            Jedis jedis = null;
            try {
                jedis = pool.getResource();
                jedis.set(key, value);
            } catch (Exception e) {
                pool.returnBrokenResource(jedis);
                e.printStackTrace();
            } finally {
                returnResource(pool, jedis);
            }
            lock.unLock(key);
        } else
            set(key, value);
    }
    
    /**
     * <p>向redis存入key和value,并释放连接资源</p>
     * <p>如果key已经存在 则覆盖</p>
     *
     * @param key
     * @param value
     * @return 成功 返回OK 失败返回 0
     */
    public String set(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.set(key, value);
        } catch (Exception e) {
            pool.returnBrokenResource(jedis);
            e.printStackTrace();
            return "0";
        } finally {
            returnResource(pool, jedis);
        }
    }
    
    /**
     * <p>向redis存入key的incr 默认+count,expireSeconds后key失效，并释放连接资源</p>
     *
     * @param key
     * @return 成功 返回>0的long 失败返回 0
     */
    public Long setIncr(String key,int count,int expireSeconds){
    	Jedis jedis = null;
        try {
            jedis = pool.getResource();
            Long rtn= jedis.incrBy(key,count);
            jedis.expire(key, expireSeconds);
            return rtn;
        } catch (Exception e) {
            pool.returnBrokenResource(jedis);
            e.printStackTrace();
            return 0L;
        } finally {
            returnResource(pool, jedis);
        }
    }

    public String set(String key, int value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            String strValue=String.valueOf(value);
            return jedis.set(key, strValue);
        } catch (Exception e) {
            pool.returnBrokenResource(jedis);
            e.printStackTrace();
            return "0";
        } finally {
            returnResource(pool, jedis);
        }
    }
    
    /**
     * <p>设置key value并制定这个键值的有效期</p>
     *
     * @param key
     * @param value
     * @param seconds 单位:秒
     * @return 成功返回OK 失败和异常返回null
     */
    public String setex(String key, int value, int seconds) {
        Jedis jedis = null;
        String res = null;
        try {
            jedis = pool.getResource();
            String strValue=String.valueOf(value);
            res = jedis.setex(key, seconds, strValue);
        } catch (Exception e) {
            pool.returnBrokenResource(jedis);
            e.printStackTrace();
        } finally {
            returnResource(pool, jedis);
        }
        return res;
    }

    public String setex(String key, String value, int seconds) {
        Jedis jedis = null;
        String res = null;
        try {
            jedis = pool.getResource();
            res = jedis.setex(key, seconds, value);
        } catch (Exception e) {
            pool.returnBrokenResource(jedis);
            e.printStackTrace();
        } finally {
            returnResource(pool, jedis);
        }
        return res;
    }
    
    public Long setnxex(String key, String value, int seconds) {
        Jedis jedis = null;
        Long res = null;
        try {
            jedis = pool.getResource();
            res = jedis.setnx(key, value);
            jedis.expire(key, seconds);
        } catch (Exception e) {
            pool.returnBrokenResource(jedis);
            e.printStackTrace();
        } finally {
            returnResource(pool, jedis);
        }
        return res;
    }

    /**
     * <p>通过key给field设置指定的值,如果key不存在,则先创建</p>
     *
     * @param key
     * @param field 字段
     * @param value
     * @return 如果存在返回0 异常返回null
     */
    public Long hset(String key, String field, String value,int seconds) {
        Jedis jedis = null;
        Long res = null;
        try {
            jedis = pool.getResource();
            res = jedis.hset(key, field, value);
            jedis.expire(key, seconds);
        } catch (Exception e) {
            pool.returnBrokenResource(jedis);
            e.printStackTrace();
        } finally {
            returnResource(pool, jedis);
        }
        return res;
    }
    
    public Long hset(String key, Map<String,String> map,int seconds) {
        Jedis jedis = null;
        Long res = null;
        try {
            jedis = pool.getResource();
            for(Map.Entry<String, String> entry:map.entrySet()){
            	res = jedis.hset(key, entry.getKey(), entry.getValue());
            	jedis.expire(key, seconds);
            }
        } catch (Exception e) {
            pool.returnBrokenResource(jedis);
            e.printStackTrace();
        } finally {
            returnResource(pool, jedis);
        }
        return res;
    }
    
    public Map<String, String> getMap(String key) {
        Jedis jedis = null;
        Map<String, String> map = null;
        try {
            jedis = pool.getResource();
            map = jedis.hgetAll(key);
        } catch (Exception e) {
            pool.returnBrokenResource(jedis);
            e.printStackTrace();
            return null;
        } finally {
            returnResource(pool, jedis);
        }
        return map;
    }
    
	public List<String> getMapValue(String key, String... fields) {
        Jedis jedis = null;
        List<String> res = null;
        try {
            jedis = pool.getResource();
            res = jedis.hmget(key, fields);
        } catch (Exception e) {
            pool.returnBrokenResource(jedis);
            e.printStackTrace();
        } finally {
            returnResource(pool, jedis);
        }
        return res;
        }
    public String getsetvalue(String key, String value, int seconds) {
        Jedis jedis = null;
        String res = null;
        try {
            jedis = pool.getResource();
            res = jedis.getSet(key, value);
            jedis.expire(key, seconds);
        } catch (Exception e) {
            pool.returnBrokenResource(jedis);
            e.printStackTrace();
        } finally {
            returnResource(pool, jedis);
        }
        return res;
    }

    /**
     * 清空redis
     */
    public void clearAll() {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            Set<String> keys = jedis.keys("*");
            System.out.println("key size:" + keys.size());
            jedis.del(keys.toArray(new String[0]));
        } catch (Exception e) {
            pool.returnBrokenResource(jedis);
            e.printStackTrace();
        } finally {
            returnResource(pool, jedis);
        }
    }
    
    /**
     * param pattern
     * 返回pattern匹配的kes的Set
     */
    public Set<String> getKeysByPattern(String pattern) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            pattern="*"+pattern+"*";
            return jedis.keys(pattern);
        } catch (Exception e) {
            pool.returnBrokenResource(jedis);
            e.printStackTrace();
        } finally {
            returnResource(pool, jedis);
        }
        return null;
    }

    public Set<String> getAllKey() {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.keys("*");
        } catch (Exception e) {
            pool.returnBrokenResource(jedis);
            e.printStackTrace();
        } finally {
            returnResource(pool, jedis);
        }
        return null;
    }

    /**
     * <p>删除指定的key,也可以传入一个包含key的数组</p>
     *
     * @param keys 一个key  也可以使 string 数组
     * @return 返回删除成功的个数
     */
    public Long del(String... keys) {
        if (null == keys || keys.length == 0)
            return 0L;

        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.del(keys);
        } catch (Exception e) {
            e.printStackTrace();
            pool.returnBrokenResource(jedis);
            return 0L;
        } finally {
            returnResource(pool, jedis);
        }
    }


    /**
     * <p>判断key是否存在</p>
     *
     * @param key
     * @return true OR false
     */
    public Boolean exists(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.exists(key);
        } catch (Exception e) {
            pool.returnBrokenResource(jedis);
            e.printStackTrace();
            return false;
        } finally {
            returnResource(pool, jedis);
        }
    }

    /**
     * <p>设置key value,如果key已经存在则返回0,nx==> not exist</p>
     *
     * @param key
     * @param value
     * @return 成功返回1 如果存在 和 发生异常 返回 0
     */
    public Long setnx(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.setnx(key, value);
        } catch (Exception e) {
            pool.returnBrokenResource(jedis);
            e.printStackTrace();
            return 0L;
        } finally {
            returnResource(pool, jedis);
        }
    }

    /**
     * 返还到连接池
     */
    public static void returnResource(JedisPool pool, Jedis jedis) {
        if (jedis != null) {
            pool.returnResource(jedis);
        }
    }
    
    public static void returnBinaryJedis(JedisPool pool, BinaryJedis jedis) {
        if (jedis != null) {
            pool.returnResource(jedis);
        }
    }


    public static JedisPool getPool() {
        return pool;
    }
    
    public Long zadd(String key, double score,String member) {
        Jedis jedis = null;
        Long res = 0l;
        try {
            jedis = pool.getResource();
            return jedis.zadd(key, score, member);
        } catch (Exception e) {
            pool.returnBrokenResource(jedis);
            e.printStackTrace();
        } finally {
            returnResource(pool, jedis);
        }
        return res;
    }
    
    /*
     * member为序列化后的
     */
    public Long zadd(String key, double score,byte[] member) {
        Jedis jedis = null;
        Long res = 0l;
        try {
            jedis = pool.getResource();
            return jedis.zadd(key.getBytes(), score, member);
        } catch (Exception e) {
            pool.returnBrokenResource(jedis);
            e.printStackTrace();
        } finally {
            returnResource(pool, jedis);
        }
        return res;
    }
    
    /**
     * 返回名称为key的zset（元素已按score从大到小排序）中的index从start到end的所有元素
     */
    public LinkedHashSet<Tuple> zrevrangeWithScores(String key, long start,long end) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return (LinkedHashSet<Tuple>)jedis.zrevrangeWithScores(key, start, end);
        } catch (Exception e) {
            pool.returnBrokenResource(jedis);
            e.printStackTrace();
        } finally {
            returnResource(pool, jedis);
        }
        return null;
    }
    public LinkedHashSet<byte[]> zrevrange(String key, int start,int end) {
    	BinaryJedis jedis = null;
        try {
            jedis = pool.getResource();
            return (LinkedHashSet<byte[]>)jedis.zrevrange(key.getBytes(), start, end);
        } catch (Exception e) {
            pool.returnBrokenResource(jedis);
            e.printStackTrace();
        } finally {
        	returnBinaryJedis(pool, jedis);
        }
        return null;
    }
    
    /**
     * 删除名称为key的zset中rank >= min且rank <= max的所有元素
     * 返回被删除的成员数量
     */
    public long zremrangebyrank(String key, long start,long end) {
        Jedis jedis = null;
        Long res = 0l;
        try {
            jedis = pool.getResource();
            return jedis.zremrangeByRank(key, start, end);
        } catch (Exception e) {
            pool.returnBrokenResource(jedis);
            e.printStackTrace();
        } finally {
            returnResource(pool, jedis);
        }
        return res;
    }
    
    public long expireKey(String key,int expireSeconds){
    	Jedis jedis = null;
        Long res = 0l;
        try {
            jedis = pool.getResource();
            return jedis.expire(key, expireSeconds);
        } catch (Exception e) {
            pool.returnBrokenResource(jedis);
            e.printStackTrace();
        } finally {
            returnResource(pool, jedis);
        }
        return res;
    }

    public static void main(String[] args) {
        //本地测试
        String key = "zwb";
        Jedis jedis = pool.getResource();
        for (int i = 1001; i < 2000; i++) {
            jedis.setbit(key, i, true);
        }
    }

}
