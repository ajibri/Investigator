package sources;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class Investigator {
	
	/**
	 * investigate method performs the main logic of the algorithm:
	 * (reads sentences from the input file, processes them and writes the results into the output file)
	 * @param inFileName
	 * @param outFileName
	 * @return groupCounter is the number of the similar sentences groups that were found in the input file.
	 */
	public int investigate(String inFileName, String outFileName) {
		Map<String, Sentence> mapOfSentences = new HashMap<>();
		int  longestSentenceLength  = readSentences(inFileName, mapOfSentences);
		Collection<Sentence> listOfSentences = mapOfSentences.values();
		if(listOfSentences == null || listOfSentences.isEmpty()) {
			System.out.println("Similar sentences were not found. " + inFileName + " is empty. Exit.");
			return 0;
		}
		File f = new File(outFileName);
		if(f.exists()) {
			f.delete();
		}
		int groupCounter = processSentences(mapOfSentences, longestSentenceLength, outFileName);
		return groupCounter;
	}
	
	
	/**
	 * readSentences method reads sentences from the input file and put them into mapOfSentences HashMap.
	 * @param mapOfSentences is HashMap, where the key is the time stamp of the sentence and 
	 * the value is the class Sentence
	 * @return longestSentenceLength is the length of the longest sentence in the input file.
	 */
	private int readSentences(String inFileName, Map<String, Sentence> mapOfSentences){
		int longestSentenceLength = 0;
		File f = new File(inFileName);
		try (BufferedReader br = new BufferedReader(new FileReader(f.getAbsolutePath()))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	Matcher m = Pattern.compile("(\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}:\\d{2}) ([^\\.]+)").matcher(line);
		    	if (m.find()) {
			    	String timestamp = m.group(1);
			        String text = m.group(2);
			        if(timestamp != null && !timestamp.isBlank() && text != null && !text.isBlank()) {
			        	Sentence sentence = new Sentence(timestamp.trim(), text.trim());
			        	if(sentence.getSize() > longestSentenceLength) {
							longestSentenceLength = sentence.getSize();
			        	}
			        	mapOfSentences.put(timestamp, sentence);
			        }
			    }
		    }
		} catch (IOException e) {
            System.err.println("Failed to open input file " + inFileName + e);
            System.exit(1);
        }
		return longestSentenceLength;
	}
	
	/**
	 * processSentences method processes sentences from mapOfSentences HashMap.
	 * Algorithm looks for group sentences that differ one from each other by last word,
	 * then the sentences that differ one from each other by (last-1) word, then (last-2) word and so on.
	 * At each iteration of external loop the word at the specific index is taken (changing word),
	 * groupMap is filled and results is written into the output file.
	 * At each iteration of internal loop, the word at the specific index (changing word) is removed from
	 * each sentence. The remained text is put into the groupMap as a key and a new Group class as a value,
	 * if the key still does not exist in the groupMap, otherwise the existed Group is retrieved.
	 * The new Group or existed one are updated by adding the time stamp of the sentence and the changing word.
	 * @param mapOfSentences is the map, of the sentences.
	 * @param longestSentenceLength is the length of the longest sentence in the input file.
	 * @param outFileName
	 * @return groupCounter is the number of similar sentences groups, found in the input file.
	 */
	private int processSentences(Map<String, Sentence> mapOfSentences, int longestSentenceLength, String outFileName) {
		int groupCounter = 0;
		//iterate word by word
		for(int i = (longestSentenceLength - 1) ;  i >= 0; i--) {
			Map<String, Group> groupMap = new HashMap<>();
			Collection<Sentence> listOfSentences = mapOfSentences.values();
			//iterate sentence by sentence
			for(Sentence sentence : listOfSentences) {
				String changingWord = sentence.getWord(i);
				String textWithoutWord = sentence.getTextWithoutWord(i);
				if(changingWord.isBlank() || textWithoutWord.isBlank()) {
					continue;
				}
				Group p = groupMap.get(textWithoutWord);
				if(p == null) {
					groupMap.put(textWithoutWord, p = new Group());
				}
				p.getTimestamps().add(sentence.getTimestamp());
				p.getChangingWords().add(changingWord);
			}
			int counter = writeResults(outFileName, mapOfSentences, groupMap);
			groupCounter = groupCounter + counter; 
		}
		return groupCounter;
	}
	
	/**
	 * writeResults method writes the groups of similar sentences into the output file.
	 * @param outFileName
	 * @param mapOfSentences is a HashMap, where the key is the time stamp of the sentence and the value is the class Sentence
	 * @param groupMap is the map with the groups of similar sentences. 
	 * The key of the groupMap is the part of text that is common for all sentences in this group.
	 * The value of the map is the class Group (the group of similar sentences), containing the list of their time stamps
	 * and the list of their changing words.
	 * @return counter is the number of similar sentences groups, that differ one from each other by (last-i) word.
	 */
	private int writeResults(String outFileName, Map<String, Sentence> mapOfSentences, Map<String, Group> groupMap) {
		int counter = 0;
		try (FileWriter f = new FileWriter(outFileName, true); 
				BufferedWriter b = new BufferedWriter(f); PrintWriter p = new PrintWriter(b);) {
			Set<String> groupKeys =  groupMap.keySet();
			for(String groupKey: groupKeys) {
				Group groupOfSimilarSentences = groupMap.get(groupKey);
				List<String> timeStamps = groupOfSimilarSentences.getTimestamps();
				Set<String> changingWords = groupOfSimilarSentences.getChangingWords();
				if(timeStamps.size() < 2 || changingWords.size() < 2) {
					continue;
				}
				timeStamps.forEach(t -> p.println(mapOfSentences.get(t)));
				String line = changingWords.stream()
						.filter(changingWord -> (changingWord != null && !changingWord.isBlank()))
							.collect(Collectors.joining(",", "The changing word was: ", ""));
				p.println(line);
				counter =  counter + 1;
			}
		} catch (IOException e) {
			System.err.println("Failed to open output file " + outFileName + e);
		    System.exit(1);
		}
		return counter;
	}
}
