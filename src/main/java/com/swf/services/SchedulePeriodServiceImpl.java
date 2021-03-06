package com.swf.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.swf.caching.SchedulePeriodServiceCacheImpl;
import com.swf.entities.SchedulePeriod;
import com.swf.mappers.ObjectMapperService;
import com.swf.models.EngineerBO;
import com.swf.models.EngineerShiftBO;
import com.swf.models.SchedulePeriodBO;
import com.swf.repositories.SchedulePeriodRepository;
import com.swf.utils.SWFConstants;

@Service
public class SchedulePeriodServiceImpl implements ISchedulePeriodService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SchedulePeriodServiceImpl.class);

	@Autowired
	private ObjectMapperService mapper;

	@Autowired
	private SchedulePeriodRepository schedulePeriodRepository;

	@Autowired
	private IEngineerService engineerService;

	@Autowired
	private IEngineerShiftService engineerShiftService;

	@Autowired
	private SchedulePeriodServiceCacheImpl schedulePeriodServiceCacheImpl;

	@Override
	public SchedulePeriodBO findActivePeriod() {
		LOGGER.info("Getting active Period");
		String key = "active_period";
		SchedulePeriodBO schedulePeriodBO = schedulePeriodServiceCacheImpl.get(key);
		try {
			SchedulePeriod schedulePeriod = schedulePeriodRepository.findByActiveTrue();
			if (null != schedulePeriod) {
				LOGGER.info("Got Active Period");
				schedulePeriodBO = mapper.map(schedulePeriod, SchedulePeriodBO.class);
			} else {
				LOGGER.info("Failed to get active Period");
			}
		} catch (Exception e) {
			LOGGER.error("Exception while getting active Period", e.getMessage());
		}
		return schedulePeriodBO;
	}

	@Override
	public void updateStatusOfAPeriod(SchedulePeriodBO schedulePeriodBO) {
		SchedulePeriod previousPeriod = mapper.map(schedulePeriodBO, SchedulePeriod.class);
		previousPeriod.setActive(false);
		if (null != previousPeriod.getId()) {
			schedulePeriodRepository.save(previousPeriod);
			schedulePeriodServiceCacheImpl.delete("active_period");
		}
	}

	@Override
	public SchedulePeriodBO saveSchedulePeriod(Date startDate, Date endDate) {
		SchedulePeriod newPeriod = new SchedulePeriod();
		newPeriod.setActive(true);
		newPeriod.setStartDate(startDate);
		newPeriod.setEndDate(endDate);
		newPeriod = schedulePeriodRepository.save(newPeriod);
		SchedulePeriodBO schedulePeriodBO = mapper.map(newPeriod, SchedulePeriodBO.class);
		schedulePeriodServiceCacheImpl.save("active_period", schedulePeriodBO);
		return schedulePeriodBO;
	}

	@Override
	@Transactional
	public SchedulePeriodBO createNextSchedulePeriod(SchedulePeriodBO schedulePeriodBO) {
		LOGGER.info("Creating Next Schedule");
		Calendar periodStart = getDate(schedulePeriodBO.getStartDate());
		Calendar periodEnd = getDate(schedulePeriodBO.getEndDate());

		List<EngineerBO> availableEngineers = engineerService.getAllAvailableEngineers();

		periodEnd.add(Calendar.DATE, 1);
		Calendar startCal = (Calendar) periodStart.clone();
		Calendar endCal = (Calendar) periodEnd.clone();
		List<EngineerShiftBO> assignedShiftBOs = saveAndGetAllAssignedShiftsToEngineers(availableEngineers, startCal,
				endCal);

		LOGGER.info("Assigned Shifts : " + assignedShiftBOs);
		// Saving all shifts at once
		engineerShiftService.saveEngineerShifts(assignedShiftBOs);

		// invalidate Previous schedule and create a new Schedule
		LOGGER.info("Invalidating Previous Schedule");
		updateStatusOfAPeriod(schedulePeriodBO);

		LOGGER.info("Saving new Schedule");
		SchedulePeriodBO newPeriodBO = saveSchedulePeriod(periodStart.getTime(), periodEnd.getTime());

		return newPeriodBO;
	}

	private List<EngineerShiftBO> saveAndGetAllAssignedShiftsToEngineers(List<EngineerBO> availableEngineers,
			Calendar startCal, Calendar endCal) {
		List<EngineerShiftBO> assignedShiftBOs = new ArrayList<EngineerShiftBO>();
		do {
			Calendar date = startCal;
			int dayOfWeek = date.get(Calendar.DAY_OF_WEEK);
			if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
				startCal.add(Calendar.DATE, 1);
				continue;
			}
			List<EngineerShiftBO> shiftsPerDay = engineerShiftService.assignShiftsToEngineerForADate(date,
					availableEngineers, assignedShiftBOs);
			LOGGER.info("Assign Engineer Shifts for a date " + startCal.getTime() + " : " + shiftsPerDay);
			startCal.add(Calendar.DATE, 1);
			assignedShiftBOs.addAll(shiftsPerDay);
		} while (startCal.before(endCal));
		return assignedShiftBOs;
	}

	private Calendar getDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, SWFConstants.SCHEDULE_WEEK_SPAN * SWFConstants.NO_OF_DAYS_IN_WEEK);
		return calendar;
	}

}
