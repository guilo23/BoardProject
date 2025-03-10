package com.dio.board.persistence.dao;

import com.dio.board.persistence.dto.CardDetails;
import com.dio.board.persistence.entity.CardEntity;
import com.mysql.cj.jdbc.StatementImpl;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static com.dio.board.persistence.dao.converter.OffSetDataTimeConverter.toOffsetDateTime;
import static java.util.Objects.nonNull;

@AllArgsConstructor
public class CardDAO {
    private Connection connection;
    public CardEntity insert(final CardEntity entity)throws SQLException{
        var sql = " INSERT INTO CARDS (title,description,board_columns_id) VALUES (?,?,?);";
        try(var statement =connection.prepareStatement(sql)){
            var i =1;
            statement.setString(i++,entity.getTitle());
            statement.setString(i++,entity.getDescription());
            statement.setLong(i,entity.getBoardColumn().getId());
            statement.executeUpdate();
            if(statement instanceof StatementImpl impl){
                entity.setId(impl.getLastInsertID());
            }
        }
        return entity;
    }
    public void moveToColumn(final Long cardId, final Long columnId)throws SQLException{
        var sql = "UPDATE CARDS SET board_columns_id = ? WHERE id = ?";
        try(var statement = connection.prepareStatement(sql)){
            var i = 1;
            statement.setLong(i++,columnId);
            statement.setLong(i,cardId);
            statement.executeUpdate();
        }
    }

    public Optional<CardDetails> findById(final Long id)throws SQLException{
        var sql =
                """
                SELECT c.id,
                            c.title,
                            c.description,
                            b.blocked_at,
                            b.block_reason,
                            c.board_columns_id,
                            bc.name,
                            (SELECT COUNT(sub_b.id)
                                        FROM blocks sub_b
                                        WHERE sub_b.card_id = c.id) blocks_amount
                FROM cards c
                LEFT JOIN blocks b
                    ON c.id = b.card_id
                    AND b.unblocked_at IS NULL
                INNER JOIN BOARDS_COLUMNS bc
                    ON bc.id = c.board_columns_id
                WHERE c.id = ?;
                """;
        try(var statement = connection.prepareStatement(sql)){
            statement.setLong(1,id);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            if(resultSet.next()){
                var dto = new CardDetails(
                        resultSet.getLong("c.id"),
                        resultSet.getString("c.title"),
                        resultSet.getString("c.description"),
                        nonNull(resultSet.getString("b.block_reason")),
                        toOffsetDateTime(resultSet.getTimestamp("b.blocked_at")),
                        resultSet.getString("b.block_reason"),
                        resultSet.getInt("blocks_amount"),
                        resultSet.getLong("c.board_columns_id"),
                        resultSet.getString("bc.name")
                );
                return Optional.of(dto);
            }
        }
        return null;
    }
}
