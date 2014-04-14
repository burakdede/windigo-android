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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;

import com.windigo.logging.Logger;
import com.windigo.utils.GlobalSettings;

/**
 * @author burakdede
 *
 * {@link HttpUrlConnectionClient} client and some preconfigured methods and configs
 * 
 */
public class HttpUrlConnectionClient implements BaseHttpClient{
	
	private HttpURLConnection connection;
	
	public HttpUrlConnectionClient() {
	}

	@Override
	public Response execute(Request request) throws IOException {
		
		if (request == null) throw new IllegalArgumentException("Request can not be null");
		
		Logger.log(HttpUrlConnectionClient.class, request.toString());
		
		connection = openHttpURLConnection(request);
		setupHttpUrlConnectionClient(connection, request);
		
		return getResponse();
		
	}
	
	protected HttpURLConnection openHttpURLConnection(Request request) 
			throws MalformedURLException, IOException {
		
		HttpURLConnection connection = (HttpURLConnection) 
				new URL(request.getFullUrl()).openConnection();
		connection.setRequestMethod(request.getHttpRequestType().toString());
		connection.setConnectTimeout(GlobalSettings.CONNNECTION_TIMEOUT);
		connection.setReadTimeout(GlobalSettings.CONNECTION_READ_TIMEOUT);
		return connection;
		
	}
	
	
	/**
	 * Setup {@link HttpURLConnection} to remote url
	 * set some configuration and body if exist
	 * 
	 * @param request
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private void setupHttpUrlConnectionClient(HttpURLConnection connection, Request request) 
			throws MalformedURLException, IOException {

		connection.setDoInput(true);
		
		// set headers for request
		addHeaders(request.getHeaders());
		
		// if its post request
		if (request.hasBody()) {
			connection.setDoOutput(true);
			OutputStream os = connection.getOutputStream();
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
			bw.write(writeBodyParams(request.getBodyParams()));
			// clean up mess
			bw.flush();
			bw.close();
			os.close();
		}
		
	}
	
	
	/**
	 * Form response wrapper with {@link Response} object
	 * 
	 * @return {@link Response}
	 * @throws IOException
	 */
	private Response getResponse() throws IOException {
		
		String reason = connection.getResponseMessage();
		reason = (reason == null ? "" : reason);
		
		// get headers of response
		List<Header> headers = getResponseHeaders();
		
		// get actual raw respose string from stream
		int statusCode = connection.getResponseCode();
		InputStream is; 
		
		if (statusCode >= 400) {
			is = connection.getErrorStream();
		} else {
			is = connection.getInputStream();
		}
		
		String rawString = readResponseStream(is);
		
		return new Response(statusCode, rawString, reason, headers);
		
	}
	
	
	/**
	 * Takes response input stream, buffer it and read
	 * 
	 * @param is
	 * @return {@link String} response
	 * @throws IOException
	 */
	private String readResponseStream(InputStream is) throws IOException {
		
		StringBuffer response = new StringBuffer();
		String line;
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		
		while ((line = br.readLine()) != null) {
			response.append(line);
		}
		br.close();
		
		return response.toString();
		
	}
	
	
	/**
	 * Read response header to {@link List}
	 * 
	 * @return {@link List} of {@link Header}
	 */
	private List<Header> getResponseHeaders() {
		
		List<Header> headers = new ArrayList<Header>();
		
		for(Map.Entry<String, List<String>> fieldEntry : connection.getHeaderFields().entrySet()) {
			String nameString = fieldEntry.getKey();
			for (String value : fieldEntry.getValue()) {
				headers.add(new Header(nameString, value));
			}
		}
		
		return headers;
		
	}
	
	
	/**
	 * Writes body parameters to as %s=%s url
	 * encoded format
	 * 
	 * @param bodyParams
	 * @return {@link String}
	 * @throws UnsupportedEncodingException
	 */
	private String writeBodyParams(List<NameValuePair> bodyParams) 
			throws UnsupportedEncodingException {
		
		StringBuilder bodyBuilder = new StringBuilder();
		
		for (NameValuePair param : bodyParams) {
			bodyBuilder.append(URLEncoder.encode(param.getName(), "UTF-8"));
			bodyBuilder.append("=");
			bodyBuilder.append(URLEncoder.encode(param.getValue(), "UTF-8"));
		}
		
		return bodyBuilder.toString();
		
	}
	
	
	/**
	 * Set the header list to {@link URLConnection} object
	 * 
	 * @param headers
	 */
	private void addHeaders(List<Header> headers) {
		
		if (headers.size() == 0) return;
		
		for (Header header : headers) {
			connection.addRequestProperty(header.getName(), header.getValue());
		}
		
	}
}
