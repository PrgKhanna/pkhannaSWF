package com.swf.utils;

import java.util.Calendar;

import org.springframework.stereotype.Component;

@Component
public class ServiceHelper {

	public Calendar getDefaultPreviousPeriodStartDate(Calendar endDate) {
		Calendar cal = (Calendar) endDate.clone();
		cal.add(Calendar.DATE, (-1) * (SWFConstants.DAY_IN_PERIOD + 1));
		return cal;
	}

	public Calendar getDefaultPreviousPeriodEndDate() {
		Calendar cal = Calendar.getInstance();
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		int daysToAdd = 0;
		switch (dayOfWeek) {
		case Calendar.MONDAY:
			daysToAdd = 4;
			break;
		case Calendar.TUESDAY:
			daysToAdd = 3;
			break;
		case Calendar.WEDNESDAY:
			daysToAdd = 2;
			break;
		case Calendar.THURSDAY:
			daysToAdd = 1;
			break;
		case Calendar.FRIDAY:
			daysToAdd = 0;
			break;
		case Calendar.SATURDAY:
			daysToAdd = -1;
			break;
		case Calendar.SUNDAY:
			daysToAdd = -2;
			break;
		default:
			break;
		}
		cal.add(Calendar.DATE, daysToAdd);
		return cal;
	}

}
