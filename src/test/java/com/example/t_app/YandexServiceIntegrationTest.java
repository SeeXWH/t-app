package com.example.t_app;


import com.example.t_app.exeptions.TranslationServiceException;
import com.example.t_app.service.YandexServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class YandexSerrviceImplTest {

    @Mock
    private RestTemplate restTemplate;

    private YandexServiceImpl yandexService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        yandexService = new YandexServiceImpl(restTemplate);
        yandexService.apiKey = "testApiKey";
        yandexService.translateUrl = "https://example.com/translate";
        yandexService.catalogId = "testCatalogId";
    }

    @Test
    void translateWord_Success() throws Exception {
        String text = "hello";
        String sourceLang = "en";
        String targetLang = "ru";
        String expectedTranslation = "привет";

        String responseJson = "{\"translations\":[{\"text\":\"" + expectedTranslation + "\"}]}";

        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseJson, HttpStatus.OK);
        when(restTemplate.exchange(any(URI.class), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        String actualTranslation = yandexService.translateWord(text, sourceLang, targetLang);

        assertEquals(expectedTranslation, actualTranslation);
    }

    @Test
    void translateWord_RestClientException_ThrowsTranslationServiceException() {
        String text = "hello";
        String sourceLang = "en";
        String targetLang = "ru";

        when(restTemplate.exchange(any(URI.class), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RestClientException("Test exception"));

        assertThrows(TranslationServiceException.class,
                () -> yandexService.translateWord(text, sourceLang, targetLang));
    }

    @Test
    void translateWord_InvalidJsonResponse_ReturnsOriginalText() throws Exception {
        String text = "hello";
        String sourceLang = "en";
        String targetLang = "ru";

        String responseJson = "{}"; // Invalid JSON response

        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseJson, HttpStatus.OK);
        when(restTemplate.exchange(any(URI.class), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        String actualTranslation = yandexService.translateWord(text, sourceLang, targetLang);

        assertEquals(text, actualTranslation); // Should return the original text
    }
}
