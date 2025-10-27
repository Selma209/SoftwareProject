package ch.unil.doplab.bankease.controller;

import ch.unil.doplab.bankease.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeService employeeService;
    public EmployeeController(EmployeeService employeeService) { this.employeeService = employeeService; }

    @PostMapping("/{employeeId}/approvals/{txId}:approve")
    public ResponseEntity<?> approve(@PathVariable String employeeId, @PathVariable String txId) {
        return ResponseEntity.ok(employeeService.approve(employeeId, txId));
    }

    @PostMapping("/{employeeId}/approvals/{txId}:reject")
    public ResponseEntity<?> reject(@PathVariable String employeeId, @PathVariable String txId) {
        return ResponseEntity.ok(employeeService.reject(employeeId, txId));
    }

    @GetMapping("/approvals/pending")
    public ResponseEntity<?> pending() {
        return ResponseEntity.ok(employeeService.pendingApprovals());
    }
}
