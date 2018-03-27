package com.swf.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.swf.entities.EngineerShift;
import com.swf.enums.Shift;
import com.swf.mappers.ObjectMapperService;
import com.swf.models.EngineerBO;
import com.swf.models.EngineerShiftBO;
import com.swf.repositories.EngineerShiftRepository;
import com.swf.rules.EngineerFilterHelper;
import com.swf.rules.IFilterRule;
import com.swf.utils.RandomNumber;
import com.swf.utils.SWFConstants;
import com.swf.utils.SWFDateFormatter;

@Service
public class EngineerShiftServiceImpl implements IEngineerShiftService {

	private static final Logger LOGGER = LoggerFactory.getLogger(EngineerShiftServiceImpl.class);

	@Autowired
	private ObjectMapperService mapper;

	@Autowired
	private EngineerShiftRepository engineerShiftRepository;

	@Autowired
	private List<IFilterRule> filterRules;

	@Autowired
	private RedisService redisService;

	@SuppressWarnings("unchecked")
	@Override
	public List<EngineerShiftBO> getShiftsForAPeriod(Date startDate, Date endDate) {
		LOGGER.info("Getting Engineer Shifts for a period");
		String startDateStr = SWFDateFormatter.DATE_FORMAT_YYYY_MM_DD.format(startDate);
		String endDateStr = SWFDateFormatter.DATE_FORMAT_YYYY_MM_DD.format(endDate);
		String key = "shifts_" + startDateStr + "_" + endDateStr;
		List<EngineerShiftBO> engineerShiftBOs = (List<EngineerShiftBO>) redisService.getValue(key);
		if (null != engineerShiftBOs) {
			LOGGER.info("Got Engineer Shifts for a period from cache");
			return engineerShiftBOs;
		}
		try {
			List<EngineerShift> engineerShifts = engineerShiftRepository.findByDateBetween(startDate, endDate);
			if (null != engineerShifts) {
				LOGGER.info("Got Engineer Shifts for a period");
				engineerShiftBOs = mapper.mapAsList(engineerShifts, EngineerShiftBO.class);
				redisService.setValueWithTimeLimit(key, engineerShiftBOs, 6, TimeUnit.HOURS);
			} else {
				LOGGER.info("Failed to get Engineer Shifts for a period");
			}
		} catch (Exception e) {
			LOGGER.error("Exception while getting Engineer Shifts for a period");
		}
		return engineerShiftBOs;
	}

	@Override
	public List<EngineerShiftBO> assignShiftsForADate(Calendar date, List<EngineerBO> availableEngineers,
			List<EngineerShiftBO> shiftsBOs) {
		LOGGER.info("Assign Engineer Shifts for a date");
		List<EngineerBO> engineerBOs = new ArrayList<EngineerBO>(availableEngineers);
		List<EngineerBO> applicableEngineers = applicableEngineersPreShiftSelection(shiftsBOs, engineerBOs);
		LOGGER.info("Got Applicable Engineers for Shifts for a date : " + applicableEngineers.size());
		// after getting applicable engineers that need to be assigned to shifts
		List<EngineerShiftBO> ShiftBOsForADay = new ArrayList<EngineerShiftBO>();
		for (int i = 1; i <= SWFConstants.NO_OF_SHIFTS; i++) {
			// This will prevent same day allotment for an engineer
			// we can have more shift level Rules same as filter Rules
			applicableEngineers = applicableEngineersForEachShiftSelection(applicableEngineers, ShiftBOsForADay);

			LOGGER.info("Applicable Engineer for Shift " + i + " : " + applicableEngineers.size());
			int randomNumber = RandomNumber.getRandomNumberInRange(0, applicableEngineers.size() - 1);
			EngineerShiftBO shiftBO = new EngineerShiftBO();
			shiftBO.setDate(date.getTime());
			shiftBO.setShift(Shift.getShiftById((byte) i));
			shiftBO.setEngineer(applicableEngineers.get(randomNumber));
			LOGGER.info("Selected Engineer for Shift " + i + " : " + shiftBO.getEngineer());
			ShiftBOsForADay.add(shiftBO);
		}

		return ShiftBOsForADay;
	}

	private List<EngineerBO> applicableEngineersForEachShiftSelection(List<EngineerBO> applicableEngineers,
			List<EngineerShiftBO> ShiftBOsForADay) {
		EngineerFilterHelper<EngineerShiftBO> postFilterHelper = new EngineerFilterHelper<EngineerShiftBO>();
		postFilterHelper.setList(ShiftBOsForADay);
		for (IFilterRule rule : filterRules) {
			if (rule.isApplicable() && rule.isPost()) {
				applicableEngineers = rule.filterRule(applicableEngineers, postFilterHelper);
			}
		}
		return applicableEngineers;
	}

	private List<EngineerBO> applicableEngineersPreShiftSelection(List<EngineerShiftBO> shiftsBOs,
			List<EngineerBO> engineerBOs) {
		List<EngineerBO> applicableEngineers = new ArrayList<EngineerBO>();

		EngineerFilterHelper<EngineerShiftBO> preFilterHelper = new EngineerFilterHelper<EngineerShiftBO>();
		preFilterHelper.setList(shiftsBOs);
		for (IFilterRule rule : filterRules) {
			if (rule.isApplicable() && rule.isPre()) {
				applicableEngineers = rule.filterRule(engineerBOs, preFilterHelper);
			}
		}
		return applicableEngineers;
	}

	@Override
	public void saveEngineerShifts(List<EngineerShiftBO> engineerShiftBOs) {
		List<EngineerShift> assignedShifts = mapper.mapAsList(engineerShiftBOs, EngineerShift.class);
		engineerShiftRepository.save(assignedShifts);
	}

}
