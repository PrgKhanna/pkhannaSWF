package com.swf.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ScheduleBO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Date date;

	private List<EngineerShiftBO> shifts;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<EngineerShiftBO> getShifts() {
		return shifts;
	}

	public void setShifts(List<EngineerShiftBO> shifts) {
		this.shifts = shifts;
	}

	@Override
	public String toString() {
		return "ScheduleDTO [date=" + date + ", shifts=" + shifts + "]";
	}

}