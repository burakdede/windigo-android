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

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;

import android.net.Uri;

import com.windigo.utils.StringHelper;

/**
 * @author burakdede
 *
 * Wrapper for generic http request
 *
 */
public class Request {

	private RequestType httpRequestType;
	
	private String fullUrl;
	
	private List<Header> headers;
	
	private List<NameValuePair> queryParams;
	
	private List<NameValuePair> bodyParams;
	
	private boolean hasBody;
	
	public Request() {
		this.headers = new ArrayList<Header>();
		this.queryParams = new ArrayList<NameValuePair>();
		this.bodyParams = new ArrayList<NameValuePair>();
		hasBody = false;
		httpRequestType = RequestType.GET;
	}
	
	public Request(RequestType httpRequestType, String fullUrl, List<Header> headers,
			List<NameValuePair> queryParams, List<NameValuePair> bodyParams) {
		
		if (httpRequestType == null) {
			throw new IllegalArgumentException("Http method type can not be null");
		}
		
		if (fullUrl == null) {
			throw new IllegalArgumentException("Url can not be null");
		}
		
		this.headers = headers;
		
		if (httpRequestType == RequestType.POST && bodyParams.size() > 0) {
			hasBody = true;
		} else {
			hasBody = false;
		}
		
		this.httpRequestType = httpRequestType;
		this.fullUrl = fullUrl;
		this.bodyParams = bodyParams;
		this.queryParams = new ArrayList<NameValuePair>();
		
	}
	
	private static String formUrlStringFromNameValueList(List<NameValuePair> params) {
		
        List<String> vals = new ArrayList<String>();
        for (NameValuePair param : params) {
            if (param.getValue() != null) {
                String value = Uri.encode(param.getValue().toString());
                vals.add(param.getName() + "=" + value);
            }
        }

        return "?" + StringHelper.join(vals.toArray(), "&");
        
	}
	
	public RequestType getHttpRequestType() {
		return httpRequestType;
	}

	public void setHttpRequestType(RequestType httpRequestType) {
		this.httpRequestType = httpRequestType;
	}

	public String getFullUrl() {
		return fullUrl;
	}

	public void setFullUrl(String fullUrl) {
		this.fullUrl = fullUrl;
	}

	public List<Header> getHeaders() {
		return headers;
	}

	public void setHeaders(List<Header> headers) {
		this.headers = headers;
	}

	public List<NameValuePair> getQueryParams() {
		return queryParams;
	}

	public void setQueryParams(List<NameValuePair> queryParams) {
		this.queryParams = queryParams;
		this.fullUrl += formUrlStringFromNameValueList(queryParams);
	}

	public List<NameValuePair> getBodyParams() {
		return bodyParams;
	}

	public void setBodyParams(List<NameValuePair> bodyParams) {
		this.bodyParams = bodyParams;
	}

	public boolean hasBody() {
		return hasBody;
	}
	
	@Override
	public String toString() {
		StringBuilder requestStringBuilder = new StringBuilder();
		for(NameValuePair param : queryParams) 
			requestStringBuilder.append(String.format("[QueryParam]%s=%s\n", param.getName(), param.getValue()));
		
		for(Header header : headers)
			requestStringBuilder.append(String.format("[Header]%s : %s\n", header.getName(), header.getValue()));
		
		for(NameValuePair param : bodyParams) 
			requestStringBuilder.append(String.format("[BodyParam]%s=%s\n", param.getName(), param.getValue()));
		
		requestStringBuilder.append(String.format("[Url]%s=%s\n", "Full url", fullUrl));
		
		
		return requestStringBuilder.toString();
	}
}
