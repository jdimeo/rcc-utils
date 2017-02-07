/*******************************************************************************
 * Copyright (c) 2017 Elder Research, Inc.
 * All rights reserved.
 *******************************************************************************/
package org.rivanna.cht.html;

import java.util.List;

import org.rivanna.cht.model.Role;

import com.google.common.collect.Lists;

import j2html.attributes.Attr;
import j2html.tags.Tag;
import static j2html.TagCreator.*;

public class RoleRenderer {
	public static Tag renderList(List<Role> roles) {
		return ul().with(Lists.transform(roles, r -> {
			return li().withClass("role").with(a(r.getName()).attr(Attr.HREF, "roles/" + r.getId() + ".html"));
		}));
	}
}
