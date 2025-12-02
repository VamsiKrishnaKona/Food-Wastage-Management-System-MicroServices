package com.helpindia.Admin.service;

import com.helpindia.Admin.Repository.AdministratorRepository;
import com.helpindia.Admin.model.Administrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdministratorServiceImpl implements AdministratorService {

    private final AdministratorRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdministratorServiceImpl(AdministratorRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<Administrator> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public Optional<Administrator> verifyLogin(String email, String rawPassword) {
        return repository.findByEmail(email)
                .filter(admin -> passwordEncoder.matches(rawPassword, admin.getPasswordHash()));
    }

    @Override
    public Administrator createAdministrator(String email, String firstName, String lastName, String mobileNumber, String rawPassword) {
        String hash = passwordEncoder.encode(rawPassword);
        Administrator admin = new Administrator(email, firstName, lastName, mobileNumber, hash);
        return repository.save(admin);
    }

    @Override
    public String getFullName(String email) {
        return repository.findById(email)
                .map(Administrator::getFullName)
                .orElse(null);
    }
}
