package markov3;

import gnu.trove.map.hash.TIntIntHashMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

public class Result {
	private TIntIntHashMap results = new TIntIntHashMap();
	
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
	public Result(int res) {
		results.put(res, 1);
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
	public Result addResult(int res) {

		if(results.contains(res))
			results.put(res, 1);
		else
			results.adjustValue(res, 1);
		totaloccur++;
		return this;
	}

	@Override
	public String toString() {
		return results.toString();
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
