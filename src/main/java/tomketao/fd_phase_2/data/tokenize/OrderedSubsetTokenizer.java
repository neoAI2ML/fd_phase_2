package tomketao.fd_phase_2.data.tokenize;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrderedSubsetTokenizer<T> implements Tokenizer<T> {

	private int minimum_size = 0;
	private int maximum_size = Integer.MAX_VALUE;
	
	public OrderedSubsetTokenizer() {
	}
	
	public OrderedSubsetTokenizer(int minimum_size, int maximum_size) {
		setMinimum_size(minimum_size);
		setMaximum_size(maximum_size);
	}
	
	/*
	 * (non-Javadoc)
	 * @see tomketao.featuredetector.data.tokenize.Tokenizer#tokenize(java.util.List)
	 */
	public Set<List<T>> tokenize(List<T> tList) {
		Set<List<T>> baseSet =  new HashSet<List<T>>();
		Set<List<T>> ret =  new HashSet<List<T>>();
		
		baseSet.add(new ArrayList<T>());
	    for (T element : tList) {
	    	// tempClone still points to the same set of List as baseSet does even they are two different Objects
	        Set<List<T>> tempClone = new HashSet<List<T>>(baseSet);

	        for (List<T> subset : tempClone) {
	        	// extended is a different Object of subset
	            List<T> extended = new ArrayList<T>(subset);
	            extended.add(element);
	            baseSet.add(extended);
	            if(extended.size() < getMinimum_size() || extended.size() > getMaximum_size()) {
	            } else {
	            	ret.add(extended);
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
