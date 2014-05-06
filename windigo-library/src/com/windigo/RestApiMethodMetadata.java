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
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.net.Uri;
import android.os.AsyncTask;

import com.windigo.annotations.Get;
import com.windigo.annotations.Header;
import com.windigo.annotations.Placeholder;
import com.windigo.annotations.Post;
import com.windigo.annotations.QueryParam;
import com.windigo.annotations.QueryParamsWith;
import com.windigo.exception.JsonConversionException;
import com.windigo.http.Request;
import com.windigo.http.RequestType;
import com.windigo.http.Response;
import com.windigo.http.client.BaseHttpClient;
import com.windigo.logging.Logger;
import com.windigo.parsers.ResponseTypeParser;

/**
 * @author burakdede
 *         <p/>
 *         Generate all the method metadata used by invocation handler
 *         parse necessarry annotation types and configure
 */
public class RestApiMethodMetadata {

	private Method method;

	private String requestPath;

	private Map<Integer, String> queryParams = new HashMap<Integer, String>();

	private Map<Integer, String> bodyParams = new HashMap<Integer, String>();

	private Map<Integer, String> placeholderParms = new HashMap<Integer, String>();

	private Map<Integer, String> headerParams = new HashMap<Integer, String>();

	private int indexOfQueryParamObject;

	private boolean haveQueryParamObject;

	private RequestType requestType;

	private Type returnType;

	private BaseHttpClient httpClient;

	private ResponseTypeParser<? extends Type> typeParser;

	public RestApiMethodMetadata(Method method, BaseHttpClient httpClient) {
		this.httpClient = httpClient;
		this.method = method;
		parseMethodMetaData();
	}


	/**
	 * Parse method and its parameters meta data
	 * to cache and initialize
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
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
						queryParams.put(i, ((QueryParam) parameterAnnotation).value());
					} else if (annotationType == QueryParamsWith.class) {
						indexOfQueryParamObject = i;
						haveQueryParamObject = true;
					} else if (annotationType == Placeholder.class) {
						placeholderParms.put(i, ((Placeholder) parameterAnnotation).value());
					} else if (annotationType == Header.class) {
						headerParams.put(i, ((Header) parameterAnnotation).value());
					} else if (annotationType == com.windigo.annotations.Field.class) {
						bodyParams.put(i, ((com.windigo.annotations.Field) parameterAnnotation).value());
					}
				}
			}
		}
	}


	/**
	 * Convert parameters map to {@link NameValuePair} list
	 *
	 * @param queryParams
	 * @param args
	 * @return {@link List} of {@link NameValuePair}
	 */
	private static List<NameValuePair> convertToNameValuePairs(Map<Integer, String> queryParams, Object[] args) {

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

		return params;

	}


	private List<NameValuePair> parseQueryParams(Map<Integer, String> queryParams, Object[] args) {
		return convertToNameValuePairs(queryParams, args);
	}


	private List<NameValuePair> parsePostBodyFieldParams(Map<Integer, String> bodyParams, Object[] args) {
		return convertToNameValuePairs(bodyParams, args);
	}


	/**
	 * Replace placeholders with actual parameter values
	 *
	 * @param path
	 * @param pathParams
	 * @param args
	 * @return {@link String}
	 */
	public String parsePlaceholderParams(String path, Map<Integer, String> pathParams, Object[] args) throws IllegalArgumentException {

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
	 * Convert given plain class to query parameters
	 * for get requests
	 *
	 * @param object
	 * @return {@link String} of url encoded query params url
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	@SuppressWarnings("unchecked")
	private List<NameValuePair> parseQueryParamsWith(Object object)
			throws IllegalAccessException, IllegalArgumentException {

		Map<String, Object> fieldParams;

		try {
			//Checking if passed object is already a map
			fieldParams = (Map<String, Object>) object;
		} catch (ClassCastException cce) {
			Class<?> paramClass = object.getClass();
			Field[] fields = paramClass.getDeclaredFields();
			fieldParams = new HashMap<String, Object>();

			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				field.setAccessible(true);
				fieldParams.put(field.getName(), field.get(object));
			}
		}

		List<NameValuePair> vals = new ArrayList<NameValuePair>();
		for (Map.Entry<String, Object> queryParamEntry : fieldParams.entrySet()) {
			if (queryParamEntry.getValue() != null) {
				String value = Uri.encode(queryParamEntry.getValue().toString());
				vals.add(new BasicNameValuePair(queryParamEntry.getKey(), value));
			}
		}

		return vals;
	}

	/**
	 * Parse header params to {@link com.windigo.http.Header} list
	 *
	 * @param headerParams
	 * @param args
	 * @return {@link List} of {@link com.windigo.http.Header} values
	 * @throws IllegalArgumentException
	 */
	public List<com.windigo.http.Header> parseHeaderParams(Map<Integer, String> headerParams, Object[] args) throws IllegalArgumentException {

		List<com.windigo.http.Header> headers = new ArrayList<com.windigo.http.Header>();

		for (Map.Entry<Integer, String> header : headerParams.entrySet()) {
			String headerVal = args[header.getKey()].toString();
			String headerName = header.getValue();
			headers.add(new com.windigo.http.Header(headerName, headerVal));
		}

		return headers;

	}


	/**
	 * Make requests asyncronously with {@link AsyncTask} and return {@link HttpResponse}
	 *
	 * @param url
	 * @param parameters
	 * @return {@link HttpResponse}
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public Object makeAsyncRequest(final Request request)
			throws IOException, InterruptedException, ExecutionException {

		AsyncTask<Void, Integer, Object> backgroundTask = new AsyncTask<Void, Integer, Object>() {
			Response response;
			Object typedResponseObject;

			@Override
			protected Object doInBackground(Void... params) {
				try {
					response = httpClient.execute(request);

					if (response != null) {
						response.setContentParser(typeParser);
						typedResponseObject = typeParser.parse(response.getContentStream());
						Logger.log("[Response] Typed response object: " + typedResponseObject.toString());
					}

				} catch (IOException e) {
					e.printStackTrace();
				} catch (JsonConversionException e) {
					e.printStackTrace();
				}
				return typedResponseObject;
			}

		}.execute();

		return backgroundTask.get();
	}


	/**
	 * Invoke operation on behalf of rest service
	 * do the basic response type parsing
	 *
	 * @param baseUrl
	 * @param args
	 * @throws IOException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws JsonConversionException
	 */
	public <T> Object invoke(String baseUrl, Object[] args)
			throws IOException, InterruptedException, ExecutionException,
			IllegalAccessException, IllegalArgumentException, JsonConversionException {

		Request request = new Request();
		String fullUrl = baseUrl + requestPath;
		List<NameValuePair> paramsList = new ArrayList<NameValuePair>();

		request.setHttpRequestType(requestType);
		request.setFullUrl(fullUrl);

		// have placeholder parameters like http://example.org/user/{id}
		if (placeholderParms.size() > 0) {
			fullUrl = parsePlaceholderParams(fullUrl, placeholderParms, args);
		}

		// have object as query parameter @QueryParamsWith
		if (haveQueryParamObject) {
			Logger.log("[Request] haveQueryParamObject=" + haveQueryParamObject);
			paramsList.addAll(parseQueryParamsWith(args[indexOfQueryParamObject]));
		}

		// have query parameter values @QueryParam convert [param=value]
		if (queryParams.size() > 0) {
			Logger.log("[Request] queryParams: " + queryParams.size() + " found");
			paramsList.addAll(parseQueryParams(queryParams, args));
		}

		// have headers parameter @Header
		if (headerParams.size() > 0) {
			request.setHeaders(parseHeaderParams(headerParams, args));
		}

		// does request have body and its http post
		if (requestType == RequestType.POST && bodyParams.size() > 0) {
			request.setBodyParams(parsePostBodyFieldParams(bodyParams, args));
		}

		// set query param list
		request.setQueryParams(paramsList);

		return makeAsyncRequest(request);
	}
}
