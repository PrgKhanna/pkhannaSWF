package com.swf.utils;

import java.util.List;

import com.swf.models.EngineerBO;

public interface IEngineerSelector {

	EngineerBO selectEngineer(List<EngineerBO> engineerBOs);
}
