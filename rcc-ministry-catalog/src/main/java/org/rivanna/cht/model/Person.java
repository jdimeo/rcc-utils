/*******************************************************************************
 * Copyright (c) 2016 Elder Research, Inc.
 * All rights reserved.
 *******************************************************************************/
package org.rivanna.cht.model;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.val;

@Data
public class Person {
	private String name, email, phone;
	
	@Override
	public String toString() {
		return name;
	}
	
	public static class PersonRegistry {
		private Map<String, Person> map = new HashMap<>();
		
		public Person getOrAdd(String name) {
			if (name == null) { return null; }
			
			val lower = name.toLowerCase();
			Person ret = map.get(lower);
			if (ret == null) {
				ret = new Person();
				ret.setName(name);
				map.put(lower, ret);
			}
			return ret;
		}
	}
}
