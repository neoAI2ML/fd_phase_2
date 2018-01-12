package tomketao.fd_phase_2.data.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

import tomketao.fd_phase_2.data.FeatureDetectObject;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "must", "should", "minimum_should_match" })
public class BooleanQuery extends FeatureDetectObject {
	private static final long serialVersionUID = 3340535477887504569L;

	@JsonProperty("must")
	private List<QueryMatch> mustList;

	@JsonProperty("should")
	private List<QueryMatch> shouldList;

	@JsonProperty("minimum_should_match")
	private int minimum_should_match = 0;

	public List<QueryMatch> getMustList() {
		return mustList;
	}

	public void setMustList(List<QueryMatch> mustList) {
		this.mustList = mustList;
	}

	public List<QueryMatch> getShouldList() {
		return shouldList;
	}

	public void setShouldList(List<QueryMatch> shouldList) {
		this.shouldList = shouldList;
	}

	public int getMinimum_should_match() {
		return minimum_should_match;
	}

	public void setMinimum_should_match(int minimum_should_match) {
		this.minimum_should_match = minimum_should_match;
	}

	public void addMatch(String key, String value) {
		if(StringUtils.isBlank(StringUtils.stripEnd(value, "_"))) {
			return;
		}
		
		Map<String, Object> term = new HashMap<String, Object>();
		term.put(key, value);

		QueryMatch qm = new QueryMatch();
		qm.setMatch(term);

		mustList.add(qm);
	}

	public void addMatch(String key, List<String> values) {
		if(values == null) {
			return;
		}
		
		if(values.size() == 1) {
			addMatch(key, values.get(0));
			return;
		}
		
		for (String val : values) {
			Map<String, Object> term = new HashMap<String, Object>();
			term.put(key, val);

			QueryMatch qm = new QueryMatch();
			qm.setMatch(term);

			shouldList.add(qm);
		}
		minimum_should_match++;
	}
}
