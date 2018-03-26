package com.swf.enums;

import java.util.HashMap;
import java.util.Map;

public enum Shift {
	
	FIRST(1, "First"),
	SECOND(2, "Second");
	
	private Byte id;
	private String name;
	
	Shift(int id, String name) {
		this.id = (byte) id;
		this.name= name;
	}
	
	public Byte getId() {
		return id;
	}

	public void setId(Byte id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private static Map<Byte, Shift> idToShiftMap = new HashMap<Byte, Shift>();
	
	static {
		for(Shift s: Shift.values()) {
			idToShiftMap.put(s.id, s);
		}
	}
	
	public static Shift getShiftById(byte id) {
		return idToShiftMap.get(id);
	}

}
