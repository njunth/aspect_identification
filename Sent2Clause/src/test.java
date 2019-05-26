import edu.stanford.nlp.ling.CoreLabel;

import edu.stanford.nlp.ling.*;

import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.semgraph.*;
import edu.stanford.nlp.trees.*;

import java.io.StringReader;
import java.util.*;

public class test {

	public static String s1 = new String("We enjoyed ourselves thoroughly and will be going back for the desserts ....");
	public static String s2 = new String("Don't go alone---even two people isn't enough for the whole experience, with p"
									+ "ickles and a selection of meats and seafoods.");
	public static String s3 = new String("I stumbled upon this second floor walk-up two Fridays ago when I was with two "
									+ "friends in town from L.A. Being serious sushi lovers, "
									+ "we sat at the sushi bar to be closer to the action.");
	public static String s4 = new String("--Eat Club is a roving group of NYC gluttons");
	public static String s5 = new String("Maybe tomorrow ;-)");
	public static String s6 = new String("Get the soup and a nosh (pastrami sandwich) for $8 and you're golden.");
	public static String s7 = new String("We've lived in the area for more than 8 years.");
	public static String s8 = new String("I have to say that if this what makes it easier to get a saet a lunch- I dont mind.");


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");  
	    Tokenizer<CoreLabel> tok = tokenizerFactory.getTokenizer(new StringReader(s7));
	    System.out.println("Example: token");
	    for(int i=0;tok.hasNext();i++)
	    	System.out.println(tok.next());
	    System.out.println();
	}

}
