package com.dio.board.persistence.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static com.dio.board.persistence.entity.BoardUnlockKindEnum.CANCEL;

@Data
public class Board {
    private Long id;
    private String name;
    private List<BoardColumnEntity> boardColumnEntities = new ArrayList<>();

    public BoardColumnEntity getinitialColumn() {
        return boardColumnEntities.stream().filter(bc -> bc.getKind()
                .equals(BoardUnlockKindEnum.INITIAL)).findFirst().orElseThrow();
    }
    public BoardColumnEntity getCancelColumn(){
        return getFilteredColumn(bc -> bc.getKind().equals(CANCEL));
    }
    private BoardColumnEntity getFilteredColumn(Predicate<BoardColumnEntity> filter){
        return boardColumnEntities.stream()
                .filter(filter)
                .findFirst().orElseThrow();
    }
}
