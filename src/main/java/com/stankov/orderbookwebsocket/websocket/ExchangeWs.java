package com.stankov.orderbookwebsocket.websocket;

import com.stankov.orderbookwebsocket.models.Exchange;
import com.stankov.orderbookwebsocket.models.Orderbook;
import com.stankov.orderbookwebsocket.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExchangeWs {

    private final static Logger logger = LoggerFactory.getLogger(ExchangeWs.class);

    public void connect(Exchange exchange, Orderbook orderbook){
        logger.info("Connecting to: " + exchange.getUrl());
        try {
            ExchangeWsEndpoint clientEndPoint = new ExchangeWsEndpoint(exchange.getUrl(),exchange, orderbook);
            subscribeChannels(clientEndPoint);
        }
        catch (Exception e){
            logger.info("Error on connecting to Websocket . " + e.getMessage());
        }
    }

    public void subscribeChannels(ExchangeWsEndpoint clientEndPoint){

        String payload= Utils.getSubscriberMessage(clientEndPoint.getExchange().getUrl());
        logger.info("Sending message to " + clientEndPoint.getExchange().getUrl() + ", payload: " + payload);
        clientEndPoint.sendMessage(payload);
    }

}
