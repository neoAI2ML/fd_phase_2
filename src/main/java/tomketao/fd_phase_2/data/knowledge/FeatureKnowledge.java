package tomketao.fd_phase_2.data.knowledge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tomketao.fd_phase_2.connection.ESConnection;
import tomketao.fd_phase_2.data.FeatureKey;
import tomketao.fd_phase_2.data.TrainingSetting;
import tomketao.fd_phase_2.data.match.StringQueryRequest;
import tomketao.fd_phase_2.data.response.MatchResponse;
import tomketao.fd_phase_2.data.response.RespHit;
import tomketao.fd_phase_2.util.CommonUtils;
import tomketao.fd_phase_2.util.StaticConstants;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "currentSequence", "sampleCount", "currentFeatureCount" })
public class FeatureKnowledge extends HashMap<Integer, FeatureKey> {
	private static final long serialVersionUID = -6712633715281112680L;
	public static final Logger LOGGER = LoggerFactory.getLogger(FeatureKnowledge.class);

	@JsonProperty("currentSequence")
	private int currentSequence;
	
	@JsonProperty("sampleCount")
	private int sampleCount;

	@JsonProperty("currentFeatureCount")
	private Map<String, Integer> currentFeatureCount = new HashMap<String, Integer>();

	public int getSampleCount() {
		return sampleCount;
	}

	public void setSampleCount(int sampleCount) {
		this.sampleCount = sampleCount;
	}

	public int getCurrentSequence() {
		return currentSequence;
	}

	public void setCurrentSequence(int currentSequence) {
		this.currentSequence = currentSequence;
	}

	public Map<String, Integer> getCurrentFeatureCount() {
		return currentFeatureCount;
	}

	public void setCurrentFeatureCount(Map<String, Integer> currentFeatureCount) {
		this.currentFeatureCount = currentFeatureCount;
	}

	public boolean put_feature(List<String> features, String featureData, int sequence, TrainingSetting trainingSetting) {
		// update knowledge base global variables
		setCurrentSequence(sequence);
		setSampleCount(getSampleCount() + 1);
		for(String ft : features) {
			Integer featureCount = getCurrentFeatureCount().get(ft);
			if (featureCount == null) {
				getCurrentFeatureCount().put(ft, 1);
			} else {
				getCurrentFeatureCount().put(ft, featureCount + 1);
			}
		}

		// update knowledge base feature keys
		String featureDataNormalized = StringUtils.normalizeSpace(featureData).toLowerCase();
		String[] keyWordList = featureDataNormalized.split(StaticConstants.SPACE);
		boolean addKeyFlag = false;

		for (int i = 0; i < keyWordList.length; i++) {
			StringBuilder keyStr = new StringBuilder();
			for (int j = 0; j < trainingSetting.getKeySize() && i + j < keyWordList.length; j++) {
				keyStr.append(StaticConstants.SPACE);
				keyStr.append(wordNormalizer(keyWordList[i + j]));
				addKeyFlag = put_feature_key(keyStr.substring(1), features, sequence, j + 1);
			}
		}

		return addKeyFlag;
	}

	public Map<String, Float> feature_probalities(String featureData, TrainingSetting trainingSetting) {
		Map<String, Float> result = new HashMap<String, Float>();
		for (String ft : currentFeatureCount.keySet()) {
			result.put(ft, (float) 0);
		}

		// update knowledge base feature keys
		String featureDataNormalized = StringUtils.normalizeSpace(featureData).toLowerCase();
		String[] keyWordList = featureDataNormalized.split(StaticConstants.SPACE);

		int totalWords = 0;

		for (int i = 0; i < keyWordList.length; i++) {
			StringBuilder keyStr = new StringBuilder();
			for (int j = 0; j < trainingSetting.getKeySize() && i + j < keyWordList.length; j++) {
				keyStr.append(StaticConstants.SPACE);
				keyStr.append(wordNormalizer(keyWordList[i + j]));

				int hashCode = keyStr.substring(1).hashCode();
				totalWords = totalWords + j + 1;

				if (this.containsKey(hashCode)) {
					FeatureKey ft_key = this.get(hashCode);
					Map<String, Float> k_prob = CommonUtils.keyProbalities(ft_key.getFeatureCounts(),
							currentFeatureCount);

					for (String ft : result.keySet()) {
						Float prob = (float) (result.get(ft) + k_prob.get(ft) * ft_key.getSizeInword());
						result.put(ft, prob);
					}
				} else {
					for (String ft : result.keySet()) {
						Float prob = result.get(ft) + (float) (j + 1) / currentFeatureCount.size();
						result.put(ft, prob);
					}
				}
			}
		}

		for (String ft : result.keySet()) {
			result.put(ft, (float) (result.get(ft) / totalWords));
		}

		return result;
	}

	private boolean put_feature_key(String key, List<String> features, int sequence, int sizeInWords) {
		int hashCode = key.hashCode();
		boolean newKeyFlag = true;
		FeatureKey ft_key = this.get(hashCode);
		if (ft_key == null) {
			ft_key = new FeatureKey(hashCode, key, sequence, sizeInWords, 1);
			this.put(hashCode, ft_key);
			ft_key.updateFeatures(features);
		} else {
			newKeyFlag = false;
			ft_key.setUpdateSeqNo(sequence);
			ft_key.setSampleKeyCount(ft_key.getSampleKeyCount() + 1);
			ft_key.updateFeatures(features);
		}

		return newKeyFlag;
	}

	private String wordNormalizer(String word) {

		return word;
	}

	public void alignment(TrainingSetting trainingSetting) {
		remove_rare(trainingSetting);
		remove_key_wo_impact(trainingSetting);
	}

	private void remove_rare(TrainingSetting trainingSetting) {
		List<Integer> rareList = new ArrayList<Integer>();
		for (Integer item : this.keySet()) {
			Integer updateSeq = this.get(item).getUpdateSeqNo();
			if (updateSeq + trainingSetting.getValidSeqRange() < getCurrentSequence()) {
				if (this.get(item).getSumOfFTCounts() < trainingSetting.getRareLimit()) {
					rareList.add(item);
				}
			}
		}

		for (Integer rareItem : rareList) {
			this.remove(rareItem);
		}
	}

	private void remove_key_wo_impact(TrainingSetting trainingSetting) {
		List<Integer> listWOImpact = new ArrayList<Integer>();
		for (Integer keyItem : this.keySet()) {
			List<Float> probList = new ArrayList<Float>();
			for (String ft : getCurrentFeatureCount().keySet()) {
				probList.add(CommonUtils.keyFeatureProbality(ft, this.get(keyItem).getFeatureCounts(),
						getCurrentFeatureCount()));
			}

			if (maximumDifference(probList) < trainingSetting.getMinimumImpact()) {
				listWOImpact.add(keyItem);
			}
		}

		for (Integer rmKeyitem : listWOImpact) {
			this.remove(rmKeyitem);
		}
	}

	private float maximumDifference(List<Float> dList) {
		Collections.sort(dList);
		return dList.get(dList.size() - 1) - dList.get(0);
	}

	public void save(TrainingSetting trainingSetting) {
		ESConnection esMeta = new ESConnection(trainingSetting.getStoreMetaDataUrl());
		esMeta.indexing("knowledge", mapForSave());

		ESConnection esFeature = new ESConnection(trainingSetting.getStoreFeatureDataUrl());

		for (Integer key : this.keySet()) {
			esFeature.indexing(key.toString(), this.get(key).mapForSave());
		}
	}

	public void load(TrainingSetting trainingSetting) {
		ESConnection esMeta = new ESConnection(trainingSetting.getStoreMetaDataUrl());
		RespHit ret = esMeta.retrieve("knowledge");
		Map<String, Object> metaData = ret.getSource();
		for (String key : metaData.keySet()) {
			if (key.equals(StaticConstants.SAMPLE_CNT)) {
				setSampleCount((Integer) metaData.get(StaticConstants.SAMPLE_CNT));
			} else if (key.equals(StaticConstants.UPDATE_SEQ)) {
				setCurrentSequence((Integer) metaData.get(StaticConstants.UPDATE_SEQ));
			} else {
				currentFeatureCount.put(key, (Integer) metaData.get(key));
			}
		}

		ESConnection esFeature = new ESConnection(trainingSetting.getStoreFeatureDataUrl());

		StringQueryRequest stringQuery = StringQueryRequest.loadFromFile("all_query.json");
		String timeout = "1m";
		MatchResponse resp = esFeature.scrollStringQuery(stringQuery.convertToString(), timeout);
		String scroll_id = resp.getScroll_id();

		while (!resp.getHits().getHits().isEmpty()) {
			for (RespHit hit : resp.getHits().getHits()) {
				Map<String, Object> featureData = hit.getSource();
				FeatureKey ft_key = new FeatureKey((Integer) featureData.get(StaticConstants.KEY_HASHCODE),
						(String) featureData.get(StaticConstants.KEY),
						(Integer) featureData.get(StaticConstants.UPDATE_SEQ),
						(Integer) featureData.get(StaticConstants.KEY_SIZE),
						(Integer) featureData.get(StaticConstants.KEY_CNT));

				for (String key : featureData.keySet()) {
					if (key.equals(StaticConstants.UPDATE_SEQ) || key.equals(StaticConstants.KEY_HASHCODE)
							|| key.equals(StaticConstants.KEY) || key.equals(StaticConstants.KEY_SIZE)
							|| key.equals(StaticConstants.KEY_CNT)) {
					} else {
						ft_key.getFeatureCounts().put(key, (Integer) featureData.get(key));
					}
				}

				this.put(ft_key.getKeyHashCode(), ft_key);
			}
			resp = esFeature.scrollStringQueryNext(scroll_id, timeout);
		}
	}

	public Map<String, Object> mapForSave() {
		Map<String, Object> store_map = new HashMap<String, Object>();

		store_map.put(StaticConstants.UPDATE_SEQ, getCurrentSequence());
		store_map.put(StaticConstants.SAMPLE_CNT, getSampleCount());
		store_map.putAll(getCurrentFeatureCount());

		return store_map;
	}
}
