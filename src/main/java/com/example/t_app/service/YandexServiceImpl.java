package com.example.t_app.service;

import com.example.t_app.exeptions.TranslationServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
@Service
public class YandexServiceImpl implements YandexService {

    @Value("${yandex.translate.api.key}")
    public String apiKey;
    @Value("${yandex.translate.url}")
    public String translateUrl;
    @Value("${yandex.catalog.id}")
    public String catalogId;

    private final RestTemplate restTemplate;

    public YandexServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String translateWord(String text, String sourceLang, String targetLang) {
        try {
            URI uri = UriComponentsBuilder.fromUriString(translateUrl)
                    .queryParam("folderId", catalogId)
                    .build()
                    .toUri();

            String requestJson = String.format("{\"targetLanguageCode\":\"%s\",\"texts\":[\"%s\"],\"sourceLanguageCode\":\"%s\"}",
                    targetLang, text, sourceLang);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Api-Key " + apiKey);

            HttpEntity<String> requestEntity = new HttpEntity<>(requestJson, headers);

            ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseEntity.getBody());
            JsonNode translationsNode = rootNode.path("translations");
            if (translationsNode.isArray() && !translationsNode.isEmpty()) {
                return translationsNode.get(0).path("text").asText();
            } else {
                return text;
            }
        } catch (RestClientException e) {
            String errorMessage = "Error translating word '" + text + "': " + e.getMessage();
            throw new TranslationServiceException(errorMessage, e);
        } catch (JsonProcessingException e) {
            String errorMessage = "Error processing JSON response: " + e.getMessage();
            throw new TranslationServiceException(errorMessage, e);
        }
    }

}

