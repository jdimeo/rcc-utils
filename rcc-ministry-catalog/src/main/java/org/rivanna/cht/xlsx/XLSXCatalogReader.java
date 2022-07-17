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
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.rivanna.cht.CatalogReader;
import org.rivanna.cht.model.Ministry;
import org.rivanna.cht.model.Ministry.MinistryRegistry;
import org.rivanna.cht.model.Ministry.MinistryType;
import org.rivanna.cht.model.Person.PersonRegistry;

import com.elderresearch.commons.lang.LambdaUtils;
import com.elderresearch.commons.lang.Utilities;

import lombok.val;

public class XLSXCatalogReader implements CatalogReader {
	private String pathToWorkbook;
	private PersonRegistry people;
	private MinistryRegistry ministries;
	private XLSXRoleReader roleReader;
	
	public XLSXCatalogReader(String pathToWorkbook) {
		this.pathToWorkbook = pathToWorkbook;
		this.people = new PersonRegistry();
		this.ministries = new MinistryRegistry();
		this.roleReader = new XLSXRoleReader(ministries);
	}
	
	@Override @SuppressWarnings("resource")
	public List<Ministry> read() throws IOException {
		try (val is = Utilities.getResourceOrFile(getClass(), pathToWorkbook)) {
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
		wb.setMissingCellPolicy(Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
		readDirectory(wb.getSheet("Directory"));
		val ret = readOutline(wb.getSheet("Outline")); 
		roleReader.read(wb.getSheet("Roles"));
		return ret;
	}
	
	private void readDirectory(Sheet sheet) {
		val n = sheet.getLastRowNum() + 1;
		for (int i = 1; i < n; i++) {
			val row = sheet.getRow(i);
			if (row == null) { continue; }
			
			val name  = LambdaUtils.apply(row.getCell(0), XLSXCatalogReader::getValue);
			val email = LambdaUtils.apply(row.getCell(1), XLSXCatalogReader::getValue);
			val phone = LambdaUtils.apply(row.getCell(2), XLSXCatalogReader::getValue);
			
			val p = people.getOrAdd(name);
			LambdaUtils.accept(email, p::setEmail);
			LambdaUtils.accept(phone, p::setPhone);
		}
	}
	
	private List<Ministry> readOutline(Sheet sheet) {
		val ret = new LinkedList<Ministry>();
		val n = sheet.getLastRowNum() + 1;
		for (int i = 1; i < n; i++) {
			val row = sheet.getRow(i);
			
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
			
			m.setPointsOfContact(Stream.of(row.getCell(6), row.getCell(7), row.getCell(8))
				.filter(Objects::nonNull)
				.map(XLSXCatalogReader::getValue)
				.map(StringUtils::trim)
				.map(people::getOrAdd)
				.collect(Collectors.toList()));
			
			ret.add(m);
		}
		return ret;
	}
	
	private static final DecimalFormat DF = new DecimalFormat("#.#");
	static String getValue(Cell c) {
		if (c == null) { return null; }
		
		if (c.getCellType() == CellType.NUMERIC) {
			return DF.format(c.getNumericCellValue());
		}
		return StringUtils.defaultIfBlank(c.getStringCellValue(), null);
	}
}
