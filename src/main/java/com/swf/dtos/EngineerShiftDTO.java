package com.swf.dtos;

import java.io.Serializable;

import com.swf.enums.Shift;

public class EngineerShiftDTO implements Serializable{
	 
	private static final long serialVersionUID = 1L;
	
	private EngineerDTO engineer;
	
	private Shift shift;

	public EngineerDTO getEngineer() {
		return engineer;
	}

	public void setEngineer(EngineerDTO engineer) {
		this.engineer = engineer;
	}

	public Shift getShift() {
		return shift;
	}

	public void setShift(Shift shift) {
		this.shift = shift;
	}

	@Override
	public String toString() {
		return "EngineerScheduleDTO [engineer=" + engineer + ", shift=" + shift + "]";
	}

}
