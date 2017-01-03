/*******************************************************************************
 * Copyright (c) 2017 Elder Research, Inc.
 * All rights reserved.
 *******************************************************************************/
package org.rivanna.cht.html;

import static j2html.TagCreator.*;

import java.util.stream.Collectors;

import org.rivanna.cht.model.Ministry;

import j2html.tags.ContainerTag;
import lombok.val;


public class MinistryRenderer {
	public static ContainerTag renderRoot(Ministry m) {
		val ret = div().withClass("root-box").with(
			div().withClass("root-title " + typeClass(m)).with(
				a().withHref(m.getId().replace('.', '-') + ".html").withText(m.getName())
			)
		);
		if (m.getPointsOfContact() != null && !m.getPointsOfContact().isEmpty()) {
			val poc = div().withClass("root-poc");
			m.getPointsOfContact().forEach(p -> {
				if (!poc.children.isEmpty()) { poc.with(span(", ")); }
				poc.with(PersonRenderer.render(p));
			});
			ret.with(poc);
		}
		return renderChildren(ret, m);
	}
	
	private static ContainerTag renderChildren(ContainerTag tag, Ministry m) {
		if (m.getChildren().isEmpty()) { return tag; }
		
		return tag.with(ul().withClass("sub-ministry").with(
			m.getChildren().stream().map(MinistryRenderer::render).collect(Collectors.toList())
		));
	}
	
	private static ContainerTag render(Ministry m) {
		return renderChildren(li(m.getName()).withClass(typeClass(m)), m);
	}
	
	private static String typeClass(Ministry m) {
		return m.getOrInfer(Ministry::getType).name().toLowerCase();
	}
}
