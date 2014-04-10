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

import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 * @author burakdede
 * 
 * Factory class to generated default apache http client
 * and set some default configurations
 *
 */
public class HttpClientFactory {
	
	private static final int TIMEOUT = 60 * 1000;

	
	public static HttpClient getDefaultHttpClient() {
		return new HttpClient(createHttpClient());
	}
	
	
	/**
	 * Create {@link DefaultHttpClient} with set of schemes, without redirect and
	 * support for http and https schemes.
	 * 
	 * @return {@link DefaultHttpClient}
	 */
	private static final DefaultHttpClient createHttpClient() {
        // Sets up the http part of the service.
        final SchemeRegistry supportedSchemes = new SchemeRegistry();

        // Register the "http" protocol scheme, it is required
        // by the default operator to look up socket factories.
        final SocketFactory sf = PlainSocketFactory.getSocketFactory();
        supportedSchemes.register(new Scheme("http", sf, 80));
        supportedSchemes.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        
        // Set some client http client parameter defaults.
        final HttpParams httpParams = createHttpParams();
        HttpClientParams.setRedirecting(httpParams, false);

        final ClientConnectionManager ccm = new ThreadSafeClientConnManager(httpParams,
                supportedSchemes);
        return new DefaultHttpClient(ccm, httpParams);
	}
	
	
	/**
	 * Create {@link HttpParams} for {@link DefaultHttpClient}
	 * 
	 * @return {@link HttpParams}
	 */
	private final static HttpParams createHttpParams() {
		final HttpParams params = new BasicHttpParams();
		
		HttpConnectionParams.setStaleCheckingEnabled(params, false);
		HttpConnectionParams.setConnectionTimeout(params, TIMEOUT);
		HttpConnectionParams.setSoTimeout(params, TIMEOUT);
		HttpConnectionParams.setSocketBufferSize(params, 8192);
		
		return params;
	}
}
