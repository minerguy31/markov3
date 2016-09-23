package markov3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.function.BiFunction;

public class Result {
	private HashMap<String, Integer> results = new HashMap<>();
	private int totaloccur = 0;
	
	public Result(String res) {
		results.put(res, 1);
	}
	
	public int getOccur() {
		return totaloccur;
	}

	public Result addResult(String res) {
		results.compute(res, new BiFunction<String, Integer, Integer>() {

			@Override
			public Integer apply(String arg0, Integer arg1) {
				return arg1 != null ? arg1 + 1 : 1;
			}}
		);
		totaloccur++;
		return this;
	}

	@Override
	public String toString() {
		return results.toString();
	}

	public String getRandom(Random rnd) {
		ArrayList<Entry<String, Integer>> entries = new ArrayList<>(results.entrySet());

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
