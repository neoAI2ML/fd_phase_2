package tomketao.fd_phase_2.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tomketao.fd_phase_2.data.match.MatchRequest;
import tomketao.fd_phase_2.data.match.StringQueryRequest;
import tomketao.fd_phase_2.data.request.SearchRequest;
import tomketao.fd_phase_2.data.response.MatchResponse;
import tomketao.fd_phase_2.data.response.RespHit;
import tomketao.fd_phase_2.util.CommonUtils;
import tomketao.fd_phase_2.util.RequestBuilder;
import tomketao.fd_phase_2.util.httpclientpool.FDHttpClientConnectionPool;
import tomketao.fd_phase_2.util.httpclientpool.FDHttpClientConnectionPoolImpl;

public class ESConnection {
	private static final Logger logger = LoggerFactory
			.getLogger(ESConnection.class);
	private String server;
	private String storeUrl;
	//private String scrollTimeout;
	private final int RETRY_COUNT = 3;
	public static final String MAPPING_NODE = "properties";

	private final FDHttpClientConnectionPool pool = new FDHttpClientConnectionPoolImpl(20, 10);

	public ESConnection(String storeUrl) {
		setStoreUrl(storeUrl);
		int indexOfFirstSlash = getStoreUrl().indexOf('/', 8);
		this.server = getStoreUrl().substring(0, indexOfFirstSlash);
	}

	public String getStoreUrl() {
		return storeUrl;
	}

	public void setStoreUrl(String storeUrl) {
		this.storeUrl = storeUrl;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	private String searchUrl() {
		return getStoreUrl() + "/_search";
	}
	
	private String scrollUrl() {
		return getServer() + "/_search/scroll";
	}
	
	public MatchResponse scrollMatchRequestNext(MatchResponse previousResp, String timeout, String sizeStr) {
		if(previousResp != null && StringUtils.isNoneBlank(previousResp.getScroll_id())) {
			String scrollId = previousResp.getScroll_id();
			HttpResponse response = null;
			MatchResponse matchResponse = null;
			String output;
			StringBuilder outputBuilder = new StringBuilder();
			ObjectMapper obm = new ObjectMapper();
			obm.setSerializationInclusion(Inclusion.NON_NULL);

			try {
				HttpPost postRequest = new HttpPost(scrollUrl());
				postRequest.setHeader("Content-Type", "application/json");
				postRequest.setHeader("scroll", timeout);
				postRequest.setHeader("size", sizeStr);
				postRequest.setHeader("scroll_id", scrollId);

				try {
					response = pool.getHttpPostResponse(postRequest);
				} catch (Exception e) {
					logger.error(e.getMessage());
				}

				if (response.getStatusLine().getStatusCode() != 200) {
					logger.info("Scroll failed : HTTP error code : "
							+ response.getStatusLine().getStatusCode());
					// throw new RuntimeException("Failed : HTTP error code : "
					// + response.getStatusLine().getStatusCode());
					return null;
				}

				BufferedReader br = new BufferedReader(new InputStreamReader(
						(response.getEntity().getContent())));

				logger.debug("Output from Server .... \n");
				while ((output = br.readLine()) != null) {
					outputBuilder.append(output);
				}
				
				br.close();
				
				// Convert the JSON output to DTO Object
				matchResponse = obm.readValue(outputBuilder.toString(),
						MatchResponse.class);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}

			return matchResponse;
		}
		return null;
	}
	
	public MatchResponse scrollMatchRequest(MatchRequest request, String timeout, String sizeStr) {
		HttpResponse response = null;
		MatchResponse matchResponse = null;
		String output;
		StringBuilder outputBuilder = new StringBuilder();
		ObjectMapper obm = new ObjectMapper();
		obm.setSerializationInclusion(Inclusion.NON_NULL);

		try {
			HttpPost postRequest = new HttpPost(searchUrl());
			postRequest.setEntity(new StringEntity(request.convertToString()));
			logger.debug(request.toString());
			postRequest.setHeader("Content-Type", "application/json");
			postRequest.setHeader("scroll", timeout);
			postRequest.setHeader("size", sizeStr);

			try {
				response = pool.getHttpPostResponse(postRequest);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}

			if (response.getStatusLine().getStatusCode() != 200) {
				logger.info("Scroll failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
				return null;
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(response.getEntity().getContent())));

			logger.debug("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				outputBuilder.append(output);
			}
			
			br.close();
			
			// Convert the JSON output to DTO Object
			matchResponse = obm.readValue(outputBuilder.toString(),
					MatchResponse.class);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return matchResponse;
	}
	
    public MatchResponse scrollStringQuery(String request, String timeout) {
        HttpResponse response = null;
        MatchResponse matchResponse = null;
        String output;
        StringBuilder outputBuilder = new StringBuilder();
        ObjectMapper obm = new ObjectMapper();
        obm.setSerializationInclusion(Inclusion.NON_NULL);

        try {
            HttpPost postRequest = new HttpPost(searchUrl() + "?scroll=" + timeout);
            postRequest.setEntity(new StringEntity(request));
            logger.debug(request.toString());
            postRequest.setHeader("Content-Type", "application/json");

            try {
                response = pool.getHttpPostResponse(postRequest);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }

            if (response.getStatusLine().getStatusCode() != 200) {
                logger.info("Scroll failed : HTTP error code : " + response.getStatusLine().getStatusCode());
                // throw new RuntimeException("Failed : HTTP error code : "
                // + response.getStatusLine().getStatusCode());
                return null;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

            logger.debug("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                outputBuilder.append(output);
            }

            br.close();

            // Convert the JSON output to DTO Object
            matchResponse = obm.readValue(outputBuilder.toString(), MatchResponse.class);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return matchResponse;
    }

    public MatchResponse scrollStringQueryNext(String scrollId, String timeout) {
        HttpResponse response = null;
        MatchResponse matchResponse = null;
        String output;
        StringBuilder outputBuilder = new StringBuilder();
        ObjectMapper obm = new ObjectMapper();
        obm.setSerializationInclusion(Inclusion.NON_NULL);
        String request = "{\"scroll\" : \"" + timeout + "\", \"scroll_id\" : \"" + scrollId + "\" }";

        try {
            HttpPost postRequest = new HttpPost(scrollUrl());
            postRequest.setEntity(new StringEntity(request));
            postRequest.setHeader("Content-Type", "application/json");

            try {
                response = pool.getHttpPostResponse(postRequest);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }

            if (response.getStatusLine().getStatusCode() != 200) {
                logger.info("Scroll failed : HTTP error code : " + response.getStatusLine().getStatusCode());
                return null;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

            logger.debug("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                outputBuilder.append(output);
            }

            br.close();

            // Convert the JSON output to DTO Object
            matchResponse = obm.readValue(outputBuilder.toString(), MatchResponse.class);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return matchResponse;
    }

	public MatchResponse indexing(String id, Map<String, Object> rec) {
		if(StringUtils.isBlank(id)) {
			return null;
		}

		HttpResponse response = null;
		MatchResponse matchResponse = null;
		String output;
		StringBuilder outputBuilder = new StringBuilder();
		ObjectMapper obm = new ObjectMapper();
		obm.setSerializationInclusion(Inclusion.NON_NULL);

		try {
			HttpPut putRequest = new HttpPut(getStoreUrl() + "/" + id);

			putRequest.setEntity(new StringEntity(RequestBuilder
					.buildIndexRequest(rec).convertToString()));
			putRequest.setHeader("Content-Type", "application/json");
			logger.debug(RequestBuilder.buildIndexRequest(rec).convertToString());
			
			try {
				response = pool.getHttpPutResponse(putRequest);
			} catch (Exception e) {
				logger.info(e.getMessage());
			}

			if (response != null && response.getStatusLine() != null) {
				int erCode = response.getStatusLine().getStatusCode();
				if (erCode == 200) {
				} else if (erCode == 201) {
					logger.debug("Indexing " + id + " HTTP error code 201: "
							+ response.getStatusLine().getStatusCode());
				} else {
					logger.info("Indexing " + id + " failed: HTTP error code : "
							+ response.getStatusLine().getStatusCode());
					return null;
				}
			} else {
				logger.info("Indexing " + id + " failed: HTTP no response");
				return null;
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(response.getEntity().getContent())));

			logger.debug("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				outputBuilder.append(output);
			}
			
			br.close();
			
			// Convert the JSON output to DTO Object
			matchResponse = obm.readValue(outputBuilder.toString(),
					MatchResponse.class);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return matchResponse;
	}

	public MatchResponse update(String id, Map<String, Object> rec) {
		if(StringUtils.isBlank(id)) {
			return null;
		}
		
		HttpResponse response = null;
		MatchResponse matchResponse = null;
		String output;
		StringBuilder outputBuilder = new StringBuilder();
		ObjectMapper obm = new ObjectMapper();
		obm.setSerializationInclusion(Inclusion.NON_NULL);

		try {
			HttpPost postRequest = new HttpPost(getStoreUrl() + "/" + id
					+ "/_update");
			String updateQueryString = RequestBuilder.buildUpdateRequest(rec)
					.convertToString();

			postRequest.setEntity(new StringEntity(updateQueryString));
			postRequest.setHeader("Content-Type", "application/json");

			int retryAttempt = 0;
			//Attempt Retry only in case of 409 error in response code.
			do {
				try {
					if (retryAttempt > 0) {
						logger.info(":::::Retried for response Code: " + response.getStatusLine().getStatusCode() + "for id:" + id);
						Thread.sleep(5);
					}

					response = pool.getHttpPostResponse(postRequest);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (Exception e) {
					logger.info("****Update ID:" + id + " Update Doc: " + updateQueryString);
					logger.info(e.getMessage());
					return null;
				}
				retryAttempt++;
			} while ((response == null || response.getStatusLine().getStatusCode() == 409) && retryAttempt <= RETRY_COUNT);
			
			if(response == null) {
				logger.info("Update failed : No response returned from Elasticsearch server.");
				return null;
			}

			if (response.getStatusLine().getStatusCode() != 200) {
				logger.info("****Request: "
						+ RequestBuilder.buildUpdateRequest(rec).toString());
				logger.info("Update failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
				return null;
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(response.getEntity().getContent())));

			logger.debug("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				outputBuilder.append(output);
			}
			
			br.close();
			
			// Convert the JSON output to DTO Object
			matchResponse = obm.readValue(outputBuilder.toString(),
					MatchResponse.class);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return matchResponse;
	}

	public MatchResponse match(MatchRequest request) {
		HttpResponse response = null;
		MatchResponse matchResponse = null;
		String output;
		StringBuilder outputBuilder = new StringBuilder();
		ObjectMapper obm = new ObjectMapper();
		obm.setSerializationInclusion(Inclusion.NON_NULL);

		try {
			HttpPost postRequest = new HttpPost(searchUrl());
			postRequest.setEntity(new StringEntity(request.convertToString()));
			logger.debug(request.toString());
			postRequest.setHeader("Content-Type", "application/json");

			try {
				response = pool.getHttpPostResponse(postRequest);
			} catch (Exception e) {
				logger.info(e.getMessage());
			}

			if(response == null) {
				logger.info("Match failed : No response returned from Elasticsearch server.");
				return null;
			}
			
			if (response.getStatusLine().getStatusCode() != 200) {
				logger.info("Match Request Match failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
				return null;
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(response.getEntity().getContent())));

			logger.debug("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				outputBuilder.append(output);
			}
			
			br.close();
			
			// Convert the JSON output to DTO Object
			matchResponse = obm.readValue(outputBuilder.toString(),
					MatchResponse.class);
		} catch (MalformedURLException e) {
			logger.info(e.getMessage());
		} catch (IOException e) {
			logger.info(e.getMessage());
		}

		return matchResponse;
	}

	public MatchResponse match(StringQueryRequest request) {
		HttpResponse response = null;
		MatchResponse matchResponse = null;
		String output;
		StringBuilder outputBuilder = new StringBuilder();
		ObjectMapper obm = new ObjectMapper();
		obm.setSerializationInclusion(Inclusion.NON_NULL);

		try {
			HttpPost postRequest = new HttpPost(searchUrl());
			postRequest.setEntity(new StringEntity(request.convertToString()));
			logger.debug(request.toString());
			postRequest.setHeader("Content-Type", "application/json");

			try {
				response = pool.getHttpPostResponse(postRequest);
			} catch (Exception e) {
				logger.info(e.getMessage());
			}
			
			if(response == null) {
				logger.info("Match failed : No response returned from Elasticsearch server.");
				return null;
			}

			if (response.getStatusLine().getStatusCode() != 200) {
				logger.info("String Query Match failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
				return null;
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(response.getEntity().getContent())));

			logger.debug("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				outputBuilder.append(output);
			}
			
			br.close();
			
			// Convert the JSON output to DTO Object
			matchResponse = obm.readValue(outputBuilder.toString(),
					MatchResponse.class);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return matchResponse;
	}

	public RespHit retrieve(String id) {
		if(StringUtils.isBlank(id)) {
			return null;
		}
		
		HttpResponse response = null;
		RespHit matchResponse = null;
		String output;
		StringBuilder temp = new StringBuilder();
		ObjectMapper obm = new ObjectMapper();
		obm.setSerializationInclusion(Inclusion.NON_NULL);

		try {
			HttpGet request = new HttpGet(getStoreUrl() + "/" + id);

			try {
				response = pool.getHttpGetResponse(request);
			} catch (Exception e) {
				logger.info("GET URL: " + getStoreUrl() + "/" + id);
				e.printStackTrace();
				logger.info("Got exception: " + e);

			}
			
			if(response == null) {
				logger.info("Retrieve failed : No response returned from Elasticsearch server.");
				return null;
			}
			
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(response.getEntity().getContent())));

			logger.debug("Output from Server .... \n");

			while ((output = br.readLine()) != null) {
				temp.append(output);
			}
			
			br.close();
			
			if (response.getStatusLine().getStatusCode() != 200) {
				logger.info("GET URL: " + getStoreUrl() + "/" + id);
				logger.info("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
				logger.info("Retrieve failed : Response: " + temp);
				return null;
			}
			// Convert the JSON output to DTO Object
			matchResponse = obm.readValue(temp.toString(), RespHit.class);

			/*
			 * HttpEntity entity = response.getEntity(); String respString =
			 * EntityUtils.toString(entity); System.out.println(respString);
			 */

		} catch (MalformedURLException e) {
			logger.info(e.getMessage());
		} catch (IOException e) {
			logger.info(e.getMessage());
		}

		return matchResponse;
	}
	
	public RespHit retrieve(String id,List<String> fields) {
		HttpResponse response = null;
		RespHit matchResponse = null;
		String output;
		StringBuilder temp = new StringBuilder();
		ObjectMapper obm = new ObjectMapper();
		obm.setSerializationInclusion(Inclusion.NON_NULL);
		logger.debug("using get by id");
		try {
			HttpGet request = new HttpGet(getStoreUrl() + "/" + id);
			 URI uri = null;
			try {
				uri = new URIBuilder(request.getURI()).addParameter("fields",StringUtils.join(fields,",") ).build();
				((HttpRequestBase) request).setURI(uri);				
				response = pool.getHttpGetResponse(request);
			}catch (URISyntaxException e1) {
				e1.printStackTrace();
			} catch (Exception e) {
				logger.info("GET URL: " + getStoreUrl() + "/" + id);
				e.printStackTrace();
				logger.info("Got exception: " + e);

			}
			
			if(response == null) {
				logger.info("Retrieve failed : No response returned from Elasticsearch server.");
				return null;
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(response.getEntity().getContent())));

			logger.debug("Output from Server .... \n");

			while ((output = br.readLine()) != null) {
				temp.append(output);
			}
			
			br.close();
			
			if (response.getStatusLine().getStatusCode() != 200) {
				logger.info("GET URL: " + getStoreUrl() + "/" + id);
				logger.info("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
				logger.info("Retrieve failed : Response: " + temp);
				return null;
			}
			
			matchResponse = obm.readValue(temp.toString(), RespHit.class);

		} catch (MalformedURLException e) {
			logger.info(e.getMessage());
		} catch (IOException e) {
			logger.info(e.getMessage());
		}

		return matchResponse;
	}

	public boolean delete(String id) {
		if(StringUtils.isBlank(id)) {
			return true;
		}
		id = StringUtils.trim(id);
		if(StringUtils.isBlank(id)) {
			return true;
		}
		
		CloseableHttpResponse response = null;

		try {
			// Create the httpClien
			String deleteUrl = getStoreUrl() + "/" + id;
			logger.debug("DELETE URL: " + deleteUrl);
			
			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpDelete request = new HttpDelete(deleteUrl);

			try {
				// Execute the httpClient
				response = httpClient.execute(request);
			} catch (Exception ee) {
				logger.info("DELETE exception: " + ee.getMessage());
			}

			if (response == null || response.getStatusLine() == null) {
				logger.info("Delete failed : No responses ");
				return false;
			} else if (response.getStatusLine().getStatusCode() == 404) {
				logger.info("The doc with id " + id + " does not exist.");
			} else if (response.getStatusLine().getStatusCode() != 200) {
				logger.info("Delete failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
				return false;
			}

		} catch (Exception e) {
			logger.info(e.getMessage());
			return false;
		} finally {
			try {
				if (response != null) {
					response.close();
				}
			} catch (IOException ec) {
				logger.info("Delete response object fail to close.");
				logger.info(ec.getMessage());
			}
		}
		return true;
	}

	public MatchResponse search(Map<String, List<String>> rec) {
		SearchRequest request = RequestBuilder.buildSearchRequest(rec);
		return search(request);
	}

	public MatchResponse search(Map<String, List<String>> rec, double mini_score) {
		SearchRequest request = RequestBuilder.buildSearchRequest(rec,
				mini_score);
		return search(request);
	}

	public MatchResponse search(Map<String, List<String>> rec, int from,
			int size) {
		SearchRequest request = RequestBuilder.buildSearchRequest(rec, from,
				size);
		if(request == null) {
			return null;
		}
		
		return search(request);
	}

	public MatchResponse search(Map<String, List<String>> rec, int from,
			int size, double mini_score) {
		SearchRequest request = RequestBuilder.buildSearchRequest(rec, from,
				size, mini_score);
		return search(request);
	}

	public MatchResponse search(Map<String, List<String>> rec,
			List<String> fields) {
		SearchRequest request = RequestBuilder.buildSearchRequest(rec, fields);
		return search(request);
	}

	public MatchResponse search(Map<String, List<String>> rec,
			List<String> fields, double mini_score) {
		SearchRequest request = RequestBuilder.buildSearchRequest(rec, fields,
				mini_score);
		return search(request);
	}

	public MatchResponse search(Map<String, List<String>> rec,
			List<String> fields, int from, int size) {
		if (rec == null) {
			return null;
		}

		SearchRequest request = RequestBuilder.buildSearchRequest(rec, fields,
				from, size);
		return search(request);
	}

	public MatchResponse search(Map<String, List<String>> rec,
			List<String> fields, int from, int size, double mini_score) {
		SearchRequest request = RequestBuilder.buildSearchRequest(rec, fields,
				from, size, mini_score);
		return search(request);
	}

	private MatchResponse search(SearchRequest request) {
		HttpResponse response = null;
		MatchResponse matchResponse = null;
		String output;
		ObjectMapper obm = new ObjectMapper();
		obm.setSerializationInclusion(Inclusion.NON_NULL);

		try {
			HttpPost postRequest = new HttpPost(this.searchUrl());
			postRequest.setEntity(new StringEntity(request.convertToString()));
			postRequest.setHeader("Content-Type", "application/json");

			try {
				response = pool.getHttpPostResponse(postRequest);
			} catch (Exception e) {
				logger.info("Http call error : " + e.getMessage());
			}
			
			if(response == null) {
				logger.info("Search failed : No response. " + request.convertToString());
				return null;
			}

			if (response.getStatusLine().getStatusCode() != 200) {
				logger.info("Search failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(response.getEntity().getContent())));

			logger.debug("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				// Convert the JSON output to DTO Object
				matchResponse = obm.readValue(output, MatchResponse.class);
			}
			
			br.close();

			/*
			 * HttpEntity entity = response.getEntity(); String respString =
			 * EntityUtils.toString(entity); System.out.println(respString);
			 */

		} catch (MalformedURLException e) {
			logger.info(e.getMessage());
		} catch (IOException e) {
			logger.info(e.getMessage());
		}

		return matchResponse;
	}
	
	public MatchResponse search(String request) {
		HttpResponse response = null;
		MatchResponse matchResponse = null;
		String output;
		ObjectMapper obm = new ObjectMapper();
		obm.setSerializationInclusion(Inclusion.NON_NULL);

		try {
			HttpPost postRequest = new HttpPost(this.searchUrl());
			postRequest.setEntity(new StringEntity(CommonUtils.decodeISO_8859_1AndUtf_8Format(request)));
			postRequest.setHeader("Content-Type", "application/json");

			try {
				response = pool.getHttpPostResponse(postRequest);
			} catch (Exception e) {
				logger.info("Http call error : " + e.getMessage());
			}
			
			if(response == null) {
				logger.info("Search failed : No response. " + request);
				return null;
			}

			if (response.getStatusLine().getStatusCode() != 200) {
				logger.info("Search failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(response.getEntity().getContent())));

			logger.debug("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				// Convert the JSON output to DTO Object
				matchResponse = obm.readValue(output, MatchResponse.class);
			}
			
			br.close();

			/*
			 * HttpEntity entity = response.getEntity(); String respString =
			 * EntityUtils.toString(entity); System.out.println(respString);
			 */

		} catch (MalformedURLException e) {
			logger.info(e.getMessage());
		} catch (IOException e) {
			logger.info(e.getMessage());
		}

		return matchResponse;
	}
	
    public HashMap<String, String> getMapping(String index, String type) {
        HashMap<String, String> leafSet = new HashMap<String, String>();
        HttpResponse response = null;
        String output;
        StringBuilder temp = new StringBuilder();
        ObjectMapper obm = new ObjectMapper();
        obm.setSerializationInclusion(Inclusion.NON_NULL);
        System.out.println("using get by id");
        try {
            HttpGet request = new HttpGet(this.server + "/" + index + "/_mapping/" + type);
            try {
                response = pool.getHttpGetResponse(request);
            } catch (Exception e) {
                System.out.println("GET URL: " + this.server + "/" + index + "/_mapping/" + type);
                e.printStackTrace();
                System.out.println("Got exception: " + e);
            }

            if (response == null) {
                logger.info("Retrieve failed : No response returned from Elasticsearch server.");
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

            logger.debug("Output from Server .... \n");

            while ((output = br.readLine()) != null) {
                temp.append(output);
            }

            br.close();

            if (response.getStatusLine().getStatusCode() != 200) {
                logger.info("GET URL: " + this.server + "/" + index + "/_mapping/" + type);
                logger.info("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
                logger.info("Retrieve failed : Response: " + temp);
            }

            JsonNode root = obm.readValue(temp.toString(), JsonNode.class);
            JsonNode nodeProp = root.findPath(MAPPING_NODE);
            leafSet = getNodeNameSet(null, nodeProp);
        } catch (MalformedURLException e) {
            logger.info(e.getMessage());
        } catch (IOException e) {
            logger.info(e.getMessage());
        }

        return leafSet;
    }

    private HashMap<String, String> getNodeNameSet(String prefix, JsonNode node) {
        HashMap<String, String> ret = new HashMap<String, String>();
        if (node.isObject()) {
            Iterator<Entry<String, JsonNode>> itr = node.getFields();
            while (itr.hasNext()) {
                Entry<String, JsonNode> subnodeEntry = itr.next();
                String nodeName = subnodeEntry.getKey();
                String nodeFullName = prefix == null ? nodeName : prefix + "." + nodeName;

                JsonNode nodeValue = subnodeEntry.getValue();
                JsonNode typeValue = nodeValue.get("type");
                String fieldType = null;
                if (typeValue != null) {
                    fieldType = typeValue.getTextValue();
                } else {
                    fieldType = null;
                }

                if (subnodeEntry.getValue().has(MAPPING_NODE)) {
                    HashMap<String, String> subprop = getNodeNameSet(nodeFullName,
                            subnodeEntry.getValue().get(MAPPING_NODE));
                    ret.putAll(subprop);
                } else {
                    ret.put(nodeFullName, fieldType);
                }
            }
        } else if (node.isArray()) {

        } else {
        }
        return ret;
    }
}
