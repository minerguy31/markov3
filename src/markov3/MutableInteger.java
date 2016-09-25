package markov3;

public class MutableInteger {

	/**
	 * The value of this MutableInteger
	 */
	private int val;

	/**
	 * Make a new MutableInteger with an initial value
	 * @param val initial value
	 */
	public MutableInteger(int val) {
		this.val = val;
	}

	/**
	 * Get the value of this MutableInteger
	 * @return
	 */
	public int get() {
		return val;
	}

	/**
	 * Set the value of this MutableInteger
	 * @param val new value
	 */
	public void set(int val) {
		this.val = val;
	}
	
	/**
	 * Does what it says on the tin. Increments value of this MutableInteger.
	 */
	public void increment() {
		val++;
	}
	
	@Override
	public String toString() {
		return Integer.toString(val);
	}
}