package com.helpindia.Admin.service;

import com.helpindia.Admin.model.Administrator;

import java.util.List;
import java.util.Optional;

public interface AdministratorService
{

    Administrator createAdmin(Administrator admin);
    Administrator updateAdmin(String email, Administrator updated);
    Optional<Administrator> getAdminByEmail(String email);
    Optional<Administrator> getAdminByUsername(String username);
    List<Administrator> getAllAdmins();
    void deleteAdmin(String email);

    /**
     * Attempt login with identifier (email or username) and raw password.
     * Returns the Administrator if authentication succeeds, otherwise empty.
     */
    Optional<Administrator> login(String identifier, String rawPassword);

    void changePassword(String email, String oldPassword, String newPassword);
    void createResetToken(String email);
    void resetPassword(String token, String newPassword);
}
