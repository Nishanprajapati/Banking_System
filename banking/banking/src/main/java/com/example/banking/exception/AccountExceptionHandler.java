package com.example.banking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class AccountExceptionHandler {


    @ExceptionHandler(value = {AccountNotFoundException.class})
    public ResponseEntity<Object> handleAccountNotFoundException (
            AccountNotFoundException accountNotFoundException){
        AccountException employeeException = new AccountException(
                accountNotFoundException.getMessage(),
                accountNotFoundException.getCause(),
                HttpStatus.NOT_FOUND
        );
        return new ResponseEntity<>(employeeException,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<Map<String, String>> handleMethodArgsNotValidException(MethodArgumentNotValidException ex){
        Map<String, String> resp = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError)error).getField();
            String Message = error.getDefaultMessage();
            resp.put(fieldName, Message);
        });
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(DuplicateAccountHolderNameException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateAccountHolderNameException(DuplicateAccountHolderNameException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT); // 409 Conflict
    }

}
