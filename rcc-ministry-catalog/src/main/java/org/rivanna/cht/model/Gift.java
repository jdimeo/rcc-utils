/*******************************************************************************
 * Copyright (c) 2017 Elder Research, Inc.
 * All rights reserved.
 *******************************************************************************/
package org.rivanna.cht.model;

import org.apache.commons.lang3.text.WordUtils;

public enum Gift {
	ADMINISTRATION(GiftColor.GREEN),
	CELIBACY(GiftColor.RED),
	DELIVERANCE(GiftColor.BLUE),
	DISCERNING_OF_SPIRITS(GiftColor.BLUE),
	EVANGELISM(GiftColor.RED),
	EXHORTATION(GiftColor.BLUE),
	FAITH(GiftColor.BLUE),
	GIVING(GiftColor.GREEN),
	HEALING(GiftColor.BLUE),
	HELPS(GiftColor.RED),
	HOSPITALITY(GiftColor.GREEN),
	INTERCESSION(GiftColor.BLUE),
	INTERPRETATION_OF_TONGUES(GiftColor.BLUE),
	KNOWLEDGE(GiftColor.GREEN),
	LEADERSHIP(GiftColor.RED),
	MERCY(GiftColor.GREEN),
	MIRACLES(GiftColor.BLUE),
	MISSIONARY(GiftColor.RED),
	PASTOR(GiftColor.RED),
	PROPHECY(GiftColor.BLUE),
	SERVICE(GiftColor.RED),
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
