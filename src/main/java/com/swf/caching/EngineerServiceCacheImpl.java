package com.swf.caching;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.swf.models.EngineerBO;

@Service
public class EngineerServiceCacheImpl extends CachingService<EngineerBO> {

	@Override
	public void save(String key, EngineerBO engineerBO) {
		redisService.setValueWithTimeLimit(key, engineerBO, 12, TimeUnit.HOURS);

	}

	@Override
	public void saveAll(String key, List<EngineerBO> engineerBOs) {
		redisService.setValueWithTimeLimit(key, engineerBOs, 12, TimeUnit.HOURS);
	}

}
