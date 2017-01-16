/*******************************************************************************
 * Copyright (c) 2017 Elder Research, Inc.
 * All rights reserved.
 *******************************************************************************/
package org.rivanna.cht.html;

import org.rivanna.cht.model.Gift;

public class GiftRenderer {
	public static String getClass(Gift gift) {
		return "gift-" + gift.name().toLowerCase().replace('_', '-');
	}
}
