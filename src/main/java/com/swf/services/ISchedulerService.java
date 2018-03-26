package com.swf.services;

import java.util.List;

import com.swf.dtos.ScheduleDTO;

public interface ISchedulerService {

	List<ScheduleDTO> getCurrentSchedule();

}
