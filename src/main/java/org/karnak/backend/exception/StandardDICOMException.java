package org.karnak.backend.exception;

public abstract class StandardDICOMException extends RuntimeException {
    public StandardDICOMException(String message) {
        super(message);
    }
}
