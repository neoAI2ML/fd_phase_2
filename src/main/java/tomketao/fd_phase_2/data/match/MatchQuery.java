package tomketao.fd_phase_2.data.match;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

import tomketao.fd_phase_2.data.FeatureDetectObject;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"function_score"})
public class MatchQuery extends FeatureDetectObject {
	private static final long serialVersionUID = -7944773610973053886L;
	
	@JsonProperty("function_score")
	private MatchFunctionScore function_score;
	
	public MatchFunctionScore getFunction_score() {
		return function_score;
	}
	public void setFunction_score(MatchFunctionScore function_score) {
		this.function_score = function_score;
	}
}
