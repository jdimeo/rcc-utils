/*******************************************************************************
 * Copyright (c) 2017 Elder Research, Inc.
 * All rights reserved.
 *******************************************************************************/
package org.rivanna.cht.xlsx;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.rivanna.cht.model.Gift;
import org.rivanna.cht.model.Ministry;
import org.rivanna.cht.model.Ministry.MinistryRegistry;
import org.rivanna.cht.model.Role;
import org.rivanna.cht.model.Role.RoleAvailability;
import org.rivanna.cht.model.Role.RoleStatus;
import org.rivanna.cht.model.Role.RoleType;

import com.datamininglab.commons.lang.LambdaUtils;
import com.datamininglab.commons.lang.Utilities;
import com.datamininglab.commons.logging.LogContext;
import com.google.common.collect.Lists;

import lombok.val;

public class XLSXRoleReader {
	private static final int MAX_ROLES = 100;
	
	private enum RoleRow {
		NAME,
		MINISTRY_ID,
		STATUS,
		FILLED_BY,
		FTBO,
		BLANK1,
		TYPE,
		OPEN_TO,
		BLANK2,
		GIFT1,
		GIFT2,
		GIFT3,
		GIFT4,
		BLANK3,
		SKILL1,
		SKILL2,
		SKILL3,
		SKILL4,
		BLANK4,
		PERSONAILTY1,
		PERSONAILTY2,
		PERSONAILTY3,
		BLANK5,
		TERM,
		TIME_COMMITMENT1,
		TIME_COMMITMENT2,
		BLANK6,
		RESPONSIBILITY1,
		RESPONSIBILITY2,
		RESPONSIBILITY3,
		RESPONSIBILITY4,
		RESPONSIBILITY5;
		
		String getValue(Map<RoleRow, Row> rowMap, int col) {
			return LambdaUtils.apply(rowMap.get(this), r -> XLSXCatalogReader.getValue(r.getCell(col)));
		}
	}
	
	private MinistryRegistry ministries;
	
	public XLSXRoleReader(MinistryRegistry ministries) {
		this.ministries = ministries;
	}
	
	public void read(Sheet sheet) {
		val rowMap = new HashMap<RoleRow, Row>();
		for (RoleRow rr : RoleRow.values()) {
			rowMap.put(rr, sheet.getRow(rr.ordinal()));
		}
		
		// Skip row header columns
		for (int i = 2; i < MAX_ROLES; i++) {
			Role r = new Role();
			r.setName(RoleRow.NAME.getValue(rowMap, i));
			if (r.getName() == null) {
				break;
			}
			
			String mid = RoleRow.MINISTRY_ID.getValue(rowMap, i);
			Ministry m = ministries.get(mid);
			if (m == null) {
				LogContext.warning("Ministry with ID %s not found in Outline tab", mid);
				continue;
			}
			m.addRole(r);
			
			// TODO: More validation/logging on enum resolution
			r.setForBenefitOf(RoleRow.FTBO.getValue(rowMap, i));
			r.setOpenTo(Utilities.valueOf(RoleAvailability.class, RoleRow.OPEN_TO.getValue(rowMap, i), null));
			r.setStatus(Utilities.valueOf(RoleStatus.class, RoleRow.STATUS.getValue(rowMap, i), null));
			r.setGifts(Lists.transform(getAll(rowMap, i,
				RoleRow.GIFT1, RoleRow.GIFT2, RoleRow.GIFT3, RoleRow.GIFT4), n -> Utilities.valueOf(Gift.class, n, null)));
			r.setPersonTraits(getAll(rowMap, i,
				RoleRow.PERSONAILTY1, RoleRow.PERSONAILTY2, RoleRow.PERSONAILTY3));
			r.setResponsibilities(getAll(rowMap, i,
				RoleRow.RESPONSIBILITY1, RoleRow.RESPONSIBILITY2, RoleRow.RESPONSIBILITY3, RoleRow.RESPONSIBILITY4, RoleRow.RESPONSIBILITY5));
			r.setSkills(getAll(rowMap, i,
				RoleRow.SKILL1, RoleRow.SKILL2, RoleRow.SKILL3, RoleRow.SKILL4));
			r.setTerm(RoleRow.TERM.getValue(rowMap, i));
			r.setTimeCommittments(getAll(rowMap, i,
				RoleRow.TIME_COMMITMENT1, RoleRow.TIME_COMMITMENT2));
			r.setType(Utilities.valueOf(RoleType.class, RoleRow.TYPE.getValue(rowMap, i), null));
		}
	}
	
	private static List<String> getAll(Map<RoleRow, Row> rowMap, int col, RoleRow... rows) {
		val ret = new LinkedList<String>();
		for (RoleRow rr : rows) {
			LambdaUtils.accept(rr.getValue(rowMap, col), ret::add);
		}
		return ret;
	}
}
