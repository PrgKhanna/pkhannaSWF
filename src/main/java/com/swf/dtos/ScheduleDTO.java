package com.swf.dtos;

import java.io.Serializable;
import java.util.List;

public class ScheduleDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String date;

	private List<EngineerShiftDTO> shifts;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public List<EngineerShiftDTO> getShifts() {
		return shifts;
	}

	public void setShifts(List<EngineerShiftDTO> shifts) {
		this.shifts = shifts;
	}

	@Override
	public String toString() {
		return "ScheduleDTO [date=" + date + ", shifts=" + shifts + "]";
	}

}
