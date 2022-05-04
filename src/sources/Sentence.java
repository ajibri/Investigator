package sources;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Sentence {
	
	String timestamp;
	String text;
	String[] textWords;
			
	public Sentence(String timestamp, String text) {
		this.timestamp = timestamp;
		this.text = text;
		textWords = text.split("\\s");
	}
		
	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public int getSize() {
		return textWords.length;
	}
	
	public String getWord(int index) {
		int length = textWords.length;
		return (index > (length - 1)) ? "" : textWords[index];
	}
	
	public String getTextWithoutWord(int index) {
		int length = textWords.length;
		if(index > (length - 1)) {
			return "";
		}
		String textWithoutWord  = Stream.of(textWords)
				.filter(w -> (w != null && !w.isBlank() && !w.equalsIgnoreCase(textWords[index])))
					.collect(Collectors.joining(" "));
		return textWithoutWord;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sentence other = (Sentence) obj;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return timestamp + " " + text; 
	}
}
