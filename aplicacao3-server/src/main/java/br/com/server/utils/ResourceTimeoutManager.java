package br.com.server.utils;

import java.util.Timer;
import java.util.TimerTask;
import javax.ws.rs.sse.SseEventSink;

import br.com.server.utils.ResourceTimeoutManagerDelegate;

public class ResourceTimeoutManager extends TimerTask {
    String resourceNumber;
    Timer timer;
    SseEventSink clientSink;
    ResourceTimeoutManagerDelegate resource;

    public ResourceTimeoutManager(
        String resourceNumber,
        Timer timer, 
        SseEventSink clientSink, 
        ResourceTimeoutManagerDelegate resource
    ) {
        this.resourceNumber = resourceNumber;
        this.timer = timer;
        this.clientSink = clientSink;
        this.resource = resource;
    }

    public void run() {
        System.out.println("Tempo limite de liberação do RECURSO" + resourceNumber + " atingido.\n");
        String message = "[RECURSO" + resourceNumber + "] timeout";

        try {
            resource.notifyClient(clientSink, message);
            clientSink.close();
        } catch (Exception e) {
            System.out.println("Error in ResourceTimeoutManager, " + e.getMessage());
        }

        timer.cancel();
    }
}
