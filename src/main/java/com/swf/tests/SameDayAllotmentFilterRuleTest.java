package com.swf.tests;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.swf.enums.Shift;
import com.swf.models.EngineerBO;
import com.swf.models.EngineerShiftBO;
import com.swf.rules.EngineerFilterHelper;
import com.swf.rules.SameDayAllotmentFilterRule;

public class SameDayAllotmentFilterRuleTest {

	List<EngineerBO> engineerBOs = new ArrayList<EngineerBO>();
	List<EngineerShiftBO> shiftsBO = new ArrayList<EngineerShiftBO>();

	@Before
	public void init() {
		EngineerBO e1 = new EngineerBO(1, "E1", "e1@e.com", "Phe1", "emp1", true);
		EngineerBO e2 = new EngineerBO(2, "E2", "e2@e.com", "Phe2", "emp2", true);
		EngineerBO e3 = new EngineerBO(3, "E3", "e3@e.com", "Phe3", "emp3", true);
		engineerBOs.add(e1);
		engineerBOs.add(e2);
		engineerBOs.add(e3);

		shiftsBO.add(new EngineerShiftBO(e1, Shift.FIRST, new Date()));
	}

	@Test
	public void sameDayAllotmentFilterRuleWithEmptyEngineerBOTest() {
		SameDayAllotmentFilterRule rule = new SameDayAllotmentFilterRule();
		List<EngineerBO> filteredEngineers = rule.filterRule(new ArrayList<EngineerBO>(),
				new EngineerFilterHelper<EngineerShiftBO>());
		Assert.assertEquals(filteredEngineers.size(), 0);
	}

	@Test
	public void sameDayAllotmentFilterRuleWithEngineerBOAndNoShiftTest() {
		SameDayAllotmentFilterRule rule = new SameDayAllotmentFilterRule();
		List<EngineerBO> filteredEngineers = rule.filterRule(engineerBOs, new EngineerFilterHelper<EngineerShiftBO>());
		Assert.assertEquals(filteredEngineers.size(), 3);
	}

	@Test
	public void sameDayAllotmentFilterRuleTest() {
		EngineerFilterHelper<EngineerShiftBO> helper = new EngineerFilterHelper<EngineerShiftBO>();
		helper.setList(shiftsBO);

		SameDayAllotmentFilterRule rule = new SameDayAllotmentFilterRule();
		List<EngineerBO> filteredEngineers = rule.filterRule(engineerBOs, helper);
		Assert.assertEquals(filteredEngineers.size(), 2);
	}

}
