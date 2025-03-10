package com.dio.board.persistence.exceptions;

public class CardBlockedException extends RuntimeException{
    public CardBlockedException(final String message){
        super(message);
    }
}
