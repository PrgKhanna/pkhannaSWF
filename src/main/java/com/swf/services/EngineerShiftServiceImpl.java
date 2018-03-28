package com.swf.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.swf.caching.EngineerShiftServiceCacheImpl;
import com.swf.entities.EngineerShift;
import com.swf.enums.Shift;
import com.swf.mappers.ObjectMapperService;
import com.swf.models.EngineerBO;
import com.swf.models.EngineerShiftBO;
import com.swf.repositories.EngineerShiftRepository;
import com.swf.rules.EngineerFilterHelper;
import com.swf.rules.IFilterRule;
import com.swf.utils.RandomEngineerSelector;
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
	private EngineerShiftServiceCacheImpl engineerShiftServiceCacheImpl;

	@Autowired
	private RandomEngineerSelector engineerSelector;

	@Override
	public List<EngineerShiftBO> getShiftsForAPeriod(String startDateStr, String endDateStr) {
		LOGGER.info("Getting Engineer Shifts for a period");
		List<EngineerShiftBO> engineerShiftBOs = null;
		try {
			Date startDate = SWFDateFormatter.DATE_FORMAT_YYYY_MM_DD.parse(startDateStr);
			Date endDate = SWFDateFormatter.DATE_FORMAT_YYYY_MM_DD.parse(endDateStr);
			String key = "shifts_" + startDateStr + "_" + endDateStr;
			engineerShiftBOs = engineerShiftServiceCacheImpl.getAll(key);

			List<EngineerShift> engineerShifts = engineerShiftRepository.findByDateBetween(startDate, endDate);
			if (null != engineerShifts) {
				LOGGER.info("Got Engineer Shifts for a period");
				engineerShiftBOs = mapper.mapAsList(engineerShifts, EngineerShiftBO.class);
				engineerShiftServiceCacheImpl.saveAll(key, engineerShiftBOs);
			} else {
				LOGGER.info("Failed to get Engineer Shifts for a period");
			}
		} catch (Exception e) {
			LOGGER.error("Exception while getting Engineer Shifts for a period");
		}
		return engineerShiftBOs;
	}

	@Override
	public List<EngineerShiftBO> assignShiftsToEngineerForADate(Calendar date, List<EngineerBO> availableEngineers,
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
			EngineerBO selectedEngineer = engineerSelector.selectEngineer(applicableEngineers);
			EngineerShiftBO shiftBO = new EngineerShiftBO();
			shiftBO.setDate(date.getTime());
			shiftBO.setShift(Shift.getShiftById((byte) i));
			shiftBO.setEngineer(selectedEngineer);
			LOGGER.info("Selected Engineer for Shift " + i + " : " + shiftBO.getEngineer());
			ShiftBOsForADay.add(shiftBO);
		}

		return ShiftBOsForADay;
	}

	private List<EngineerBO> applicableEngineersForEachShiftSelection(List<EngineerBO> applicableEngineers,
			List<EngineerShiftBO> ShiftBOsForADay) {
		LOGGER.info("Applicable Rules during selection of Engineers for a Shift");
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
		LOGGER.info("Applicable Rules during pre selection of Engineers for a Shift");
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
		LOGGER.info("Saving Engineer Shifts");
		List<EngineerShift> assignedShifts = mapper.mapAsList(engineerShiftBOs, EngineerShift.class);
		engineerShiftRepository.save(assignedShifts);
	}

}
