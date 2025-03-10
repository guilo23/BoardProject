package com.dio.board.persistence.dto;

import com.dio.board.persistence.entity.BoardUnlockKindEnum;

public record BoardColumnInfoDTO(Long id, int order, BoardUnlockKindEnum kind) {
}
