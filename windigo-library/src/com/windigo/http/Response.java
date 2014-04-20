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

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

import com.windigo.parsers.ResponseTypeParser;


/**
 * @author burakdede
 * 
 * Wrapper for generic http repsonse
 * 
 */
public final class Response {

	private int statusCode;
	
	private String reason;
	
	private List<Header> headers;
	
	private InputStream contentStream;
	
	@SuppressWarnings("unused")
	private ResponseTypeParser<? extends Type> contentParser;

	/**
	 * Create new response wrapper
	 * 
	 * @param httpStatusCode http status codwe
	 * @param content actual response
	 * @param reason store data about error
	 * @param headers response header list
	 * @param contentStream response stream object
	 */
	public Response(int httpStatusCode, String reason, 
			List<Header> headers, InputStream contentStream) {
		this.statusCode = httpStatusCode;
		this.reason = reason;
		this.headers = headers;
		this.contentStream = contentStream;
	}

	public void setContentParser(ResponseTypeParser<? extends Type> contentParser) {
		this.contentParser = contentParser;
	}

	public InputStream getContentStream() {
		return contentStream;
	}

	public int getHttpStatusCode() {
		return statusCode;
	}

	public void setHttpStatusCode(int httpStatusCode) {
		this.statusCode = httpStatusCode;
	}

	public String getReason() {
		return reason;
	}

	public List<Header> getHeaders() {
		return headers;
	}
	
}
