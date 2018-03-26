package com.swf.services;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

	@Autowired
	private RedisTemplate<String, Object> template;

	public Object getValue(final String key) {
		return template.opsForValue().get(key);
	}
	
	public void setValue(final String key, Object value) {
		template.opsForValue().set(key, value);
	}

	public void setValueWithTimeLimit(final String key, Object value, long time, TimeUnit unit) {
		template.opsForValue().set(key, value);

		// set a expire for a message
		template.expire(key, 6, TimeUnit.HOURS);
	}
	
	public void invalidate(final String key) {
		template.delete(key);
	}

}