package tomketao.fd_phase_2.data.request;

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

import tomketao.fd_phase_2.data.FeatureDetectObject;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "explain", "from", "size", "min_score", "query", "fields" })
public class SearchRequest extends FeatureDetectObject {
	private static final long serialVersionUID = -5688399685346629369L;

	@JsonProperty("explain")
	private String explain;

	@JsonProperty("min_score")
	private String min_score;

	@JsonProperty("from")
	private String from;

	@JsonProperty("size")
	private String size;

	@JsonProperty("query")
	private SearchQuery query;

	@JsonProperty("fields")
	private List<String> fields;

	public static SearchRequest loadFromFile(String filePath) {
		ObjectMapper obm = new ObjectMapper();
		obm.setSerializationInclusion(Inclusion.NON_NULL);

		try {
			InputStream configInputStream = SearchRequest.class
					.getClassLoader().getResourceAsStream(filePath);
			SearchRequest req = obm.readValue(configInputStream,
					SearchRequest.class);
			configInputStream.close();
			return req;
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public SearchQuery getQuery() {
		return query;
	}

	public void setQuery(SearchQuery query) {
		this.query = query;
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

	public String getMin_score() {
		return min_score;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getSize() {
		return size;
	}

	public void setExplain(String explain) {
		this.explain = explain;
	}

	public void setMin_score(String min_score) {
		this.min_score = min_score;
	}

	public void setSize(String size) {
		this.size = size;
	}
}
