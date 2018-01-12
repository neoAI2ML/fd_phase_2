package tomketao.fd_phase_2.data.match;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tomketao.fd_phase_2.data.FeatureDetectObject;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "explain", "size", "from", "min_score", "query", "fields" })
public class StringQueryRequest extends FeatureDetectObject {
	private static final long serialVersionUID = -2210672071719579883L;
	private static final Logger logger = LoggerFactory
			.getLogger(StringQueryRequest.class);

	@JsonProperty("explain")
	private String explain;

	@JsonProperty("min_score")
	private String min_score;

	@JsonProperty("size")
	private String size;

	@JsonProperty("from")
	private String from;

	@JsonProperty("query")
	private MatchFunctionScoreQuery query;

	@JsonProperty("fields")
	private List<String> fields;

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public List<String> getFields() {
		return fields;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}

	public String getExplain() {
		return explain;
	}

	public String getSize() {
		return size;
	}

	public String getMin_score() {
		return min_score;
	}

	public void setMin_score(String min_score) {
		this.min_score = min_score;
	}

	public MatchFunctionScoreQuery getQuery() {
		return query;
	}

	public void setExplain(String explain) {
		this.explain = explain;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public void setQuery(MatchFunctionScoreQuery query) {
		this.query = query;
	}

	public static StringQueryRequest loadFromFile(String filePath) {
		ObjectMapper obm = new ObjectMapper();
		obm.setSerializationInclusion(Inclusion.NON_NULL);

		try {
			InputStream inputStream = StringQueryRequest.class.getClassLoader()
					.getResourceAsStream(filePath);
			StringQueryRequest req = obm.readValue(inputStream,
					StringQueryRequest.class);
			inputStream.close();
			return req;
		} catch (JsonParseException e) {
			logger.debug(e.getMessage());
			e.printStackTrace();
		} catch (JsonMappingException e) {
			logger.debug(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.debug(e.getMessage());
			e.printStackTrace();
		}

		return null;
	}
}
