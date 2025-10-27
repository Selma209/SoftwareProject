package ch.unil.doplab.bankease.config;

import ch.unil.doplab.bankease.domain.*;
import ch.unil.doplab.bankease.store.InMemoryStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner seed(InMemoryStore store) {
        return args -> {
            // Employee admin
            Employee admin = new Employee("emp_admin","Admin#123","Alex","Morgan","alex@bank.ch","+41 79 000 00 00", Role.ADMINISTRATOR);
            store.employees().put(admin.getId(), admin);

            // Client + deux comptes
            Client cara = new Client("client_cara","Cl1ent#Pwd","Cara","Dune","cara@client.ch","+41 79 111 11 11");
            admin.createClientAccount(cara);
            store.clients().put(cara.getId(), cara);

            Account acc1 = cara.openAccount(AccountType.CURRENT);
            Account acc2 = cara.openAccount(AccountType.SAVINGS);
            store.accounts().put(acc1.getAccountNumber(), acc1);
            store.accounts().put(acc2.getAccountNumber(), acc2);

            // Dépôt initial
            cara.deposit(acc1, new BigDecimal("1500.00"), "Initial funding");

            // index par username (utile pour tests)
            store.clientByUsername().put(cara.getUsername(), cara.getId());
            store.employeeByUsername().put(admin.getUsername(), admin.getId());
        };
    }
}
