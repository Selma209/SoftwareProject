package ch.unil.doplab.bankease.service.impl;

import ch.unil.doplab.bankease.domain.*;
import ch.unil.doplab.bankease.dto.DepositRequest;
import ch.unil.doplab.bankease.dto.TransferRequest;
import ch.unil.doplab.bankease.dto.WithdrawRequest;
import ch.unil.doplab.bankease.exception.ApiException;
import ch.unil.doplab.bankease.service.TransactionService;
import ch.unil.doplab.bankease.store.InMemoryStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class TransactionServiceImpl implements TransactionService {

    private final InMemoryStore store;

    @Inject
    public TransactionServiceImpl(InMemoryStore store) {
        this.store = store;
    }

    @Override
    public Map<String, Object> deposit(DepositRequest req) {
        Client client = getClientOr404(req.clientId());
        Account acc = getAccountOr404(req.accountNumber());
        ensureOwnership(client, acc);
        client.deposit(acc, req.amount(), req.description());
        Transaction last = client.getTransactions().get(client.getTransactions().size() - 1);
        store.transactions().put(last.getId(), last);
        return txToMap(last);
    }

    @Override
    public Map<String, Object> withdraw(WithdrawRequest req) {
        Client client = getClientOr404(req.clientId());
        Account acc = getAccountOr404(req.accountNumber());
        ensureOwnership(client, acc);
        client.withdraw(acc, req.amount(), req.description());
        Transaction last = client.getTransactions().get(client.getTransactions().size() - 1);
        store.transactions().put(last.getId(), last);
        return txToMap(last);
    }

    @Override
    public Map<String, Object> transfer(TransferRequest req) {
        Client client = getClientOr404(req.clientId());
        Account src = getAccountOr404(req.sourceAccountNumber());
        Account dst = getAccountOr404(req.destinationAccountNumber());
        ensureOwnership(client, src);
        Transaction tx = client.transfer(src, dst, req.amount(), req.description());
        store.transactions().put(tx.getId(), tx);
        return txToMap(tx);
    }

    @Override
    public Map<String, Object> cancel(String txId) {
        Transaction tx = store.transactions().get(txId);
        if (tx == null) throw new ApiException(404, "Transaction not found");
        // On autorise l'annulation directe si COMPLETED (cf. modèle)
        if (tx.getStatus() != TransactionStatus.COMPLETED) {
            throw new ApiException(400, "Only COMPLETED transactions can be canceled directly");
        }
        tx.cancel();
        return txToMap(tx);
    }

    @Override
    public List<Map<String, Object>> historyByClient(String clientId) {
        Client c = getClientOr404(clientId);
        return c.getTransactions().stream().map(this::txToMap).toList();
    }

    private Map<String, Object> txToMap(Transaction t) {
        String src = t.getSource() == null ? "-" : t.getSource().getAccountNumber();
        String dst = t.getDestination() == null ? "-" : t.getDestination().getAccountNumber();
        return Map.of(
                "id", t.getId(),
                "type", t.getType().name(),
                "amount", t.getAmount(),
                "source", src,
                "destination", dst,
                "status", t.getStatus().name(),
                "timestamp", t.getTimestamp().toString(),
                "description", t.getDescription()
        );
    }

    private Client getClientOr404(String clientId) {
        Client c = store.clients().get(clientId);
        if (c == null) throw new ApiException(404, "Client not found");
        return c;
    }

    private Account getAccountOr404(String accountNumber) {
        Account a = store.accounts().get(accountNumber);
        if (a == null) throw new ApiException(404, "Account not found");
        return a;
    }

    private void ensureOwnership(Client client, Account acc) {
        if (!client.getAccounts().contains(acc)) {
            throw new ApiException(403, "Account does not belong to client");
        }
    }
}
