package com.example.t_app.repository;



import com.example.t_app.model.TranslationRequest;
import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

@Repository
public class JdbcTranslationRequestDao implements TranslationRequestDao {

    @Autowired
    private DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public JdbcTranslationRequestDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    private void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS translation_requests (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "client_ip VARCHAR(255), " +
                "source_text VARCHAR(255), " +
                "source_language VARCHAR(2), " +
                "target_language VARCHAR(2), " +
                "translated_text VARCHAR(255))";

        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(sql);
            }
        } catch (SQLException ex) {
            throw new SQLException(ex);
        }
    }

    @Override
    public void save(@NotNull TranslationRequest request) {
        String sql = "INSERT INTO translation_requests (client_ip, source_text, source_language, target_language, translated_text) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, request.getClientIp());
            ps.setString(2, request.getSourceText());
            ps.setString(3, request.getSourceLanguage());
            ps.setString(4, request.getTargetLanguage());
            ps.setString(5, request.getTranslatedText());
            return ps;
        }, keyHolder);
        request.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
    }

    @Override
    public List<TranslationRequest> findAll() {
        String sql = "SELECT * FROM translation_requests";
        return jdbcTemplate.query(sql, new TranslationRequestRowMapper());
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE FROM translation_requests";
        jdbcTemplate.update(sql);
    }


}