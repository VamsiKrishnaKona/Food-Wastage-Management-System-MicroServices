package com.helpindia.Admin.service;

import com.helpindia.Admin.Repository.AdministratorRepository;
import com.helpindia.Admin.model.Administrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AdministratorServiceImpl implements AdministratorService
{
    private final AdministratorRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AdministratorServiceImpl(AdministratorRepository repository, PasswordEncoder passwordEncoder)
    {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Administrator createAdmin(Administrator admin) {
        // Basic checks (could be expanded)
        if (repository.existsById(admin.getEmail())) {
            throw new DataIntegrityViolationException("Administrator with this email already exists");
        }
        if (repository.existsByUsername(admin.getUsername())) {
            throw new DataIntegrityViolationException("Username already taken");
        }
        // hash password
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        return repository.save(admin);
    }

    @Override
    public Administrator updateAdmin(String email, Administrator updated) {
        Administrator existing = repository.findById(email)
                .orElseThrow(() -> new IllegalArgumentException("Administrator not found: " + email));
        // Only update selected fields; don't overwrite email (PK)
        existing.setUsername(updated.getUsername() == null ? existing.getUsername() : updated.getUsername());
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setMobileNumber(updated.getMobileNumber());
        if (updated.getPassword() != null && !updated.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(updated.getPassword()));
        }
        return repository.save(existing);
    }

    @Override
    public Optional<Administrator> getAdminByEmail(String email) {
        return repository.findById(email);
    }

    @Override
    public Optional<Administrator> getAdminByUsername(String username) {
        return repository.findByUsername(username);
    }

    @Override
    public List<Administrator> getAllAdmins() {
        return repository.findAll();
    }

    @Override
    public void deleteAdmin(String email) {
        repository.deleteById(email);
    }

    @Override
    public Optional<Administrator> login(String identifier, String rawPassword) {
        Optional<Administrator> byEmail = repository.findById(identifier);
        Optional<Administrator> byUsername = repository.findByUsername(identifier);

        Optional<Administrator> possible = byEmail.isPresent() ? byEmail : byUsername;

        if (possible.isPresent()) {
            Administrator admin = possible.get();
            if (passwordEncoder.matches(rawPassword, admin.getPassword())) {
                return Optional.of(admin);
            }
        }
        return Optional.empty();
    }
}
