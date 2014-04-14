/**
 * 
 */
package com.windigo.http;

import java.util.ArrayList;
import java.util.List;

/**
 * @author burakdede
 * 
 * Generic key-value based header implementation
 * for all http clients
 * 
 */
public class Header {

	private String name;
	
	private String value;
	
	public Header(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}
	
	
	public static List<Header> convertFromApacheHeaders(org.apache.http.Header[] headers) {
		
		List<Header> headerList = new ArrayList<Header>();
		
		if (headers != null) {
			for (int i = 0; i < headers.length; i++) {
				org.apache.http.Header header = headers[i];
				headerList.add(new Header(header.getName(), header.getValue()));
			}	
		}
		
		
		return headerList;
		
	}
	
	
	public static org.apache.http.Header[] convertToApacheHeaders(List<Header> headers) {
		
		if (headers != null) {
			return headers.toArray(new org.apache.http.Header[headers.size()]);
		}
		
		return null; 

	}
}
