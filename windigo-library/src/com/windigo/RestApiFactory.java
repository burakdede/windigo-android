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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;

import com.windigo.annotations.Get;
import com.windigo.annotations.Post;
import com.windigo.annotations.RestApi;
import com.windigo.http.BaseHttpClient;
import com.windigo.http.ApacheHttpClient;

/**
 * @author burakdede
 * 
 *  Factory class to generate new api and setup proxy instance
 *  
 */
public class RestApiFactory {
	
	private static final ConcurrentHashMap<String, Object> cachedServices = 
			new ConcurrentHashMap<String, Object>();
	private static final ConcurrentHashMap<Method, RestMethodMetaDataCache> cachedMethodMetaData = 
			new ConcurrentHashMap<Method, RestMethodMetaDataCache>();

	@SuppressWarnings("unchecked")
	public static <T> T createNewService(String apiEndpointUrl, Class<T> restServiceClass, BaseHttpClient client) {
		T service;
		
		if (restServiceClass.isAnnotationPresent(RestApi.class)) {
			String restClassName = restServiceClass.getName();
			service = (T) cachedServices.get(restClassName);
			
			if (service == null) {
				service = (T) Proxy.newProxyInstance(restServiceClass.getClassLoader(), new Class[] { restServiceClass }, new RestServiceInvocationHandler(apiEndpointUrl));
				T found = (T) cachedServices.putIfAbsent(restClassName, service);
                if (found != null) {
                    service = found;
                }
				setupMethodMetaDataCache(restServiceClass, client);
			}
			
		} else {
			throw new IllegalArgumentException(restServiceClass + " missing @RestApi annotation.");
		}
		
		return service;
	}
	
	
	/**
	 * Initialize method meta data cache for every method
	 * 
	 * @param restServiceClass {@link T}
	 * @param client {@link ApacheHttpClient}
	 */
	private static void setupMethodMetaDataCache(Class<?> restServiceClass, BaseHttpClient httpClient) {
		for (Method method : restServiceClass.getMethods()) {
			if (method.isAnnotationPresent(Get.class) || method.isAnnotationPresent(Post.class)) {
				cachedMethodMetaData.putIfAbsent(method, new RestMethodMetaDataCache(method, httpClient));
			}
		}
	}
	
	
	/**
	 * Handle all the calls to rest service class 
	 *
	 */
	private static class RestServiceInvocationHandler implements InvocationHandler {
		
		private String baseUrl;
		
		public RestServiceInvocationHandler(String baseUrl) {
			this.baseUrl = baseUrl; 
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			
			RestMethodMetaDataCache methodMetaDataCache = cachedMethodMetaData.get(method);
			try {
				if (methodMetaDataCache != null) {
					return methodMetaDataCache.invoke(baseUrl, args);
				} else {
					return method.invoke(this, args);	
				}
			} catch (InvocationTargetException e) {
				throw e.getCause();
			}
		}
	}
	
}
