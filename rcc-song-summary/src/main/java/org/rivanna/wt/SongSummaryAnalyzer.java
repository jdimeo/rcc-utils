package org.rivanna.wt;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.tuple.Pair;
import org.rivanna.wt.SongStats.Leader;

import com.elderresearch.commons.lang.Utilities;

public class SongSummaryAnalyzer {
	private static final int N_TOP_SONGS = 6;
	
	public static void main(String[] args) throws IOException {
		int year = NumberUtils.toInt(Utilities.get(args, 1));
		System.out.println(year > 0? "ONLY YEAR " + year : "ALL TIME");
		
		var stats = SongStats.parse(Path.of(Utilities.get(args, 0)), year);
		
		System.out.println("Leader\tSets\tSongs/Set\tUnique Songs\tTotal Songs\tUnique/Total\tSongs done once");
		
		MutableInt leaderIdx = new MutableInt();
		String[][] topSongs = new String[N_TOP_SONGS + 1][stats.getLeaders().size()];
		for (Entry<String, Leader> e : stats.getLeaders().entrySet()) {
			if (e.getValue().getTotal() < 1) { continue; }
			
			int unq = e.getValue().getSongs().size();
			
			MutableInt once = new MutableInt();
			List<Pair<Integer, String>> sortedSongs = new ArrayList<>();
			e.getValue().getSongs().entrySet().forEach(e2 -> {
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
				.append(e.getValue().getSets()).append("\t")
				.append(e.getValue().getTotal() / (float) e.getValue().getSets()).append("\t")
				.append(unq).append("\t")
				.append(e.getValue().getTotal()).append("\t")
				.append(unq * 100.0f / e.getValue().getTotal()).append("\t")
				.append(once.intValue()));
			leaderIdx.increment();
		}
		
		System.out.println("Typical Set");
		for (String[] arr : topSongs) {
			System.out.println(Arrays.stream(arr).filter(Objects::nonNull).collect(Collectors.joining("\t")));
		}
		
		System.out.println("Top Songs");
		stats.getSongCount().entrySet().stream()
			.sorted(Comparator.comparing(e -> -1 * e.getValue()))
			.filter(e -> e.getValue() > 1)
			.forEachOrdered(e -> System.out.format("%s\t%d%n", e.getKey(), e.getValue()));
	}
}
