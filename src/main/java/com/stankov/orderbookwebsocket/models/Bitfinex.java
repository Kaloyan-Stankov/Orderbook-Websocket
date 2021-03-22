package com.stankov.orderbookwebsocket.models;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Bitfinex extends Exchange {

    public Bitfinex(@Value("${bitfinex.url}") String url) {
        super(url);
    }
}
