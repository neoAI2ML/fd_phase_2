package tomketao.fd_phase_2.data.tokenize;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConsecutiveTokenizer<T> implements Tokenizer<T> {

	private int minimum_size = 0;
	private int maximum_size = Integer.MAX_VALUE;
	
	public ConsecutiveTokenizer() {
	}
	
	public ConsecutiveTokenizer(int minimum_size, int maximum_size) {
		setMinimum_size(minimum_size);
		setMaximum_size(maximum_size);
	}
	
	/*
	 * (non-Javadoc)
	 * @see tomketao.featuredetector.data.tokenize.Tokenizer#tokenize(java.util.List)
	 */
	public Set<List<T>> tokenize(List<T> tList) {
		Set<List<T>> ret =  new HashSet<List<T>>();
		for (int i = 0; i < tList.size(); i++) {
			ArrayList<T> token = new ArrayList<T>();
			for (int j = 0; j < maximum_size && i + j < tList.size(); j++) {
				token.add(tList.get(i + j));
				if(token.size() < minimum_size) {
				} else {
					ret.add(token);
					token = new ArrayList<T>(token);
				}
			}
		}
		return ret;
	}
	public int getMinimum_size() {
		return minimum_size;
	}
	public void setMinimum_size(int minimum_size) {
		this.minimum_size = minimum_size;
	}
	public int getMaximum_size() {
		return maximum_size;
	}
	public void setMaximum_size(int maximum_size) {
		this.maximum_size = maximum_size;
	}
}
