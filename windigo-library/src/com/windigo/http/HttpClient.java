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
package com.windigo.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import android.util.Log;

import com.windigo.exception.BaseException;
import com.windigo.exception.HttpCredentialException;
import com.windigo.exception.HttpEndpointNotFoundException;
import com.windigo.utils.GlobalSettings;

/**
 * @author burakdede
 *
 * Plain class for doing all http related operations
 * 
 */
public class HttpClient implements HttpClientOperations {
	
	private static final String TAG = HttpClient.class.getCanonicalName();
	
	protected static final boolean DEBUG = GlobalSettings.DEBUG;
	
	private Header[] headers;

	private final DefaultHttpClient mHttpClient;
	
	public HttpClient(DefaultHttpClient httpClient) {
		mHttpClient = httpClient;
	}
	
	/**
	 * @param headers the headers to set
	 */
	public void setHeaders(Header[] headers) {
		this.headers = headers;
	}
	

	/**
	 * Execute the post request and return content.
	 * Reuse the createHttpPostRequest and executeHttpRequest methods
	 * 
	 * @param url
	 * @param nameValuePairs
	 * @exception IOException, {@link HttpCredentialException}, 
	 * 			{@link BaseException}, {@link HttpEndpointNotFoundException}
	 * 
	 * @return {@link String}
	 */
	@Override
	public HttpResponse doHttpPost(String url, NameValuePair... nameValuePairs)
			throws IOException, HttpCredentialException, BaseException, HttpEndpointNotFoundException {
		if (DEBUG) Log.d(TAG, "doHttpPost for: " + url);
		HttpPost post = createHttpPostRequest(url, nameValuePairs);
		
		return executeHttpRequest(post);
	}


	/**
	 * @param url
	 * @param nameValuePairs
	 * @exception IOException, {@link HttpCredentialException}, 
	 * 			{@link BaseException}, {@link HttpEndpointNotFoundException}
	 * 
	 * @return {@link String}
	 */
	@Override
	public HttpResponse doHttpGet(String url, NameValuePair... nameValuePairs)
			throws IOException, HttpCredentialException, BaseException, HttpEndpointNotFoundException {
		if (DEBUG) Log.d(TAG, "doHttpGet for: " + url);
		HttpGet get = createHttpGetRequest(url, nameValuePairs);

		return executeHttpRequest(get);
	}
	

	/**
	 * Execute the given {@link HttpRequestBase}. Closes all the expired 
	 * connection pool items.
	 * 
	 * @param httpRequest
	 * @throws IOException
	 * @return {@link HttpResponse}
	 */
	@Override
	public HttpResponse executeHttpRequest(HttpRequestBase httpRequest)
			throws IOException {
		if (DEBUG) Log.d(TAG, "executing HttpRequest for: " 
			+ httpRequest.getURI().toString());
		
		// set headers for request
		if (headers != null && headers.length > 0) {
			httpRequest.setHeaders(headers);
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
	 * Create {@link HttpGet} request with given {@link String} url
	 * and {@link NameValuePair} request parameters
	 * 
	 * @param url
	 * @param nameValuePairs
	 * @return {@link HttpGet}
	 */
	@Override
	public HttpGet createHttpGetRequest(String url,
			NameValuePair... nameValuePairs) {
		String queryString = URLEncodedUtils.format(sanitizeParameters(nameValuePairs), HTTP.UTF_8);
		String fullUrlString = new StringBuilder(url + "?" + queryString).toString();
		HttpGet httpGet = new HttpGet(fullUrlString);
		if (DEBUG) Log.d(TAG, "Created http get request " + httpGet.getURI());
		
		return httpGet;
	}

	
	/**
	 * Create {@link HttpPost} request with given {@link String} url
	 * and {@link NameValuePair} request parameters
	 * 
	 * @param url
	 * @param nameValuePairs
	 * @throws IllegalArgumentException
	 * @return {@link HttpPost}
	 */
	@Override
	public HttpPost createHttpPostRequest(String url,
			NameValuePair... nameValuePairs) {
		HttpPost httpPost = new HttpPost(url);
		
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(sanitizeParameters(nameValuePairs), HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Problem while encoding url parameters " + e.getMessage());
		}
		
		return httpPost;
	}
	
	
	/**
	 * Remove the null parameters from the list of {@link NameValuePair} pairs
	 * 
	 * @param paramPairs
	 * @return {@link List}
	 */
	private List<NameValuePair> sanitizeParameters(NameValuePair... paramPairs) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		
		for (NameValuePair param : paramPairs) {
			if (param.getValue() != null) {
				if (DEBUG) Log.d(TAG, "Adding param: " + param);
				params.add(param);
			}
		}
		
		return params;
	}

}
