/*******************************************************************************
 * Copyright (c) 2016 Elder Research, Inc.
 * All rights reserved.
 *******************************************************************************/
package org.rivanna.cht;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.rivanna.cht.html.HTMLCatalogWriter;
import org.rivanna.cht.xlsx.XLSXCatalogReader;

import com.datamininglab.commons.lang.Utilities;

public class GenerateMinistryCatalog {
	private static final String DEF_CATALOG = "/RCC Ministry Catalog.xlsx",
	                            DEF_OUTPUT  = "out.html";
	
	public static void main(String[] args) throws IOException {
		String path = StringUtils.defaultString(Utilities.first(args), DEF_CATALOG);
		new HTMLCatalogWriter(DEF_OUTPUT).write(new XLSXCatalogReader(path).read());
	}
}
