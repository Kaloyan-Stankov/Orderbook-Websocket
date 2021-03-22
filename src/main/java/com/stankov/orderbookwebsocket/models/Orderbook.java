package com.stankov.orderbookwebsocket.models;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class Orderbook {

    private Set<Exchange> exchanges;

    private TreeMap<Double, Double> aggregatedBids;

    private  TreeMap<Double, Double> aggregatedAsks;

    public Orderbook() {
        this.exchanges = new HashSet<>();
        this.aggregatedAsks = new TreeMap<>();
        this.aggregatedBids = new TreeMap<>(Comparator.reverseOrder());
    }

    public Set<Exchange> getExchanges() {
        return exchanges;
    }

    public void setExchanges(Set<Exchange> exchanges) {
        this.exchanges = exchanges;
    }

    public TreeMap<Double, Double> getAggregatedBids() {
        return aggregatedBids;
    }

    public void setAggregatedBids(TreeMap<Double, Double> aggregatedBids) {
        this.aggregatedBids = aggregatedBids;
    }

    public TreeMap<Double, Double> getAggregatedAsks() {
        return aggregatedAsks;
    }

    public void setAggregatedAsks(TreeMap<Double, Double> aggregatedAsks) {
        this.aggregatedAsks = aggregatedAsks;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("asks:").append(System.getProperty("line.separator"));
        sb.append("[ ");
        this.aggregatedAsks.entrySet().stream().limit(10).forEach(e-> sb.append("[ " + e.getKey() + ", " + e.getValue() + " ],").append(System.getProperty("line.separator")));
        sb.setLength(sb.length() - 3);
        sb.append(" ]").append(System.getProperty("line.separator"));
        sb.append("best bid: [ ").append(this.aggregatedBids.firstKey() + ", " + this.aggregatedBids.firstEntry().getValue() + " ]").append(System.getProperty("line.separator"));
        sb.append("best ask: [ ").append(this.aggregatedAsks.firstKey() + ", " + this.aggregatedAsks.firstEntry().getValue() + " ]").append(System.getProperty("line.separator"));
        sb.append("bids:").append(System.getProperty("line.separator"));
        sb.append("[ ");
        this.aggregatedBids.entrySet().stream().limit(10).forEach(e-> sb.append("[ " + e.getKey() + ", " + e.getValue() + " ],").append(System.getProperty("line.separator")));
        sb.setLength(sb.length() - 3);
        sb.append(" ]").append(System.getProperty("line.separator"));
        sb.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        return sb.toString();
    }

    public void update () {
        TreeMap<Double, Double> bids = new TreeMap<>(Comparator.reverseOrder());
        TreeMap<Double, Double> asks = new TreeMap<>();

        for (Exchange exchange : this.exchanges) {
            if (null != exchange.getBids() && exchange.getBids().size() > 0 ) {
                exchange.getBids().keySet().forEach(key -> {
                    if (bids.containsKey(key)) {
                        bids.put(key, bids.get(key) + exchange.getBids().get(key));
                    } else {
                        bids.put(key, exchange.getBids().get(key));
                    }
                });
            }

            if (null != exchange.getAsks() && exchange.getAsks().size() > 0 ) {
                exchange.getAsks().keySet().forEach(key -> {
                    if (asks.containsKey(key)) {
                        asks.put(key, asks.get(key) + exchange.getAsks().get(key));
                    } else {
                        asks.put(key, exchange.getAsks().get(key));
                    }
                });
            }
        }

        this.aggregatedBids = bids;
        this.aggregatedAsks = asks;

        System.out.println(this.toString());
    }


}
