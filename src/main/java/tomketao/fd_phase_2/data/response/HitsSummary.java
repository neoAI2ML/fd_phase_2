package tomketao.fd_phase_2.data.response;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

import tomketao.fd_phase_2.data.FeatureDetectObject;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "total", "max_score", "hits" })
public class HitsSummary extends FeatureDetectObject {
	private static final long serialVersionUID = 7658159079162193892L;

	@JsonProperty("total")
	private String total;

	@JsonProperty("max_score")
	private String max_score;

	@JsonProperty("hits")
	private List<RespHit> hits;

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getMax_score() {
		return max_score;
	}

	public void setMax_score(String max_score) {
		this.max_score = max_score;
	}

	public List<RespHit> getHits() {
		return hits;
	}

	public void setHits(List<RespHit> hits) {
		this.hits = hits;
	}
}
