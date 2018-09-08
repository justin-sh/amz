package com.lu.justin.tool.file.service;

class StorageException extends RuntimeException {

    StorageException(String message) {
        super(message);
    }

    StorageException(String message, Exception rootCause) {
        super(message, rootCause);
    }
}
