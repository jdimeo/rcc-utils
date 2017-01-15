/*******************************************************************************
 * Copyright (c) 2017 Elder Research, Inc.
 * All rights reserved.
 *******************************************************************************/
package org.rivanna.cht.model;

import org.apache.commons.lang3.text.WordUtils;

public enum Gift {
	APOSTLE(GiftColor.RED),
	ARTISTIC_CREATIVITY(GiftColor.GREEN),
	COUNSELING(GiftColor.BLUE),
	CRAFTSMANSHIP(GiftColor.GREEN),
	DELIVERANCE(GiftColor.BLUE),
	DISCERNMENT(GiftColor.BLUE),
	EVANGELISM(GiftColor.RED),
	FAITH(GiftColor.BLUE),
	GIVING(GiftColor.GREEN),
	HEALING(GiftColor.BLUE),
	HELPS(GiftColor.RED),
	HOSPITALITY(GiftColor.GREEN),
	INTERPRETATION(GiftColor.BLUE),
	KNOWLEDGE(GiftColor.GREEN),
	LEADERSHIP(GiftColor.RED),
	MERCY(GiftColor.GREEN),
	MIRACLES(GiftColor.BLUE),
	MISSIONARY(GiftColor.RED),
	MUSIC(GiftColor.GREEN),
	ORGANIZATION(GiftColor.GREEN),
	PRAYER(GiftColor.BLUE),
	PROPHECY(GiftColor.BLUE),
	SERVICE(GiftColor.RED),
	SHEPHERDING(GiftColor.RED),
	SINGLENESS(GiftColor.RED),
	SUFFERING(GiftColor.BLUE),
	TEACHING(GiftColor.RED),
	TONGUES(GiftColor.BLUE),
	VOLUNTARY_POVERTY(GiftColor.GREEN),
	WISDOM(GiftColor.GREEN);
	
	private GiftColor color;
	private String dispName;
	
	Gift(GiftColor color) {
		this.color = color;
		this.dispName = WordUtils.capitalizeFully(name().replace('_', ' '));
	}
	
	public GiftColor getColor() { return color; }
	
	@Override
	public String toString() { return dispName; }
}
