package tomketao.fd_phase_2.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.queryparser.classic.QueryParser;

import tomketao.fd_phase_2.data.match.MatchFunctionScoreQuery;
import tomketao.fd_phase_2.data.match.QueryString;
import tomketao.fd_phase_2.data.match.StringQueryRequest;
import tomketao.fd_phase_2.data.request.BooleanQuery;
import tomketao.fd_phase_2.data.request.IndexRequest;
import tomketao.fd_phase_2.data.request.SearchRequest;
import tomketao.fd_phase_2.data.request.UpdateRequest;

public class RequestBuilder {
	private static SearchRequest searchTemplate = SearchRequest
			.loadFromFile("search.json");
	private static UpdateRequest updateTemplate = UpdateRequest
			.loadFromFile("update.json");
	private static HashSet<String> reservedWords = new HashSet<String>(
			Arrays.asList(new String[] { "AND", "OR", "NOT" }));
	private static final char[] reservedChars = new char[] { '+', '-', '&',
			'|', '!', '(', ')', '{', '}', '[', ']', '^', '"', '~', '*', '?',
			':', '\\', '/' };
	private static final char[] dollar = new char[] { '$' };

	static public SearchRequest buildSearchRequest(
			Map<String, List<String>> request) {
		if (request == null) {
			return null;
		}

		SearchRequest temp = (SearchRequest) DeepClone
				.deepClone(searchTemplate);

		BooleanQuery mList = temp.getQuery().getBool();
		for (String key : request.keySet()) {
			List<String> val = request.get(key);
			if (val != null && val.size() > 0) {
				mList.addMatch(key, val);
			}
		}

		return temp;
	}

	static public SearchRequest buildSearchRequest(
			Map<String, List<String>> request, double mini_score) {
		SearchRequest temp = buildSearchRequest(request);
		temp.setMin_score(Double.toString(mini_score));

		return temp;
	}

	static public SearchRequest buildSearchRequest(
			Map<String, List<String>> request, int from, int size) {
		// check null or empty
		if (isEmpty(request)) {
			return null;
		}

		SearchRequest temp = buildSearchRequest(request);
		temp.setFrom(Integer.toString(from));
		temp.setSize(Integer.toString(size));

		return temp;
	}

	private static boolean isEmpty(Map<String, List<String>> request) {
		boolean flag = true;

		for (List<String> fieldValues : request.values()) {
			if (fieldValues == null || fieldValues.size() == 0) {
				continue;
			}
			for (String valueStr : fieldValues) {
				if (StringUtils.isNotBlank(valueStr)) {
					return false;
				}
			}
		}
		return flag;
	}

	static public SearchRequest buildSearchRequest(
			Map<String, List<String>> request, int from, int size,
			double mini_score) {
		SearchRequest temp = buildSearchRequest(request, from, size);
		if (temp != null) {
			temp.setMin_score(Double.toString(mini_score));
		}

		return temp;
	}

	static public SearchRequest buildSearchRequest(
			Map<String, List<String>> request, List<String> fields) {
		if (request == null) {
			return null;
		}

		SearchRequest temp = buildSearchRequest(request);

		if (fields != null && fields.size() > 0) {
			temp.setFields(fields);
		}

		return temp;
	}

	public static StringQueryRequest buildStringQueryRequestWithoutFuzzy(
			Map<String, String> request) {

		StringQueryRequest rqst = new StringQueryRequest();
		StringBuilder strb = new StringBuilder();
		for (String key : request.keySet()) {
			String nfieldValue = StringUtils.strip(request.get(key), " ,;.@-");
			String[] words = nfieldValue.split("[ ,;@-]");
			for (String word : words) {
				word = StringUtils.strip(word, " ,;.@-");
				if (reservedWords.contains(word)) {
					word = "'" + word + "'";
				} else if (StringUtils.containsAny(word, reservedChars)) {
					word = QueryParser.escape(QueryParser.escape(word));
				} else {
				}

				if (StringUtils.containsAny(word, dollar)) {
					word = word.replaceAll("\\$", "\\\\\\$");
				}

				if (StringUtils.isNotBlank(word)) {
					strb.append(" AND ");
					strb.append(key);
					strb.append(":");
					strb.append(word);
				} else {
					strb.append(" AND !");
					strb.append(key);
					strb.append(":[* TO *]");
				}
			}
		}
		QueryString qryStr = new QueryString();
		qryStr.setQuery(strb.substring(5));
		MatchFunctionScoreQuery mfsq = new MatchFunctionScoreQuery();
		mfsq.setQuery_string(qryStr);
		rqst.setQuery(mfsq);
		rqst.setSize("100");
		return rqst;
	}

	public static StringQueryRequest buildStringQueryRequestWithoutFuzzy_NAField(
			Map<String, String> request) {
		StringQueryRequest rqst = new StringQueryRequest();
		StringBuilder strb = new StringBuilder();
		for (String key : request.keySet()) {
			String nfieldValue = StringUtils.strip(request.get(key), " ,;.@-");
			String[] words = nfieldValue.split("[ ,;@-]");
			for (String word : words) {
				word = StringUtils.strip(word, " ,;.@-");
				if (reservedWords.contains(word)) {
					word = "'" + word + "'";
				} else if (StringUtils.containsAny(word, reservedChars)) {
					word = QueryParser.escape(QueryParser.escape(word));
				} else {
				}

				if (StringUtils.containsAny(word, dollar)) {
					word = word.replaceAll("\\$", "\\\\\\$");
				}

				if (StringUtils.isNotBlank(word)) {
					strb.append(" AND ");
					strb.append(key);
					strb.append(":");
					strb.append(word);
				} else {
					strb.append(" AND ");
					strb.append(key);
					strb.append(":\"\"");
				}
			}
		}
		QueryString qryStr = new QueryString();
		qryStr.setQuery(strb.substring(5));
		MatchFunctionScoreQuery mfsq = new MatchFunctionScoreQuery();
		mfsq.setQuery_string(qryStr);
		rqst.setQuery(mfsq);
		rqst.setSize("100");
		return rqst;
	}

	static public SearchRequest buildSearchRequest(
			Map<String, List<String>> request, List<String> fields,
			double mini_score) {
		SearchRequest temp = buildSearchRequest(request, fields);
		temp.setMin_score(Double.toString(mini_score));

		return temp;
	}

	static public SearchRequest buildSearchRequest(
			Map<String, List<String>> request, List<String> fields, int from,
			int size) {
		SearchRequest temp = buildSearchRequest(request, fields);
		temp.setFrom(Integer.toString(from));
		temp.setSize(Integer.toString(size));

		return temp;
	}

	static public SearchRequest buildSearchRequest(
			Map<String, List<String>> request, List<String> fields, int from,
			int size, double mini_score) {
		SearchRequest temp = buildSearchRequest(request, fields, from, size);
		temp.setMin_score(Double.toString(mini_score));

		return temp;
	}

	static public UpdateRequest buildUpdateRequest(Map<String, Object> request) {
		UpdateRequest temp = (UpdateRequest) DeepClone
				.deepClone(updateTemplate);
		temp.setDoc(request);

		return temp;
	}

	static public IndexRequest buildIndexRequest(Map<String, Object> request) {
		IndexRequest temp = new IndexRequest();
		temp.putAll(request);

		return temp;
	}

	static public Map<String, String> mapStrObjToStrStr(Map<String, Object> hit) {
		Map<String, String> inputRec = new HashMap<String, String>();
		for (String fld : hit.keySet()) {
			inputRec.put(fld, hit.get(fld).toString());
		}

		return inputRec;
	}

	static public Map<String, Object> mapStrStrToStrObj(Map<String, String> hit) {
		Map<String, Object> inputRec = new HashMap<String, Object>();
		for (String fld : hit.keySet()) {
			inputRec.put(fld, hit.get(fld).toString());
		}

		return inputRec;
	}

	static public Map<String, List<String>> recordToRequest(
			Map<String, Object> rec) {
		if (rec == null || rec.size() == 0) {
			return null;
		}

		Map<String, List<String>> inputR = new HashMap<String, List<String>>();
		for (String key : rec.keySet()) {
			List<String> val = new ArrayList<String>();
			Object recObj = rec.get(key);
			if (recObj == null) {
				continue;
			}
			val.add(recObj.toString());
			inputR.put(key, val);
		}

		return inputR;
	}

	static public String convertMapToString(Map<String, Object> doc) {
		StringBuilder str = new StringBuilder();
		for (String key : doc.keySet()) {
			str.append(key);
			str.append(": ");
			Object val = doc.get(key);
			if (val != null) {
				str.append(val.toString());
			} else {
				str.append("null");
			}
			str.append("|");
		}

		return str.toString();
	}

	static public String convertMapToCSVString(Map<String, Object> doc,
			String[] header, String delimmiter) {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < header.length; i++) {
			Object fiedValue = doc.get(header[i]);
			str.append(delimmiter);
			if (fiedValue == null || StringUtils.isBlank(fiedValue.toString())) {

			} else {
				str.append(fiedValue.toString());
			}
		}

		return str.substring(delimmiter.length());
	}
}
