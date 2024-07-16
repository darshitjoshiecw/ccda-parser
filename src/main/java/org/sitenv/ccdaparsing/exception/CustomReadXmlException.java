package org.sitenv.ccdaparsing.exception;

public class CustomReadXmlException extends RuntimeException {
    private static final long serialVersionUID = -1594360240965172616L;
    public CustomReadXmlException(String message, Throwable e) {
        super(message,e);
    }
}
