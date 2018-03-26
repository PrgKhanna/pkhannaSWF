package com.swf.services;

import java.util.Date;

import com.swf.models.SchedulePeriodBO;

public interface ISchedulePeriodService {

	SchedulePeriodBO findActivePeriod();

	void updateStatusOfAPeriod(SchedulePeriodBO schedulePeriodBO);

	SchedulePeriodBO saveSchedulePeriod(Date startDate, Date endDate);

	SchedulePeriodBO createNextSchedulePeriod(SchedulePeriodBO schedulePeriodBO);

}
