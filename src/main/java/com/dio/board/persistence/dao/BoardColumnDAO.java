package com.dio.board.persistence.dao;

import com.dio.board.persistence.dto.BoardColumnDTO;
import com.dio.board.persistence.entity.BoardColumnEntity;
import com.dio.board.persistence.entity.BoardUnlockKindEnum;
import com.dio.board.persistence.entity.CardEntity;
import com.mysql.cj.jdbc.StatementImpl;
import lombok.AllArgsConstructor;

import java.beans.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.dio.board.persistence.entity.BoardUnlockKindEnum.findByName;

@AllArgsConstructor
public class BoardColumnDAO {
    private final Connection connection;

    public BoardColumnEntity insert(final BoardColumnEntity entity) throws SQLException {
        var sql = "INSERT INTO BOARDS_COLUMNS (name,`order`,kind,board_id) VALUES (?,?,?,?)";
        try (var statement = connection.prepareStatement(sql)) {
            var i = 1;
            statement.setString(i++, entity.getName());
            statement.setInt(i++, entity.getOrder());
            statement.setString(i++, entity.getKind().name());
            statement.setLong(i, entity.getBoard().getId());
            statement.executeUpdate();
            if (statement instanceof StatementImpl impl) {
                entity.setId(impl.getLastInsertID());
            }
            return entity;
        }
    }

    public List<BoardColumnEntity> findByBoardId(Long id) throws SQLException {
        List<BoardColumnEntity> entities = new ArrayList<>();
        var sql = "SELECT id, name, `order`, kind FROM BOARDS_COLUMNS WHERE board_id = ? ORDER BY `order`";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            while (resultSet.next()) {
                var entity = new BoardColumnEntity();
                entity.setId(resultSet.getLong("id"));
                entity.setName(resultSet.getString("name"));
                entity.setOrder(resultSet.getInt("order"));
                entity.setKind(findByName(resultSet.getString("kind")));
                entities.add(entity);
            }
            return entities;
        }
    }

    public List<BoardColumnDTO> findByBoardIdWithDetails(Long id) throws SQLException {
        List<BoardColumnDTO> dto = new ArrayList<>();
        var sql =
                """
                        SELECT bc.id,
                              bc.name,
                              bc.kind,
                        (SELECT COUNT( c.id)
                                FROM CARDS c
                                WHERE c.board_columns_id = bc.id) cards_amount
                        FROM BOARDS_COLUMNS bc
                        WHERE board_id = ?
                        ORDER BY `order`;
                        """;
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            while (resultSet.next()) {
                var dtos = new BoardColumnDTO(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        findByName(resultSet.getString("kind")),
                        resultSet.getInt("cards_amount")
                );
                dto.add(dtos);
            }
            return dto;
        }
    }

    public Optional<BoardColumnEntity> findById(Long boardId) throws SQLException {
        var sql =
                """
                        SELECT
                            bc.name,
                            bc.kind,
                            c.id,
                            c.title,
                            c.description
                         FROM BOARDS_COLUMNS bc
                         left JOIN CARDS c
                            ON c = bc.id
                         WHERE bc.id = ? 
           
                        """;
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, boardId);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            if (resultSet.next()) {
                var entity = new BoardColumnEntity();
                entity.setName(resultSet.getString("name"));
                entity.setKind(findByName(resultSet.getString("kind")));

                do {
                    var card = new CardEntity();
                    card.setId(resultSet.getLong("c.id"));
                    card.setTitle(resultSet.getString("c.title"));
                    card.setDescription(resultSet.getString("c.description"));
                    entity.getCards().add(card);
                } while (resultSet.next());
            }
        }
        return Optional.empty();
    }
}
