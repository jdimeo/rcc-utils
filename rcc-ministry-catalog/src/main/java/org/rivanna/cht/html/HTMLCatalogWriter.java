/*******************************************************************************
 * Copyright (c) 2016 Elder Research, Inc.
 * All rights reserved.
 *******************************************************************************/
package org.rivanna.cht.html;

import static j2html.TagCreator.body;
import static j2html.TagCreator.header;
import static j2html.TagCreator.html;
import static j2html.TagCreator.styleWithInlineFile;
import static j2html.TagCreator.title;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.rivanna.cht.CatalogWriter;
import org.rivanna.cht.model.Ministry;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

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
				styleWithInlineFile("/css/style.css")
			),
			body().with(
				ministries.stream().filter(Ministry::isRoot).map(MinistryRenderer::renderRoot).collect(Collectors.toList())
			)
		).render(), new File(outputFile), Charsets.UTF_8);
	}
}
