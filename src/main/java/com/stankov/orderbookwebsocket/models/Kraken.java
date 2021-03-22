package com.stankov.orderbookwebsocket.models;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Kraken extends Exchange {

    public Kraken(@Value("${kraken.url}")String url) {
        super(url);
    }
}
