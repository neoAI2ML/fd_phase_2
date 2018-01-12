package tomketao.fd_phase_2.data.match;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

import tomketao.fd_phase_2.data.FeatureDetectObject;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"query"})
public class MatchFunctionScore extends FeatureDetectObject {
	private static final long serialVersionUID = -7681574519000363163L;
	
	@JsonProperty("query")
	private MatchFunctionScoreQuery query;

	public MatchFunctionScoreQuery getQuery() {
		return query;
	}

	public void setQuery(MatchFunctionScoreQuery query) {
		this.query = query;
	}
}
