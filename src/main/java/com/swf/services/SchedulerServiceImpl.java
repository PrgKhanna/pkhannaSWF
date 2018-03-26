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

import com.swf.controllers.ScheduleController;
import com.swf.dtos.ScheduleDTO;
import com.swf.mappers.ObjectMapperService;
import com.swf.models.EngineerShiftBO;
import com.swf.models.ScheduleBO;
import com.swf.models.SchedulePeriodBO;

@Service
public class SchedulerServiceImpl implements ISchedulerService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleController.class);

	@Autowired
	private ObjectMapperService mapper;

	@Autowired
	private IEngineerShiftService engineerShiftService;

	@Autowired
	private ISchedulePeriodService schedulePeriodService;

	public List<ScheduleDTO> getCurrentSchedule() {
		LOGGER.info("Getting Current Schedule");
		List<ScheduleDTO> scheduleDTOS = null;
		try {
			SchedulePeriodBO schedulePeriodBO = schedulePeriodService.findActivePeriod();
			if (null == schedulePeriodBO || isInBetweenPeriod(schedulePeriodBO, new Date())) {
				LOGGER.info("Got Current Schedule : " + schedulePeriodBO);
				// No need to do anything
			} else {
				LOGGER.info("No Current Date Schedule");
				// means schedule period is not there corresponding to current date
				// Save schedule for next Period and return schedule Period
				schedulePeriodBO = schedulePeriodService.createNextSchedulePeriod(schedulePeriodBO);
			}

			List<EngineerShiftBO> engineerShiftBOs = engineerShiftService
					.getShiftsForAPeriod(schedulePeriodBO.getStartDate(), schedulePeriodBO.getEndDate());
			scheduleDTOS = getScheduleForShifts(engineerShiftBOs);
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

	private boolean isInBetweenPeriod(SchedulePeriodBO schedulePeriodBO, Date date) {
		return (date.compareTo(schedulePeriodBO.getStartDate()) >= 0
				&& date.compareTo(schedulePeriodBO.getEndDate()) <= 0);
	}

}
