package org.rivanna.wt;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.elderresearch.commons.lang.extract.DateExtractor;
import com.elderresearch.commons.lang.extract.LocalityLevel;

public class SongSummaryAnalyzer {
	private static final int N_TOP_SONGS = 6;
	
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
	
	private static class Leader {
		Map<String, List<Date>> songs = new HashMap<>();
		int sets, total;
	}
	
	public static void main(String[] args) throws IOException {
		DateExtractor de = DateExtractor.getInstance(LocalityLevel.LANGUAGE);
		Map<String, Leader> leaders = new HashMap<>();
		Map<String, Integer> songCount = new HashMap<>();
		
		try (FileInputStream fis = new FileInputStream(args[0]); Workbook wb = new XSSFWorkbook(fis)) {
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
					Leader l = leaders.get(lname);
					if (l == null) {
						l = new Leader();
						leaders.put(lname, l);
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
						
						Leader l = leaderSeq[c];
						List<Date> list = l.songs.get(song);
						if (list == null) {
							list = new ArrayList<>();
							l.songs.put(song, list);
						}
						list.add(d);
						l.total++;
						
						songCount.put(song, songCount.getOrDefault(song, 0) + 1);
					}
				}
			}
		}
		
		System.out.println("Leader\tSets\tSongs/Set\tUnique Songs\tTotal Songs\tUnique/Total\tSongs done once");
		
		MutableInt leaderIdx = new MutableInt();
		String[][] topSongs = new String[N_TOP_SONGS + 1][leaders.size()];
		for (Entry<String, Leader> e : leaders.entrySet()) {
			int unq = e.getValue().songs.size();
			
			MutableInt once = new MutableInt();
			List<Pair<Integer, String>> sortedSongs = new ArrayList<>();
			e.getValue().songs.entrySet().forEach(e2 -> {
				int n = e2.getValue().size();
				sortedSongs.add(Pair.of(-n, e2.getKey()));
				if (n == 1) { once.increment(); }
			});
			
			MutableInt songIdx = new MutableInt(1);
			topSongs[0][leaderIdx.intValue()] = e.getKey() + "\t#";
			sortedSongs.stream().sorted().limit(N_TOP_SONGS).forEach(p -> {
				topSongs[songIdx.intValue()][leaderIdx.intValue()] = p.getValue() + "\t" + Math.abs(p.getKey());
				songIdx.increment();
			});
			
			System.out.println(new StringBuilder()
				.append(e.getKey()).append("\t")
				.append(e.getValue().sets).append("\t")
				.append(e.getValue().total / (float) e.getValue().sets).append("\t")
				.append(unq).append("\t")
				.append(e.getValue().total).append("\t")
				.append(unq * 100.0f / e.getValue().total).append("\t")
				.append(once.intValue()));
			leaderIdx.increment();
		}
		
		System.out.println("Typical Set");
		for (String[] arr : topSongs) {
			System.out.println(Arrays.stream(arr).collect(Collectors.joining("\t")));
		}
		
		System.out.println("Top Songs");
		songCount.entrySet().stream()
			.sorted(Comparator.comparing(e -> -1 * e.getValue()))
			.filter(e -> e.getValue() > 1)
			.forEachOrdered(e -> System.out.format("%s\t%d%n", e.getKey(), e.getValue()));
	}
}
