package br.com.server.exception;

public class SystemException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SystemException(String message) {
		super(message);
	}
}
