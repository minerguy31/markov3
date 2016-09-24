package markov3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Dataset {
	
	// The pairings
	public HashMap<String, Result> pairset = new HashMap<>(3000000);
	
	// The sentence starters
	public HashMap<String, MutableInteger> starters = new HashMap<>(30000);

	public static transient final int DEFAULT_LOOKAHEAD = 5;
	
	private boolean debug = false;
	
	public long totaloccur = 0;
	
	public void setDebugging(boolean b) {
		debug = b;
	}
	
	/**
	 * Makes dataset with the default lookahead
	 */
	public Dataset() {
		this(DEFAULT_LOOKAHEAD);
	}
	
	
	/**
	 * Makes dataset with specified lookahead
	 */
	public Dataset(int lookahead) {
		this.lookahead = lookahead;
	}
	
	/**
	 * Makes dataset from file
	 * @param fis File to read from
	 * @param i Lookahead
	 */
	public Dataset(File fis, int i) {
		this(i);
		addData(fis);
	}
	
	/**
	 * Makes dataset from file
	 * @param fis File to read from
	 */
	public Dataset(File fis) {
		this(fis, DEFAULT_LOOKAHEAD);
	}
	
	/**
	 * Lookahead
	 */
	public int lookahead = DEFAULT_LOOKAHEAD;
	
	/**
	 * Add a pairing
	 * 
	 * For example, with a lookahead of 4, "hello" is properly parameterized as "hell" and "o"
	 * @param p The phrase before
	 * @param result The phrase that it results in
	 */
	public void addPair(String p, String result) {
		totaloccur++;
		pairset.compute(p, new BiFunction<String, Result, Result>() {

			@Override
			public Result apply(String t, Result u) {
				return u != null ? u.addResult(result) : new Result(result);
			}}
		);
	}
	
	/**
	 * Does what it says on the tin, adds data from a BufferedReader
	 * @param sc
	 */
	public void addData(BufferedReader sc) {
		Stream<String> lines = sc.lines().parallel();
		lines.forEach(new Consumer<String>(){

			@Override
			public void accept(String arg0) {
				addSentence(arg0);
				
			}}
		);
	}
	
	/**
	 * Breaks a sentence into N-grams (based on lookahead) and uses addPair correspondingly
	 * @param sentence The sentence to be parsed
	 */
	public void addSentence(String sentence) {
		if(sentence.length() < lookahead)
			return;
		addStarter(sentence.substring(0, lookahead));
		
		String[] grams = ngrams(sentence, lookahead);
		for(int i = 0; i < grams.length; i++) {
			addPair(grams[i], String.valueOf(sentence.charAt(i + lookahead)));
		}
	}
	
	/**
	 * Returns all but the last ngram in a given string
	 * @param str String to ngram-ify
	 * @param length Length of ngrams
	 * @return The array of ngrams
	 */
	private static String[] ngrams(String str, int length) {
	    char[] chars = str.toCharArray();
	    final int resultCount = chars.length - length;
	    String[] result = new String[resultCount];
	    for (int i = 0; i < resultCount; i++) {
	        result[i] = new String(chars, i, length);
	    }
	    return result;
	}
	
	/**
	 * Adds a starter, which is something that  was used to begin a sentence (pretty straightforward)
	 * @param p
	 */
	public void addStarter(String p) {
		MutableInteger mi = starters.get(p);
		if(mi == null)
			starters.put(p, new MutableInteger(1));
		else
			mi.increment();
	}
	
	/**
	 * Get a random sentence
	 * @param rnd Instance of Random to use
	 * @return the generated sentence
	 */
	public String getSentence(Random rnd) {
		StringBuilder ret;
		int iters = 0;
		do {
			StringBuilder s = new StringBuilder(getRandomStarter(rnd));
			ret = new StringBuilder(s);
			String next;
			while(true) {
				if(!pairset.containsKey(s.toString()))
					break;
				next = getRandom(s.toString(), rnd);
				ret.append(next);
				s.deleteCharAt(0);
				s.append(next);
				if(next.endsWith("."))
					break;
			}
			iters++;
			
			if(ret.length() > lookahead * 2)
				break;
			
			// System.out.println(iters);
		} while(ret.length() > lookahead + 1 && iters < 10);
		return ret.toString();
	}
	
	/** 
	 * Get a random pairing
	 * @param starter The starter to use
	 * @param rnd Instance of Random to use
	 * @return The next bit of the chain
	 */
	public String getRandom(String starter, Random rnd) {
		// TODO: Null check
		return pairset.get(starter)
				.getRandom(rnd);
	}
	
	@Override
	public String toString() {
		return " Current lookahead: " + lookahead + "\n Dataset unique/mappings: "
				+ pairset.size() + "/" + totaloccur
				+ " (Average saturation per pairing: " + 
				(double)(int)((((double)totaloccur) * 1000d) / (double)pairset.size()) / 1000d + ")\n "
				+ " Number of starters (unique): " + starters.size() + "\n";
	}
	
	/**
	 * Get a random starter
	 * @param rnd Instance of Random to use
	 * @return the starter
	 */
	public String getRandomStarter(Random rnd) {
		// TODO: OPTIMIZE OPTIMIZE OPTIMIZE OPTIMIZE OPTIMIZE OPTIMIZE OPTIMIZE
		ArrayList<Entry<String, MutableInteger>> entries = new ArrayList<>(starters.entrySet());

		ArrayList<Long> ints = new ArrayList<>();
		ArrayList<String> vals = new ArrayList<>();

		long cumulative = -1;

		for(Entry<String, MutableInteger> e : entries) {
			ints.add(cumulative += e.getValue().get());
			vals.add(e.getKey());
		}

		long l = Math.abs(rnd.nextLong()) % (cumulative == 0 ? 1 : cumulative);
		int index = Collections.binarySearch(ints, l);
		index = (index >= 0) ? index : -index-1;

		return vals.get(index);
	}

	
	/**
	 * Add data from file
	 * @param file ditto
	 */
	public void addData(File file) {
		try {
			addData(new BufferedReader(new FileReader(file)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
