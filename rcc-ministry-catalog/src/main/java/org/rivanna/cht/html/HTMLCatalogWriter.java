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

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import j2html.attributes.Attr;
import lombok.val;

public class HTMLCatalogWriter implements CatalogWriter {
	private String outputFile;
	
	public HTMLCatalogWriter(String outputFile) {
		this.outputFile = outputFile;
	}
	
	@Override
	public void write(List<Ministry> ministries) throws IOException {
		Files.write(html().with(
			header().with(
				title("Rivanna Community Church Ministry Catalog"),
				styleWithInlineFile("/css/style.css"),
				scriptWithInlineFile("/js/jquery-3.1.1.slim.min.js"),
				scriptWithInlineFile("/js/script.js")				
			),
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
					text("Show ministries by gift:"), br()
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
		).render(), new File(outputFile), Charsets.UTF_8);
	}
}
