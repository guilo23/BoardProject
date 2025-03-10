package com.dio.board.persistence.exceptions;

public class CardFinishedException extends RuntimeException{
    public CardFinishedException(final String message){
        super(message);
    }
}
