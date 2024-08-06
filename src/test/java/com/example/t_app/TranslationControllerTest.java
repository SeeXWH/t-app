package com.example.t_app;
import com.example.t_app.exeptions.TranslationServiceException;
import com.example.t_app.service.TranslationService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestClientException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TranslationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TranslationService translationService;

    @Test
    public void testTranslateTextOverloaded() throws Exception {
        String text = "Hello world!";
        String sourceLang = "en";
        String targetLang = "ru";

        when(translationService.translateText(anyString(), anyString(), anyString(), any(HttpServletRequest.class)))
                .thenThrow(new TranslationServiceException("Translation service is overloaded.", new Throwable()));
        mockMvc.perform(MockMvcRequestBuilders.get("/translate")
                        .param("text", text)
                        .param("sourceLang", sourceLang)
                        .param("targetLang", targetLang)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isTooManyRequests())
                .andExpect(content().string("Translation service is overloaded. Please try again later."));
    }

    @Test
    public void testTranslateTextUnavailable() throws Exception {
        String text = "Hello world!";
        String sourceLang = "en";
        String targetLang = "ru";

        when(translationService.translateText(anyString(), anyString(), anyString(), any(HttpServletRequest.class)))
                .thenThrow(new TranslationServiceException("Translation service is unavailable.", new Throwable()));

        mockMvc.perform(MockMvcRequestBuilders.get("/translate")
                        .param("text", text)
                        .param("sourceLang", sourceLang)
                        .param("targetLang", targetLang)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().string("Translation service is temporarily unavailable. Please try again later."));
    }

    @Test
    public void testTranslateTextUnsupportedTargetLanguage() throws Exception {
        String text = "Hello world!";
        String sourceLang = "en";
        String targetLang = "invalid";

        when(translationService.translateText(anyString(), anyString(), anyString(), any(HttpServletRequest.class)))
                .thenThrow(new TranslationServiceException("unsupported target_language_code: invalid", new Throwable()));
        mockMvc.perform(MockMvcRequestBuilders.get("/translate")
                        .param("text", text)
                        .param("sourceLang", sourceLang)
                        .param("targetLang", targetLang)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("unsupported target language code"));
    }
}