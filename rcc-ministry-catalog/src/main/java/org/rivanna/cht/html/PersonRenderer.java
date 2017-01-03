/*******************************************************************************
 * Copyright (c) 2017 Elder Research, Inc.
 * All rights reserved.
 *******************************************************************************/
package org.rivanna.cht.html;

import org.apache.commons.lang3.StringUtils;
import org.rivanna.cht.model.Person;

import static j2html.TagCreator.*;
import j2html.tags.ContainerTag;

public class PersonRenderer {

	public static ContainerTag render(Person p) {
		if (p.getEmail() != null) {
			return a(p.getName()).withClass("person").withHref("mailto:" + p.getEmail());
		}
		if (p.getPhone() != null) {
			return a(p.getName()).withClass("person").withHref("tel:" + StringUtils.replaceChars(p.getPhone(), "-() ", null));
		}
		return span(p.getName()).withClass("person");
	}
	
}
