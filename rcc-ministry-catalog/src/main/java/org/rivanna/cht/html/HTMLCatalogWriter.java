/*******************************************************************************
 * Copyright (c) 2016 Elder Research, Inc.
 * All rights reserved.
 *******************************************************************************/
package org.rivanna.cht.html;

import java.io.IOException;
import java.util.List;

import org.rivanna.cht.CatalogWriter;
import org.rivanna.cht.model.Ministry;

import com.datamininglab.commons.lang.LambdaUtils;

public class HTMLCatalogWriter implements CatalogWriter {
	private String outputFile;
	
	public HTMLCatalogWriter(String outputFile) {
		this.outputFile = outputFile;
	}
	
	@Override
	public void write(List<Ministry> ministries) throws IOException {
		ministries.forEach(m -> {
			System.out.print(m.getName());
			LambdaUtils.accept(m.getParent(), p -> System.out.print(" under " + p.getName()));
			System.out.println(" is " + m.getOrInfer(Ministry::getType));
		});
	}
}
