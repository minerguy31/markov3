package markov3;

import java.util.ArrayList;

public class StringIDRegister {
	private ArrayList<String> registered = new ArrayList<>();
	
	public int register(String s) {
		registered.add(s);
		return registered.size() - 1;
	}
	
	public String getForID(int i) {
		return registered.get(i);
	}
}
