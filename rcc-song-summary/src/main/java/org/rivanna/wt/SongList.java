package org.rivanna.wt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiConsumer;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jooq.lambda.Seq;

import com.elderresearch.commons.lang.LambdaUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class SongList implements BiConsumer<Path, OpenSongSong>, AutoCloseable {
	private static final String[] STOPWORDS = {
		"and", "a", "of", "the"
	};
	
	public static void main(String[] args) throws IOException, InvalidFormatException {
		var mapper = XmlMapper.builder().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build();
		try (var list = new SongList(Path.of(SystemUtils.USER_HOME, "Dropbox", "RCC WT", "RCC Song Summary.xlsx"))) {
			Files.walk(Paths.get(SystemUtils.USER_HOME, "Dropbox", "RCC WT", "OpenSong Database", "Songs")).filter(Files::isRegularFile).forEach(path -> {
				try {
					list.accept(path, mapper.readValue(path.toFile(), OpenSongSong.class));
				} catch (IOException e) {
					log.warn("Error parsing {}", path, e);
				}
			});
		}
	}
	
	private Workbook wb;
	private Sheet sheet, countSheet;
	private int row = 1;
	private SongStats stats;
	private CellStyle dateStyle;
	
	private Set<String> provenances = new TreeSet<>();
	private Map<String, Map<String, Integer>> wordCountsByProvenance = new TreeMap<>();
	
	public SongList(Path statsPath) throws InvalidFormatException, IOException {
		wb = new XSSFWorkbook(Path.of("RCC Song List Template.xlsx").toFile());
		sheet = wb.getSheetAt(0);
		countSheet = wb.getSheetAt(1);
		stats = SongStats.parse(statsPath, 0);
		
		dateStyle = wb.createCellStyle();
	    dateStyle.setDataFormat((short)14);
	}
	
	@Override
	public void accept(Path p, OpenSongSong song) {
		if (StringUtils.isBlank(song.getProvenance())) {
			var authorInfo = StringUtils.lowerCase(String.join(" ", song.getAuthor(), song.getCopyright()));
			if (StringUtils.containsAny(authorInfo, "johnson", "bethel", "riddle")) {
				log.info(p.toString() + " appears to be Bethel");
			} else if (StringUtils.containsAny(authorInfo, "furtick", "elevation")) {
				log.info(p.toString() + " appears to be Elevation");
			}
		}
		
		LambdaUtils.accept(StringUtils.stripToNull(song.getLyrics()), lyrics -> {
			var prov = StringUtils.defaultIfBlank(song.getProvenance(), "Other");
			provenances.add(prov);
			
			var lines = StringUtils.split(lyrics, "\r\n");
			for (var line : lines) {
				if (StringUtils.startsWithAny(line, ".", "[", ";")) { continue; }
				
				var arr = StringUtils.lowerCase(line).chars().filter($ -> Character.isLetter($) || Character.isSpaceChar($)).toArray();
				line = new String(arr, 0, arr.length);
				
				for (var word : StringUtils.splitByCharacterType(line)) {
					if (ArrayUtils.contains(STOPWORDS, word)) { continue; }
					
					if (Character.isLetter(word.charAt(0))) {
						wordCountsByProvenance.computeIfAbsent(word, $ -> new TreeMap<>()).merge(prov, 1, (c1, c2) -> c1 + c2);
					}
				}
			}
		});
		
		song.setTitle(Objects.toString(song.getTitle(), p.getFileName().toString()).trim());
		
		var normTitle = SongStats.normalizeTitle(song.getTitle());
		log.debug("{} normalized to {}", song.getTitle(), normTitle);
		
		var count = stats.getSongCount().remove(stats.getSongTitles().get(normTitle));
		var dates = stats.getSongDates().get(stats.getSongTitles().get(normTitle));
		if (count == null) {
			normTitle = SongStats.normalizeTitle(p.getFileName().toString());
			count = stats.getSongCount().remove(stats.getSongTitles().get(normTitle));
			dates = stats.getSongDates().get(stats.getSongTitles().get(normTitle));
		}
		
		var r = sheet.createRow(row++);
		newCell(r, 0, p.getParent().getFileName().toString());
		newCell(r, 1, song.getTitle());
		newCell(r, 2, song.getProvenance());
		newCell(r ,3, song.getSongApproval());
		if (count != null) {
			r.createCell(4, CellType.NUMERIC).setCellValue(count);
		}
		if (dates != null) {
			newCell(r, 5, dates.upperEndpoint()).setCellStyle(dateStyle);
			newCell(r, 6, dates.lowerEndpoint()).setCellStyle(dateStyle);
		}
		newCell(r, 7, song.getAuthor());
		newCell(r, 8, song.getCopyright());
	}
	
	@Override
	public void close() throws IOException {
		log.info("Remaining songs: {}", stats.getSongCount().size());
		Seq.seq(stats.getSongCount()).sorted($ -> -$.v2).forEach($ -> log.info("{} {}", $.v2, $.v1));
		
		var r = 0;
		var c = 1;
		
		var countHeader = countSheet.createRow(r++);
		for (var p : provenances) { newCell(countHeader, c++, p); }
		
		for (var e : wordCountsByProvenance.entrySet()) {
			var row = countSheet.createRow(r++);
			newCell(row, 0, e.getKey());
			
			c = 1;
			for (var p : provenances) {
				row.createCell(c++, CellType.NUMERIC).setCellValue(e.getValue().getOrDefault(p, 0));	
			}
		}
		
		try (var os = Files.newOutputStream(Path.of("RCC Song List.xlsx"))) {
			wb.write(os);
		}
	}
	
	private static Cell newCell(Row r, int c, String s) {
		var ret = r.createCell(c, CellType.STRING);
		ret.setCellValue(s);
		return ret;
	}
	
	private static Cell newCell(Row r, int c, Date d) {
		var ret = r.createCell(c, CellType.NUMERIC);
		ret.setCellValue(d);
		return ret;
	}
}
