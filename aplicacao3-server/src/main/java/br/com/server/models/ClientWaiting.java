package br.com.server.models;

import java.util.*;
import javax.ws.rs.sse.SseEventSink;

public class ClientWaiting {
    public SseEventSink sink;
    public String id;

    public ClientWaiting(SseEventSink sink, String id) {
        this.sink = sink;
        this.id = id;
    }
}
