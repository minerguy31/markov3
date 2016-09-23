package markov3;

public class MutableInteger {

	private int val;

	public MutableInteger(int val) {
		this.val = val;
	}

	public int get() {
		return val;
	}

	public void set(int val) {
		this.val = val;
	}
	
	public void increment() {
		val++;
	}
}