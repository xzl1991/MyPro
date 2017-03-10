package com.huoli.bmall.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * @author :zhouwenbin
 * @time :2016-2-23
 * @comment:
 **/
public class RedisLock {

	private final JedisPool jedisPool;
	
	private static final long timeout=10000000000L;
	
	private static Logger log = Logger.getLogger(RedisLock.class);

	public RedisLock(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}
	
//	private static RedisLock instance;

	    private static class SingletonHolder {
	        private final static RedisLock INSTANCE =new RedisLock(RedisUtils.getPool());
	     }
	   
	     public static RedisLock getInstance() {
	        return SingletonHolder.INSTANCE;
	     }

	/**
	 * 获取锁 如果锁可用 立即返回true， 否则返回false
	 * 
	 * @param key
	 * @return
	 */
	public boolean tryLock(String key) {
		return tryLock(key, RedisLock.timeout, TimeUnit.NANOSECONDS);
	}

	/**
	 * 锁在给定的等待时间内空闲，则获取锁成功 返回true， 否则返回false
	 * 
	 * @param key
	 * @param timeout
	 * @param unit
	 * @return
	 */
	private boolean tryLock(String key, long timeout, TimeUnit unit) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			long nano = System.nanoTime();
			Long rtn = jedis.setnx(key, Long.toString(nano));

			while (rtn == 0) {
				Long lockTs =null;
				try {
					String tempStr=jedis.get(key);
					if(StringUtils.isNotBlank(tempStr)){
						lockTs = Long.parseLong(tempStr);
						if ((System.nanoTime() - lockTs) > unit.toNanos(timeout)) {//对比时间戳超时
							String oldStr=jedis.getSet(key,Long.toString(System.nanoTime()));
							if(StringUtils.isBlank(oldStr)){
								return true;
							}
							Long old = Long.parseLong(oldStr);
							if (old == null || lockTs.equals(old)) {
								return true;
							}					
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					log.error(e);
				}
				Thread.sleep(50);
				rtn = jedis.setnx(key, Long.toString(nano));
			}
			return Boolean.TRUE;
		} catch (JedisConnectionException je) {
			je.printStackTrace();
			returnBrokenResource(jedis);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			returnResource(jedis);
		}
		return Boolean.FALSE;
	}

	/**
	 * 如果锁空闲立即返回 获取失败 一直等待
	 * 
	 * @param key
	 */
	public void lock(String key) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			do {
				String ts = Long.toString(System.nanoTime());
				Long i = jedis.setnx(key, ts);
				if (i == 1) {
					return;
				}
				Thread.sleep(50);
			} while (true);
		} catch (JedisConnectionException je) {
			je.printStackTrace();
			returnBrokenResource(jedis);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			returnResource(jedis);
		}
	}

	/**
	 * 释放锁
	 * 
	 * @param key
	 */
	public void unLock(String key) {
		List<String> list = new ArrayList<String>();
		list.add(key);
		unLock(list);
	}

	/**
	 * 批量释放锁
	 * 
	 * @param keyList
	 */
	public void unLock(Collection<String> keyList) {
		List<String> keys = new CopyOnWriteArrayList<String>();
		for (String key : keyList)
			keys.add(key);

		Jedis jedis = null;
		try {
			jedis = getResource();
			jedis.del(keys.toArray(new String[0]));
		} catch (JedisConnectionException je) {
			je.printStackTrace();
			returnBrokenResource(jedis);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			returnResource(jedis);
		}
	}

	private Jedis getResource() {
		return jedisPool.getResource();
	}

	/**
	 * 销毁连接
	 * 
	 * @param jedis
	 */
	private void returnBrokenResource(Jedis jedis) {
		if (jedis == null) {
			return;
		}
		try {
			// 容错
			jedisPool.returnBrokenResource(jedis);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void returnResource(Jedis jedis) {
		if (jedis == null) {
			return;
		}
		try {
			jedisPool.returnResource(jedis);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
