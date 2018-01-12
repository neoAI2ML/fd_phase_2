package tomketao.fd_phase_2.data.request;

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
@JsonPropertyOrder({ "doc" })
public class UpdateRequest extends FeatureDetectObject {
	private static final long serialVersionUID = -8183495272362554737L;
	private static final Logger logger = LoggerFactory
			.getLogger(UpdateRequest.class);

	@JsonProperty("doc")
	private Map<String, Object> doc;

	public Map<String, Object> getDoc() {
		return doc;
	}

	public void setDoc(Map<String, Object> doc) {
		this.doc = doc;
	}

	private UpdateRequest() {
	}

	public static UpdateRequest loadFromFile(String filePath) {
		ObjectMapper obm = new ObjectMapper();
		obm.setSerializationInclusion(Inclusion.NON_NULL);

		try {
			InputStream configInputStream = UpdateRequest.class
					.getClassLoader().getResourceAsStream(filePath);
			UpdateRequest req = obm.readValue(configInputStream,
					UpdateRequest.class);
			configInputStream.close();
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
