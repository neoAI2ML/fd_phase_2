package tomketao.fd_phase_2.data.response;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

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
@JsonPropertyOrder({ "took", "time_out", "_shards", "hits" , "_scroll_id"})
public class MatchResponse extends FeatureDetectObject {
	private static final long serialVersionUID = 109806908173306794L;
	private static final Logger logger = LoggerFactory
			.getLogger(MatchResponse.class);

	@JsonProperty("took")
	private String took;

	@JsonProperty("time_out")
	private String timeout;

	@JsonProperty("_shards")
	private Map<String, String> shards;

	@JsonProperty("hits")
	private HitsSummary hits;

	@JsonProperty("_scroll_id")
	private String scroll_id;

	public String getScroll_id() {
		return scroll_id;
	}

	public void setScroll_id(String scroll_id) {
		this.scroll_id = scroll_id;
	}
	
	public String getTook() {
		return took;
	}

	public void setTook(String took) {
		this.took = took;
	}

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	public Map<String, String> getShards() {
		return shards;
	}

	public void setShards(Map<String, String> shards) {
		this.shards = shards;
	}

	public HitsSummary getHits() {
		return hits;
	}

	public void setHits(HitsSummary hits) {
		this.hits = hits;
	}

	public static MatchResponse loadFromFile(String filePath) {
		ObjectMapper obm = new ObjectMapper();
		obm.setSerializationInclusion(Inclusion.NON_NULL);

		try {
			InputStream inputStream = MatchResponse.class.getClassLoader()
					.getResourceAsStream(filePath);
			MatchResponse req = obm.readValue(inputStream, MatchResponse.class);
			inputStream.close();
			return req;
		} catch (JsonParseException e) {
			logger.error(e.toString());
			e.printStackTrace();
		} catch (JsonMappingException e) {
			logger.error(e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.toString());
			e.printStackTrace();
		}

		return null;
	}
}
