package com.swf.services;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.swf.models.EngineerBO;
import com.swf.models.EngineerShiftBO;

public interface IEngineerShiftService {

	List<EngineerShiftBO> getShiftsForAPeriod(String startDate, String endDate);

	List<EngineerShiftBO> assignShiftsToEngineerForADate(Calendar date, List<EngineerBO> engineerBOs,
			List<EngineerShiftBO> shiftsBOs);

	void saveEngineerShifts(List<EngineerShiftBO> engineerShiftBOs);

}
