/*******************************************************************************
 * Copyright (c) 2017 Elder Research, Inc.
 * All rights reserved.
 *******************************************************************************/
package org.rivanna.cht.xlsx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.rivanna.cht.model.Gift;
import org.rivanna.cht.model.Ministry;
import org.rivanna.cht.model.Ministry.MinistryRegistry;
import org.rivanna.cht.model.Role;
import org.rivanna.cht.model.Role.RoleStatus;
import org.rivanna.cht.model.Role.RoleType;

import com.datamininglab.commons.lang.LambdaUtils;
import com.datamininglab.commons.lang.Utilities;
import com.datamininglab.commons.logging.LogContext;
import com.google.common.collect.Lists;

import lombok.val;

public class XLSXRoleReader {
	private enum RoleRow {
		NAME,
		MINISTRY_ID,
		STATUS,
		FILLED_BY,
		FTBO,
		TYPE,
		GIFT,
		SKILLS,
		PERSONAILTY,
		TERM,
		TIME_COMMITMENTS,
		RESPONSIBILITIES;
		
		String get(Sheet sheet, Map<RoleRow, int[]> schema, int col) {
			return Utilities.first(getAll(sheet, schema, col));
		}
		
		List<String> getAll(Sheet sheet, Map<RoleRow, int[]> schema, int col) {
			val rows = schema.get(this);
			val ret = new ArrayList<String>(rows.length);
			for (int row : rows) {
				LambdaUtils.accept(XLSXCatalogReader.getValue(sheet.getRow(row).getCell(col)), ret::add);
			}
			return ret;
		}
	}
	
	private static final RoleRow[] ROWS = RoleRow.values();
	
	private MinistryRegistry ministries;
	
	public XLSXRoleReader(MinistryRegistry ministries) {
		this.ministries = ministries;
	}
	
	public void read(Sheet sheet) {
		Map<RoleRow, int[]> schema = new HashMap<>();
		
		val nRows = sheet.getLastRowNum() + 1;
		if (nRows < RoleRow.values().length) {
			LogContext.warning("Ignoring sheet %s; not enough rows", sheet.getSheetName());
			return;
		}
		
		RoleRow row = null;
		for (int r = 0, i = 0; r < nRows; r++) {
			boolean hasHeader = XLSXCatalogReader.getValue(sheet.getRow(r).getCell(0)) != null;
			if (row == null || hasHeader) { row = ROWS[i++]; }
			schema.put(row, ArrayUtils.add(schema.get(row), r));
		}	
		
		// Skip row header column, skip two since some fields are split between two columns
		val nCols = sheet.getRow(0).getLastCellNum();
		for (int i = 1; i < nCols; i += 2) {
			Role r = new Role();
			r.setId(RoleRow.NAME.get(sheet, schema, i));
			r.setName(RoleRow.NAME.get(sheet, schema, i + 1));
			if (r.getId() == null || r.getName() == null) { continue; }
			
			String mid = RoleRow.MINISTRY_ID.get(sheet, schema, i);
			Ministry m = ministries.get(mid);
			if (m == null) {
				LogContext.warning("Ministry with ID %s not found in Outline tab", mid);
				continue;
			}
			m.addRole(r);
			
			// TODO: More validation/logging on enum resolution
			r.setForBenefitOf(RoleRow.FTBO.get(sheet, schema, i));
			// r.setOpenTo(Utilities.valueOf(RoleAvailability.class, RoleRow.OPEN_TO.getValue(rowMap, i), null));
			r.setStatus(Utilities.valueOf(RoleStatus.class, RoleRow.STATUS.get(sheet, schema, i), null));
			r.setGifts(Lists.transform(RoleRow.GIFT.getAll(sheet, schema, i), n -> Utilities.valueOf(Gift.class, n, null)));
			r.setPersonTraits(RoleRow.PERSONAILTY.getAll(sheet, schema, i));
			r.setResponsibilities(RoleRow.RESPONSIBILITIES.getAll(sheet, schema, i));
			r.setSkills(RoleRow.SKILLS.getAll(sheet, schema, i));
			r.setTerm(RoleRow.TERM.get(sheet, schema, i));
			r.setTimeCommittments(RoleRow.TIME_COMMITMENTS.getAll(sheet, schema, i));
			r.setType(Utilities.valueOf(RoleType.class, RoleRow.TYPE.get(sheet, schema, i), null));
		}
	}
}
