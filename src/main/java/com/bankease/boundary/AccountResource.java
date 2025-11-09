package com.bankease.boundary;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;

@ApplicationScoped
@Path("/accounts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {

    // --- EXISTING CREATE ENDPOINT ---
    @POST
    public Response create(Map<String, Object> body) {
        String clientId = (String) body.get("clientId");
        String type = (String) body.get("type");
        String accountNumber = "ACC-" + (int)(Math.random() * 900 + 100);

        return Response.status(Response.Status.CREATED)
                .entity(Map.of(
                        "status", "CREATED",
                        "clientId", clientId,
                        "type", type,
                        "accountNumber", accountNumber,
                        "balance", 0.0
                ))
                .build();
    }

    // --- 🔹 NEW GET: get the balance of one account ---
    @GET
    @Path("/{accountNumber}/balance")
    public Response getBalance(@PathParam("accountNumber") String accountNumber) {
        // In a real app you would query the DB; here we simulate.
        double balance = switch (accountNumber) {
            case "ACC-001" -> 1500.0;
            case "ACC-002" -> 800.0;
            default -> 0.0;
        };

        return Response.ok(Map.of(
                "accountNumber", accountNumber,
                "balance", balance
        )).build();
    }

    // --- 🔹 NEW DELETE: remove an account ---
    @DELETE
    @Path("/{accountNumber}")
    public Response deleteAccount(@PathParam("accountNumber") String accountNumber) {
        // Simulated deletion logic
        return Response.ok(Map.of(
                "status", "DELETED",
                "accountNumber", accountNumber,
                "message", "Account successfully removed"
        )).build();
    }

    // --- (Optionnel) GET all accounts for demo ---
    @GET
    public Response getAllAccounts() {
        return Response.ok(
                java.util.List.of(
                        Map.of("accountNumber", "ACC-001", "clientId", "C001", "balance", 1500.0),
                        Map.of("accountNumber", "ACC-002", "clientId", "C002", "balance", 900.0)
                )
        ).build();
    }
}
