package tomketao.fd_phase_2.data.request;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jettison.json.JSONObject;

import tomketao.fd_phase_2.util.CommonUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IndexRequest extends HashMap<String, Object> implements
		Serializable {
	private static final long serialVersionUID = -5532930193655165449L;

	@Override
	public String toString() {
		try {
			return new JSONObject(convertInToString()).toString(4); // pretty json
		} catch (Exception e) {
			return super.toString();
		}
	}

	private String convertInToString() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(this);
		} catch (Exception e) {
			return super.toString();
		}
	}
	
	public String convertToString() {
		try {
			return CommonUtils.decodeISO_8859_1AndUtf_8Format(convertInToString());
		} catch (Exception e) {
			return super.toString();
		}
	}

	public static IndexRequest loadFromFile(String filePath) {
		ObjectMapper obm = new ObjectMapper();
		obm.setSerializationInclusion(Inclusion.NON_NULL);

		try {
			InputStream inputStream = IndexRequest.class.getClassLoader()
					.getResourceAsStream(filePath);
			IndexRequest req = obm.readValue(inputStream, IndexRequest.class);
			inputStream.close();
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
}
