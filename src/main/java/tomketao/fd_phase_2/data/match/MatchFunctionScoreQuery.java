package tomketao.fd_phase_2.data.match;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

import tomketao.fd_phase_2.data.FeatureDetectObject;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"query_string"})
public class MatchFunctionScoreQuery extends FeatureDetectObject {
	private static final long serialVersionUID = -3482303434999022260L;
	
	@JsonProperty("query_string")
	private QueryString query_string;
	
	public QueryString getQuery_string() {
		return query_string;
	}
	public void setQuery_string(QueryString query_string) {
		this.query_string = query_string;
	}
}
