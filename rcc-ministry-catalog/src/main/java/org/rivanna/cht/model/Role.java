/*******************************************************************************
 * Copyright (c) 2017 Elder Research, Inc.
 * All rights reserved.
 *******************************************************************************/
package org.rivanna.cht.model;

import java.util.List;

import org.apache.commons.lang3.text.WordUtils;

import lombok.Data;

@Data
public class Role {
	public enum RoleStatus {
		VACANCY,
		PARTIALLY_FILLED,
		FILLED;
		
		@Override
		public String toString() {
			return WordUtils.capitalizeFully(name().replace('_', ' '));
		}
	}
	
	public enum RoleType {
		VOLUNTEER,
		STAFF
	}
	
	public enum RoleAvailability {
		ANYONE,
		CHURCH_MEMBER,
		DEACON,
		ELDER,
		PASTOR
	}
	
	private String id;
	private String name;
	private Ministry ministry;
	private RoleStatus status;
	private List<Person> filledBy;
	private String forBenefitOf;
	private RoleType type;
	private RoleAvailability openTo;
	private List<Gift> gifts;
	private List<String> skills;
	private List<String> personTraits;
	private String term;
	private List<String> timeCommittments;
	private List<String> responsibilities;
	
	public String getFullId() {
		return String.format("%s_%s", ministry.getId(), id);
	}
}
