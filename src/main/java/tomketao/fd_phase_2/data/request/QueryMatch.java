package tomketao.fd_phase_2.data.request;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

import tomketao.fd_phase_2.data.FeatureDetectObject;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "match" })
public class QueryMatch extends FeatureDetectObject {
	private static final long serialVersionUID = -7005117986841090874L;

	@JsonProperty("match")
	private Map<String, Object> match;

	public Map<String, Object> getMatch() {
		return match;
	}

	public void setMatch(Map<String, Object> match) {
		this.match = match;
	}
}
