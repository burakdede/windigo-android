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
package com.windigo.utils;

/**
 * @author burakdede
 * 
 * Global library related settings logging, profiling etc.
 */
public class GlobalSettings {
	public static boolean DEBUG = true;
	public static int CONNNECTION_TIMEOUT = 60 * 1000;
	public static int CONNECTION_READ_TIMEOUT = 60 * 1000;
	public final static String CHARSET = "utf-8"; 
}
