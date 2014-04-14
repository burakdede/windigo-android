/*
 * Copyright (C) 2013 Square, Inc.
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import com.squareup.okhttp.OkHttpClient;
import com.windigo.utils.GlobalSettings;

/**
 * @author burakdede
 *
 * Square's {@link OkHttpClient}
 * 
 */
public class OkClient extends HttpUrlConnectionClient {
	
	private final OkHttpClient client;
	
	private static OkHttpClient generateDefaultOkHttp() {
	    OkHttpClient client = new OkHttpClient();
	    client.setConnectTimeout(GlobalSettings.CONNNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
	    client.setReadTimeout(GlobalSettings.CONNECTION_READ_TIMEOUT, TimeUnit.MILLISECONDS);
	    return client;
	}
	
	public OkClient() {
		this(generateDefaultOkHttp());
	}
	
	public OkClient(OkHttpClient client) {
		this.client = client;
	}

	@Override
	protected HttpURLConnection openHttpURLConnection(Request request)
			throws MalformedURLException, IOException {
		return client.open(new URL(request.getFullUrl()));
	}
}
