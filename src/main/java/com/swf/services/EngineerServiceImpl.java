package com.swf.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.swf.entities.Engineer;
import com.swf.mappers.ObjectMapperService;
import com.swf.models.EngineerBO;
import com.swf.repositories.EngineerRepository;

@Service
public class EngineerServiceImpl implements IEngineerService {

	private static final Logger LOGGER = LoggerFactory.getLogger(EngineerServiceImpl.class);

	@Autowired
	private EngineerRepository engineerRepository;

	@Autowired
	private ObjectMapperService mapper;

	@Autowired
	private RedisService redisService;

	/**
	 * Return available Engineers
	 */
	public List<EngineerBO> getAllAvailableEngineers() {
		LOGGER.info("Getting all available Engineers");
		List<EngineerBO> availableEngineerBOs = (List<EngineerBO>) redisService.getValue("employees");
		if (null != availableEngineerBOs) {
			LOGGER.error("Got Available Engineers from cache");
			return availableEngineerBOs;
		}
		try {
			List<Engineer> availableEngineers = engineerRepository.findByActiveTrue();
			if (null != availableEngineers) {
				LOGGER.error("Got Available Engineers");
				availableEngineerBOs = mapper.mapAsList(availableEngineers, EngineerBO.class);
				return availableEngineerBOs;
			}
			LOGGER.error("Got NULL while getting Available Engineers");
			return availableEngineerBOs;
		} catch (Exception e) {
			LOGGER.error("Failed to get Available Engineers", e.getMessage());
		}
		return availableEngineerBOs;
	}

	@Override
	public EngineerBO getById(Integer id) {
		LOGGER.info("Getting Engineer By id");
		EngineerBO engineerBO = null;
		try {
			Engineer engineer = engineerRepository.findOne(id);
			if (engineer != null) {
				engineerBO = mapper.map(engineer, EngineerBO.class);
			} else {
				LOGGER.error("Failed to get Engineer by id");
			}
		} catch (Exception e) {
			LOGGER.error("Failed to get Engineer by id", e.getMessage());
		}
		return engineerBO;
	}

}
