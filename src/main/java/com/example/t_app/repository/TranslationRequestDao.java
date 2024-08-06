package com.example.t_app.repository;

import com.example.t_app.model.TranslationRequest;

import java.util.List;

public interface TranslationRequestDao {

    void save(TranslationRequest request);

    List<TranslationRequest> findAll();

    void deleteAll();

}
