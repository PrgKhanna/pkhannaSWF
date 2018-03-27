package com.swf.utils;

import java.util.List;

import org.springframework.stereotype.Component;

import com.swf.models.EngineerBO;

@Component
public class RandomEngineerSelector implements IEngineerSelector {

	@Override
	public EngineerBO selectEngineer(List<EngineerBO> engineerBOs) {
		int randomNumber = RandomNumber.getRandomNumberInRange(0, engineerBOs.size() - 1);
		return engineerBOs.get(randomNumber);
	}

}
