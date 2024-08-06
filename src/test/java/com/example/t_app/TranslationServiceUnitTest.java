package com.example.t_app;

import com.example.t_app.exeptions.TranslationServiceException;
import com.example.t_app.model.TranslationRequest;
import com.example.t_app.repository.JdbcTranslationRequestDao;
import com.example.t_app.service.TranslationServiceImpl;
import com.example.t_app.service.YandexService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TranslationServiceUnitTest {

    @Mock
    private JdbcTranslationRequestDao translationRequestDao;

    @Mock
    private YandexService yandexService;

    @Mock
    private HttpServletRequest request;

    private TranslationServiceImpl translationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        translationService = new TranslationServiceImpl(translationRequestDao, yandexService);
    }

    @Test
    void translateText_SingleWord() {
        String text = "Hello";
        String sourceLang = "en";
        String targetLang = "ru";
        String expectedTranslation = "Привет";
        String clientIp = "127.0.0.1";

        when(request.getRemoteAddr()).thenReturn(clientIp);
        when(yandexService.translateWord("Hello", sourceLang, targetLang)).thenReturn("Привет");

        String actualTranslation = translationService.translateText(text, sourceLang, targetLang, request);

        assertEquals(expectedTranslation, actualTranslation);

        verify(yandexService, times(1)).translateWord("Hello", sourceLang, targetLang);

        TranslationRequest expectedRequest = new TranslationRequest(clientIp, text, sourceLang, targetLang, expectedTranslation);
        verify(translationRequestDao, times(1)).save(expectedRequest);
    }

    @Test
    void translateText_MultipleWords() {
        String text = "Hello world, how are you";
        String sourceLang = "en";
        String targetLang = "ru";
        String expectedTranslation = "Здравствуйте мир, как являются ты";
        String clientIp = "127.0.0.1";

        when(request.getRemoteAddr()).thenReturn(clientIp);
        when(yandexService.translateWord("Hello", sourceLang, targetLang)).thenReturn("Здравствуйте");
        when(yandexService.translateWord("world,", sourceLang, targetLang)).thenReturn("мир,");
        when(yandexService.translateWord("how", sourceLang, targetLang)).thenReturn("как");
        when(yandexService.translateWord("are", sourceLang, targetLang)).thenReturn("являются");
        // Исправление: убираем лишний пробел перед "?"
        when(yandexService.translateWord("you", sourceLang, targetLang)).thenReturn("ты");

        String actualTranslation = translationService.translateText(text, sourceLang, targetLang, request);

        assertEquals(expectedTranslation, actualTranslation);

        // ... остальные проверки ...
    }



    @Test
    void translateText_TranslationError() {
        String text = "Hello world";
        String sourceLang = "en";
        String targetLang = "ru";
        String clientIp = "127.0.0.1";

        when(request.getRemoteAddr()).thenReturn(clientIp);
        when(yandexService.translateWord("Hello", sourceLang, targetLang))
                .thenThrow(new TranslationServiceException("Error translating word", new Throwable()));

        try {
            translationService.translateText(text, sourceLang, targetLang, request);
            fail("Expected TranslationServiceException to be thrown");
        } catch (CompletionException e) {
            // Проверяем, что причина исключения - TranslationServiceException
            assertTrue(e.getCause() instanceof TranslationServiceException);
            assertEquals("Error translating word", e.getCause().getMessage());
        }

        verify(translationRequestDao, never()).save(any(TranslationRequest.class));
    }
}