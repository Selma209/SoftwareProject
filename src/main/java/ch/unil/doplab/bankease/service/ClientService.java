package ch.unil.doplab.bankease.service;

import ch.unil.doplab.bankease.domain.Client;

public interface ClientService {

    Client register(String username,
                    String password,
                    String firstName,
                    String lastName,
                    String email,
                    String phoneNumber);

    Client findByUsername(String username);

    Client validateLogin(String username, String password);
}
