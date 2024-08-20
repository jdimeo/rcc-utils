package org.rivanna.wt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.BiConsumer;

import org.apache.commons.lang3.SystemUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class SongList implements BiConsumer<Path, OpenSongSong>, AutoCloseable {
	public static void main(String[] args) throws IOException, InvalidFormatException {
		var mapper = XmlMapper.builder().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build();
		try (var list = new SongList()) {
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
	private Sheet sheet;
	private int row = 1;
	
	public SongList() throws InvalidFormatException, IOException {
		wb = new XSSFWorkbook(Path.of("RCC Song List Template.xlsx").toFile());
		sheet = wb.getSheetAt(0);
	}
	
	@Override
	public void accept(Path p, OpenSongSong song) {
		var r = sheet.createRow(row++);
		newCell(r, 0, p.getParent().getFileName().toString());
		newCell(r, 1, song.getTitle());
		newCell(r, 2, song.getProvenance());
		newCell(r ,3, song.getSongApproval());
		newCell(r, 4, song.getAuthor());
		newCell(r, 5, song.getCopyright());
	}
	
	@Override
	public void close() throws IOException {
		try (var os = Files.newOutputStream(Path.of("RCC Song List.xlsx"))) {
			wb.write(os);	
		}
	}
	
	private static Cell newCell(Row r, int c, String s) {
		var ret = r.createCell(c, CellType.STRING);
		ret.setCellValue(s);
		return ret;
	}
}
