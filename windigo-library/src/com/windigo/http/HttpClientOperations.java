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

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;

import com.windigo.exception.BaseException;
import com.windigo.exception.HttpCredentialException;
import com.windigo.exception.HttpEndpointNotFoundException;

/**
 * @author burakdede
 *
 * Interface for http client operations
 * 
 */
public interface HttpClientOperations {

	public HttpResponse executeHttpRequest(HttpRequestBase httpRequest) throws IOException;
	
	public HttpResponse doHttpPost(String url, NameValuePair... nameValuePairs) 
			throws IOException, HttpCredentialException, BaseException, HttpEndpointNotFoundException;
	
	public HttpResponse doHttpGet(String url, NameValuePair... nameValuePairs) 
			throws IOException, HttpCredentialException, BaseException, HttpEndpointNotFoundException;

	public HttpGet createHttpGetRequest(String url, NameValuePair... nameValuePairs);
	
	public HttpPost createHttpPostRequest(String url, NameValuePair... nameValuePairs);
	
}