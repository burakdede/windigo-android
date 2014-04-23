/*
 * Copyright (C) Burak Dede.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.windigo.http.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;

import com.windigo.exception.BaseException;
import com.windigo.exception.HttpCredentialException;
import com.windigo.exception.HttpEndpointNotFoundException;
import com.windigo.http.Request;
import com.windigo.http.Response;
import com.windigo.logging.Logger;
import com.windigo.utils.GlobalSettings;
import com.windigo.utils.StringHelper;

/**
 * @author burakdede
 * 
 *         Apache {@link HttpClient} client and some preconfigured methods and
 *         configs
 * 
 */
public class ApacheHttpClient implements BaseHttpClient {

	private Header[] headers;

	private final DefaultHttpClient mHttpClient;

	private Context context;

	public ApacheHttpClient() {
		mHttpClient = createHttpClient();
	}

	public ApacheHttpClient(Context context) {
		this();
		this.context = context;
	}

	/**
	 * Create {@link DefaultHttpClient} with set of schemes, without redirect
	 * and support for http and https schemes.
	 * 
	 * @return {@link DefaultHttpClient}
	 */
	private static final DefaultHttpClient createHttpClient() {
		// Sets up the http part of the service.
		final SchemeRegistry supportedSchemes = new SchemeRegistry();

		// Register the "http" protocol scheme, it is required
		// by the default operator to look up socket factories.
		final SocketFactory sf = PlainSocketFactory.getSocketFactory();
		supportedSchemes.register(new Scheme("http", sf, 80));
		supportedSchemes.register(new Scheme("https", SSLSocketFactory
				.getSocketFactory(), 443));

		// Set some client http client parameter defaults.
		final HttpParams httpParams = createHttpParams();
		HttpClientParams.setRedirecting(httpParams, false);

		final ClientConnectionManager ccm = new ThreadSafeClientConnManager(
				httpParams, supportedSchemes);
		return new DefaultHttpClient(ccm, httpParams);
	}

	/**
	 * Create {@link HttpParams} for {@link DefaultHttpClient}
	 * 
	 * @return {@link HttpParams}
	 */
	private final static HttpParams createHttpParams() {
		final HttpParams params = new BasicHttpParams();

		HttpConnectionParams.setStaleCheckingEnabled(params, false);
		HttpConnectionParams.setConnectionTimeout(params,
				GlobalSettings.CONNNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(params,
				GlobalSettings.CONNNECTION_TIMEOUT);
		HttpConnectionParams.setSocketBufferSize(params, 8192);

		return params;
	}

	/**
	 * Execute the post request and return content. Reuse the
	 * createHttpPostRequest and executeHttpRequest methods
	 * 
	 * @param url
	 * @param nameValuePairs
	 * @exception IOException
	 *                , {@link HttpCredentialException}, {@link BaseException},
	 *                {@link HttpEndpointNotFoundException}
	 * 
	 * @return {@link String}
	 */
	private HttpResponse doHttpPost(String url,
			List<NameValuePair> nameValuePairs) throws IOException {

		Logger.log("[Request] Doing http post with url " + url);
		HttpPost post = createHttpPostRequest(url, nameValuePairs);

		return executeHttpRequest(post);

	}

	/**
	 * @param url
	 * @param nameValuePairs
	 * @exception IOException
	 *                , {@link HttpCredentialException}, {@link BaseException},
	 *                {@link HttpEndpointNotFoundException}
	 * 
	 * @return {@link String}
	 */
	private HttpResponse doHttpGet(String url,
			List<NameValuePair> nameValuePairs) throws IOException {

		Logger.log("[Request] Doing http get with url " + url);
		HttpGet get = createHttpGetRequest(url, nameValuePairs);

		return executeHttpRequest(get);

	}

	/**
	 * Execute the given {@link HttpRequestBase}. Closes all the expired
	 * connection pool items and set heeaders if present.
	 * 
	 * @param httpRequest
	 * @throws IOException
	 * @return {@link HttpResponse}
	 */
	public HttpResponse executeHttpRequest(HttpRequestBase httpRequest)
			throws IOException {

		Logger.log("[Request] Executing http request for: "
				+ httpRequest.getURI().toString());

		// set headers for request
		if (headers != null && headers.length > 0) {
			httpRequest.setHeaders(headers);
			Logger.log("[Request] Found " + headers.length + " header");
		}

		try {
			mHttpClient.getConnectionManager().closeExpiredConnections();
			return mHttpClient.execute(httpRequest);
		} catch (IOException e) {
			httpRequest.abort();
			throw e;
		}

	}

	/**
	 * Create {@link HttpGet} request with given {@link String} url and
	 * {@link NameValuePair} request parameters
	 * 
	 * @param url
	 * @param nameValuePairs
	 * @return {@link HttpGet}
	 */
	public HttpGet createHttpGetRequest(String url,
			List<NameValuePair> nameValuePairs) {

		String queryString = URLEncodedUtils.format(
				sanitizeParameters(nameValuePairs), HTTP.UTF_8);
		String fullUrlString = new StringBuilder(url + "?" + queryString)
				.toString();
		HttpGet httpGet = new HttpGet(fullUrlString);
		Logger.log("[Request] Creating http get request " + httpGet.getURI());

		return httpGet;

	}

	/**
	 * Create {@link HttpPost} request with given {@link String} url and
	 * {@link NameValuePair} request parameters
	 * 
	 * @param url
	 * @param nameValuePairs
	 * @throws IllegalArgumentException
	 * @return {@link HttpPost}
	 */
	public HttpPost createHttpPostRequest(String url,
			List<NameValuePair> nameValuePairs) {

		HttpPost httpPost = new HttpPost(url);

		try {
			httpPost.setEntity(new UrlEncodedFormEntity(
					sanitizeParameters(nameValuePairs), HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(
					"Problem while encoding url parameters " + e.getMessage());
		}

		return httpPost;

	}

	/**
	 * Remove the null parameters from the list of {@link NameValuePair} pairs
	 * 
	 * @param paramPairs
	 * @return {@link List}
	 */
	private List<NameValuePair> sanitizeParameters(
			List<NameValuePair> paramPairs) {

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		for (NameValuePair param : paramPairs) {
			if (param.getValue() != null) {
				Logger.log("[Request] Adding parameter: " + param.getName()
						+ " " + param.getValue());
				params.add(param);
			}
		}

		return params;

	}

	private Response handleHttpResponse(HttpResponse httpResponse)
			throws IOException {

		int statusCode = httpResponse.getStatusLine().getStatusCode();
		InputStream contentStream = httpResponse.getEntity().getContent();
		Logger.log("[Response] Status code " + statusCode);

		switch (statusCode) {
		case 200:
			// everything ok
			// TODO: remove raw strings alongp project
			// String content = EntityUtils.toString(httpResponse.getEntity());
			return new Response(statusCode, StringHelper.EMPTY,
					com.windigo.http.Header
							.convertFromApacheHeaders(httpResponse
									.getAllHeaders()), contentStream);

		case 401:
			// authorization problem
			httpResponse.getEntity().consumeContent();
			return new Response(statusCode, httpResponse.getStatusLine()
					.toString(),
					com.windigo.http.Header
							.convertFromApacheHeaders(httpResponse
									.getAllHeaders()), contentStream);

		case 404:
			// endpoint does not exist
			httpResponse.getEntity().consumeContent();
			return new Response(statusCode, httpResponse.getStatusLine()
					.toString(),
					com.windigo.http.Header
							.convertFromApacheHeaders(httpResponse
									.getAllHeaders()), contentStream);

		case 500:
			// service down
			httpResponse.getEntity().consumeContent();
			return new Response(statusCode, httpResponse.getStatusLine()
					.toString(),
					com.windigo.http.Header
							.convertFromApacheHeaders(httpResponse
									.getAllHeaders()), contentStream);

		default:
			// generic exception
			httpResponse.getEntity().consumeContent();
			return new Response(statusCode, httpResponse.getStatusLine()
					.toString(),
					com.windigo.http.Header
							.convertFromApacheHeaders(httpResponse
									.getAllHeaders()), contentStream);
		}

	}

	/**
	 * Handle cookies with cookie-handler
	 * 
	 */
	private void setupCookieManager() {

		CookieManager cookieManager = new CookieManager();
		CookieHandler.setDefault(cookieManager);
		Logger.log("[Request] Setting cookie manager");

	}

	@Override
	public Response execute(Request request) throws IOException {

		long start = System.nanoTime();

		if (request == null)
			throw new IllegalArgumentException("Request can not be null");
		Logger.log(request.toString());

		Response response = null;
		headers = com.windigo.http.Header.convertToApacheHeaders(request
				.getHeaders());
		setupCookieManager();

		switch (request.getHttpRequestType()) {
		case GET:
			HttpResponse httpGetResponse = doHttpGet(request.getFullUrl(),
					request.getQueryParams());
			response = handleHttpResponse(httpGetResponse);
			return response;

		case POST:
			HttpResponse httpPostResponse = doHttpPost(request.getFullUrl(),
					request.getBodyParams());
			response = handleHttpResponse(httpPostResponse);
			long end = System.nanoTime();
			Logger.log("[Response] Response time: " + (end - start) / 1000d
					+ " seconds");
			return response;

		default:
			throw new IllegalArgumentException(
					"Http request type should be POST or GET");
		}

	}

	/**
	 * Setup transparent http response cache for http clients
	 * 
	 */
	@Override
	public void setupResponseCache() {

		if (context == null) {
			throw new IllegalStateException(
					"Context of the application is null");
		}

		try {
			File httpCacheDir = new File(context.getCacheDir(), "http");
			long httpCacheSize = 10 * 1024 * 1024; // 10 mb
			Class.forName("android.net.http.HttpResponseCache")
					.getMethod("install", File.class, long.class)
					.invoke(null, httpCacheDir, httpCacheSize);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
