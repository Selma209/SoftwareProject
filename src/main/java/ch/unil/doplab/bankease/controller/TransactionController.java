package ch.unil.doplab.bankease.controller;

import ch.unil.doplab.bankease.dto.DepositRequest;
import ch.unil.doplab.bankease.dto.TransferRequest;
import ch.unil.doplab.bankease.dto.WithdrawRequest;
import ch.unil.doplab.bankease.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService txService;
    public TransactionController(TransactionService txService) { this.txService = txService; }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestBody @Valid DepositRequest req) {
        return ResponseEntity.ok(txService.deposit(req));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@RequestBody @Valid WithdrawRequest req) {
        return ResponseEntity.ok(txService.withdraw(req));
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody @Valid TransferRequest req) {
        return ResponseEntity.ok(txService.transfer(req));
    }

    @PostMapping("/{txId}:cancel")
    public ResponseEntity<?> cancel(@PathVariable String txId) {
        return ResponseEntity.ok(txService.cancel(txId));
    }

    @GetMapping("/clients/{clientId}")
    public ResponseEntity<?> historyByClient(@PathVariable String clientId) {
        return ResponseEntity.ok(txService.historyByClient(clientId));
    }
}
