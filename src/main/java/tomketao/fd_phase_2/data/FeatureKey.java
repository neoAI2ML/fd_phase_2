package tomketao.fd_phase_2.data;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

import tomketao.fd_phase_2.util.CommonUtils;
import tomketao.fd_phase_2.util.StaticConstants;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "keyString", "updateSeqNo", "keyHashCode", "sizeInword", "featureCounts", "missingFeatureCounts" })
public class FeatureKey extends FeatureDetectObject {
	private static final long serialVersionUID = 6569937637133679136L;
	
	@JsonProperty("keyString")
	private String keyString;
	
	@JsonProperty("updateSeqNo")
	private int updateSeqNo;
	
	@JsonProperty("keyHashCode")
	private int keyHashCode;
	
	@JsonProperty("sizeInword")
	private int sizeInword;
	
	@JsonProperty("featureCounts")
	private Map<String, Integer> featureCounts;

	@JsonProperty("missingFeatureCounts")
	private Map<String, Integer> missingFeatureCounts;
	
	public FeatureKey(int hashCode, String key, int seqNo, int wordCount) {
		setKeyString(key);
		setUpdateSeqNo(seqNo);
		setKeyHashCode(hashCode);
		setSizeInword(wordCount);
		featureCounts = new HashMap<String, Integer>();
		missingFeatureCounts = new HashMap<String, Integer>();
	}

	public Map<String, Integer> getMissingFeatureCounts() {
		return missingFeatureCounts;
	}

	public void setMissingFeatureCounts(Map<String, Integer> missingFeatureCounts) {
		this.missingFeatureCounts = missingFeatureCounts;
	}

	public Map<String, Integer> getFeatureCounts() {
		return featureCounts;
	}

	public void setFeatureCounts(Map<String, Integer> featureCounts) {
		this.featureCounts = featureCounts;
	}

	public String getKeyString() {
		return keyString;
	}

	public void setKeyString(String keyString) {
		this.keyString = keyString;
	}
	
	public int getUpdateSeqNo() {
		return updateSeqNo;
	}

	public void setUpdateSeqNo(int updateSeqNo) {
		this.updateSeqNo = updateSeqNo;
	}

	public int getKeyHashCode() {
		return keyHashCode;
	}

	public void setKeyHashCode(int keyHashCode) {
		this.keyHashCode = keyHashCode;
	}

	public int getSizeInword() {
		return sizeInword;
	}

	public void setSizeInword(int sizeInword) {
		this.sizeInword = sizeInword;
	}
	
	public int getSumOfFTCounts() {
		return CommonUtils.getSumOfFTCounts(featureCounts);
	}
	
	public Map<String, Object> mapForSave() {
		Map<String, Object> store_map = new HashMap<String, Object>();
		
		store_map.put(StaticConstants.KEY, getKeyString());
		store_map.put(StaticConstants.KEY_HASHCODE, getKeyHashCode());
		store_map.put(StaticConstants.KEY_SIZE, getSizeInword());
		store_map.put(StaticConstants.UPDATE_SEQ, getUpdateSeqNo());
		store_map.putAll(getFeatureCounts());
		
		return store_map;
	}
}
