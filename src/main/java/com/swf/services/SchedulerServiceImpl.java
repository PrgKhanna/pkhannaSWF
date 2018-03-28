package com.swf.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.swf.caching.ScheduleServiceCacheImpl;
import com.swf.controllers.ScheduleController;
import com.swf.dtos.ScheduleDTO;
import com.swf.mappers.ObjectMapperService;
import com.swf.models.EngineerShiftBO;
import com.swf.models.ScheduleBO;
import com.swf.models.SchedulePeriodBO;
import com.swf.utils.SWFConstants;
import com.swf.utils.SWFDateFormatter;

@Service
public class SchedulerServiceImpl implements ISchedulerService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleController.class);

	@Autowired
	private ObjectMapperService mapper;

	@Autowired
	private IEngineerShiftService engineerShiftService;

	@Autowired
	private ISchedulePeriodService schedulePeriodService;

	@Autowired
	private ScheduleServiceCacheImpl scheduleServiceCacheImpl;

	@SuppressWarnings("unchecked")
	public List<ScheduleDTO> getCurrentSchedule() {
		LOGGER.info("Getting Current Schedule");
		List<ScheduleDTO> scheduleDTOS = null;
		try {
			SchedulePeriodBO schedulePeriodBO = schedulePeriodService.findActivePeriod();
			if (null == schedulePeriodBO || isInBetweenPeriod(schedulePeriodBO, new Date())) {
				// No need to do anything as everything is already there
				LOGGER.info("Got Current Schedule : " + schedulePeriodBO);
			} else {
				LOGGER.info("No Current Date Schedule, creating for next " + SWFConstants.DAY_IN_PERIOD + " days");
				// means schedule period is not there corresponding to current date
				// Save schedule for next Period and return schedule Period
				schedulePeriodBO = schedulePeriodService.createNextSchedulePeriod(schedulePeriodBO);
			}

			String startDateStr = SWFDateFormatter.DATE_FORMAT_YYYY_MM_DD.format(schedulePeriodBO.getStartDate());
			String endDateStr = SWFDateFormatter.DATE_FORMAT_YYYY_MM_DD.format(schedulePeriodBO.getEndDate());
			String key = "schedules_" + startDateStr + "_" + endDateStr;
			scheduleDTOS = scheduleServiceCacheImpl.getAll(key);

			LOGGER.info("Getting Engineer Shifts for the period : " + startDateStr + " - " + endDateStr);
			List<EngineerShiftBO> engineerShiftBOs = engineerShiftService.getShiftsForAPeriod(startDateStr, endDateStr);
			scheduleDTOS = getScheduleForShifts(engineerShiftBOs);
			scheduleServiceCacheImpl.saveAll(key, scheduleDTOS);
		} catch (Exception e) {
			LOGGER.error("Failed to get Current Schedule");
		}
		LOGGER.info("Got Current Schedule : " + scheduleDTOS);
		return scheduleDTOS;
	}

	private List<ScheduleDTO> getScheduleForShifts(List<EngineerShiftBO> engineerShiftBOs) {
		List<ScheduleDTO> scheduleDTOS;
		Map<Date, List<EngineerShiftBO>> dateToShiftsBO = engineerShiftBOs.stream()
				.collect(Collectors.groupingBy(EngineerShiftBO::getDate, Collectors.toList()));
		List<ScheduleBO> scheduleBOs = new ArrayList<ScheduleBO>();
		for (Date date : dateToShiftsBO.keySet()) {
			ScheduleBO scheduleBO = new ScheduleBO();
			scheduleBO.setDate(date);
			scheduleBO.setShifts(dateToShiftsBO.get(date));
			scheduleBOs.add(scheduleBO);
		}
		scheduleDTOS = mapper.mapAsList(scheduleBOs, ScheduleDTO.class);
		scheduleDTOS.sort((ScheduleDTO s1, ScheduleDTO s2) -> s1.getDate().compareTo(s2.getDate()));
		return scheduleDTOS;
	}

	public boolean isInBetweenPeriod(SchedulePeriodBO schedulePeriodBO, Date date) {
		return (date.compareTo(schedulePeriodBO.getStartDate()) >= 0
				&& date.compareTo(schedulePeriodBO.getEndDate()) <= 0);
	}

}
