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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.windigo.exception.JsonConversionException;
import com.windigo.logging.Logger;
import com.windigo.utils.GlobalSettings;


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
	
	public T parse(InputStream is) throws JsonConversionException {
		Logger.log(ResponseTypeParser.class, "Parser is taking input stream and expecting of type " + 
					responseType.getClass());
		try {
			return gson.fromJson(new InputStreamReader(is, GlobalSettings.CHARSET), responseType);
		} catch (JsonIOException e) {
			throw new JsonConversionException(e);
		} catch (JsonSyntaxException e) {
			throw new JsonConversionException(e);
		} catch (UnsupportedEncodingException e) {
			throw new JsonConversionException(e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {}
			}
		}
		
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
