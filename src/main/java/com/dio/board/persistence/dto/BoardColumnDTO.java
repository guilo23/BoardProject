package com.dio.board.persistence.dto;

import com.dio.board.persistence.entity.BoardUnlockKindEnum;

public record BoardColumnDTO(Long id, String name,
                             BoardUnlockKindEnum kind,
                             int cardsAmount) {
}
