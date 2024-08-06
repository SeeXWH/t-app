package com.example.t_app.exeptions;


import org.springframework.web.client.RestClientException;

public class TranslationServiceException extends RestClientException {
    public TranslationServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
