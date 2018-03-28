package com.swf.caching;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.swf.models.EngineerShiftBO;

@Service
public class EngineerShiftServiceCacheImpl extends CachingService<EngineerShiftBO> {

	@Override
	public void save(String key, EngineerShiftBO engineerShiftBO) {
		redisService.setValueWithTimeLimit(key, engineerShiftBO, 6, TimeUnit.HOURS);

	}

	@Override
	public void saveAll(String key, List<EngineerShiftBO> engineerShiftBOs) {
		redisService.setValueWithTimeLimit(key, engineerShiftBOs, 6, TimeUnit.HOURS);

	}

}
