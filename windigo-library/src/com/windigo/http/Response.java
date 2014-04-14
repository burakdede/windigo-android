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

import java.util.List;


/**
 * @author burakdede
 * 
 * Wrapper for generic http repsonse
 * 
 */
public final class Response {

	private int httpStatusCode;
	
	private String rawString;
	
	private String reason;
	
	private List<Header> headers;
	
	public Response(int httpStatusCode, String rawString, String reason, List<Header> headers) {
		this.httpStatusCode = httpStatusCode;
		this.rawString = rawString;
		this.reason = reason;
		this.headers = headers;
	}
	

	public int getHttpStatusCode() {
		return httpStatusCode;
	}


	public void setHttpStatusCode(int httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
	}


	public String getRawString() {
		return rawString;
	}


	public void setRawString(String rawString) {
		this.rawString = rawString;
	}


	public String getReason() {
		return reason;
	}


	public void setReason(String reason) {
		this.reason = reason;
	}


	public List<Header> getHeaders() {
		return headers;
	}


	public void setHeaders(List<Header> headers) {
		this.headers = headers;
	}
	
}
