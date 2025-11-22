package ch.unil.doplab.bankease.service.impl;

import ch.unil.doplab.bankease.domain.Client;
import ch.unil.doplab.bankease.exception.ApiException;
import ch.unil.doplab.bankease.service.ClientService;
import ch.unil.doplab.bankease.store.InMemoryStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@ApplicationScoped
@Named("clientService")
public class ClientServiceImpl implements ClientService {

    @Inject
    InMemoryStore store;

    @Override
    public Client register(String username,
                           String password,
                           String firstName,
                           String lastName,
                           String email,
                           String phoneNumber) {

        if (username == null || username.isBlank()) {
            throw new ApiException(400, "Username is required");
        }
        if (password == null || password.isBlank()) {
            throw new ApiException(400, "Password is required");
        }

        if (store.clients().containsKey(username)) {
            throw new ApiException(400, "Username already exists");
        }

        Client client = new Client(
                username,
                password,
                firstName,
                lastName,
                email,
                phoneNumber
        );

        // on stocke le client en mémoire
        store.clients().put(username, client);

        return client;
    }

    @Override
    public Client findByUsername(String username) {
        if (username == null) return null;
        return store.clients().get(username);
    }

    @Override
    public Client validateLogin(String username, String password) {
        Client c = findByUsername(username);
        if (c == null) return null;
        if (!c.getPassword().equals(password)) return null;
        return c;
    }
}
