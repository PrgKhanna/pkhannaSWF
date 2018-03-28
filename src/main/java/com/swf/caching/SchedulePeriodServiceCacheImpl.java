package com.swf.caching;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.swf.models.SchedulePeriodBO;

@Service
public class SchedulePeriodServiceCacheImpl extends CachingService<SchedulePeriodBO> {

	@Override
	public void save(String key, SchedulePeriodBO schedulePeriodBO) {
		redisService.setValueWithTimeLimit(key, schedulePeriodBO, 6, TimeUnit.HOURS); // Config Time and Unit
	}

	@Override
	public void saveAll(String key, List<SchedulePeriodBO> schedulePeriodBOs) {
		redisService.setValueWithTimeLimit(key, schedulePeriodBOs, 6, TimeUnit.HOURS); // Config Time and Unit
	}

}
