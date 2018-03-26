package com.swf.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.swf.entities.Engineer;

@Repository
public interface EngineerRepository extends CrudRepository<Engineer, Integer> {

	List<Engineer> findByActiveTrue();

}
