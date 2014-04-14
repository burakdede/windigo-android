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
package com.windigo.logging;

import android.util.Log;

/**
 * @author burakdede
 *
 * Some logging utilities
 */
public class Logger {
	
	public static <T> void log(Class<T> instance, String message) {
		Log.d(instance.getCanonicalName(), message);
	}
	
	public static <T> void log(Class<T> instance, String message, Exception e) {
		Log.d(instance.getName(), message);
		e.printStackTrace();
	}
}
