package com.swf.rules;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.swf.models.EngineerBO;
import com.swf.models.EngineerShiftBO;

@Component
public class SameDayAllotmentFilterRule implements IFilterRule<EngineerBO, EngineerFilterHelper<EngineerShiftBO>> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SameDayAllotmentFilterRule.class);

	@Override
	public List<EngineerBO> filterRule(List<EngineerBO> applicableEngineerBOs,
			EngineerFilterHelper<EngineerShiftBO> helper) {
		LOGGER.info("Applying SameDayAllotmentFilterRule");
		List<EngineerShiftBO> ShiftBOsForADay = helper.getList();
		if (null != ShiftBOsForADay && !ShiftBOsForADay.isEmpty()) {
			Set<EngineerBO> allocatedEngineers = ShiftBOsForADay.stream().map(sh -> sh.getEngineer())
					.collect(Collectors.toSet());
			applicableEngineerBOs.removeAll(allocatedEngineers);
		}
		return applicableEngineerBOs;
	}

	@Override
	public Boolean isApplicable() {
		return true;
	}

	@Override
	public Boolean isPre() {
		return false;
	}

	@Override
	public Boolean isPost() {
		return true;
	}

	@Override
	public String getType() {
		return SWFFilterRules.SAME_DAY_ALLOCATION;
	}

}
