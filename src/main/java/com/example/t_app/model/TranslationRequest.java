package com.example.t_app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class TranslationRequest {
    @Id
    private Long id;
    private String clientIp;
    private String sourceText;
    private String sourceLanguage;
    private String targetLanguage;
    private String translatedText;

    public TranslationRequest() {
    }

    public TranslationRequest(String clientIp, String sourceText, String sourceLanguage, String targetLanguage, String translatedText) {
        this.clientIp = clientIp;
        this.sourceText = sourceText;
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.translatedText = translatedText;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getSourceText() {
        return sourceText;
    }

    public void setSourceText(String sourceText) {
        this.sourceText = sourceText;
    }

    public String getSourceLanguage() {
        return sourceLanguage;
    }

    public void setSourceLanguage(String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }

    public String getTargetLanguage() {
        return targetLanguage;
    }

    public void setTargetLanguage(String targetLanguage) {
        this.targetLanguage = targetLanguage;
    }

    public String getTranslatedText() {
        return translatedText;
    }

    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TranslationRequest that = (TranslationRequest) o;
        return Objects.equals(id, that.id) && Objects.equals(clientIp, that.clientIp) && Objects.equals(sourceText, that.sourceText) && Objects.equals(sourceLanguage, that.sourceLanguage) && Objects.equals(targetLanguage, that.targetLanguage) && Objects.equals(translatedText, that.translatedText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, clientIp, sourceText, sourceLanguage, targetLanguage, translatedText);
    }

    @Override
    public String toString() {
        return "TransationRequest{" +
                "id=" + id +
                ", clientIp='" + clientIp + '\'' +
                ", sourceText='" + sourceText + '\'' +
                ", sourceLanguage='" + sourceLanguage + '\'' +
                ", targetLanguage='" + targetLanguage + '\'' +
                ", translatedText='" + translatedText + '\'' +
                '}';
    }
}
