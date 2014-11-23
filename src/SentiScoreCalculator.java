import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SentiScoreCalculator {
	String pathToSWN = "SentiWordNet_3.0.0_20130122.txt";
	private Map<String, Double> dictionary;
	
	//Creates Dictionary
	public SentiScoreCalculator() throws IOException {
		dictionary = new HashMap<String, Double>();
		HashMap<String, HashMap<Integer, Double>> tempDictionary = new HashMap<String, HashMap<Integer, Double>>();
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(pathToSWN));
			int lineNumber = 0;
			String line;
			
			while ((line = reader.readLine()) != null) {
				lineNumber++;
				if (!line.trim().startsWith("#")) {
					String[] data = line.split("\t");
					String wordTypeMarker = data[0];
					if (data.length != 6) {
						throw new IllegalArgumentException("Incorrect tabulation format in file, line: " + lineNumber);	}
					Double synsetScore = Double.parseDouble(data[2]) - Double.parseDouble(data[3]);
					String[] synTermsSplit = data[4].split(" ");
					
					// Get synterm and synterm rank
					for (String synTermSplit : synTermsSplit) {
						String[] synTermAndRank = synTermSplit.split("#");
						String synTerm = synTermAndRank[0] + "#" + wordTypeMarker;
						int synTermRank = Integer.parseInt(synTermAndRank[1]);
						
						if (!tempDictionary.containsKey(synTerm)) {
							tempDictionary.put(synTerm, new HashMap<Integer, Double>());	}

						tempDictionary.get(synTerm).put(synTermRank,synsetScore);
					}}}
			
			for (Map.Entry<String, HashMap<Integer, Double>> entry : tempDictionary.entrySet()) {
				String word = entry.getKey();
				Map<Integer, Double> synSetScoreMap = entry.getValue();

				// Calculate weighted average for synsets according to their rank.
				double score = 0.0;
				double sum = 0.0;
				for (Map.Entry<Integer, Double> setScore : synSetScoreMap.entrySet()) {
					score += setScore.getValue() / (double) setScore.getKey();
					sum += 1.0 / (double) setScore.getKey();	}
				score /= sum;
				dictionary.put(word, score);
		}} catch (Exception e) {
				e.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();		}}}
	
	//Extracts from dictionary
	public Double extract(String word, String pos) {
		return dictionary.get(word + "#" + pos);	}
	
	//Calculates score and normalizes to sentence length between (-5,+5)
	public double calculateNormalizedScore(ArrayList<String[]> phrase){
		Double sentSum = 0.0;
		int scoredWords = 0;
    	for(String[] word: phrase){
    		Double score = extract(word[0], word[1]); 
    		if(score!=null) {
    			sentSum += score;
    			scoredWords++;		}}
    	double normalizedScore = (sentSum*5)/scoredWords;
    	//System.out.println(sentSum + " " + scoredWords);
    	return normalizedScore;
	}
	
}
	
