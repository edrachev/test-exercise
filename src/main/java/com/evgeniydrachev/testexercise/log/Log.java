package com.evgeniydrachev.testexercise.log;

import com.evgeniydrachev.testexercise.Request;

import java.time.Instant;

public class Log {
    private final String text;
    private final String from;
    private final String to;
    private final Instant when;
    private final String ip;

    public Log(Request request, String ip) {
        this.text = request.getText();
        this.from = request.getFrom();
        this.to = request.getTo();
        this.when = Instant.now();
        this.ip = ip;
    }

    public Log(String text, String from, String to, Instant when, String ip) {
        this.text = text;
        this.from = from;
        this.to = to;
        this.when = when;
        this.ip = ip;
    }

    public String getText() {
        return text;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public Instant getWhen() {
        return when;
    }

    public String getIp() {
        return ip;
    }
}
