package ch.unil.doplab.bankease.controller;

import ch.unil.doplab.bankease.dto.CreateAccountRequest;
import ch.unil.doplab.bankease.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AccountController {
    private final AccountService accountService;
    public AccountController(AccountService accountService) { this.accountService = accountService; }

    @PostMapping("/clients/{clientId}/accounts")
    public ResponseEntity<?> openAccount(@PathVariable String clientId, @RequestBody @Valid CreateAccountRequest req) {
        return ResponseEntity.ok(accountService.openAccount(clientId, req));
    }

    @GetMapping("/clients/{clientId}/accounts")
    public ResponseEntity<?> listAccounts(@PathVariable String clientId) {
        return ResponseEntity.ok(accountService.listAccounts(clientId));
    }

    @GetMapping("/clients/{clientId}/balance")
    public ResponseEntity<?> totalBalance(@PathVariable String clientId) {
        return ResponseEntity.ok(accountService.totalBalance(clientId));
    }
}
