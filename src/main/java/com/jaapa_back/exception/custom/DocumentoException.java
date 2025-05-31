package com.jaapa_back.exception.custom;

public class DocumentoException extends RuntimeException {
    public DocumentoException(String message) {
        super(message);
    }
    public DocumentoException(String message, Throwable cause) {
        super(message, cause);
    }
}
