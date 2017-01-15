/*******************************************************************************
 * Copyright (c) 2017 Elder Research, Inc.
 * All rights reserved.
 *******************************************************************************/
package org.rivanna.cht.html;

import static j2html.TagCreator.a;
import static j2html.TagCreator.div;
import static j2html.TagCreator.li;
import static j2html.TagCreator.ul;

import java.util.stream.Collectors;

import org.rivanna.cht.model.Ministry;
import org.rivanna.cht.model.Ministry.MinistryType;

import j2html.tags.ContainerTag;


public class MinistryRenderer {
	public static ContainerTag renderRoot(Ministry m) {
		return renderChildren(
			div().withClass("root-box").with(
				div().withClass("root-title " + typeClass(m)).with(
					a().withHref(m.getId().replace('.', '-') + ".html").withText(m.getName())
				).with(PersonRenderer.renderList(m.getPointsOfContact()))
			), 
		m);
	}
	
	private static ContainerTag renderChildren(ContainerTag tag, Ministry m) {
		if (m.getChildren().isEmpty()) { return tag; }
		
		return tag.with(ul().withClass("sub-ministry").with(
			m.getChildren().stream().map(MinistryRenderer::render).collect(Collectors.toList())
		));
	}
	
	private static ContainerTag render(Ministry m) {
		return renderChildren(
			li(m.getName()).withClass(typeClass(m)).with(PersonRenderer.renderList(m.getPointsOfContact())),
		m);
	}
	
	private static String typeClass(Ministry m) {
		return getClass(m.getOrInfer(Ministry::getType));
	}
	
	public static String getClass(MinistryType type) {
		return "type-" + type.name().toLowerCase();
	}
	
	
}
