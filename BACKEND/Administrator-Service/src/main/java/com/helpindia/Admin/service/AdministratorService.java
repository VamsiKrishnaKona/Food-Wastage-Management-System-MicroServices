package com.helpindia.Admin.service;

import com.helpindia.Admin.model.Administrator;

import java.util.Optional;

public interface AdministratorService {

    Optional<Administrator> findByEmail(String email);

    Optional<Administrator> verifyLogin(String email, String rawPassword);

    Administrator createAdministrator(String email, String firstName, String lastName, String mobileNumber, String rawPassword);

    String getFullName(String email);
}
