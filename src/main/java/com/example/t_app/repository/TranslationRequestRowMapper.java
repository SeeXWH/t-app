package com.example.t_app.repository;

import com.example.t_app.model.TranslationRequest;
import org.springframework.jdbc.core.RowMapper;

import java.sql.SQLException;
class TranslationRequestRowMapper implements RowMapper<TranslationRequest> {
    @Override
    public TranslationRequest mapRow(java.sql.ResultSet rs, int rowNum) throws SQLException {
        TranslationRequest request = new TranslationRequest();
        request.setId(rs.getLong("id"));
        request.setClientIp(rs.getString("client_ip"));
        request.setSourceText(rs.getString("source_text"));
        request.setSourceLanguage(rs.getString("source_language"));
        request.setTargetLanguage(rs.getString("target_language"));
        request.setTranslatedText(rs.getString("translated_text"));
        return request;
    }
}