import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;


public class VPExtractor {
	String in = null;
	PrintWriter out = null;
	
	VPExtractor(String in, PrintWriter out){
		this.in = in;
		this.out = out;
	}
	
	public ArrayList<Object[]> extractVerbPhrases() throws IOException {	
		ArrayList<Object[]> taggedDoc = new ArrayList<Object[]>();
		PrintWriter out2 = new PrintWriter("postags.txt");
		
		// creates a StanfordCoreNLP object
	    Properties props = new Properties();
	    props.put("annotators", "tokenize, ssplit, parse");
	    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	    Annotation document = new Annotation(in);
	    pipeline.annotate(document);	   
	    List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	    
	    //extracts verb phrases from each sentence
	    for(CoreMap sentence: sentences) {
	    	Object[] snt = new Object[2]; 
	    	ArrayList<String[]> taggedSentence = new ArrayList<String[]>();
	    	Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
	        //tree.pennPrint(System.out);
	        for (Tree subtree : tree) { 
	        	//Get verb phrase
	            if (subtree.label().value().equals("VP")) {
	            	//out.println(subtree);
	            	String sent = Sentence.listToString(subtree.yield());
	            	snt[0] = sent; 
	            	
	                //regex to get POS-word pairs: only verbs, nouns, adjectives and superlatives
	            	//considered to match sentiword type
	                String regex = "\\([J|V|N|R][A-Z]+\\s[a-z]+\\)";
	        		Pattern r = Pattern.compile(regex);
	        		Matcher m = r.matcher(subtree.toString());
	        		while (m.find()) {
	        		      String match = m.group().replaceAll("[()]", "");
	        		      String[] posWord = new String[2];
	        		      posWord[0]= match.split(" ")[1];
	        		      //Convert to pos type used by sentiword
	        		      posWord[1] = mapToSentiPOS(match.split(" ")[0]);
	        		      taggedSentence.add(posWord);
	        		}
	            	break;	}}
	        snt[1] = taggedSentence;
	        taggedDoc.add(snt);
	    }
	    
	    //Print to postags.txt
	    for(Object[] sent: taggedDoc){
	    	String sentence = (String) sent[0]; 
	    	out2.println(sentence);
	    	@SuppressWarnings("unchecked")
			ArrayList<String[]> phrase = (ArrayList<String[]>) sent[1];
	    	for(String[] word: phrase){
	    		out2.println(word[0] + " " + word[1]); }
	    	out2.println();
	    }
	    out2.close();
	    return taggedDoc;}
	
	    //map to SentiWordNetScore
		public String mapToSentiPOS(String pos){
			char pos_type = pos.charAt(0);
			String senti_pos = null;
			switch(pos_type){
				case 'J':
					senti_pos = "a";
					break;
				case 'V':
					senti_pos = "v";
					break;
				case 'N':
					senti_pos = "n";
					break;
				case 'R':
					senti_pos = "r";
					break;
				default:
					System.out.println("POS tag cannot be matched to senti-wordnet tag");
					}	    
			return senti_pos;
			}
	
}
