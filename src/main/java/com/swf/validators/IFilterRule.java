package com.swf.validators;

import java.util.List;

public interface IFilterRule<T, U> {

	List<T> filterRule(List<T> t, U u);

	Boolean isApplicable();

	Boolean isPre();

	Boolean isPost();

	String getType();

}
