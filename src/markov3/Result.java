package markov3;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Stream;

public class Result {
	private HashMap<String, MutableInteger> results = new HashMap<>();
	
	/**
	 * The number of times this result occurs. Mostly just shorthand for getting random.
	 */
	private int totaloccur = 0;
	
	/**
	 * Default constructor
	 */
	public Result() {}
	
	/**
	 * Make new result and then add a string to results. Identical as making an
	 * empty Result and then calling addResult. It just saves time and is 
	 * marginally faster.
	 * @param res
	 */
	public Result(String res) {
		results.put(res, new MutableInteger(1));
	}
	
	/**
	 * Get the number of times these results have occured
	 * @return
	 */
	public int getOccur() {
		return totaloccur;
	}

	/**
	 * Add a result to the map
	 * @param res Result to add
	 * @return
	 */
	public Result addResult(String res) {

		MutableInteger i = results.get(res);
		if(i == null)
			results.put(res, new MutableInteger(1));
		else
			i.increment();
		totaloccur++;
		return this;
	}

	@Override
	public String toString() {
		return results.toString();
	}

	/**
	 * Serialize this Result to a String
	 * @return
	 */
	
	public String serialize() {
		StringBuilder sb = new StringBuilder(String.valueOf(totaloccur));
		
		for(Entry<String, MutableInteger> e : results.entrySet()) {
			sb.append(Dataset.SEP_PAIRING);
			sb.append(e.getKey());
			sb.append(Dataset.SEP_PAIRING);
			sb.append(e.getValue());
		}
		
		return sb.toString();
	}
	
	/**
	 * Get a Result from a serialized String
	 * @param s
	 * @return
	 */
	public static Result unserialize(String s) {
		Result ret = new Result();
		
		String[] parts = s.split(Dataset.SEP_PAIRING);
		
		ret.totaloccur = Integer.parseInt(parts[0]);
		
		for(int i = 1; i < parts.length - 1; i += 2) {
			ret.results.put(parts[i], new MutableInteger(Integer.parseInt(parts[i + 1])));
		}
		
		return ret;
	}
	
	/**
	 * Get random result string
	 * @param rnd instance of Random to use
	 * @return a result string, randomly selected using rnd
	 */
	public String getRandom(Random rnd) {
		// TODO: OPTIMIZE OPTIMIZE OPTIMIZE OPTIMIZE OPTIMIZE OPTIMIZE OPTIMIZE
		ArrayList<Entry<String, MutableInteger>> entries = new ArrayList<>(results.entrySet());

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
}
