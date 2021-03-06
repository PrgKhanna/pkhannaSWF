package com.swf.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.swf.dtos.ScheduleDTO;
import com.swf.dtos.ScheduleResponseDTO;
import com.swf.services.ISchedulerService;

@Controller
@RequestMapping("schedule")
public class ScheduleController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleController.class);

	@Autowired
	private ISchedulerService scheduleService;

	@RequestMapping(method = RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE })
	public String getAPI() {
		LOGGER.info("Landing API");
		return "schedule";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/get")
	@ResponseBody
	public ResponseEntity<ScheduleResponseDTO> getSchedule() {
		LOGGER.info("API Getting Schedule for the Engineers");
		List<ScheduleDTO> schedules = scheduleService.getCurrentSchedule();
		if (schedules != null) {
			LOGGER.info("API Getting Schedule for the Engineers : Successful");
			ScheduleResponseDTO response = new ScheduleResponseDTO();
			response.setSchedules(schedules);
			return new ResponseEntity<ScheduleResponseDTO>(response, HttpStatus.OK);
		} else {
			LOGGER.error("API Getting Schedule for the Engineers : Failed");
			return new ResponseEntity<ScheduleResponseDTO>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
