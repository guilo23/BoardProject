package com.dio.board.persistence.dto;

import java.time.OffsetDateTime;

public record CardDetails(Long id,
                          String title,
                          String description,
                          boolean blocked,
                          OffsetDateTime blockedAt,
                          String blockReason,
                          int blockAmount,
                          Long columnId,
                          String columnName
) {
}
