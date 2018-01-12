package tomketao.fd_phase_2.util;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author tketao
 * 
 */
public class CommonUtils {
	/**
	 * Checks if provided date is before current date
	 * @param date Date in String format
	 * @param dateFormat Date format
	 * @return returns true is given date is before current date, otherwise returns false
	 */
	public static boolean isBeforeDate(String date, String dateFormat) {
		
		if(StringUtils.isBlank(date)) {
			return false;
		}
		
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		Date dateObj = new Date();
		try {
			dateObj = format.parse(date);
		} catch (ParseException e) {
			return false;
		}
		
		Date currentDate = new Date();
		if(dateObj.before(currentDate)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Check if date1 is before than date2.
	 * if equals it returns false.
	 * 
	 * @param date1 - Should be in given dateFormat param.
	 * @param date2 - Should be in given dateFormat param.
	 * @param dateFormat - format of date like YYYYMMdd.
	 * @return returns true if date1 is before date2, otherwise returns false
	 */
	public static boolean isBeforeDate(String date1, String date2, String dateFormat) {

		if (StringUtils.isBlank(date1) || StringUtils.isBlank(date2)) {
			return false;
		}

		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		Date dateObj1 = new Date();
		Date dateObj2 = new Date();
		try {
			dateObj1 = format.parse(date1);
			dateObj2 = format.parse(date2);
		} catch (ParseException e) {
			return false;
		}

		if (dateObj1.before(dateObj2)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if provided date is after current date
	 * @param date Date in String format
	 * @param dateFormat Date format
	 * @return returns true is given date is after current date, otherwise returns false
	 */
	public static boolean isAfterDate(String date, String dateFormat) {
		
		if(StringUtils.isBlank(date)) {
			return false;
		}
		
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		Date dateObj = new Date();
		try {
			dateObj = format.parse(date);
		} catch (ParseException e) {
			return false;
		}
		
		Date currentDate = new Date();
		if(dateObj.after(currentDate)) {
			return true;
		}
		return false;
	}
	
	public static String decodeISO_8859_1AndUtf_8Format(String requestString) {
		try {
			byte[] utf8 = new String(requestString.getBytes(), "ISO-8859-1").getBytes("UTF-8");
			return new String(utf8, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return requestString;
		}
	}
	
	public static int getSumOfFTCounts(Map<String, Integer> ftCounts) {
		int sum_of_ft_count = 0;
		for (Integer ft_count : ftCounts.values()) {
			sum_of_ft_count = sum_of_ft_count + ft_count;
		}
		
		return sum_of_ft_count;
	}
	
	public static float getSumOfFTAdjustedCounts(Map<String, Float> ftCounts) {
		Float sum_of_ft_count = (float) 0.0;
		for (Float ft_count : ftCounts.values()) {
			sum_of_ft_count = sum_of_ft_count + ft_count;
		}
		
		return sum_of_ft_count;
	}
	
	public static float keyFeatureProbality(String feature, Map<String, Integer> keyFeatureCount, Map<String, Integer> globalFeatureCount) {
		Map<String, Float> adjusted = adjustedFeatureCounts(keyFeatureCount, globalFeatureCount);
		return adjusted.get(feature) / getSumOfFTAdjustedCounts(adjusted);
	}
	
	public static Map<String, Float> keyProbalities(Map<String, Integer> keyFeatureCount, Map<String, Integer> globalFeatureCount) {
		Map<String, Float> adjusted = adjustedFeatureCounts(keyFeatureCount, globalFeatureCount);
		Map<String, Float> keyProbalities = new HashMap<String, Float>();
		
		for(String ft : globalFeatureCount.keySet()) {
			keyProbalities.put(ft, adjusted.get(ft) / getSumOfFTAdjustedCounts(adjusted));
		}
		return keyProbalities;
	}
	
	public static Map<String, Float> adjustedFeatureCounts(Map<String, Integer> keyFeatureCount, Map<String, Integer> globalFeatureCount) {
		Map<String, Float> adjusted = new HashMap<String, Float>();
		float averageGlobalFTCount = getSumOfFTCounts(globalFeatureCount) / globalFeatureCount.size();
		
		for(String ft : globalFeatureCount.keySet()) {
			Integer cur = keyFeatureCount.get(ft);
			float current = cur == null ? 0 : cur;
			for(String ft_again : globalFeatureCount.keySet()) {
				if(!StringUtils.equals(ft, ft_again)) {
					current = current * globalFeatureCount.get(ft_again) / averageGlobalFTCount;
				}
			}
			
			adjusted.put(ft, current);
		}
		
		return adjusted;
	}
}
