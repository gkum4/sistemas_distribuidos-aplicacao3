package br.com.server.resources;

import java.util.*;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

import br.com.server.models.Test;
import br.com.server.models.ClientWaiting;
import br.com.server.utils.ResourceTimeoutManagerDelegate;
import br.com.server.utils.ResourceTimeoutManager;

@Path("/resource1")
@Singleton
public class Resource1Resource implements ResourceTimeoutManagerDelegate {
    private List<ClientWaiting> waitingList = new Vector<>();
    private Boolean resourceInUse = false;
    private Timer resourceTimer;
    final private long timeoutTime = 10000;

    @Context
    Sse sse;

    public Resource1Resource() {
        new Thread(notifyHandler).start();
    }

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
	@Path("{id}")
    public void register(final @Context SseEventSink clientSink, @PathParam("id") String id) {
        if (checkIfClientIsAlreadyRegistered(id)) {
            System.out.println("Cliente (id: " + id + ") já está registrado na lista de espera do RECURSO1.\n");
            return;
        }

        System.out.println("Cliente (id: " + id + ") foi registrado com sucesso na lista de espera do RECURSO1.\n");

        waitingList.add(new ClientWaiting(clientSink, id));
        String message = "[RECURSO1] waiting";
        notifyClient(clientSink, message);

        while(true) {
            if (clientSink.isClosed()) {
                return;
            }
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/deregister/{id}")
    public Response deregister(@PathParam("id") String id) {
        if (waitingList.size() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        ClientWaiting client = findClient(id);

        if (client == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (!client.id.equals(id)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        System.out.println("Cliente (id: " + id + ") liberou RECURSO1.\n");

        String message = "[RECURSO1] free";
        notifyClient(client.sink, message);
        client.sink.close();
        waitingList.remove(client);

        return Response.status(Response.Status.OK).build();
    }

    private Boolean checkIfClientIsAlreadyRegistered(String id) {
        ClientWaiting clientWaiting = findClient(id);
        
        if (clientWaiting != null) {
            return true;
        }

        return false;
    }

    private ClientWaiting findClient(String id) {
        ClientWaiting clientWaiting = waitingList
            .stream()
            .filter(client -> client.id.equals(id))
            .findAny()
            .orElse(null);
        
        return clientWaiting;
    }

    public void notifyClient(SseEventSink clientSink, String message) {
        clientSink.send(sse.newEvent(message));
    }

    public void releaseResource() {
        resourceInUse = false;
    }

    public void removeActualClient() {
        if (waitingList.size() == 0) {
            System.out.println("Error: trying to remove client that does not exist in RECURSO1 waiting list.");
            return;
        }
        waitingList.remove(0);
    }

    private Runnable notifyHandler = () -> {
        while (true) {
            if (resourceInUse) {
                ClientWaiting client = waitingList.get(0);

                if (client.sink.isClosed()) {
                    System.out.println("RECURSO1 foi liberado.\n");
                    removeActualClient();
                    resourceInUse = false;
                    resourceTimer.cancel();
                    continue;
                }

                continue;
            }

            if (waitingList.size() == 0) {
                continue;
            }
            
            ClientWaiting client = waitingList.get(0);
            String message = "[RECURSO1] available";

            try {
                System.out.println("RECURSO1 disponível para o cliente (id: " + client.id + ").\n");
                resourceInUse = true;
                notifyClient(client.sink, message);
                scheduleResourceTimeout(client.sink);
            } catch (Exception e) {
                System.out.println("Error notifying client about RECURSO1, " + e.getMessage());
            }
        }
    };

    private void scheduleResourceTimeout(SseEventSink clientSink) {
        resourceTimer = new Timer();
        resourceTimer.schedule(
            new ResourceTimeoutManager("1", resourceTimer, clientSink, this), 
            timeoutTime
        );
    }
}
