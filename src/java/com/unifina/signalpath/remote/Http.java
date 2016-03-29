package com.unifina.signalpath.remote;

import com.unifina.signalpath.*;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.codehaus.groovy.grails.web.json.JSONArray;
import org.codehaus.groovy.grails.web.json.JSONObject;
import org.codehaus.groovy.grails.web.json.JSONTokener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Module that lets user make HTTP requests with maximum control over
 *  - how response is formed (e.g. sending both URL params AND body)
 *  - getting all response headers, statusCodes, etc.
 * Maps will be used as both Input and Output type, though JSON output can also be List
 * @see SimpleHttp for module that does input construction and output de-construction for you
 */
public class Http extends AbstractSignalPathModule {

	private HttpVerbParameter verb = new HttpVerbParameter(this, "verb");
	private StringParameter URL = new StringParameter(this, "URL", "localhost");

	private Input<Object> body = new Input<>(this, "body", "Object");
	private MapInput headers = new MapInput(this, "headers");
	private MapInput queryParams = new MapInput(this, "params");
	private MapOutput responseHeaders = new MapOutput(this, "headers");
	private ListOutput errorOut = new ListOutput(this, "errors");
	private Output<Object> response = new Output<>(this, "data", "Object");
	private TimeSeriesOutput statusCode = new TimeSeriesOutput(this, "status code");
	private TimeSeriesOutput pingMillis = new TimeSeriesOutput(this, "ping(ms)");

	private static final Logger log = Logger.getLogger(Http.class);

	@Override
	public void init() {
		addInput(verb);
		verb.setUpdateOnChange(true);	// input name changes: POST -> body; GET -> trigger
		addInput(URL);
		addInput(queryParams);
		addInput(headers);
		addInput(body);
		addOutput(errorOut);
		addOutput(response);
		addOutput(statusCode);
		addOutput(pingMillis);
		addOutput(responseHeaders);
	}

	/** This function is overridden in HttpSpec to inject mock HttpClient */
	protected HttpClient getHttpClient() {
		if (_httpClient == null) {
			// commented out: SSL client that supports self-signed certs
			/*SSLContext sslcontext = SSLContexts.custom()
				.loadTrustMaterial(null, new TrustSelfSignedStrategy())
				.build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext);*/
			_httpClient = HttpClients.createMinimal(); /* .custom()
				.setSSLSocketFactory(sslsf)
				.build();*/
		}
		return _httpClient;
	}
	private transient CloseableHttpClient _httpClient;

	@Override
	public void onConfiguration(Map<String, Object> config) {
		super.onConfiguration(config);
		if (verb.hasBody()) {
			body.setDisplayName("body");
			body.canToggleDrivingInput = true;
		} else {
			body.setDisplayName("trigger");
			body.canToggleDrivingInput = false;
			body.setDrivingInput(true);
		}
	}

	@Override
	public void sendOutput() {
		List<String> errors = new LinkedList<>();

		List<NameValuePair> queryPairs = new LinkedList<>();
		for (Map.Entry<String, Object> pair : queryParams.getValue().entrySet()) {
			NameValuePair p = new BasicNameValuePair(pair.getKey(), pair.getValue().toString());
			queryPairs.add(p);
		}
		boolean alreadyAdded = (URL.getValue().indexOf('?') > -1);
		String url = URL.getValue() + (alreadyAdded ? "&" : "?") + URLEncodedUtils.format(queryPairs, "UTF-8");

		HttpRequestBase request = verb.getRequest(url);
		for (Map.Entry<String, Object> pair : headers.getValue().entrySet()) {
			request.addHeader(pair.getKey(), pair.getValue().toString());
		}

		if (verb.hasBody()) {
			Object b = body.getValue();
			String bodyString = b instanceof Map ? new JSONObject((Map)b).toString() :
								b instanceof List ? new JSONArray((List)b).toString() :
								b.toString();
			try {
				((HttpEntityEnclosingRequestBase)request).setEntity(new StringEntity(bodyString));
			} catch (UnsupportedEncodingException e) {
				errors.add(e.getMessage());
			}
		}

		long startTime = System.currentTimeMillis();
		HttpResponse httpResponse;
		try {
			httpResponse = getHttpClient().execute(request);
			pingMillis.send(System.currentTimeMillis() - startTime);
			statusCode.send(httpResponse.getStatusLine().getStatusCode());

			String responseString = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
			JSONTokener parser = new JSONTokener(responseString);
			Object responseData = parser.nextValue();	// parser returns Map, List, or String
			response.send(responseData);
		} catch (IOException e) {
			errors.add(e.getMessage());
		}

		if (!errors.isEmpty()) {
			errorOut.send(errors);
		}
	}

	@Override
	public void clearState() {}

	private static class HttpVerbParameter extends StringParameter {
		public HttpVerbParameter(AbstractSignalPathModule owner, String name) {
			super(owner, name, "POST"); //this.getValueList()[0]);
		}
		private List<PossibleValue> getValueList() {
			return Arrays.asList(
				new PossibleValue("GET", "GET"),
				new PossibleValue("POST", "POST"),
				new PossibleValue("PUT", "PUT"),
				new PossibleValue("DELETE", "DELETE"),
				new PossibleValue("PATCH", "PATCH")
			);
		}
		@Override public Map<String, Object> getConfiguration() {
			Map<String, Object> config = super.getConfiguration();
			config.put("possibleValues", getValueList());
			return config;
		}
		public boolean hasBody() {
			String v = this.getValue();
			return v.equals("POST") || v.equals("PUT") || v.equals("PATCH");
		}
		public HttpRequestBase getRequest(String url) {
			String v = this.getValue();
			return v.equals("GET") ? new HttpGet(url) :
				   	v.equals("POST") ? new HttpPost(url) :
					v.equals("PUT") ? new HttpPut(url) :
					v.equals("DELETE") ? new HttpDelete(url) :
					v.equals("PATCH") ? new HttpPatch(url) : new HttpPost(url);

		}
	}
}
