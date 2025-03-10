package com.dio.board.persistence.dao;

import com.dio.board.persistence.entity.Board;
import com.mysql.cj.jdbc.StatementImpl;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class BoardDAO {
    private final Connection connection;

    public Board insert(final Board entity) throws SQLException {
        var sql = "INSERT INTO BOARDS (name) values (?)";
        try(var statement = connection.prepareStatement(sql)){
            statement.setString(1, entity.getName());
            statement.executeUpdate();
            if(statement instanceof StatementImpl impl){
                entity.setId(impl.getLastInsertID());
            }

        }
        return entity;
    }
    public void delete(final Long id) throws SQLException {
       var sql = "DELETE FROM BOARDS WHERE id = ?";
        try(var statement = connection.prepareStatement(sql)){
            statement.setLong(1,id);
            statement.executeUpdate();
        }

    }
    public Optional<Board> findById(final Long id) throws SQLException {
        var sql ="SELECT id,name FROM BOARDS WHERE id = ?";
        try(var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            if(resultSet.next()){
                var entity = new Board();
                entity.setId(resultSet.getLong("id"));
                entity.setName(resultSet.getString("name"));
                return Optional.of(entity);

            }
            return Optional.empty();
        }
    }
    public Boolean exists(final Long id) throws SQLException{
        var sql ="SELECT 1 FROM BOARDS WHERE id = ?";
        try(var statement = connection.prepareStatement(sql)){
            statement.setLong(1,id);
            statement.executeQuery();
            return statement.getResultSet().next();
        }
    }
}
