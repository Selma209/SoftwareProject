package ch.unil.doplab.bankease.service.impl;

import ch.unil.doplab.bankease.domain.*;
import ch.unil.doplab.bankease.dto.CreateAccountRequest;
import ch.unil.doplab.bankease.exception.ApiException;
import ch.unil.doplab.bankease.service.AccountService;
import ch.unil.doplab.bankease.store.InMemoryStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.HashMap;
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

        // 🔥 HashMap modifiable (PAS Map.of)
        Map<String, Object> map = new HashMap<>();
        map.put("clientId", client.getId());
        map.put("accountNumber", acc.getAccountNumber());
        map.put("type", acc.getType().name());
        map.put("balance", acc.getBalance());
        map.put("openingDate", acc.getOpeningDate());
        return map;
    }

    @Override
    public List<Map<String, Object>> listAccounts(String clientId) {
        Client client = getClientOr404(clientId);

        return client.getAccounts().stream().map(a -> {
            Map<String, Object> map = new HashMap<>();
            map.put("accountNumber", a.getAccountNumber());
            map.put("type", a.getType().name());
            map.put("balance", a.getBalance());
            map.put("openingDate", a.getOpeningDate());
            return map;
        }).toList();
    }

    @Override
    public Map<String, Object> totalBalance(String clientId) {
        Client client = getClientOr404(clientId);

        Map<String, Object> map = new HashMap<>();
        map.put("clientId", client.getId());
        map.put("totalBalance", client.getTotalBalance());
        return map;
    }

    private Client getClientOr404(String clientId) {
        Client c = store.clients().get(clientId);
        if (c == null) throw new ApiException(404, "Client not found");
        return c;
    }
}
