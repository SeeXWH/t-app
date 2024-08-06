package com.example.t_app.service;

import jakarta.servlet.http.HttpServletRequest;

public interface TranslationService {

    String translateText(String text, String sourceLang, String targetLang, HttpServletRequest request);

}
