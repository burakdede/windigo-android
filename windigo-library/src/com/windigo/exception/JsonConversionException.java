/**
 * 
 */
package com.windigo.exception;

/**
 * @author burakdede
 *
 */
public class JsonConversionException extends BaseException {

	private static final long serialVersionUID = 1L;

	public JsonConversionException(String message) {
		super(message);
	}
	
	public JsonConversionException(Throwable throwable) {
		super(throwable);
	}
}
