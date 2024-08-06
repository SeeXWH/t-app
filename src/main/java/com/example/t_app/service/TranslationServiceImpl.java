package com.example.t_app.service;

import com.example.t_app.model.TranslationRequest;
import com.example.t_app.repository.JdbcTranslationRequestDao;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;


@Service
public class TranslationServiceImpl implements TranslationService {

    private final JdbcTranslationRequestDao translationRequestDao;

    private final YandexService yandexService;

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Autowired
    public TranslationServiceImpl(JdbcTranslationRequestDao translationRequestDao, YandexService yandexService) {
        this.translationRequestDao = translationRequestDao;
        this.yandexService = yandexService;
    }

    @Override
    public String translateText(String text, String sourceLang, String targetLang, HttpServletRequest request) {
        String[] words = text.split(" ");
        List<CompletableFuture<String>> futureTranslations = new ArrayList<>();
        for (String word : words) {
            futureTranslations.add(CompletableFuture.supplyAsync(
                    () -> yandexService.translateWord(word, sourceLang, targetLang),
                    executorService
            ));
        }
        List<String> translatedWords = futureTranslations.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
        String translatedText = String.join(" ", translatedWords);
        String clientIp = request.getRemoteAddr();
        translationRequestDao.save(new TranslationRequest(clientIp, text, sourceLang, targetLang, translatedText));
        return translatedText;
    }

}
