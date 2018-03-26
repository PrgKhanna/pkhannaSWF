package com.swf.services;

import java.util.List;

import com.swf.models.EngineerBO;

public interface IEngineerService {

	List<EngineerBO> getAllAvailableEngineers();

	EngineerBO getById(Integer id);

}
