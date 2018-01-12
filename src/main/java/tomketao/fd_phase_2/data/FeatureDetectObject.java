package tomketao.fd_phase_2.data;

import java.io.Serializable;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jettison.json.JSONObject;

import tomketao.fd_phase_2.util.CommonUtils;

public class FeatureDetectObject implements Serializable {
	private static final long serialVersionUID = -1423059275993741072L;

	@Override
	public String toString() {
		try {
			return new JSONObject(convertIntoString()).toString(4); // pretty json
		} catch (Exception e) {
			return super.toString();
		}
	}

	private String convertIntoString() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(Inclusion.NON_NULL);
			return mapper.writeValueAsString(this);
		} catch (Exception e) {
			return super.toString();
		}
	}
	
    public String convertToString() {
        try {
            return CommonUtils.decodeISO_8859_1AndUtf_8Format(convertIntoString());
        } catch (Exception e) {
            return super.toString();
        }
    }
    
    public String convertToStringAsItis() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            return super.toString();
        }
    }
}
