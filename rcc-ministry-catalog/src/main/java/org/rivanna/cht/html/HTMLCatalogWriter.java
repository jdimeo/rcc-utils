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
				ministries.stream().filter(Ministry::isRoot).map(MinistryRenderer::renderRoot).collect(Collectors.toList())
			).with(
				div().withClass("option-box").with(Arrays.stream(MinistryType.values()).flatMap(t -> {
					val cls = MinistryRenderer.getClass(t);
					val check = input().withType("checkbox").withClass(cls).withName(cls).attr(Attr.CHECKED, Attr.CHECKED);
					val label = label(t.toString()).attr(Attr.FOR, cls);
					return Stream.of(check, label, br());
				}).collect(Collectors.toList()))
			)
		).render(), new File(outputFile), Charsets.UTF_8);
	}
}
