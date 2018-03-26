package com.swf.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.swf.utils.RandomNumber;
import com.swf.utils.SWFConstants;
import com.swf.validators.EngineerFilterHelper;
import com.swf.validators.IFilterRule;

@Service
public class EngineerShiftServiceImpl implements IEngineerShiftService {

	private static final Logger LOGGER = LoggerFactory.getLogger(EngineerShiftServiceImpl.class);

	@Autowired
	private ObjectMapperService mapper;

	@Autowired
	private EngineerShiftRepository engineerShiftRepository;

	@Autowired
	private List<IFilterRule> filterRules;

	@Override
	public List<EngineerShiftBO> getShiftsForAPeriod(Date startDate, Date endDate) {
		LOGGER.info("Getting Engineer Shifts for a period");
		List<EngineerShiftBO> engineerShiftBOs = null;
		try {
			List<EngineerShift> engineerShifts = engineerShiftRepository.findByDateBetween(startDate, endDate);
			if (null != engineerShifts) {
				LOGGER.info("Got Engineer Shifts for a period");
				engineerShiftBOs = mapper.mapAsList(engineerShifts, EngineerShiftBO.class);
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
		List<EngineerBO> applicableEngineers = new ArrayList<EngineerBO>();

		EngineerFilterHelper<EngineerShiftBO> filterHelper = new EngineerFilterHelper<EngineerShiftBO>();
		filterHelper.setList(shiftsBOs);
		for (IFilterRule rule : filterRules) {
			if (rule.isApplicable()) {
				applicableEngineers = rule.filterRule(engineerBOs, filterHelper);
			}
		}
		LOGGER.info("Got Applicable Engineers for Shifts for a date : " + applicableEngineers.size());
		// after getting applicable engineers that need to be assigned to shifts
		List<EngineerShiftBO> ShiftBOsForADay = new ArrayList<EngineerShiftBO>();
		for (int i = 1; i <= SWFConstants.NO_OF_SHIFTS; i++) {
			// This will prevent same day allotment for an engineer
			// we can have more shift level Rules same as filter Rules
			if (!ShiftBOsForADay.isEmpty()) {
				Set<EngineerBO> allocatedEngineers = ShiftBOsForADay.stream().map(sh -> sh.getEngineer())
						.collect(Collectors.toSet());
				applicableEngineers.removeAll(allocatedEngineers);
			}
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

}
