package com.swf.rules;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.swf.models.EngineerBO;
import com.swf.models.EngineerShiftBO;
import com.swf.utils.SWFConstants;

@Component
public class MaxAllocationFilterRule implements IFilterRule<EngineerBO, EngineerFilterHelper<EngineerShiftBO>> {

	private static final Logger LOGGER = LoggerFactory.getLogger(MaxAllocationFilterRule.class);

	@Override
	public List<EngineerBO> filterRule(List<EngineerBO> engineerBOs, EngineerFilterHelper<EngineerShiftBO> helper) {
		LOGGER.info("Applying MaxAllocationFilterRule");

		if (null != helper.getList() && !helper.getList().isEmpty()) {
			List<EngineerShiftBO> shifts = helper.getList();
			Map<EngineerBO, Long> engineerToShiftCountMap = shifts.stream()
					.collect(Collectors.groupingBy(EngineerShiftBO::getEngineer, Collectors.counting()));
			List<EngineerBO> notApplicableEngineerBOs = engineerToShiftCountMap.keySet().stream()
					.filter(k -> engineerToShiftCountMap.get(k) == SWFConstants.ALLOWED_SHIFT_PER_SCHEDULE)
					.collect(Collectors.toList());

			List<EngineerBO> applicableEngineers = engineerBOs;
			applicableEngineers.removeAll(notApplicableEngineerBOs);
			return applicableEngineers;
		} else {
			return engineerBOs;
		}
	}

	@Override
	public Boolean isApplicable() {
		return true;
	}

	@Override
	public String getType() {
		return SWFFilterRules.MAX_ALLOCATION_RULE;
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
