package com.stankov.orderbookwebsocket.models;

import java.util.Map;

abstract public class Exchange {

    protected String url;

    protected  Map<Double, Double> bids;

    protected  Map<Double, Double> asks;

    public Exchange(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<Double, Double> getBids() {
        return bids;
    }

    public void setBids(Map<Double, Double> bids) {
        this.bids = bids;
    }

    public Map<Double, Double> getAsks() {
        return asks;
    }

    public void setAsks(Map<Double, Double> asks) {
        this.asks = asks;
    }
}
