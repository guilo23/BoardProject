package com.dio.board.persistence.entity;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class BlockEntity {
    private Long id;
    private OffsetDateTime blockedAt;
    private String BlockedReason;
    private OffsetDateTime unlockedAt;
    private String UnlockedReason;


}
