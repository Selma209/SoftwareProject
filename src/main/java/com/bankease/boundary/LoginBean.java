package com.bankease.boundary;

import ch.unil.doplab.bankease.domain.Client;
import ch.unil.doplab.bankease.service.ClientService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

import java.io.Serializable;

@Named
@SessionScoped
public class LoginBean implements Serializable {

    private String username;
    private String password;
    private Client loggedClient;

    @Inject
    private ClientService clientService;

    // ====== Getters / Setters ======

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Client getLoggedClient() {
        return loggedClient;
    }

    public boolean isLogged() {
        return loggedClient != null;
    }

    public String getClientId() {
        return (loggedClient != null) ? loggedClient.getUsername() : null;
    }

    // ====== Actions ======

    public String login() {
        Client c = clientService.validateLogin(username, password);
        if (c == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Invalid username or password", null));
            return null;
        }

        loggedClient = c;
        password = null; // on efface le mdp en mémoire

        return "dashboard?faces-redirect=true";
    }

    public String logout() {
        FacesContext.getCurrentInstance()
                .getExternalContext()
                .invalidateSession();

        return "index.xhtml?faces-redirect=true";
    }
}
