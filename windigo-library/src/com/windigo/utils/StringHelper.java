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

import java.util.List;

/**
 * @author burakdede
 *
 */
public class StringHelper {
	
	public static final String EMPTY = "";

	/**
	 * Join list of string with given delimeter to form string
	 * @param stringList
	 * @param delimeter
	 * 
	 * @return {@link String}
	 */
	public static String join(List<String> stringList, String delimeter) {
		StringBuilder joinedString = new StringBuilder();
		
		if (stringList.size() > 0) {
			for (int i = 0; i < stringList.size(); i++) {
				joinedString.append(stringList.get(i).toString());
				if (i != stringList.size() -1) {
					joinedString.append(delimeter);	
				}
			}
		}
		
		return joinedString.toString();
		
	}
	
	
	/**
	 * join array of object with given delimeter to form string
	 * 
	 * @param iterable
	 * @param delimeter
	 * @return {@link String}
	 */
    public static String join(Object[] iterable, String delimeter) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < iterable.length; i++) {
            builder.append(iterable[i]);
            if (i != iterable.length - 1) {
                builder.append(delimeter);
            }
        }
        return builder.toString();
    }
}
