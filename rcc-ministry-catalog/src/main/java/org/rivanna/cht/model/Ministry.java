/*******************************************************************************
 * Copyright (c) 2016 Elder Research, Inc.
 * All rights reserved.
 *******************************************************************************/
package org.rivanna.cht.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.lang3.text.WordUtils;

import lombok.Data;
import lombok.val;

@Data
public class Ministry {
	public enum MinistryType {
		CORE,
		ELECTIVE;
		
		@Override
		public String toString() { return WordUtils.capitalizeFully(name()); }
	}
	
	private String id;
	private MinistryType type;
	private Ministry parent;
	private String name;
	private Person elderLiason;
	private List<Person> pointsOfContact;
	private List<Gift> gifts;
	private List<Ministry> children = new LinkedList<>();
	
	public void setParent(Ministry parent) {
		this.parent = parent;
		parent.children.add(this);
	}
	
	public boolean isRoot() {
		return parent == null;
	}
	
	public <T> T getOrInfer(Function<Ministry, T> fn) {
		val ret = fn.apply(this);
		return isRoot() || ret != null? ret : parent.getOrInfer(fn);
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
