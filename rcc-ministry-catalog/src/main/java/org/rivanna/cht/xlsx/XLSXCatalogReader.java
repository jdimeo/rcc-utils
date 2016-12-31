/*******************************************************************************
 * Copyright (c) 2016 Elder Research, Inc.
 * All rights reserved.
 *******************************************************************************/
package org.rivanna.cht.xlsx;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.rivanna.cht.CatalogReader;
import org.rivanna.cht.model.Ministry;
import org.rivanna.cht.model.Ministry.MinistryRegistry;
import org.rivanna.cht.model.Ministry.MinistryType;
import org.rivanna.cht.model.Person.PersonRegistry;

import com.datamininglab.commons.lang.LambdaUtils;
import com.datamininglab.commons.lang.Utilities;

import lombok.val;

public class XLSXCatalogReader implements CatalogReader {
	private String pathToWorkbook;
	private PersonRegistry people;
	private MinistryRegistry ministries;
	
	public XLSXCatalogReader(String pathToWorkbook) {
		this.pathToWorkbook = pathToWorkbook;
		this.people = new PersonRegistry();
		this.ministries = new MinistryRegistry();
	}
	
	@Override @SuppressWarnings("resource")
	public List<Ministry> read() throws IOException {
		try (val is = Utilities.getResourceAsStream(getClass(), pathToWorkbook)) {
			val pkg = OPCPackage.open(is);
			try {
				return read(new XSSFWorkbook(pkg));
			} finally {
				pkg.revert();
			}
		} catch (InvalidFormatException e) {
			throw new IOException(e);
		}
	}
	
	private List<Ministry> read(Workbook wb) {
		wb.setMissingCellPolicy(Row.RETURN_BLANK_AS_NULL);

		val overview = wb.getSheetAt(0);
		val n = overview.getLastRowNum() + 1;
		
		List<Ministry> ret = new LinkedList<>();
		for (int i = 1; i < n; i++) {
			val row = overview.getRow(i);
			
			// Find any defined ID from the first 3 columns
			val idCells = Arrays.asList(row.getCell(0), row.getCell(1), row.getCell(2));
			val id = idCells.stream().filter(Objects::nonNull).findAny().map(XLSXCatalogReader::getValue).get();
			if (id == null) { continue; }
			
			val m = ministries.getOrAdd(id);
			m.setName(LambdaUtils.apply(row.getCell(3), XLSXCatalogReader::getValue));
			m.setType(LambdaUtils.apply(row.getCell(4), c ->
				Utilities.valueOf(MinistryType.class, getValue(c), null)));
			m.setElderLiason(LambdaUtils.apply(row.getCell(5), c ->
				people.getOrAdd(getValue(c))));
			m.setPointsOfContact(LambdaUtils.apply(row.getCell(6), c ->
				Arrays.stream(StringUtils.split(getValue(c), ','))
				      .map(StringUtils::trim)
				      .map(people::getOrAdd)
				      .collect(Collectors.toList())));
			ret.add(m);
		}
		return ret;
	}
	
	private static final DecimalFormat DF = new DecimalFormat("#.#");
	
	private static String getValue(Cell c) {
		if (c.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			return DF.format(c.getNumericCellValue());
		}
		return c.getStringCellValue();
	}
}
