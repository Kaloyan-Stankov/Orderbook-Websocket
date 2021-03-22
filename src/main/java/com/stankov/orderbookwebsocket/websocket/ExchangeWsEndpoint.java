package com.stankov.orderbookwebsocket.websocket;

import com.stankov.orderbookwebsocket.models.Exchange;
import com.stankov.orderbookwebsocket.models.Orderbook;
import com.stankov.orderbookwebsocket.utils.Constants;
import com.stankov.orderbookwebsocket.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

@ClientEndpoint
public class ExchangeWsEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeWsEndpoint.class);
    public static final String BS = "bs";
    public static final String AS = "as";
    public static final String A = "a";
    public static final String B = "b";

    private Session userSession = null;

    private Exchange exchange;

    private Orderbook orderbook;

    public ExchangeWsEndpoint(String uri, Exchange exchange, Orderbook orderbook) {
        this.exchange = exchange;
        this.orderbook = orderbook;

        WebSocketContainer container =
                ContainerProvider.getWebSocketContainer();
        try {
            this.userSession=container.connectToServer(this,new URI(uri));
        }
        catch (URISyntaxException ex){
            LOGGER.error("URI syntax exception: "+ex.getMessage());
        }
        catch (DeploymentException ex){
            LOGGER.error("DeploymentException: "+ex.getMessage());
        }
        catch (IOException ex){
            LOGGER.error("IOException: "+ex.getMessage() );
        }
    }

    @OnOpen
    public void myOnOpen (Session session) {

        try {
            this.userSession=session;
        }
        catch (Exception ex){
            LOGGER.error("myOnOpen: "+ ex.getMessage());
        }
    }

    @OnError
    public void onError(final Session session, final Throwable t) {
        LOGGER.error("OnError called websocket: "+ t.getMessage());
        this.userSession=null;
    }

    @OnMessage
    public void myOnMessage (String message) {
        if(message.startsWith("{")){
            LOGGER.info("channel subscribed. Message:" + message);
        }
        else if(message.startsWith("[")){
            addOrderBook(message);
        }
    }

    private void addOrderBook(String message) {

        switch (this.exchange.getUrl()) {
            case Constants
                    .BITFINEX_WS_URL:
                JSONArray array= new JSONArray(message).getJSONArray(1);

                if (array.length() > 3) {
                    this.exchange.setAsks(Utils.jsonArrayToMap(array,true));
                    this.exchange.setBids(Utils.jsonArrayToMap(array, false));
                } else {
                    Double price = array.getDouble(0);
                    Integer count = array.getInt(1);
                    Double amount = array.getDouble(2);

                    if (amount > 0) {
                        if (count == 0) {
                            this.exchange.getBids().remove(price);
                        } else {
                            this.exchange.getBids().put(price, (count*amount));
                        }
                    } else {
                        if (count == 0) {
                            this.exchange.getAsks().remove(price);
                        } else {
                            this.exchange.getAsks().put(price, (count*amount));
                        }
                    }
                }
                break;
            case Constants.KRAKEN_WS_URL:
                JSONObject krakenArray= new JSONArray(message).getJSONObject(1);
                if (krakenArray.has(AS) && krakenArray.has(BS)) {
                    JSONArray asks = krakenArray.getJSONArray(AS);
                    JSONArray bids = krakenArray.getJSONArray(BS);
                    exchange.setAsks(Utils.jsonArrayToMap2(asks));
                    exchange.setBids(Utils.jsonArrayToMap2(bids));
                } else if (krakenArray.has(A)) {
                    JSONArray asks = krakenArray.getJSONArray(A);
                    Map<Double, Double> map = Utils.jsonArrayToMap2(asks);
                    map.entrySet().stream().forEach(e -> {
                        if (e.getValue() > 0) {
                            if (exchange.getAsks().containsKey(e.getKey())) {
                                exchange.getAsks().put(e.getKey(), exchange.getAsks().get(e.getKey()) + e.getValue());
                            } else {
                                exchange.getAsks().put(e.getKey(), e.getValue());
                            }
                        } else {
                            exchange.getAsks().remove(e.getKey());
                        }
                    });
                } else if (krakenArray.has(B)) {
                    JSONArray asks = krakenArray.getJSONArray(B);
                    Map<Double, Double> map = Utils.jsonArrayToMap2(asks);
                    map.entrySet().stream().forEach(e -> {
                        if (e.getValue() > 0) {
                            if (exchange.getBids().containsKey(e.getKey())) {
                                exchange.getBids().put(e.getKey(), exchange.getBids().get(e.getKey()) + e.getValue());
                            } else {
                                exchange.getBids().put(e.getKey(), e.getValue());
                            }
                        } else {
                            exchange.getBids().remove(e.getKey());
                        }
                    });
                }

                break;
        }

        orderbook.update();
    }

    @OnClose
    public void myOnClose (CloseReason reason) {
        LOGGER.error("Closing a WebSocket due to "+reason.getReasonPhrase());
        if(userSession == null)return;
        try {
            userSession.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Socket closed"));
        } catch (Throwable e) {
        }
        userSession = null;
    }

    public void sendMessage(String text){
        try {
            this.userSession.getAsyncRemote().sendText(text);
        }catch (Exception ex){
            userSession=null;
            LOGGER.info("exception occurred at send message: " + ex.getMessage());
        }
    }

    public Exchange getExchange() {
        return exchange;
    }

    public void setExchange(Exchange exchange) {
        this.exchange = exchange;
    }
}
