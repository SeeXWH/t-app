package com.example.t_app.controller;


import com.example.t_app.exeptions.TranslationServiceException;
import com.example.t_app.service.TranslationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class TranslationController {

    private final TranslationService translationService;


    @Autowired
    public TranslationController(TranslationService translationService) {
        this.translationService = translationService;
    }

    @GetMapping("/translate")
    public String translateText(
            @RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "sourceLang", required = false) String sourceLang,
            @RequestParam(value = "targetLang", required = false) String targetLang,
            HttpServletRequest request) {
        if (text == null & sourceLang == null & targetLang == null) {
            return "none of the parameters were passed";
        } else if (text == null) {
            return "the text was not transmitted";
        } else if (sourceLang == null | targetLang == null) {
            return "the language parameter was not transmitted";
        }
        return translationService.translateText(text, sourceLang, targetLang, request);
    }

    @ExceptionHandler(TranslationServiceException.class)
    public ResponseEntity<String> handleTranslationException(TranslationServiceException ex) {
        System.out.println(ex.getMessage());
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "An error occurred while processing your request.";
        if (ex.getMessage().contains("overloaded")) {
            status = HttpStatus.TOO_MANY_REQUESTS;
            message = "Translation service is overloaded. Please try again later.";
        } else if (ex.getMessage().contains("unavailable")) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
            message = "Translation service is temporarily unavailable. Please try again later.";
        }
        if (ex.getMessage().contains("unsupported target_language_code")) {
            status = HttpStatus.BAD_REQUEST;
            message = "unsupported target language code";
        } else if (ex.getMessage().contains("target_language_code must be set")) {
            status = HttpStatus.BAD_REQUEST;
            message = "target_language_code must be set";
        }
        return ResponseEntity.status(status).body(message);
    }


}