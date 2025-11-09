package com.bankease.boundary;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

@ApplicationScoped
@Path("/transactions")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TransactionResource {

    // ------------------------------
    // 🔹 POST /transactions/deposit
    // ------------------------------
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

    // ------------------------------
    // 🔹 POST /transactions/withdraw
    // ------------------------------
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

    // ------------------------------
    // 🔹 POST /transactions/transfer
    // ------------------------------
    @POST
    @Path("/transfer")
    public Response transfer(Map<String, Object> body) {
        // Tolerate multiple key conventions
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

    // -------------------------------------------------------
    // 🔹 NEW: GET /transactions/exchange/{currency}
    // Calls a real external API: https://api.exchangerate.host
    // -------------------------------------------------------
    @GET
    @Path("/exchange/{currency}")
    public Response getExchangeRate(@PathParam("currency") String currency) {
        try {
            // External API endpoint (no API key required)
            String apiUrl = "https://api.apilayer.com/exchangerates_data/latest?base=CHF&symbols="
                    + currency.toUpperCase()
                    + "xCZd3ALFLMKdpjDq6qgORAtq6qg9Dj9C";


            // Make HTTP connection
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            // Handle non-200 responses
            int status = conn.getResponseCode();
            if (status != 200) {
                return Response.status(Response.Status.BAD_GATEWAY)
                        .entity(Map.of(
                                "error", "External API call failed",
                                "status", status))
                        .build();
            }

            // Read JSON response from the API
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            reader.close();

            // Return result as JSON
            return Response.ok(Map.of(
                    "currency", currency.toUpperCase(),
                    "source", "https://api.exchangerate.host",
                    "data", jsonBuilder.toString()
            )).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
}
