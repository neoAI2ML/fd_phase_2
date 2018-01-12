package tomketao.fd_phase_2.data.tokenize;

import java.util.List;
import java.util.Set;

public interface Tokenizer<T> {
	Set<List<T>> tokenize(List<T> tList);
}
