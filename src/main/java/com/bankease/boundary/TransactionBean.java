package com.bankease.boundary;

import ch.unil.doplab.bankease.dto.DepositRequest;
import ch.unil.doplab.bankease.dto.TransferRequest;
import ch.unil.doplab.bankease.exception.ApiException;
import ch.unil.doplab.bankease.service.TransactionService;
import ch.unil.doplab.bankease.domain.Client;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

@Named
@RequestScoped
public class TransactionBean implements Serializable {

    @Inject
    private TransactionService transactionService;

    @Inject
    private LoginBean loginBean;

    private String accountNumber;
    private String sourceAccountNumber;
    private String destinationAccountNumber;
    private BigDecimal amount;
    private String description;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getSourceAccountNumber() {
        return sourceAccountNumber;
    }

    public void setSourceAccountNumber(String sourceAccountNumber) {
        this.sourceAccountNumber = sourceAccountNumber;
    }

    public String getDestinationAccountNumber() {
        return destinationAccountNumber;
    }

    public void setDestinationAccountNumber(String destinationAccountNumber) {
        this.destinationAccountNumber = destinationAccountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    private Client getLoggedClientOrNull() {
        return loginBean.getLoggedClient();
    }

    private String getLoggedClientId() {
        Client c = getLoggedClientOrNull();
        return (c != null) ? c.getUsername() : null;
    }


    public String makeDeposit() {
        String clientId = getLoggedClientId();
        if (clientId == null) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Session expirée", "Veuillez vous reconnecter.");
            return "index?faces-redirect=true";
        }

        try {
            DepositRequest req = new DepositRequest(
                    clientId,
                    accountNumber,
                    amount,
                    description
            );
            Map<String, Object> result = transactionService.deposit(req);

            addMessage(FacesMessage.SEVERITY_INFO,
                    "Dépôt réussi",
                    "Nouveau solde : " + result.get("balance") + " CHF");

            // retour au dashboard pour voir le solde mis à jour
            return "dashboard?faces-redirect=true";

        } catch (ApiException ex) {
            addMessage(FacesMessage.SEVERITY_ERROR,
                    "Erreur (" + ex.getStatus() + ")",
                    ex.getMessage());
            return null;
        } catch (Exception ex) {
            addMessage(FacesMessage.SEVERITY_ERROR,
                    "Erreur inattendue",
                    ex.getMessage());
            return null;
        }
    }

    public String makeTransfer() {
        String clientId = getLoggedClientId();
        if (clientId == null) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Session expirée", "Veuillez vous reconnecter.");
            return "index?faces-redirect=true";
        }

        try {
            TransferRequest req = new TransferRequest(
                    clientId,
                    sourceAccountNumber,
                    destinationAccountNumber,
                    amount,
                    description
            );
            Map<String, Object> result = transactionService.transfer(req);

            addMessage(FacesMessage.SEVERITY_INFO,
                    "Virement réussi",
                    "Transaction : " + result.get("id"));

            return "dashboard?faces-redirect=true";

        } catch (ApiException ex) {
            addMessage(FacesMessage.SEVERITY_ERROR,
                    "Erreur (" + ex.getStatus() + ")",
                    ex.getMessage());
            return null;
        } catch (Exception ex) {
            addMessage(FacesMessage.SEVERITY_ERROR,
                    "Erreur inattendue",
                    ex.getMessage());
            return null;
        }
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, summary, detail));
    }
}
