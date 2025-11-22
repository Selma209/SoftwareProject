package ch.unil.doplab.bankease.service.impl;

import ch.unil.doplab.bankease.domain.*;
import ch.unil.doplab.bankease.dto.CreateAccountRequest;
import ch.unil.doplab.bankease.exception.ApiException;
import ch.unil.doplab.bankease.service.AccountService;
import ch.unil.doplab.bankease.store.InMemoryStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.List;
import java.util.Map;

@ApplicationScoped
@Named("accountService")
public class AccountServiceImpl implements AccountService {

    private final InMemoryStore store;

    @Inject
    public AccountServiceImpl(InMemoryStore store) {
        this.store = store;
    }

    @Override
    public Map<String, Object> openAccount(String clientId, CreateAccountRequest req) {
        Client client = getClientOr404(clientId);
        AccountType type;
        try {
            type = AccountType.valueOf(req.type());
        } catch (Exception e) {
            throw new ApiException(400, "Invalid account type");
        }
        Account acc = client.openAccount(type);
        store.accounts().put(acc.getAccountNumber(), acc);
        return Map.of(
                "clientId", client.getId(),
                "accountNumber", acc.getAccountNumber(),
                "type", acc.getType().name(),
                "balance", acc.getBalance(),
                "openingDate", acc.getOpeningDate()
        );
    }

    @Override
    public List<Map<String, Object>> listAccounts(String clientId) {
        Client client = getClientOr404(clientId);
        return client.getAccounts().stream()
                .map(a -> Map.<String, Object>of(
                        "accountNumber", a.getAccountNumber(),
                        "type", a.getType().name(),
                        "balance", a.getBalance(),
                        "openingDate", a.getOpeningDate()
                ))
                .toList();
    }

    @Override
    public Map<String, Object> totalBalance(String clientId) {
        Client client = getClientOr404(clientId);
        return Map.of("clientId", client.getId(), "totalBalance", client.getTotalBalance());
    }

    private Client getClientOr404(String clientId) {
        Client c = store.clients().get(clientId);
        if (c == null) throw new ApiException(404, "Client not found");
        return c;
    }
}
