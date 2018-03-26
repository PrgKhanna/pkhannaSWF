package com.swf.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.swf.entities.EngineerShift;

@Repository
public interface EngineerShiftRepository extends CrudRepository<EngineerShift, Integer> {
	
	List<EngineerShift> findByDateBetween(Date startDate, Date endDate);

}
