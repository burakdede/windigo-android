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
package com.windigo;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import android.net.Uri;
import android.util.Log;

import com.windigo.annotations.Get;
import com.windigo.annotations.Header;
import com.windigo.annotations.Placeholder;
import com.windigo.annotations.Post;
import com.windigo.annotations.QueryParam;
import com.windigo.annotations.QueryParamsObject;
import com.windigo.exception.BaseException;
import com.windigo.exception.HttpCredentialException;
import com.windigo.exception.HttpEndpointNotFoundException;
import com.windigo.http.HttpClient;
import com.windigo.parsers.ResponseTypeParser;
import com.windigo.types.RequestType;
import com.windigo.types.Response;
import com.windigo.utils.StringHelper;

/**
 * @author burakdede
 *
 * Generate all the method metadata used by invocation handler
 * parse necessarry annotation types and configure
 * 
 */
public class RestMethodMetaDataCache {
	
	private static final String TAG = RestMethodMetaDataCache.class.getCanonicalName();

	private Method method;
	
	private String requestPath;
	
	private Map<Integer, String> queryParams = new HashMap<Integer, String>();
	
	private Map<Integer, String> placeholderParms = new HashMap<Integer, String>();
	
	private Map<Integer, String> headerParams = new HashMap<Integer, String>();
	
	private RequestType requestType;
	
	private Type returnType;
	
	private HttpClient httpClient;
	
	private int indexOfQueryParamObject;
	
	private boolean haveQueryParamObject;
	
	private ResponseTypeParser<? extends Type> typeParser;
	
	public RestMethodMetaDataCache(Method method, HttpClient httpClient) {
		this.httpClient = httpClient;
		this.method = method;
		parseMethodMetaData();
	}
	
	
	/**
	 * Parse method and its parameters meta data
	 * to cache and initialize
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void parseMethodMetaData() {
		if (method.isAnnotationPresent(Get.class)) {
			requestType = RequestType.GET;
			requestPath = method.getAnnotation(Get.class).value();
		} else if (method.isAnnotationPresent(Post.class)) {
			requestType = RequestType.POST;
			requestPath = method.getAnnotation(Post.class).value();
		} else {
			throw new IllegalArgumentException("Could not find any http method annotation. " +
					"Use @Get or @Post on " + method);
		}
		
		returnType = method.getGenericReturnType();
		if (returnType == Void.class) {
			throw new IllegalArgumentException("Void return type is not allowed used typed class of yours");
		}
		typeParser = new ResponseTypeParser(returnType);
		
		Annotation[][] annotationArrays = method.getParameterAnnotations();
		
		for (int i = 0; i < annotationArrays.length; i++) {
			Annotation[] parameterAnnotations = annotationArrays[i];
			if (parameterAnnotations != null) {
				for (Annotation parameterAnnotation : parameterAnnotations) {
					Class<? extends Annotation> annotationType = parameterAnnotation.annotationType();
					if (annotationType == QueryParam.class) {
						queryParams.put(i,((QueryParam) parameterAnnotation).value());
					} else if (annotationType == QueryParamsObject.class) {
						indexOfQueryParamObject = i;
						haveQueryParamObject = true;
					} else if (annotationType == Placeholder.class) {
						placeholderParms.put(i, ((Placeholder) parameterAnnotation).value());
					} else if (annotationType == Header.class) {
						headerParams.put(i, ((Header) parameterAnnotation).value());
					}
				}
			}
		}
	}
	
	
	/**
	 * Convert query parameters map to {@link NameValuePair} list
	 * 
	 * @param queryParams
	 * @param args
	 * @return {@link List} of {@link NameValuePair}
	 */
	private static NameValuePair[] getListOfNameValuePairs(Map<Integer, String> queryParams, Object[] args) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		
		if (queryParams.size() > 0) {			
			for (Entry<Integer, String> queryParamEntry : queryParams.entrySet()) {
				Object parameterValue = args[queryParamEntry.getKey()];
				if (parameterValue != null) {
					String valueString = Uri.encode(parameterValue.toString());
					params.add(new BasicNameValuePair(queryParamEntry.getValue(), valueString));
				}
			}
		}
		
		return params.toArray(new NameValuePair[params.size()]);
	}
	
	
	/**
	 * Convert given plain class to query parameters
	 * for get requests
	 * 
	 * @param object
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * 
	 * @return {@link String} of url encoded query params url 
	 */
	private String mapObjectToQueryParamsUrlString(Object object) 
			throws IllegalAccessException, IllegalArgumentException {
		
		Class<?> paramClass = object.getClass();
		Field[] fields = paramClass.getDeclaredFields();
		Map<String, Object> fieldParams = new HashMap<String, Object>();

		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			fieldParams.put(field.getName(), field.get(object));
		}
		
		
        List<String> vals = new ArrayList<String>();
        for (Map.Entry<String, Object> queryParamEntry : fieldParams.entrySet()) {
            if (queryParamEntry.getValue() != null) {
                String value = Uri.encode(queryParamEntry.getValue().toString());
                vals.add(queryParamEntry.getKey() + "=" + value);
            }
        }
        
        return StringHelper.join(vals.toArray(), "&");
	}

	
	/**
	 * Replace placeholders with actual parameter values
	 * 
	 * @param path
	 * @param pathParams
	 * @param args
	 * @return {@link String}
	 */
    public String parsePlaceholderParams(String path, Map<Integer, String> pathParams, Object[] args) throws IllegalArgumentException{
        for (Map.Entry<Integer, String> placeholder : placeholderParms.entrySet()) {
            Object paramVal = args[placeholder.getKey()];
            if (paramVal == null) {
                throw new IllegalArgumentException(String.format("Null parameters are not allowed for : [%s]", placeholder.getValue()));
            }
            String value = Uri.encode(paramVal.toString());
            path = path.replaceAll("\\{(" + placeholder.getValue() + ")\\}", value);
        }
        return path;
    }
    
    
    
    /**
     * Parse header params to {@link org.apache.http.Header} array
     * 
     * @param headerParams
     * @param args
     * @return {@link org.apache.http.Header}
     * @throws IllegalArgumentException
     * 
     */
    public org.apache.http.Header[] parseHeaderParams(Map<Integer, String> headerParams, Object[] args) throws IllegalArgumentException{
    	org.apache.http.Header[] headersArray = new org.apache.http.Header[headerParams.size()];
    	
    	int i = 0;    	
    	for (Map.Entry<Integer, String> header : headerParams.entrySet()) {
			String headerVal = args[header.getKey()].toString();
			if (headerVal == null) throw new IllegalArgumentException(String.format("Null header values are not allowed for : [%s]", header.getValue()));
			headersArray[i] = new BasicHeader(header.getValue(), headerVal);
			i++;
		}
    	
    	return headersArray;
    }
    
	
	/**
	 * Invoke operation on behalf of rest service
	 * do the basic response type parsing
	 * 
	 * @param baseUrl
	 * @param args
	 * @throws HttpCredentialException
	 * @throws HttpEndpointNotFoundException
	 * @throws IOException
	 * @throws BaseException
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public <T> Object invoke(String baseUrl, Object[] args) 
			throws HttpCredentialException, HttpEndpointNotFoundException, 
			IOException, BaseException, IllegalAccessException, IllegalArgumentException {
		
		String fullUrl = baseUrl + requestPath;
		HttpResponse httpResponse;
		Response response;
		
		// parse and replace any placeholder parameters
		fullUrl = parsePlaceholderParams(fullUrl, placeholderParms, args);
		
		// set additional headers
		if (headerParams.size() > 0) {
			httpClient.setHeaders(parseHeaderParams(headerParams, args));
		}
		
		switch (requestType) {
			case GET:
				// map plain java class as query parameters
				if (haveQueryParamObject)  fullUrl = fullUrl + mapObjectToQueryParamsUrlString(args[indexOfQueryParamObject]);
				httpResponse = httpClient.doHttpGet(fullUrl, getListOfNameValuePairs(queryParams, args));
				response = new Response(httpResponse);
				Log.d(TAG, "Raw response: " + response.getRawString());
				return typeParser.parse(response.getRawString());
				
			case POST:
				httpResponse = httpClient.doHttpPost(fullUrl, getListOfNameValuePairs(queryParams, args));
				response = new Response(httpResponse);
				Log.d(TAG, "Raw response: " + response.getRawString());
				return typeParser.parse(response.getRawString());
				
			default:
				throw new IllegalStateException("Http method annotation does not exist (Get, Post)");
		}
		
	}
}
