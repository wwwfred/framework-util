package net.wwwfred.framework.util.secret;

public class SecretException extends RuntimeException {

	private static final long serialVersionUID = -7378635579134212442L;

	private static final String _code = "500";
	private static final String _message="后台系统处理异常";
	private String message;
	private String code;
	public SecretException() {
		this(null,null,null);
	}
	
	public SecretException(String message, Throwable cause) {
		this(null,message,cause);
	}

	
	public SecretException(String message) {
		this(null,message,null);
	}

	public SecretException(Throwable cause) {
		this(null,null,cause);
	}
	
	public SecretException(String code,String message)
    {
	    this(code,message,null);
    }
	
	public SecretException(String code,String message,Throwable cause)
	{
	    super(message,cause);
	    this.code = (code==null||"".equals(code.trim()))?_code:code;
	    this.message = (message==null||"".equals(message.trim()))?_message:message;
	}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
	
}
