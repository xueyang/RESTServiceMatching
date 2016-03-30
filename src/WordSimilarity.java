import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.dictionary.Dictionary;
import wordnet.SimilarityInfo;
import wordnet.SimilarityMeasure;


public class WordSimilarity {

	public static SimilarityMeasure initializeWordNet() throws Exception,JWNLException
	{
		//Initialize WordNet - this must be done before you try
		//and create a similarity measure otherwise nasty things
		//might happen!
		JWNL.initialize(new FileInputStream("src/wordnet/wordnet.xml"));
		
		//Create a map to hold the similarity config params
		Map<String,String> params = new HashMap<String,String>();
		
		//the simType parameter is the class name of the measure to use
		params.put("simType","wordnet.Lin");
		
		//this param should be the URL to an infocontent file (if required
		//by the similarity measure being loaded)
		params.put("infocontent","file:src/wordnet/ic-bnc-resnik-add1.dat");
		
		//this param should be the URL to a mapping file if the
		//user needs to make synset mappings
		params.put("mapping","file:src/wordnet/domain_independent.txt");
		
		//create the similarity measure
		SimilarityMeasure sim = SimilarityMeasure.newInstance(params);
		
		//Get two words from WordNet
		/*Dictionary dict = Dictionary.getInstance();		
		IndexWord word1 = dict.getIndexWord(POS.NOUN, wd1);
		IndexWord word2 = dict.getIndexWord(POS.NOUN,wd2);*/
		
		return sim;
	}
	
	public static void main(String[] args) throws Exception
	{
		System.out.println("\n"+wordSimilarity("forward#n", "forward#n"));
	}
	
	public static double wordSimilarity(String wd1,String wd2) throws Exception, JWNLException
	{   
		SimilarityMeasure sim = initializeWordNet();
		try
		{
	      SimilarityInfo simResult=sim.getSimilarity(wd1, wd2);
		  System.out.println(simResult.getSimilarity());
		  simResult = sim.getSimilarity("word", "word");
		  System.out.println(simResult.getSimilarity());
		  return 0.1;
		}catch(Exception e)
		{
			e.printStackTrace();
		return 0;
		}		
	}
	
	
}

