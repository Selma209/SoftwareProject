package com.bankease.boundary;

import ch.unil.doplab.bankease.store.InMemoryStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

@ApplicationScoped
@Path("/transactions")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TransactionResource {

    // --------- DEPOSIT ----------
    @POST
    @Path("/deposit")
    public Response deposit(Map<String, Object> body) {
        String account = String.valueOf(body.get("accountNumber"));
        if (!InMemoryStore.exists(account)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Account not found", "accountNumber", account))
                    .build();
        }
        double amount = Double.parseDouble(body.get("amount").toString());
        double newBalance = InMemoryStore.deposit(account, amount);

        return Response.ok(Map.of(
                "status", "OK",
                "op", "deposit",
                "accountNumber", account,
                "amount", amount,
                "newBalance", newBalance
        )).build();
    }

    // --------- WITHDRAW ----------
    @POST
    @Path("/withdraw")
    public Response withdraw(Map<String, Object> body) {
        String account = String.valueOf(body.get("accountNumber"));
        if (!InMemoryStore.exists(account)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Account not found", "accountNumber", account))
                    .build();
        }
        double amount = Double.parseDouble(body.get("amount").toString());
        double current = InMemoryStore.get(account);

        if (amount > current) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                            "status", "REFUSED",
                            "reason", "Insufficient funds",
                            "balance", current,
                            "attemptedWithdraw", amount
                    )).build();
        }
        InMemoryStore.withdraw(account, amount);
        return Response.ok(Map.of(
                "status", "OK",
                "op", "withdraw",
                "accountNumber", account,
                "amount", amount,
                "newBalance", InMemoryStore.get(account)
        )).build();
    }

    // --------- TRANSFER ----------
    @POST
    @Path("/transfer")
    public Response transfer(Map<String, Object> body) {
        String from = String.valueOf(body.getOrDefault("fromAccount",
                body.getOrDefault("sourceAccount", body.get("from"))));
        String to = String.valueOf(body.getOrDefault("toAccount",
                body.getOrDefault("destinationAccount", body.get("to"))));
        double amount = Double.parseDouble(body.get("amount").toString());

        if (!InMemoryStore.exists(from) || !InMemoryStore.exists(to)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Source or destination account not found"))
                    .build();
        }
        double balanceFrom = InMemoryStore.get(from);
        if (balanceFrom < amount) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                            "status", "REFUSED",
                            "reason", "Insufficient funds in source account",
                            "balance", balanceFrom
                    )).build();
        }
        InMemoryStore.withdraw(from, amount);
        InMemoryStore.deposit(to, amount);

        return Response.ok(Map.of(
                "status", "OK",
                "op", "transfer",
                "fromAccount", from,
                "toAccount", to,
                "amount", amount,
                "newBalanceFrom", InMemoryStore.get(from),
                "newBalanceTo", InMemoryStore.get(to)
        )).build();
    }

    // --------- EXCHANGE (free plan: base EUR only) ----------
    // Compute CHF->{CURRENCY} = (EUR->{CURRENCY}) / (EUR->CHF)
    @GET
    @Path("/exchange/{currency}")
    public Response getExchangeRate(@PathParam("currency") String currency) {
        String target = currency.toUpperCase();
        String accessKey = System.getenv().getOrDefault(
                "EXCHANGERATES_API_KEY",
                "988d5995c35fa0902752097116adf294"
        );

        String apiUrl = "https://api.exchangeratesapi.io/v1/latest"
                + "?access_key=" + accessKey
                + "&symbols=CHF," + target; // base=EUR on free plan

        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            InputStream is = conn.getResponseCode() < 300
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            try (JsonReader jr = Json.createReader(is)) {
                JsonObject root = jr.readObject();
                if (root.containsKey("error")) {
                    return Response.status(Response.Status.BAD_GATEWAY)
                            .entity(Map.of("source", "https://api.exchangeratesapi.io/v1", "error", root.get("error")))
                            .build();
                }
                JsonObject rates = root.getJsonObject("rates");
                double eurToChf = rates.getJsonNumber("CHF").doubleValue();
                double eurToTarget = rates.getJsonNumber(target).doubleValue();
                double chfToTarget = eurToTarget / eurToChf;

                return Response.ok(Map.of(
                        "base", "CHF",
                        "currency", target,
                        "rate", chfToTarget,
                        "explanation", "rate = (EUR→" + target + ") / (EUR→CHF)",
                        "source", "https://api.exchangeratesapi.io/v1",
                        "date", root.getString("date", "")
                )).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    // --------- BALANCE helper ----------
    @GET
    @Path("/balance/{account}")
    public Response getBalance(@PathParam("account") String account) {
        if (!InMemoryStore.exists(account)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Account not found", "accountNumber", account))
                    .build();
        }
        return Response.ok(Map.of(
                "accountNumber", account,
                "balance", InMemoryStore.get(account)
        )).build();
    }
}
