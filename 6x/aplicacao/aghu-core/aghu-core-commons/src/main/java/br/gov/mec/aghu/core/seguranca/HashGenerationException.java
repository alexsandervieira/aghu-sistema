package br.gov.mec.aghu.core.seguranca;

public class HashGenerationException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8761292289248321038L;

	public HashGenerationException() {
		super();
	}

	public HashGenerationException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public HashGenerationException(String message, Throwable cause) {
		super(message, cause);
	}

	public HashGenerationException(String message) {
		super(message);
	}

	public HashGenerationException(Throwable cause) {
		super(cause);
	}

}
