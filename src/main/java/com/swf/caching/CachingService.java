package com.swf.caching;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public abstract class CachingService<T> {

	@Autowired
	RedisService redisService;

	public T get(final String key) {
		return (T) redisService.getValue(key);
	}

	public List<T> getAll(final String key) {
		return (List<T>) redisService.getValue(key);
	}
	
	abstract void save(String key, T t);
	
	abstract void saveAll(String key, List<T> t);

	public void delete(final String key) {
		redisService.invalidate(key);
	}

}
