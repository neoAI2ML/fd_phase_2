package tomketao.fd_phase_2.data.request;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

import tomketao.fd_phase_2.data.FeatureDetectObject;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "bool" })
public class SearchQuery extends FeatureDetectObject {
	private static final long serialVersionUID = 8813985139034421899L;

	@JsonProperty("bool")
	private BooleanQuery bool;

	public BooleanQuery getBool() {
		return bool;
	}

	public void setBool(BooleanQuery bool) {
		this.bool = bool;
	}
}
