package br.com.server.utils;

import javax.ws.rs.sse.SseEventSink;

public interface ResourceTimeoutManagerDelegate {
    public void notifyClient(SseEventSink clientSink, String message);
    public void removeActualClient();
    public void releaseResource();
}
