package org.rivanna.wt;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.elderresearch.commons.lang.CalendarUtils;
import com.elderresearch.commons.lang.extract.DateExtractor;
import com.elderresearch.commons.lang.extract.LocalityLevel;

import lombok.Getter;

@Getter
public class SongStats {
	@Getter
	public static class Leader {
		private Map<String, List<Date>> songs = new HashMap<>();
		private int sets, total;
	}
	
	private Map<String, Leader> leaders = new HashMap<>();
	private Map<String, Integer> songCount = new HashMap<>();
	
	@SuppressWarnings("serial")
	private static final Map<String, String> LEADER_FIXES = new HashMap<String, String>() {{
		put("JELDER",   "JE");
		put("JEJ",      "JE");
		put("WALT",     "WL");
		put("SCOTT",    "SK");
		put("LEIGH",    "LA");
		put("LEIGHANN", "LA");
		put("LAS",      "LA");
		put("BARBARA",  "BG");
		put("YOUTH",    "YWT");
		put("JK",       "JD");
		put("JEN",      "JD");
		put("E4",       "JOINT");
	}};
	
	public static SongStats parse(String file, int year) throws IOException {
		DateExtractor de = DateExtractor.getInstance(LocalityLevel.LANGUAGE);
		
		var ret = new SongStats();
		try (FileInputStream fis = new FileInputStream(file); Workbook wb = new XSSFWorkbook(fis)) {
			for (int i = 0; i < wb.getNumberOfSheets(); i++) {
				Sheet ws = wb.getSheetAt(i);
				System.out.format("Processing sheet %s..%n", ws.getSheetName());				
				
				Row header = ws.getRow(0);
				int ncol = header.getLastCellNum();
				
				Leader[] leaderSeq = new Leader[ncol];
				for (int c = 0; c < ncol; c++) {
					Cell cell = header.getCell(c);
					if (cell == null || cell.getCellType() != CellType.STRING) { continue; }
					
					String s = StringUtils.remove(cell.getStringCellValue(), '*').trim();
					if (s.isEmpty() || !s.contains(" ")) { continue; }
					
					String lname = StringUtils.substringBefore(s, " ").trim().toUpperCase();
					if (StringUtils.equalsIgnoreCase(lname, "CANCELED")) { continue; }
					
					lname = LEADER_FIXES.getOrDefault(lname, lname);
					Leader l = ret.leaders.get(lname);
					if (l == null) {
						l = new Leader();
						ret.leaders.put(lname, l);
					}
					l.sets++;
					leaderSeq[c] = l;
				}
				
				for (int r = 1; r <= ws.getLastRowNum(); r++) {
					Row row = ws.getRow(r);
					Cell cell = row.getCell(0);
					if (cell == null || cell.getCellType() != CellType.STRING) { break; }
					
					String song = cell.getStringCellValue().trim();
					if (song.isEmpty()) { continue; }
					
					for (int c = 0; c < ncol; c++) {
						if (leaderSeq[c] == null) { continue; }
						
						cell = row.getCell(c);
						if (cell == null) { continue; }
						
						Date d = null;
						if (cell.getCellType() == CellType.NUMERIC) {
							d = cell.getDateCellValue();
						} else {
							String s = StringUtils.substringBefore(cell.getStringCellValue(), " ");
							d = de.parse(s);
							if (d == null && !s.isEmpty()) { System.err.println("Failed to parse " + s); }
						}
						if (d == null) { continue; }
						
						if (year > 0 && CalendarUtils.at(d).get(Calendar.YEAR) != year) { continue; }
						
						Leader l = leaderSeq[c];
						List<Date> list = l.songs.get(song);
						if (list == null) {
							list = new ArrayList<>();
							l.songs.put(song, list);
						}
						list.add(d);
						l.total++;
						
						ret.songCount.put(song, ret.songCount.getOrDefault(song, 0) + 1);
					}
				}
			}
		}
		return ret;
	}
}
