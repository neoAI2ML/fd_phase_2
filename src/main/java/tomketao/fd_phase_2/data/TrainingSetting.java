package tomketao.fd_phase_2.data;

import java.io.FileReader;
import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "validSeqRange", "knowledgeLimit", "rareLimit", "minimumImpact", "keySize", "inputDelimiter", "storeMetaDataUrl", "storeFeatureDataUrl" })
public class TrainingSetting  extends FeatureDetectObject {
	private static final long serialVersionUID = -1675848364528330441L;
	
	public int getValidSeqRange() {
		return validSeqRange;
	}
	public void setValidSeqRange(int validSeqRange) {
		this.validSeqRange = validSeqRange;
	}
	public int getKnowledgeLimit() {
		return knowledgeLimit;
	}
	public void setKnowledgeLimit(int knowledgeLimit) {
		this.knowledgeLimit = knowledgeLimit;
	}
	public int getRareLimit() {
		return rareLimit;
	}
	public void setRareLimit(int reaeLimit) {
		this.rareLimit = reaeLimit;
	}
	public float getMinimumImpact() {
		return minimumImpact;
	}
	public void setMinimumImpact(float minimumImpact) {
		this.minimumImpact = minimumImpact;
	}
	public int getKeySize() {
		return keySize;
	}
	public void setKeySize(int keySize) {
		this.keySize = keySize;
	}
	public String getInputDelimiter() {
		return inputDelimiter;
	}
	public void setInputDelimiter(String inputDelimiter) {
		this.inputDelimiter = inputDelimiter;
	}
	public String getStoreMetaDataUrl() {
		return storeMetaDataUrl;
	}
	public void setStoreMetaDataUrl(String storeMetaDataUrl) {
		this.storeMetaDataUrl = storeMetaDataUrl;
	}
	public String getStoreFeatureDataUrl() {
		return storeFeatureDataUrl;
	}
	public void setStoreFeatureDataUrl(String storeFeatureDataUrl) {
		this.storeFeatureDataUrl = storeFeatureDataUrl;
	}


	@JsonProperty("validSeqRange")
	private int validSeqRange;
	
	@JsonProperty("knowledgeLimit")
	private int knowledgeLimit;
	
	@JsonProperty("rareLimit")
	private int rareLimit;
	
	@JsonProperty("minimumImpact")
	private float minimumImpact;
	
	@JsonProperty("keySize")
	private int keySize;
	
	@JsonProperty("inputDelimiter")
	private String inputDelimiter;
	
	@JsonProperty("storeMetaDataUrl")
	private String storeMetaDataUrl;
	
	@JsonProperty("storeFeatureDataUrl")
	private String storeFeatureDataUrl;
	
	public static TrainingSetting loadFromFile(String filePath) {
		ObjectMapper obm = new ObjectMapper();
		obm.setSerializationInclusion(Inclusion.NON_NULL);

		try {
			FileReader fileReader = new FileReader(filePath);
			TrainingSetting req = obm.readValue(fileReader,
					TrainingSetting.class);
			fileReader.close();
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
