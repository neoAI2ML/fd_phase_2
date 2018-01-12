package tomketao.fd_phase_2.data.response;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

import tomketao.fd_phase_2.data.FeatureDetectObject;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "_index", "_type", "_id", "_score", "_source", "fields" })
public class RespHit extends FeatureDetectObject {
	private static final long serialVersionUID = -3579994062552290272L;

	@JsonProperty("_index")
	private String index;

	@JsonProperty("_type")
	private String type;

	@JsonProperty("_id")
	private String id;

	@JsonProperty("_score")
	private double score;

	@JsonProperty("_source")
	private Map<String, Object> source;

	@JsonProperty("fields")
	private Map<String, Object> fields;

	public Map<String, Object> getFields() {
		return fields;
	}

	public void setFields(Map<String, Object> fields) {
		this.fields = fields;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public Map<String, Object> getSource() {
		return source;
	}

	public void setSource(Map<String, Object> source) {
		this.source = source;
	}

	public Map<String, Object> getContent() {
		Map<String, Object> inputRec = getFields();
		if (inputRec == null || inputRec.isEmpty()) {
			inputRec = getSource();
		}

		return inputRec;
	}

	public Object getField(String fieldName) {
		if (getContent() != null && getContent().size() > 0) {
			return getContent().get(fieldName);
		}
		return null;
	}
	
	public String getFieldStringValue(String fieldName, String defaultValue) {
		String ret = defaultValue;
		Object valueObj = getField(fieldName);
		if(valueObj != null) {
			String valueObjString = valueObj.toString();
			if(StringUtils.isNotBlank(valueObjString)) {
				ret = valueObjString;
			}
		}
		return ret;
	}
}
