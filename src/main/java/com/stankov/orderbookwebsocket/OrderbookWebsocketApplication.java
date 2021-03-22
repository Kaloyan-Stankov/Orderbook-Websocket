package com.stankov.orderbookwebsocket;

import com.stankov.orderbookwebsocket.models.Bitfinex;
import com.stankov.orderbookwebsocket.models.Kraken;
import com.stankov.orderbookwebsocket.models.Orderbook;
import com.stankov.orderbookwebsocket.websocket.ExchangeWs;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


@SpringBootApplication
public class OrderbookWebsocketApplication {

	public static void main(String[] args) throws InterruptedException {
        ApplicationContext ctxt = SpringApplication.run(OrderbookWebsocketApplication.class, args);

		Orderbook orderbook = ctxt.getBean(Orderbook.class);
		Bitfinex bitfinex = ctxt.getBean(Bitfinex.class);
		Kraken kraken = ctxt.getBean(Kraken.class);

		orderbook.getExchanges().add(bitfinex);
		orderbook.getExchanges().add(kraken);

		ExchangeWs exchangeWs = new ExchangeWs();

		exchangeWs.connect(bitfinex, orderbook);
		exchangeWs.connect(kraken, orderbook);

		Thread.sleep(10000);
	}

}
