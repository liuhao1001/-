package myproject.common.utils;

import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * spring redis 工具类
 **/
@SuppressWarnings(value = {"unchecked", "rawtypes"})
@Component
@Slf4j
public class RedisCache {
	@Autowired
	public RedisTemplate redisTemplate;

	/**
	 * 缓存基本的对象，Integer、String、实体类等
	 *
	 * @param key   缓存的键值
	 * @param value 缓存的值
	 */
	public <T> void setCacheObject(final String key, final T value) {
		redisTemplate.opsForValue().set(key, value);
	}

	/**
	 * 缓存基本的对象，Integer、String、实体类等
	 *
	 * @param key      缓存的键值
	 * @param value    缓存的值
	 * @param timeout  时间
	 * @param timeUnit 时间颗粒度
	 */
	public <T> void setCacheObject(final String key, final T value, final Integer timeout, final TimeUnit timeUnit) {
		redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
	}

	/**
	 * 设置有效时间
	 *
	 * @param key     Redis键
	 * @param timeout 超时时间
	 * @return true=设置成功；false=设置失败
	 */
	public boolean expire(final String key, final long timeout) {
		return expire(key, timeout, TimeUnit.SECONDS);
	}

	/**
	 * 设置有效时间
	 *
	 * @param key     Redis键
	 * @param timeout 超时时间
	 * @param unit    时间单位
	 * @return true=设置成功；false=设置失败
	 */
	public boolean expire(final String key, final long timeout, final TimeUnit unit) {
		return redisTemplate.expire(key, timeout, unit);
	}

	/**
	 * 获取有效时间
	 *
	 * @param key Redis键
	 * @return 有效时间
	 */
	public long getExpire(final String key) {
		return redisTemplate.getExpire(key);
	}

	/**
	 * 判断 key是否存在
	 *
	 * @param key 键
	 * @return true 存在 false不存在
	 */
	public Boolean hasKey(String key) {
		return redisTemplate.hasKey(key);
	}

	/**
	 * 获得缓存的基本对象。
	 *
	 * @param key 缓存键值
	 * @return 缓存键值对应的数据
	 */
	public <T> T getCacheObject(final String key) {
		ValueOperations<String, T> operation = redisTemplate.opsForValue();
		return operation.get(key);
	}

	/**
	 * 删除单个对象
	 *
	 * @param key
	 */
	public boolean deleteObject(final String key) {
		return redisTemplate.delete(key);
	}

	/**
	 * 删除集合对象
	 *
	 * @param collection 多个对象
	 * @return
	 */
	public boolean deleteObject(final Collection collection) {
		return redisTemplate.delete(collection) > 0;
	}

	/**
	 * 缓存List数据
	 *
	 * @param key      缓存的键值
	 * @param dataList 待缓存的List数据
	 * @return 缓存的对象
	 */
	public <T> long setCacheList(final String key, final List<T> dataList) {
		Long count = redisTemplate.opsForList().rightPushAll(key, dataList);
		return count == null ? 0 : count;
	}

	/**
	 * 获得缓存的list对象
	 *
	 * @param key 缓存的键值
	 * @return 缓存键值对应的数据
	 */
	public <T> List<T> getCacheList(final String key) {
		return redisTemplate.opsForList().range(key, 0, -1);
	}

	/**
	 * 缓存Set
	 *
	 * @param key     缓存键值
	 * @param dataSet 缓存的数据
	 * @return 缓存数据的对象
	 */
	public <T> BoundSetOperations<String, T> setCacheSet(final String key, final Set<T> dataSet) {
		BoundSetOperations<String, T> setOperation = redisTemplate.boundSetOps(key);
		Iterator<T> it = dataSet.iterator();
		while (it.hasNext()) {
			setOperation.add(it.next());
		}
		return setOperation;
	}

	/**
	 * 获得缓存的set
	 *
	 * @param key
	 * @return
	 */
	public <T> Set<T> getCacheSet(final String key) {
		return redisTemplate.opsForSet().members(key);
	}

	/**
	 * 缓存Map
	 *
	 * @param key
	 * @param dataMap
	 */
	public <T> void setCacheMap(final String key, final Map<String, T> dataMap) {
		if (dataMap != null) {
			redisTemplate.opsForHash().putAll(key, dataMap);
		}
	}

	/**
	 * 获得缓存的Map
	 *
	 * @param key
	 * @return
	 */
	public <T> Map<String, T> getCacheMap(final String key) {
		return redisTemplate.opsForHash().entries(key);
	}

	/**
	 * 往Hash中存入数据
	 *
	 * @param key   Redis键
	 * @param hKey  Hash键
	 * @param value 值
	 */
	public <T> void setCacheMapValue(final String key, final String hKey, final T value) {
		redisTemplate.opsForHash().put(key, hKey, value);
	}

	/**
	 * 获取Hash中的数据
	 *
	 * @param key  Redis键
	 * @param hKey Hash键
	 * @return Hash中的对象
	 */
	public <T> T getCacheMapValue(final String key, final String hKey) {
		HashOperations<String, String, T> opsForHash = redisTemplate.opsForHash();
		return opsForHash.get(key, hKey);
	}

	/**
	 * 获取多个Hash中的数据
	 *
	 * @param key   Redis键
	 * @param hKeys Hash键集合
	 * @return Hash对象集合
	 */
	public <T> List<T> getMultiCacheMapValue(final String key, final Collection<Object> hKeys) {
		return redisTemplate.opsForHash().multiGet(key, hKeys);
	}

	/**
	 * 删除Hash中的某条数据
	 *
	 * @param key  Redis键
	 * @param hKey Hash键
	 * @return 是否成功
	 */
	public boolean deleteCacheMapValue(final String key, final String hKey) {
		return redisTemplate.opsForHash().delete(key, hKey) > 0;
	}

	/**
	 * 获得缓存的基本对象列表
	 *
	 * @param pattern 字符串前缀
	 * @return 对象列表
	 */
	public Collection<String> keys(final String pattern) {
		return redisTemplate.keys(pattern);
	}

	/**
	 * 锁最大存活时长
	 */
	private final static int LOCK_MAX_TIMEOUT_SEC = 600;

	/**
	 * 加锁/获取锁 [超时时间]
	 *
	 * @param key       锁名称
	 * @param identity  标识
	 * @param expireSec 过期时间
	 * @return boolean 是否加锁成功
	 */
	private boolean lock(String key, String identity, int expireSec) {
		boolean lock;
		try {
			lock = Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, identity, Duration.ofSeconds(expireSec)));
		} catch (Exception e) {
			log.error("获取锁：{}，出现错误{}", key, e);
			return false;
		}
		return lock;
	}

	/**
	 * 加锁/获取锁，自定义实现[过期时间]
	 *
	 * @param key       锁名称
	 * @param identity  标识
	 * @param expireSec 过期时间
	 * @return boolean 是否加锁成功
	 */
	private boolean lockV2(String key, String identity, int expireSec) {
		// 每个节点时间可能不一样
		Long currTime = redisTemplate.getRequiredConnectionFactory().getConnection().time();
		Assert.notNull(currTime, "Redis 服务异常");
		// 超时时间戳
		String expire = String.valueOf(currTime + expireSec);
		String separator = ",";
		// 获取锁
		if (Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, identity + separator + expire))) {
			return true;
		}
		// 未获取锁，检查锁状态
		Object obj = redisTemplate.opsForValue().get(key);
		String value = obj != null ? obj.toString() : null;
		if (value != null) {
			// 锁已过期
			if (Long.parseLong(value.split(separator)[1]) < currTime) {
				log.error("锁：{}，未显示释放，已过期", key);
				// 释放锁
				unlockV2(key, value.split(separator)[0]);
				// 再次尝试获取锁
				return lockV2(key, identity, expireSec);
			}
		}
		return false;
	}

	/**
	 * 释放锁
	 *
	 * @param key      锁名称
	 * @param identity 标识
	 */
	private void unlockV2(String key, String identity) {
		try {
			Object obj = redisTemplate.opsForValue().get(key);
			String value = obj != null ? obj.toString() : null;
			if (value != null) {
				if (value.split(",")[0].equals(identity)) {
					redisTemplate.delete(key);
				}
			}
		} catch (Exception ignored) {
		}
	}

	public boolean lock(String key) {
		return lock(key, "redisLock", LOCK_MAX_TIMEOUT_SEC);
	}

	public void unlock(String key) {
		unlockV2(key, "redisLock");
	}

	/**
	 * 加锁/获取锁，自定义实现[过期时间]
	 *
	 * @param key      锁名称
	 * @param identity 标识
	 * @return 是否获取锁
	 */
	public boolean lockV2(String key, String identity) {
		return lockV2(key, identity, LOCK_MAX_TIMEOUT_SEC);
	}

	/**
	 * 拥有排他时长的锁
	 *
	 * @param key       锁名称
	 * @param identity  标识
	 * @param expireSec 过期时间
	 * @param interval  排它时间
	 * @return 是否获取锁
	 */
	private boolean lock(String key, String identity, int expireSec, int interval) {
		if (lock(key, identity, expireSec)) {
			String preTimeStr = redisTemplate.opsForValue().get(key + "t") != null ? redisTemplate.opsForValue().get(key + "t").toString() : null;
			String nowTimeStr = Objects.requireNonNull(redisTemplate.opsForValue().get(key)).toString().split(",")[1];
			if (preTimeStr != null) {
				long preTime = Long.parseLong(preTimeStr);
				long nowTime = Long.parseLong(nowTimeStr);
				if (nowTime - preTime < interval * 1000L) {
					unlockV2(key, identity);
					return false;
				}
			}
			setString(key + "t", nowTimeStr);
			return true;
		}
		return false;
	}

	private void setString(String key, String value) {
		try {
			redisTemplate.opsForValue().set(key, value);
		} catch (Exception e) {
			log.error("设置key: " + key + ",value: " + value, e);
		}
	}

}
