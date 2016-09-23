package markov3;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.function.BiFunction;

public class Dataset {
	public HashMap<String, Result> pairset = new HashMap<>(3000000);
	public HashMap<String, Integer> starters = new HashMap<>(30000);

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
	
	public Dataset(FileInputStream fis, int i) {
		this(i);
		addData(new Scanner(fis));
	}
	
	public Dataset(FileInputStream fis) {
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
	
	public void addData(Scanner sc) {
		long before = System.currentTimeMillis();
		long init = System.currentTimeMillis();
		while(sc.hasNext()) {
			String line = sc.nextLine();
			addSentence(line);
			if(debug && System.currentTimeMillis() - before > 1000) {
				// iters = 0;
				System.out.println("Pairings: " + pairset.size() + " - delta " + 
				((double)pairset.size()) * 1000d / (double)(System.currentTimeMillis() - init));
				before = System.currentTimeMillis();
				// prct = pairset.size();
			}
		}
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
		starters.computeIfPresent(p, new BiFunction<String, Integer, Integer>() {

			@Override
			public Integer apply(String arg0, Integer arg1) {
				return arg1 + 1;
			}}
		);
		starters.putIfAbsent(p, 1);
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
		ArrayList<Entry<String, Integer>> entries = new ArrayList<>(starters.entrySet());

		ArrayList<Long> ints = new ArrayList<>();
		ArrayList<String> vals = new ArrayList<>();

		long cumulative = -1;

		for(Entry<String, Integer> e : entries) {
			ints.add(cumulative += e.getValue());
			vals.add(e.getKey());
		}

		long l = Math.abs(rnd.nextLong()) % (cumulative == 0 ? 1 : cumulative);
		int index = Collections.binarySearch(ints, l);
		index = (index >= 0) ? index : -index-1;

		return vals.get(index);
	}
}
