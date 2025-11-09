package com.bankease.boundary;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;

@ApplicationScoped
@Path("/transactions")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TransactionResource {

    @POST
    @Path("/deposit")
    public Response deposit(Map<String, Object> body) {
        return Response.ok(Map.of(
                "status", "OK",
                "op", "deposit",
                "clientId", body.get("clientId"),
                "accountNumber", body.get("accountNumber"),
                "amount", body.get("amount"),
                "description", body.get("description")
        )).build();
    }

    @POST
    @Path("/withdraw")
    public Response withdraw(Map<String, Object> body) {
        return Response.ok(Map.of(
                "status", "OK",
                "op", "withdraw",
                "clientId", body.get("clientId"),
                "accountNumber", body.get("accountNumber"),
                "amount", body.get("amount"),
                "description", body.get("description")
        )).build();
    }

    @POST
    @Path("/transfer")
    public Response transfer(Map<String, Object> body) {
        // on tolère plusieurs conventions de champs
        Object from = body.getOrDefault("fromAccount",
                        body.getOrDefault("sourceAccount", body.get("from")));
        Object to   = body.getOrDefault("toAccount",
                        body.getOrDefault("destinationAccount", body.get("to")));
        return Response.ok(Map.of(
                "status", "OK",
                "op", "transfer",
                "clientId", body.get("clientId"),
                "fromAccount", from,
                "toAccount", to,
                "amount", body.get("amount"),
                "description", body.get("description")
        )).build();
    }
}
