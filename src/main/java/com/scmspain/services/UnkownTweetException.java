package com.scmspain.services;

/**
 * Created by xgimenez on 2/10/17.
 */
public class UnkownTweetException extends RuntimeException {

    private Long id;

    public UnkownTweetException(Long id) {
        this.id = id;
    }

    @Override
    public String getMessage() {
        return "Unknown tweet: " + id;
    }
}
