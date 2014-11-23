import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;

import edu.stanford.nlp.io.IOUtils;


public class Main {
	public static void main(String[] args) throws IOException {
		// I/O operations
		String in = IOUtils.slurpFile("input.txt");		//input file 
		PrintWriter out = new PrintWriter("output.txt");	//output file
		out.println("<xml>");
		
		//Extract verb phrases from input text to output file
		VPExtractor vp = new VPExtractor(in, out);
		ArrayList<Object[]> taggedDoc = vp.extractVerbPhrases();
		
		//Generate sentiment score for text
		SentiScoreCalculator sc = new SentiScoreCalculator();
		for(Object[] sent: taggedDoc){
			String sentence = (String) sent[0]; 
	    	@SuppressWarnings("unchecked")
			ArrayList<String[]> phrase = (ArrayList<String[]>) sent[1];
			Double normalizedSum = sc.calculateNormalizedScore(phrase);
			DecimalFormat df = new DecimalFormat("####0.00");
	    	out.println("<verb_phrase><sentence>" + sentence + "</sentence><score>" + df.format(normalizedSum) + "</score><verb_phrase>");
	    }
		out.println("</xml>");
		out.close();
		
	}

}
