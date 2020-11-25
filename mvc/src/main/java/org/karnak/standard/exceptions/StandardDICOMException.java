package org.karnak.standard.exceptions;

public abstract class StandardDICOMException extends RuntimeException {
    public StandardDICOMException(String message) {
        super(message);
    }
}
