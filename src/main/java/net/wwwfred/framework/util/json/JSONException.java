package net.wwwfred.framework.util.json;

@SuppressWarnings("serial")
public class JSONException extends RuntimeException{

	public JSONException() {
		super();
	}

	public JSONException(String message, Throwable cause) {
		super(message, cause);
	}

	public JSONException(String message) {
		super(message);
	}

	public JSONException(Throwable cause) {
		super(cause);
	}
	
}
