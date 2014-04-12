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
package com.windigo.types;

import java.io.IOException;
import java.lang.reflect.Type;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.windigo.exception.BaseException;
import com.windigo.exception.HttpCredentialException;
import com.windigo.exception.HttpEndpointNotFoundException;
import com.windigo.exception.HttpServiceNotAvailableException;
import com.windigo.utils.GlobalSettings;


/**
 * @author burakdede
 * 
 * Wrapper for http request repsonse
 * 
 */
public final class Response {
	
	private final static String TAG = Response.class.getCanonicalName();
	
	protected static final boolean DEBUG = GlobalSettings.DEBUG;

	private int httpStatusCode;
	
	private String rawString;
	
	private String reason;
	
	private Type responseType;
	
	public Response(HttpResponse httpResponse) throws BaseException, IOException {
		if (httpResponse == null) {
			rawString = "";
			reason = "Http response is null";
		} else {
			handleHttpResponse(httpResponse);			
		}
	}
	

	/**
	 * @return the httpStatusCode
	 */
	public int getHttpStatusCode() {
		return httpStatusCode;
	}

	/**
	 * @param httpStatusCode the httpStatusCode to set
	 */
	public void setHttpStatusCode(int httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
	}

	/**
	 * @return the rawString
	 */
	public String getRawString() {
		return rawString;
	}

	/**
	 * @param rawString the rawString to set
	 */
	public void setRawString(String rawString) {
		this.rawString = rawString;
	}

	/**
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * @param reason the reason to set
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}

	/**
	 * @return the responseType
	 */
	public Type getResponseType() {
		return responseType;
	}

	/**
	 * @param responseType the responseType to set
	 */
	public void setResponseType(Type responseType) {
		this.responseType = responseType;
	}


	public void handleHttpResponse(HttpResponse httpResponse) throws BaseException, IOException {
		
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		Log.d(TAG, "Status code : " + statusCode);
		setHttpStatusCode(statusCode);
		
		switch (statusCode) {
			case 200:
				// everything ok
				rawString = EntityUtils.toString(httpResponse.getEntity());
				break;
			case 401:
				// authorization problem
				httpResponse.getEntity().consumeContent();
				setReason(httpResponse.getStatusLine().toString());
				throw new HttpCredentialException(httpResponse.getStatusLine().toString());
				
			case 404:
				// endpoint does not exist
				httpResponse.getEntity().consumeContent();
				setReason(httpResponse.getStatusLine().toString());
				throw new HttpEndpointNotFoundException(httpResponse.getStatusLine().toString());
				
			case 500:
				// service down
				httpResponse.getEntity().consumeContent();
				setReason(httpResponse.getStatusLine().toString());
				throw new HttpServiceNotAvailableException(httpResponse.getStatusLine().toString());
				
			default:
				// generic exception
				httpResponse.getEntity().consumeContent();
				setReason(httpResponse.getStatusLine().toString());
				throw new BaseException(httpResponse.getStatusLine().toString());
		}
		
	}
}
