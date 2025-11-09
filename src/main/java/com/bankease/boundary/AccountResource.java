package com.bankease.boundary;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import ch.unil.doplab.bankease.dto.CreateAccountRequest;

import java.util.Map;

@ApplicationScoped
@Path("/accounts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {

    @POST
    public Response create(@Valid CreateAccountRequest req) {
        // stub: simulate "account created"
        String accNumber = "ACC-" + (int)(Math.random() * 900 + 100);
        return Response.status(Response.Status.CREATED)
                .entity(Map.of(
                        "status", "CREATED",
                        "clientId", req.clientId(),
                        "type", String.valueOf(req.type()),
                        "accountNumber", accNumber
                ))
                .build();
    }
}
