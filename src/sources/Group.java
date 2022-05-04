package sources;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Group {
	List<String> timestamps = new ArrayList<>();
	Set<String>  changingWords = new HashSet<>();
	
	public List<String> getTimestamps() {
		return timestamps;
	}
	public void setTimestamps(List<String> timestamps) {
		this.timestamps = timestamps;
	}
	public Set<String> getChangingWords() {
		return changingWords;
	}
	public void setChangingWords(Set<String> changingWords) {
		this.changingWords = changingWords;
	}
}
