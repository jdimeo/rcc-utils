/*******************************************************************************
 * Copyright (c) 2017 Elder Research, Inc.
 * All rights reserved.
 *******************************************************************************/
package org.rivanna.cht.html;

import static j2html.TagCreator.*;

import java.util.List;

import org.apache.commons.text.WordUtils;
import org.rivanna.cht.model.Role;

import com.google.common.collect.Lists;

import j2html.attributes.Attr;
import j2html.tags.ContainerTag;
import j2html.tags.Tag;

public class RoleRenderer {
	static final String ROLE_DIR = "roles";
	
	public static ContainerTag render(Role role) {
		return html().with(
			HTMLCatalogWriter.header(role.getName()),
			body().with(
				
			)
		);
	}
	
	public static Tag renderList(List<Role> roles) {
		return ul().with(Lists.transform(roles, r -> {
			String status = r.getStatus().name();
			return li().withClass("role")
				.with(a(r.getName())
					.attr(Attr.HREF, ROLE_DIR + "/" + r.getFullId() + ".html"))
				.with(span(status.substring(0, 1))
					.withClass("gift-box gift-" + status.toLowerCase())
					.attr(Attr.TITLE, WordUtils.capitalizeFully(status)));
		}));
	}
}
