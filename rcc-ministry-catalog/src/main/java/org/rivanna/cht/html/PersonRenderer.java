/*******************************************************************************
 * Copyright (c) 2017 Elder Research, Inc.
 * All rights reserved.
 *******************************************************************************/
package org.rivanna.cht.html;

import static j2html.TagCreator.a;
import static j2html.TagCreator.span;
import static j2html.TagCreator.text;
import static j2html.TagCreator.unsafeHtml;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.rivanna.cht.model.Person;

import j2html.tags.ContainerTag;
import j2html.tags.Tag;

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
	
	public static Tag renderList(List<Person> people) {
		if (people.isEmpty()) { return text(StringUtils.EMPTY); }
		
		return unsafeHtml(people.stream().map(PersonRenderer::render).map(Tag::render).collect(Collectors.joining(", ", " (", ")")));
	}
}
