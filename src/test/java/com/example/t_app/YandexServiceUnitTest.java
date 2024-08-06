package com.example.t_app;


import com.example.t_app.exeptions.TranslationServiceException;
import com.example.t_app.service.YandexServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class YandexServiceUniTest {

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

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseJson);
        JsonNode translationsNode = rootNode.path("translations");
        when(restTemplate.exchange(any(URI.class), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(responseJson, HttpStatus.OK));

        String actualTranslation = yandexService.translateWord(text, sourceLang, targetLang);

        assertEquals(expectedTranslation, actualTranslation);
    }

    @Test
    void translateWord_RestClientException_ThrowsTranslationServiceException() {
        String text = "hello";
        String sourceLang = "en";
        String targetLang = "ru";

        when(restTemplate.exchange(any(URI.class), any(HttpMethod.class),
                any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RestClientException("Test exception"));

        assertThrows(TranslationServiceException.class,
                () -> yandexService.translateWord(text, sourceLang, targetLang));
    }

    @Test
    void translateWord_InvalidJsonResponse_ReturnsOriginalText() throws Exception {
        String text = "hello";
        String sourceLang = "en";
        String targetLang = "ru";

        String responseJson = "{}";

        when(restTemplate.exchange(any(URI.class), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(responseJson, HttpStatus.OK));

        String actualTranslation = yandexService.translateWord(text, sourceLang, targetLang);

        assertEquals(text, actualTranslation);
    }
}
