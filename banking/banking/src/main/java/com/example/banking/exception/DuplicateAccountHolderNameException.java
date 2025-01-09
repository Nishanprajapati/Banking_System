package com.example.banking.exception;

public class DuplicateAccountHolderNameException extends RuntimeException{
    public DuplicateAccountHolderNameException(String message) {
        super(message);
    }
}
