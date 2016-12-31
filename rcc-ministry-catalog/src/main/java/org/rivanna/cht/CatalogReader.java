/*******************************************************************************
 * Copyright (c) 2016 Elder Research, Inc.
 * All rights reserved.
 *******************************************************************************/
package org.rivanna.cht;

import java.io.IOException;
import java.util.List;

import org.rivanna.cht.model.Ministry;

public interface CatalogReader {
	List<Ministry> read() throws IOException;
}
