/*******************************************************************************
 * Copyright (c) 2016 Elder Research, Inc.
 * All rights reserved.
 *******************************************************************************/
package org.rivanna.cht.html;

import static j2html.TagCreator.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.rivanna.cht.CatalogWriter;
import org.rivanna.cht.model.Gift;
import org.rivanna.cht.model.Ministry;
import org.rivanna.cht.model.Ministry.MinistryType;
import org.rivanna.cht.model.Role;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import j2html.TagCreator;
import j2html.attributes.Attr;
import j2html.tags.ContainerTag;
import lombok.val;

public class HTMLCatalogWriter implements CatalogWriter {
	private static final String TITLE = "RCC Ministries";
	
	private String outputFile;
	
	public HTMLCatalogWriter(String outputFile) {
		this.outputFile = outputFile;
	}
	
	static ContainerTag header(String title) {
		return TagCreator.header().with(
			title(title == null? TITLE : String.format("%s - %s", TITLE, title)),
			styleWithInlineFile("/css/style.css"),
			scriptWithInlineFile("/js/jquery-3.1.1.slim.min.js"),
			scriptWithInlineFile("/js/script.js")				
		);
	}
	
	@Override
	public void write(List<Ministry> ministries) throws IOException {
		val f = new File(outputFile);
		val rf = new File(f.getParentFile(), RoleRenderer.ROLE_DIR);
		rf.mkdirs();
		
		Files.write(html().with(
			header(null),
			body().with(
				div().withClass("option-box").with(
					text("Show ministries by type:"), br()
				).with(Arrays.stream(MinistryType.values()).flatMap(t -> {
					val cls = MinistryRenderer.getClass(t);
					val check = input().withType("checkbox")
						.withClass("toggle-type")
						.withId("toggle-" + cls)
						.withName(cls)
						.attr(Attr.CHECKED, Attr.CHECKED);
					return Stream.of(check, label(t.toString()).attr(Attr.FOR, cls), br());
				}).collect(Collectors.toList()))
				.with(
					text("Show ministry roles:"), br()
				).with(
					input().withType("checkbox")
						.withClass("toggle-role")
						.withId("toggle-role")
						.withName("role")
						.attr(Attr.CHECKED, Attr.CHECKED),
					label("Show roles"),
					br()
				).with(
					text("Show spiritual gifts:"), br()
				).with(Arrays.stream(Gift.values()).flatMap(g -> {
					val cls = GiftRenderer.getClass(g);
					val check = input().withType("checkbox")
						.withClass("toggle-gift")
						.withId("toggle-" + cls)
						.withName(cls);
					return Stream.of(check, label(g.toString()).withClass(GiftRenderer.getClassColor(g)).attr(Attr.FOR, cls), br());
				}).collect(Collectors.toList()))
			).with(
				ministries.stream().filter(Ministry::isRoot).map(MinistryRenderer::renderRoot).collect(Collectors.toList())
			)
		).render(), f, Charsets.UTF_8);
		
		for (Ministry m : ministries) {
			for (Role r : m.getRoles()) {
				Files.write(RoleRenderer.render(r).render(), new File(rf, r.getFullId() + ".html"), Charsets.UTF_8);	
			}
		}
	}
}
