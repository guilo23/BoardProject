package com.dio.board.service;

import com.dio.board.persistence.dao.CardDAO;
import com.dio.board.persistence.dto.CardDetails;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class CardQueryService {
    private final Connection  connection;

    public Optional<CardDetails> findById(Long id) throws SQLException {
        var dao = new CardDAO(connection);
        return dao.findById(id);
    }
}
