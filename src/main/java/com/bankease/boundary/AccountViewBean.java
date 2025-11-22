package com.bankease.boundary;

import ch.unil.doplab.bankease.domain.Client;
import ch.unil.doplab.bankease.service.AccountService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Named
@RequestScoped
public class AccountViewBean implements Serializable {

    @Inject
    private AccountService accountService;

    @Inject
    private LoginBean loginBean;

    // Liste des comptes du client connecté
    public List<Map<String, Object>> getAccounts() {
        Client logged = loginBean.getLoggedClient();
        if (logged == null) {
            return List.of();
        }
        String clientId = logged.getUsername();
        return accountService.listAccounts(clientId);
    }


    public Map<String, Object> getSummary() {
        Client logged = loginBean.getLoggedClient();
        if (logged == null) {
            return Map.of();
        }
        String clientId = logged.getUsername();
        return accountService.totalBalance(clientId);
    }
}
