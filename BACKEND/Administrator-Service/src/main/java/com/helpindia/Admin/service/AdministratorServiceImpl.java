package com.helpindia.Admin.service;

import com.helpindia.Admin.Repository.AdministratorRepository;
import com.helpindia.Admin.Repository.PasswordResetTokenRepository;
import com.helpindia.Admin.Repository.RefreshTokenRepository;
import com.helpindia.Admin.model.Administrator;
import com.helpindia.Admin.model.PasswordResetToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class AdministratorServiceImpl implements AdministratorService
{
    private final AdministratorRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    public AdministratorServiceImpl(AdministratorRepository repository, PasswordEncoder passwordEncoder)
    {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Administrator createAdmin(Administrator admin)
    {
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
    public Optional<Administrator> login(String identifier, String rawPassword)
    {
        Optional<Administrator> byEmail = repository.findById(identifier);
        Optional<Administrator> byUsername = repository.findByUsername(identifier);

        Optional<Administrator> possible = byEmail.isPresent() ? byEmail : byUsername;

        if (possible.isPresent()) {
            Administrator admin = possible.get();
            if (passwordEncoder.matches(rawPassword, admin.getPassword()))
            {
                return Optional.of(admin);
            }
        }
        return Optional.empty();
    }

    @Override
    public Administrator updateAdmin(String email, Administrator updated)
    {
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
    public void changePassword(String email, String oldPassword, String newPassword)
    {
        Administrator admin = repository.findById(email).orElseThrow(() -> new UsernameNotFoundException("Admin Not Found"));

        if(!passwordEncoder.matches(oldPassword, admin.getPassword()))
        {
            throw new RuntimeException("Your old password is incorrect");
        }

        admin.setPassword(passwordEncoder.encode(newPassword));
        repository.save(admin);

        refreshTokenRepository.deleteByUsername(email);
    }

    @Override
    public void createResetToken(String email)
    {
        Administrator admin = repository.findById(email).orElseThrow(() -> new UsernameNotFoundException("Admin Not Found"));

        passwordResetTokenRepository.deleteByEmail(email);

        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(UUID.randomUUID().toString());
        passwordResetToken.setEmail(email);
        passwordResetToken.setExpiryDate(Instant.now().plusSeconds(60 * 15));

        passwordResetTokenRepository.save(passwordResetToken);

        System.out.println("reset token: " + passwordResetToken.getToken());
    }

    @Override
    public void resetPassword(String token, String newPassword)
    {
        PasswordResetToken storedToken = passwordResetTokenRepository.findByToken(token).orElseThrow(() -> new RuntimeException("Invalid Token"));

        if(storedToken.isUsed() || storedToken.getExpiryDate().isBefore(Instant.now()))
            throw new RuntimeException("Token is expired");

        Administrator admin = repository.findById(storedToken.getEmail()).orElseThrow(() -> new RuntimeException("Admin not found"));

        admin.setPassword(passwordEncoder.encode(newPassword));
        repository.save(admin);

        storedToken.setUsed(true);
        passwordResetTokenRepository.save(storedToken);

        refreshTokenRepository.deleteByUsername(admin.getEmail());
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

}
