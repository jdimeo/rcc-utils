/*******************************************************************************
 * Copyright (c) 2017 Elder Research, Inc.
 * All rights reserved.
 *******************************************************************************/
package org.rivanna.cht.html;

import static j2html.TagCreator.span;
import static j2html.TagCreator.text;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.rivanna.cht.model.Gift;

import com.google.common.collect.Lists;

import j2html.attributes.Attr;
import j2html.tags.Tag;

public class GiftRenderer {
	public static String getClass(Gift gift) {
		return "gift-" + gift.name().toLowerCase().replace('_', '-');
	}
	public static String getClassColor(Gift gift) {
		return "gift-" + gift.getColor().name().toLowerCase();
	}
	
	public static Tag renderList(List<Gift> gifts) {
		if (gifts.isEmpty()) { return text(StringUtils.EMPTY); }
		
		return span().with(Lists.transform(gifts, GiftRenderer::render));
		
	}
	
	public static Tag render(Gift gift) {
		return span(gift.name().substring(0, 1))
			.withClass(StringUtils.joinWith(" ", getClass(gift), getClassColor(gift), "gift-box"))
			.attr(Attr.TITLE, gift.toString())
			.attr(Attr.STYLE, "display: none;");
	}
}
