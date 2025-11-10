package com.bankease.boundary;

import ch.unil.doplab.bankease.store.InMemoryStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
@Path("/accounts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {

    @POST
    public Response create(Map<String, Object> body) {
        String clientId = (String) body.get("clientId");
        String type = (String) body.getOrDefault("type", "CURRENT");
        String accountNumber = "ACC-" + (int) (Math.random() * 900 + 100);

        InMemoryStore.create(accountNumber);

        return Response.status(Response.Status.CREATED)
                .entity(Map.of(
                        "status", "CREATED",
                        "clientId", clientId,
                        "type", type,
                        "accountNumber", accountNumber,
                        "balance", InMemoryStore.get(accountNumber)
                ))
                .build();
    }

    @GET
    @Path("/{accountNumber}/balance")
    public Response getBalance(@PathParam("accountNumber") String accountNumber) {
        if (!InMemoryStore.exists(accountNumber)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Account not found", "accountNumber", accountNumber))
                    .build();
        }
        return Response.ok(Map.of(
                "accountNumber", accountNumber,
                "balance", InMemoryStore.get(accountNumber)
        )).build();
    }

    @DELETE
    @Path("/{accountNumber}")
    public Response deleteAccount(@PathParam("accountNumber") String accountNumber) {
        if (!InMemoryStore.exists(accountNumber)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Account not found", "accountNumber", accountNumber))
                    .build();
        }
        InMemoryStore.remove(accountNumber);
        return Response.ok(Map.of(
                "status", "DELETED",
                "accountNumber", accountNumber,
                "message", "Account successfully removed"
        )).build();
    }

    @GET
    public Response getAllAccounts() {
        // Évite les soucis de typage avec Map.of (...) en forçant HashMap<String,Object>
        List<Map<String, Object>> list = InMemoryStore.all().entrySet().stream()
                .map(e -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("accountNumber", e.getKey());
                    m.put("balance", e.getValue());
                    return m;
                })
                .toList();
        return Response.ok(list).build();
    }
}
