package com.swf.models;

import java.io.Serializable;
import java.util.Date;

import com.swf.enums.Shift;

public class EngineerShiftBO implements Serializable {

	private static final long serialVersionUID = 1L;

	private EngineerBO engineer;

	private Shift shift;

	private Date date;

	public EngineerBO getEngineer() {
		return engineer;
	}

	public void setEngineer(EngineerBO engineer) {
		this.engineer = engineer;
	}

	public Shift getShift() {
		return shift;
	}

	public void setShift(Shift shift) {
		this.shift = shift;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "EngineerShiftBO [engineer=" + engineer + ", shift=" + shift + ", date=" + date + "]";
	}

}
