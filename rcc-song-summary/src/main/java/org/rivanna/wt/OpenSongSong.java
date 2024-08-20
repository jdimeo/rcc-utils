package org.rivanna.wt;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JsonRootName("song")
public class OpenSongSong {
	private String title, author, copyright, ccli, tempo, key, lyrics;
	
	@JsonProperty("user1")
	private String provenance;
	
	@JsonProperty("user2")
	private String songApproval;
}
