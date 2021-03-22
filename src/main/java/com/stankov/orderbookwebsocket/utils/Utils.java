package com.stankov.orderbookwebsocket.utils;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Utils {

    public static Map<Double, Double> jsonArrayToMap(JSONArray array, Boolean isBid) {
        Map<Double, Double> result = new HashMap<>();

        for (int i = 0; i < array.length(); i++) {
            JSONArray current = array.getJSONArray(i);
            Double price = current.getDouble(0);
            Integer count = current.getInt(1);
            Double amount = current.getDouble(2);

            if (isBid && amount > 0) {
                result.put(price, count * amount);
            } else if (!isBid && amount < 0) {
                result.put(price, Math.abs(count * amount));
            }
        }

        return result;
    }

    public static Map<Double, Double> jsonArrayToMap2(JSONArray array) {
        Map<Double, Double> result = new HashMap<>();

        for (int i = 0; i < array.length(); i++) {
            JSONArray current = array.getJSONArray(i);
            Double price = current.getDouble(0);
            Double volume = current.getDouble(1);

            result.put(price, volume);
        }

        return result;
    }

    public static Map<Double, Double> jsonSingleArrayToMap(JSONArray array) {
        Map<Double, Double> result = new HashMap<>();

        Double price = array.getDouble(0);
        Integer count = array.getInt(1);
        Double amount = array.getDouble(2);
        result.put(price, count * amount);

        return result;
    }

    public static String getSubscriberMessage(String exchangeUrl) {
        switch (exchangeUrl) {
            case Constants.BITFINEX_WS_URL:
                return getBitfinexSubscriberMessage();
            case Constants.KRAKEN_WS_URL:
                return getKrakenSubscriberMessage();
        }
        return null;
    }

    private static String getBitfinexSubscriberMessage() {
        JSONObject objectPayload=new JSONObject();
        objectPayload.put("event","subscribe");
        objectPayload.put("channel","book");
        objectPayload.put("symbol","tBTCUSD");
        objectPayload.put("prec","P0");
        objectPayload.put("freq","F0");
        objectPayload.put("len", 25);
        return objectPayload.toString();
    }

    private static String getKrakenSubscriberMessage() {
        JSONObject objectPayload=new JSONObject();
        objectPayload.put("event","subscribe");
        JSONArray pair = new JSONArray();
        pair.put("XBT/USD");
        objectPayload.put("pair", pair);
        JSONObject subscription = new JSONObject();
        subscription.put("name", "book");
        objectPayload.put("subscription", subscription);
        return objectPayload.toString();
    }
}
