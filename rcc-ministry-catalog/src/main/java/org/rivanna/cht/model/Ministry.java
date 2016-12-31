/*******************************************************************************
 * Copyright (c) 2016 Elder Research, Inc.
 * All rights reserved.
 *******************************************************************************/
package org.rivanna.cht.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import lombok.Data;
import lombok.Setter;
import lombok.val;

@Data
public class Ministry {
	public enum MinistryType {
		ESSENTIAL,
		OPPORTUNITY
	}
	
	@Setter private MinistryType type;
	private String id;
	private Ministry parent;
	private String name;
	private Person elderLiason;
	private List<Person> pointsOfContact;
	
	public <T> T getOrInfer(Function<Ministry, T> fn) {
		val ret = fn.apply(this);
		return parent == null || ret != null? ret : parent.getOrInfer(fn);
	}
	
	public static class MinistryRegistry {
		private Map<String, Ministry> map = new HashMap<>();
		
		public Ministry getOrAdd(String id) {
			if (id == null) { return null; }
			
			Ministry ret = map.get(id);
			if (ret == null) {
				ret = new Ministry();
				ret.setId(id);
				
				val i = id.lastIndexOf('.');
				if (i > 0) {
					ret.setParent(getOrAdd(id.substring(0, i)));	
				}
				map.put(id, ret);
			}
			return ret;
		}
	}
}
