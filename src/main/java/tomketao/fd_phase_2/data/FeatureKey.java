package tomketao.fd_phase_2.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

import tomketao.fd_phase_2.util.CommonUtils;
import tomketao.fd_phase_2.util.StaticConstants;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "keyString", "updateSeqNo", "keyHashCode", "sizeInword", "featureCounts", "sampleKeyCount" })
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
	
	@JsonProperty("sampleKeyCount")
	private int sampleKeyCount;
	
	public FeatureKey(int hashCode, String key, int seqNo, int wordCount, int keyCount) {
		setKeyString(key);
		setUpdateSeqNo(seqNo);
		setKeyHashCode(hashCode);
		setSizeInword(wordCount);
		setSampleKeyCount(keyCount);
		featureCounts = new HashMap<String, Integer>();
	}
	
	/*
	 * The method updates the feature counts for each FeatureKey.
	 */
	public void updateFeatures(List<String> features) {
		for(String feature : features) {
			if(features != null && featureCounts.containsKey(feature)) {
				featureCounts.put(feature, featureCounts.get(feature) + 1);
			} else {
				featureCounts.put(feature, 1);
			}
		}
	}

	public int getSampleKeyCount() {
		return sampleKeyCount;
	}

	public void setSampleKeyCount(int sampleKeyCount) {
		this.sampleKeyCount = sampleKeyCount;
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
		store_map.put(StaticConstants.KEY_CNT, getSampleKeyCount());
		store_map.put(StaticConstants.UPDATE_SEQ, getUpdateSeqNo());
		store_map.putAll(getFeatureCounts());
		
		return store_map;
	}
}
