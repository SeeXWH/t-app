package com.example.t_app;



import com.example.t_app.model.TranslationRequest;
import com.example.t_app.repository.JdbcTranslationRequestDao;
import com.example.t_app.service.TranslationServiceImpl;
import com.example.t_app.service.YandexService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
class TranslationServiceImplTest {

    @Autowired
    private TranslationServiceImpl translationService;

    @MockBean
    private YandexService yandexService;

    @Autowired
    private JdbcTranslationRequestDao translationRequestDao;

    @BeforeEach
    void setUp() {
        translationRequestDao.deleteAll();
    }

    @Test
    void translateText_Success() throws Exception {
        String text = "Hello world";
        String sourceLang = "en";
        String targetLang = "ru";
        String expectedTranslation = "Привет мир";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");

        when(yandexService.translateWord("Hello", sourceLang, targetLang))
                .thenReturn("Привет");
        when(yandexService.translateWord("world", sourceLang, targetLang))
                .thenReturn("мир");

        String actualTranslation = translationService.translateText(text, sourceLang, targetLang, request);

        assertEquals(expectedTranslation, actualTranslation);

        TimeUnit.SECONDS.sleep(1);
        List<TranslationRequest> savedRequests = translationRequestDao.findAll();
        assertEquals(1, savedRequests.size());
        TranslationRequest savedRequest = savedRequests.get(0);
        assertEquals("127.0.0.1", savedRequest.getClientIp());
        assertEquals(text, savedRequest.getSourceText());
        assertEquals(sourceLang, savedRequest.getSourceLanguage());
        assertEquals(targetLang, savedRequest.getTargetLanguage());
        assertEquals(expectedTranslation, savedRequest.getTranslatedText());
    }
}
