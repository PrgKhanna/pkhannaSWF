package com.swf.caching;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.swf.dtos.ScheduleDTO;

@Service
public class ScheduleServiceCacheImpl extends CachingService<ScheduleDTO> {

	@Override
	public void save(String key, ScheduleDTO scheduleDTO) {
		redisService.setValueWithTimeLimit(key, scheduleDTO, 6, TimeUnit.HOURS);
	}

	@Override
	public void saveAll(String key, List<ScheduleDTO> scheduleDTOs) {
		redisService.setValueWithTimeLimit(key, scheduleDTOs, 6, TimeUnit.HOURS);
	}

}
