package com.dio.board.persistence.entity;

import lombok.Data;

@Data
public class CardEntity {
    private Long id;
    private String Title;
    private String description;
    private int order;
    private BoardUnlockKindEnum kind;
    private BoardColumnEntity boardColumn = new BoardColumnEntity();
}
