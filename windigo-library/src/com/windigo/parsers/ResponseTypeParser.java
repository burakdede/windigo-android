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
package com.windigo.parsers;

import java.lang.reflect.Type;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;


/**
 * @author burakdede
 * @param <T>
 *
 * Parser class for response type
 */
public class ResponseTypeParser<T> {
	
	private static final String TAG = ResponseTypeParser.class.getCanonicalName();
	
	private Gson gson;
	
	private Type responseType; 
	
	/**
	 * Init new repsonse type parse with given return type
	 * 
	 * @param responseType
	 * @return {@link Type}
	 */
	public ResponseTypeParser(Type responseType) {
		this.gson = new Gson();
		this.responseType = responseType;
	}

	/**
	 * Convert json string response data to model object
	 * 
	 * @param jsonResponse
	 * @throws JsonSyntaxException
	 * @return {@link Type}
	 */
	public T parse(String jsonResponse) throws JsonSyntaxException {
		Log.d(TAG, "Parser is expecting of type " + responseType.getClass());
		return gson.fromJson(jsonResponse, responseType);
	}
}
