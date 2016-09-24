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
	public HashMap<String, Result> pairset = new HashMap<>(3000000);
	public HashMap<String, MutableInteger> starters = new HashMap<>(30000);

	public static transient final int DEFAULT_LOOKAHEAD = 5;
	
	private boolean debug = false;
	
	public long totaloccur = 0;
	
	public void setDebugging(boolean b) {
		debug = b;
	}
	
	public Dataset() {
		this(DEFAULT_LOOKAHEAD);
	}
	
	public Dataset(int lookahead) {
		this.lookahead = lookahead;
	}
	
	public Dataset(File fis, int i) {
		this(i);
		addData(fis);
	}
	
	public Dataset(File fis) {
		this(fis, DEFAULT_LOOKAHEAD);
	}
	
	public int lookahead = 5;
	
	public void addPair(String p, String result) {
		totaloccur++;
		pairset.compute(p, new BiFunction<String, Result, Result>() {

			@Override
			public Result apply(String t, Result u) {
				return u != null ? u.addResult(result) : new Result(result);
			}}
		);
	}
	
	public void addData(BufferedReader sc) {
		Stream<String> lines = sc.lines().parallel();
		lines.forEach(new Consumer<String>(){

			@Override
			public void accept(String arg0) {
				addSentence(arg0);
				
			}}
		);
	}
	
	public void addSentence(String sentence) {
		if(sentence.length() < lookahead)
			return;
		addStarter(sentence.substring(0, lookahead));
		for(int i = 0; i < sentence.length() - lookahead; i++) {
			addPair(sentence.substring(i, i + lookahead), "" + sentence.charAt(i + lookahead));
		}
	}

	public void addStarter(String p) {
		MutableInteger mi = starters.get(p);
		if(mi == null)
			starters.put(p, new MutableInteger(1));
	}
	
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
	
	public String getRandom(String starter, Random rnd) {
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
	
	public String getRandomStarter(Random rnd) {
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

	public void addData(File file) {
		try {
			addData(new BufferedReader(new FileReader(file)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
