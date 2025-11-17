package ch.unil.doplab.bankease.service.impl;

import ch.unil.doplab.bankease.domain.Employee;
import ch.unil.doplab.bankease.domain.Transaction;
import ch.unil.doplab.bankease.domain.TransactionStatus;
import ch.unil.doplab.bankease.exception.ApiException;
import ch.unil.doplab.bankease.service.EmployeeService;
import ch.unil.doplab.bankease.store.InMemoryStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class EmployeeServiceImpl implements EmployeeService {

    private final InMemoryStore store;

    @Inject
    public EmployeeServiceImpl(InMemoryStore store) {
        this.store = store;
    }

    @Override
    public Map<String, Object> approve(String employeeId, String txId) {
        Employee emp = getEmployeeOr404(employeeId);
        Transaction tx = getTxOr404(txId);
        emp.approve(tx);
        return txToMap(tx);
    }

    @Override
    public Map<String, Object> reject(String employeeId, String txId) {
        Employee emp = getEmployeeOr404(employeeId);
        Transaction tx = getTxOr404(txId);
        emp.reject(tx);
        return txToMap(tx);
    }

    @Override
    public List<Map<String, Object>> pendingApprovals() {
        return store.transactions().values().stream()
                .filter(t -> t.getStatus() == TransactionStatus.PENDING_APPROVAL)
                .map(this::txToMap)
                .toList();
    }

    private Employee getEmployeeOr404(String employeeId) {
        Employee e = store.employees().get(employeeId);
        if (e == null) throw new ApiException(404, "Employee not found");
        return e;
    }

    private Transaction getTxOr404(String txId) {
        Transaction t = store.transactions().get(txId);
        if (t == null) throw new ApiException(404, "Transaction not found");
        return t;
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
}
