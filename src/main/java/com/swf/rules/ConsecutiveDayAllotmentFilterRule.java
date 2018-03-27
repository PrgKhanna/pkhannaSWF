package com.swf.rules;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.swf.models.EngineerBO;
import com.swf.models.EngineerShiftBO;
import com.swf.utils.SWFConstants;

@Component
public class ConsecutiveDayAllotmentFilterRule
		implements IFilterRule<EngineerBO, EngineerFilterHelper<EngineerShiftBO>> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConsecutiveDayAllotmentFilterRule.class);

	@Override
	public List<EngineerBO> filterRule(List<EngineerBO> engineerBOs, EngineerFilterHelper<EngineerShiftBO> helper) {
		LOGGER.info("Applying ConsecutiveDayAllotmentRule");
		Set<EngineerBO> notApplicableEngineerBOs = new HashSet<EngineerBO>();
		if (null != helper.getList() && !helper.getList().isEmpty()) {
			List<EngineerShiftBO> sortedShiftBOs = helper.getList();
			sortedShiftBOs.sort((EngineerShiftBO es1, EngineerShiftBO es2) -> es2.getDate().compareTo(es1.getDate()));

			notApplicableEngineerBOs = sortedShiftBOs.subList(0, SWFConstants.NO_OF_SHIFTS).stream()
					.map(s -> s.getEngineer()).collect(Collectors.toSet());
		}
		List<EngineerBO> applicableEngineers = engineerBOs;
		applicableEngineers.removeAll(notApplicableEngineerBOs);
		return applicableEngineers;
	}

	@Override
	public Boolean isApplicable() {
		return true;
	}

	@Override
	public String getType() {
		return SWFFilterRules.CONSECUTIVE_DAY_ALLOCATION_RULE;
	}

	@Override
	public Boolean isPre() {
		return true;
	}

	@Override
	public Boolean isPost() {
		return false;
	}

}
