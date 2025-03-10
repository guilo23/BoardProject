package com.dio.board.persistence.entity;

import java.util.stream.Stream;

public enum BoardUnlockKindEnum {
    INITIAL, FINAL, CANCEL, PENDING;

    public static BoardUnlockKindEnum findByName(final String name){
        return Stream.of(BoardUnlockKindEnum.values())
                .filter(b -> b.name().equals(name))
                .findFirst().orElseThrow();
    }
}
