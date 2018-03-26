package com.swf.dtos;

import java.io.Serializable;
import java.util.List;

public class ScheduleResponseDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<ScheduleDTO> schedules;

	public List<ScheduleDTO> getSchedules() {
		return schedules;
	}

	public void setSchedules(List<ScheduleDTO> schedules) {
		this.schedules = schedules;
	}

	@Override
	public String toString() {
		return "ScheduleResponseDTO [schedules=" + schedules + "]";
	}

}
