package com.example.test.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;

@Slf4j
@Component
@Api("Jedis Cache 工具类")
public class JedisUtils {
	
	private JedisPool jedisPool;

	/**
	 * 获取缓存
	 * @param key 键
	 * @return 值
	 */
	public  String get(String key) {
		String value = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(key)) {
				value = jedis.get(key);
				value = StringUtils.isNotBlank(value) && !"nil".equalsIgnoreCase(value) ? value : null;
				log.debug("get {} = {}", key, value);
			}
		} catch (Exception e) {
			log.warn("get {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return value;
	}
	
	/**
	 * 获取缓存
	 * @param key 键
	 * @return 值
	 */
	public  Object getObject(String key) {
		Object value = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(getBytesKey(key))) {
				value = toObject(jedis.get(getBytesKey(key)));
				log.debug("getObject {} = {}", key, value);
			}
		} catch (Exception e) {
			log.warn("getObject {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return value;
	}
	
	/**
	 * 设置缓存
	 * @param key 键
	 * @param value 值
	 * @param cacheSeconds 超时时间，0为不超时
	 * @return
	 */
	public  String set(String key, String value, int cacheSeconds) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.set(key, value);
			if (cacheSeconds != 0) {
				jedis.expire(key, cacheSeconds);
			}
			log.debug("set {} = {}", key, value);
		} catch (Exception e) {
			log.warn("set {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}
	
	/**
	 * 延长缓存时间
	 * @param key 键
	 * @param value 值
	 * @param cacheSeconds 超时时间，0为不超时
	 * @return
	 */
	public  Long expire(String key, int cacheSeconds) {
		Long result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (cacheSeconds != 0) {
				result=jedis.expire(key, cacheSeconds);
			}
			log.debug("set {} = {}", key);
		} catch (Exception e) {
			log.warn("set {} = {}", key, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 设置缓存
	 * @param key 键
	 * @param value 值
	 * @param cacheSeconds 超时时间，0为不超时
	 * @return
	 */
	public  String setObject(String key, Object value, int cacheSeconds) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.set(getBytesKey(key), toBytes(value));
			if (cacheSeconds != 0) {
				jedis.expire(key, cacheSeconds);
			}
			log.debug("setObject {} = {}", key, value);
		} catch (Exception e) {
			log.warn("setObject {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}
	
	/**
	 * 获取List缓存
	 * @param key 键
	 * @return 值
	 */
	public  List<String> getList(String key) {
		List<String> value = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(key)) {
				value = jedis.lrange(key, 0, -1);
				log.debug("getList {} = {}", key, value);
			}
		} catch (Exception e) {
			log.warn("getList {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return value;
	}
	
	/**
	 * 获取List缓存
	 * @param key 键
	 * @return 值
	 */
	public  List<Object> getObjectList(String key)throws Exception {
		List<Object> value = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(getBytesKey(key))) {
//				List<byte[]> list = jedis.lrange(getBytesKey(key), 0, -1);
//				value = Lists.newArrayList();
//				for (byte[] bs : list){
//					value.add(toObject(bs));
//				}
				
				byte[] bs = jedis.get(getBytesKey(key));
				value = bytesToList(bs);
				log.debug("getObjectList {} = {}", key, value);
			}
		} catch (Exception e) {
			log.warn("getObjectList {} = {}", key, value, e);
			throw new Exception("获取List缓存失败",e);
		} finally {
			returnResource(jedis);
		}
		return value;
	}
	
	/**
	 * 设置List缓存
	 * @param key 键
	 * @param value 值
	 * @param cacheSeconds 超时时间，0为不超时
	 * @return
	 */
	public  long setList(String key, List<String> value, int cacheSeconds) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(key)) {
				jedis.del(key);
			}
			result = jedis.rpush(key, (String[])value.toArray());
			if (cacheSeconds != 0) {
				jedis.expire(key, cacheSeconds);
			}
			log.debug("setList {} = {}", key, value);
		} catch (Exception e) {
			log.warn("setList {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}
	
	/**
	 * 设置List缓存
	 * @param key 键
	 * @param value 值
	 * @param cacheSeconds 超时时间，0为不超时
	 * @return
	 */
	public  long setObjectList(String key, List<Object> value, int cacheSeconds) throws Exception {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(getBytesKey(key))) {
				jedis.del(key);
			}
//			List<byte[]> list = Lists.newArrayList();
//			for (Object o : value){
//				list.add(toBytes(o));
//			}
//			result = jedis.rpush(getBytesKey(key), (byte[][])list.toArray());
			String result1 = jedis.set(getBytesKey(key), listToBytes(value));
			System.out.println(result1);
			
			if (cacheSeconds != 0) {
				jedis.expire(key, cacheSeconds);
			}
			log.debug("setObjectList {} = {}", key, value);
		} catch (Exception e) {
			log.warn("setObjectList {} = {}", key, value, e);
			throw new Exception("设置List缓存失败",e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}
	
	/**
	 * 向List缓存中添加值
	 * @param key 键
	 * @param value 值
	 * @return
	 */
	public  long listAdd(String key, String... value) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.rpush(key, value);
			log.debug("listAdd {} = {}", key, value);
		} catch (Exception e) {
			log.warn("listAdd {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}
	
	/**
	 * 向List缓存中添加值
	 * @param key 键
	 * @param value 值
	 * @return
	 */
	public  long listObjectAdd(String key, Object... value) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			List<byte[]> list = Lists.newArrayList();
			for (Object o : value){
				list.add(toBytes(o));
			}
			result = jedis.rpush(getBytesKey(key), (byte[][])list.toArray());
			log.debug("listObjectAdd {} = {}", key, value);
		} catch (Exception e) {
			log.warn("listObjectAdd {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 获取缓存
	 * @param key 键
	 * @return 值
	 */
	public  Set<String> getSet(String key) {
		Set<String> value = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(key)) {
				value = jedis.smembers(key);
				log.debug("getSet {} = {}", key, value);
			}
		} catch (Exception e) {
			log.warn("getSet {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return value;
	}
	
	/**
	 * 获取缓存
	 * @param key 键
	 * @return 值
	 */
	public  Set<Object> getObjectSet(String key) {
		Set<Object> value = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(getBytesKey(key))) {
				value = Sets.newHashSet();
				Set<byte[]> set = jedis.smembers(getBytesKey(key));
				for (byte[] bs : set){
					value.add(toObject(bs));
				}
				log.debug("getObjectSet {} = {}", key, value);
			}
		} catch (Exception e) {
			log.warn("getObjectSet {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return value;
	}
	
	/**
	 * 设置Set缓存
	 * @param key 键
	 * @param value 值
	 * @param cacheSeconds 超时时间，0为不超时
	 * @return
	 */
	public  long setSet(String key, Set<String> value, int cacheSeconds) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(key)) {
				jedis.del(key);
			}
			result = jedis.sadd(key, (String[])value.toArray());
			if (cacheSeconds != 0) {
				jedis.expire(key, cacheSeconds);
			}
			log.debug("setSet {} = {}", key, value);
		} catch (Exception e) {
			log.warn("setSet {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}
	
	/**
	 * 设置Set缓存
	 * @param key 键
	 * @param value 值
	 * @param cacheSeconds 超时时间，0为不超时
	 * @return
	 */
	public  long setObjectSet(String key, Set<Object> value, int cacheSeconds) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(getBytesKey(key))) {
				jedis.del(key);
			}
			Set<byte[]> set = Sets.newHashSet();
			for (Object o : value){
				set.add(toBytes(o));
			}
			result = jedis.sadd(getBytesKey(key), (byte[][])set.toArray());
			if (cacheSeconds != 0) {
				jedis.expire(key, cacheSeconds);
			}
			log.debug("setObjectSet {} = {}", key, value);
		} catch (Exception e) {
			log.warn("setObjectSet {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}
	
	/**
	 * 向Set缓存中添加值
	 * @param key 键
	 * @param value 值
	 * @return
	 */
	public  long setSetAdd(String key, String... value) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.sadd(key, value);
			log.debug("setSetAdd {} = {}", key, value);
		} catch (Exception e) {
			log.warn("setSetAdd {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 向Set缓存中添加值
	 * @param key 键
	 * @param value 值
	 * @return
	 */
	public  long setSetObjectAdd(String key, Object... value) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			Set<byte[]> set = Sets.newHashSet();
			for (Object o : value){
				set.add(toBytes(o));
			}
			result = jedis.rpush(getBytesKey(key), (byte[][])set.toArray());
			log.debug("setSetObjectAdd {} = {}", key, value);
		} catch (Exception e) {
			log.warn("setSetObjectAdd {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}
	
	/**
	 * 获取Map缓存
	 * @param key 键
	 * @return 值
	 */
	public  Map<String, String> getMap(String key) {
		Map<String, String> value = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(key)) {
				value = jedis.hgetAll(key);
				log.debug("getMap {} = {}", key, value);
			}
		} catch (Exception e) {
			log.warn("getMap {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return value;
	}
	
	/**
	 * 获取Map缓存
	 * @param key 键
	 * @return 值
	 */
	public  Map<String, Object> getObjectMap(String key) {
		Map<String, Object> value = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(getBytesKey(key))) {
				value = Maps.newHashMap();
				Map<byte[], byte[]> map = jedis.hgetAll(getBytesKey(key));
				for (Map.Entry<byte[], byte[]> e : map.entrySet()){
					value.put(StringUtils.toString(e.getKey()), toObject(e.getValue()));
				}
				log.debug("getObjectMap {} = {}", key, value);
			}
		} catch (Exception e) {
			log.warn("getObjectMap {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return value;
	}
	
	/**
	 * 设置Map缓存
	 * @param key 键
	 * @param value 值
	 * @param cacheSeconds 超时时间，0为不超时
	 * @return
	 */
	public  String setMap(String key, Map<String, String> value, int cacheSeconds) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(key)) {
				jedis.del(key);
			}
			result = jedis.hmset(key, value);
			if (cacheSeconds != 0) {
				jedis.expire(key, cacheSeconds);
			}
			log.debug("setMap {} = {}", key, value);
		} catch (Exception e) {
			log.warn("setMap {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}
	
	/**
	 * 设置Map缓存
	 * @param key 键
	 * @param value 值
	 * @param cacheSeconds 超时时间，0为不超时
	 * @return
	 */
	public  String setObjectMap(String key, Map<String, Object> value, int cacheSeconds) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(getBytesKey(key))) {
				jedis.del(key);
			}
			Map<byte[], byte[]> map = Maps.newHashMap();
			for (Map.Entry<String, Object> e : value.entrySet()){
				map.put(getBytesKey(e.getKey()), toBytes(e.getValue()));
			}
			result = jedis.hmset(getBytesKey(key), (Map<byte[], byte[]>)map);
			if (cacheSeconds != 0) {
				jedis.expire(key, cacheSeconds);
			}
			log.debug("setObjectMap {} = {}", key, value);
		} catch (Exception e) {
			log.warn("setObjectMap {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}
	
	/**
	 * 向Map缓存中添加值
	 * @param key 键
	 * @param value 值
	 * @return
	 */
	public  String mapPut(String key, Map<String, String> value) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.hmset(key, value);
			log.debug("mapPut {} = {}", key, value);
		} catch (Exception e) {
			log.warn("mapPut {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}
	
	/**
	 * 向Map缓存中添加值
	 * @param key 键
	 * @param value 值
	 * @return
	 */
	public  String mapObjectPut(String key, Map<String, Object> value) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			Map<byte[], byte[]> map = Maps.newHashMap();
			for (Map.Entry<String, Object> e : value.entrySet()){
				map.put(getBytesKey(e.getKey()), toBytes(e.getValue()));
			}
			result = jedis.hmset(getBytesKey(key), (Map<byte[], byte[]>)map);
			log.debug("mapObjectPut {} = {}", key, value);
		} catch (Exception e) {
			log.warn("mapObjectPut {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}
	
	/**
	 * 移除Map缓存中的值
	 * @param key 键
	 * @param value 值
	 * @return
	 */
	public  long mapRemove(String key, String mapKey) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.hdel(key, mapKey);
			log.debug("mapRemove {}  {}", key, mapKey);
		} catch (Exception e) {
			log.warn("mapRemove {}  {}", key, mapKey, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}
	
	/**
	 * 移除Map缓存中的值
	 * @param key 键
	 * @param value 值
	 * @return
	 */
	public  long mapObjectRemove(String key, String mapKey) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.hdel(getBytesKey(key), getBytesKey(mapKey));
			log.debug("mapObjectRemove {}  {}", key, mapKey);
		} catch (Exception e) {
			log.warn("mapObjectRemove {}  {}", key, mapKey, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}
	
	/**
	 * 判断Map缓存中的Key是否存在
	 * @param key 键
	 * @param value 值
	 * @return
	 */
	public  boolean mapExists(String key, String mapKey) {
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.hexists(key, mapKey);
			log.debug("mapExists {}  {}", key, mapKey);
		} catch (Exception e) {
			log.warn("mapExists {}  {}", key, mapKey, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}
	
	/**
	 * 判断Map缓存中的Key是否存在
	 * @param key 键
	 * @param value 值
	 * @return
	 */
	public  boolean mapObjectExists(String key, String mapKey) {
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.hexists(getBytesKey(key), getBytesKey(mapKey));
			log.debug("mapObjectExists {}  {}", key, mapKey);
		} catch (Exception e) {
			log.warn("mapObjectExists {}  {}", key, mapKey, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}
	
	/**
	 * 删除缓存
	 * @param key 键
	 * @return
	 */
	public  long del(String key) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(key)){
				result = jedis.del(key);
				log.debug("del {}", key);
			}else{
				log.debug("del {} not exists", key);
			}
		} catch (Exception e) {
			log.warn("del {}", key, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 删除缓存
	 * @param key 键
	 * @return
	 */
	public  long delObject(String key) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(getBytesKey(key))){
				result = jedis.del(getBytesKey(key));
				log.debug("delObject {}", key);
			}else{
				log.debug("delObject {} not exists", key);
			}
		} catch (Exception e) {
			log.warn("delObject {}", key, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}
	
	/**
	 * 缓存是否存在
	 * @param key 键
	 * @return
	 */
	public  boolean exists(String key) {
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.exists(key);
			log.debug("exists {}", key);
		} catch (Exception e) {
			log.warn("exists {}", key, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}
	
	/**
	 * 缓存是否存在
	 * @param key 键
	 * @return
	 */
	public  boolean existsObject(String key) {
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.exists(getBytesKey(key));
			log.debug("existsObject {}", key);
		} catch (Exception e) {
			log.warn("existsObject {}", key, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 获取资源
	 * @return
	 * @throws JedisException
	 */
	public synchronized  Jedis getResource() throws JedisException {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
		    log.debug("getResource.", jedis);
		} catch (JedisException e) {
			log.warn("getResource.", e);
			returnBrokenResource(jedis);
			throw e;
		}
		return jedis;
	}

	/**
	 * 归还资源
	 * @param jedis
	 * @param isBroken
	 */
	public  void returnBrokenResource(Jedis jedis) {
		if (jedis != null) {
			jedisPool.returnBrokenResource(jedis);
		}
	}
	
	/**
	 * 释放资源
	 * @param jedis
	 * @param isBroken
	 */
	public  void returnResource(Jedis jedis) {
		if (jedis != null) {
			jedisPool.returnResource(jedis);
		}
	}

	/**
	 * 获取byte[]类型Key
	 * @param key
	 * @return
	 */
	public  byte[] getBytesKey(Object object){
		if(object instanceof String){
    		return StringUtils.getBytes((String)object);
    	}else{
    		return ObjectUtils.serialize(object);
    	}
	}
	
	/**
	 * Object转换byte[]类型
	 * @param key
	 * @return
	 */
	public  byte[] toBytes(Object object){
    	return ObjectUtils.serialize(object);
	}
	
	/**
	 * List<Object>转换byte[]类型 wfj
	 * @param List<Object>
	 * @return
	 */
	public  byte[] listToBytes(List<Object> objects){
		if (objects == null)
			throw new NullPointerException("Can't serialize null");
		byte[] results = null;
		ByteArrayOutputStream bos = null;
		ObjectOutputStream os = null;

		try {
			bos = new ByteArrayOutputStream();
			os = new ObjectOutputStream(bos);
			for (Object o : objects) {
				os.writeObject(o);
			}

		// os.writeObject(null);
			os.close();
			bos.close();
			results = bos.toByteArray();
		} catch (IOException e) {
			throw new IllegalArgumentException("Non-serializable object", e);
		} finally {
			if(os != null)
			{
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(bos != null)
			{
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}


		return results;
	}
	
	/**
	 * List<Object>转换byte[]类型 wfj
	 * @param List<Object>
	 * @return
	 */
	public List<Object> bytesToList( byte[] bs){
		if (bs == null)
			throw new NullPointerException("Can't serialize null");
		List<Object> results = new ArrayList<Object>();
		ByteArrayInputStream bis = null;
		ObjectInputStream ois = null;

		try {
			bis = new ByteArrayInputStream(bs);
			ois = new ObjectInputStream(bis);
			while(true){
				try {
					Object o = ois.readObject();
					if(o == null){
						break;
					}else{
						results.add(o);
					}
				} catch (EOFException e) {
					break;
				}
			}

		} catch (IOException e) {
			throw new IllegalArgumentException("Non-serializable object", e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}finally {
			if(bis != null)
			{
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(ois != null)
			{
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}


		return results;
	}

	/**
	 * byte[]型转换Object
	 * @param key
	 * @return
	 */
	public  Object toObject(byte[] bytes){
		return ObjectUtils.unserialize(bytes);
	}
	public  String getString(String key){
    	String val = null ;
    	Jedis jedis =  getResource();
    	try{
    		val = jedis.get(key);
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		if(jedis!=null){
    			release(jedis, false);
    			jedis = null ;
    		}
    	}
    	return val;
    }
	private  void release(Jedis jedis, boolean isBroken) {  
        if (jedis != null) {  
            if (isBroken) {  
                jedisPool.returnBrokenResource(jedis);  
            } else {  
                jedisPool.returnResource(jedis);  
            }  
        }  
    }
	 public  boolean setString(String key,String value,int seconds){
	    	Jedis jedis = getResource();
	    	boolean status = false ;
	    	try{
	    		jedis.set(key, value);
	    		jedis.expire(key, seconds);
	    		status = true ;
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}finally{
	    		if(jedis!=null){
	    			release(jedis, false);
	    			jedis = null ;
	    		}
	    	}
	    	return status;
	    }
	 
	 public    boolean  flushDbByIndex(int index){
		 Jedis jedis = getResource();
		 boolean status = false ;
		 try{
			    jedis.select(index);
			    jedis.flushDB();
	    		status = true ;
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}finally{
	    		if(jedis!=null){
	    			release(jedis, false);
	    			jedis = null ;
	    		}
	    	}
	    	return status;
	 }
	 public    boolean  flushDbByIndexAndMsgIds(int index,String msg_ids){
		 Jedis jedis = getResource();
		 boolean status = false ;
		 try{
			    jedis.select(index);
			    Set<String> set=jedis.keys("*");
			    Iterator<String> it = set.iterator();  
			    while (it.hasNext()) {  
			      String key = it.next();  
			      String msg_id=key.split(":")[0] ;
			      if(msg_ids.contains(msg_id)){
			    	  jedis.del(key);
			      }
			    } 
	    		status = true ;
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}finally{
	    		if(jedis!=null){
	    			release(jedis, false);
	    			jedis = null ;
	    		}
	    	}
	    	return status;
	 }
	 public    boolean  isExistDbByIndexAndMsgIds(int index,String msg_ids){
		 Jedis jedis = getResource();
		 boolean status = false ;
		 try{
			    jedis.select(index);
			    Set<String> set=jedis.keys("*");
			    Iterator<String> it = set.iterator();  
			    while (it.hasNext()) {  
			      String key = it.next();  
			      String msg_id=key.split(":")[0] ;
			      if(msg_ids.contains(msg_id)){
			    	  status=true;
			    	  break;
			      }
			    } 
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}finally{
	    		if(jedis!=null){
	    			release(jedis, false);
	    			jedis = null ;
	    		}
	    	}
	    	return status;
	 }	
	 

	public static JedisUtils jedisUtils = null;
	 
	private JedisUtils (){
		super();
	}

	@PostConstruct
	public void init() {
		jedisUtils = this;
	}

		
	/**
	 * 
	 * @return
	 */
	public static synchronized JedisUtils newInstance(){
		if(jedisUtils == null){
			jedisUtils = new JedisUtils();
		}
		return jedisUtils;
	}
}
