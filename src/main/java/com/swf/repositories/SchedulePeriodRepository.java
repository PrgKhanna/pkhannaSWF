package com.swf.repositories;

import org.springframework.data.repository.CrudRepository;

import com.swf.entities.SchedulePeriod;

public interface SchedulePeriodRepository extends CrudRepository<SchedulePeriod, Integer>{
	
	SchedulePeriod findByActiveTrue();

}
