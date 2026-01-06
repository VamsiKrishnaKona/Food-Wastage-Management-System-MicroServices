package com.helpindia.Admin.controller;

import com.helpindia.Admin.DTOs.*;
import com.helpindia.Admin.JWT.JWTService;
import com.helpindia.Admin.model.Administrator;
import com.helpindia.Admin.service.AdministratorService;
import com.helpindia.Admin.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admins")
public class AdministratorController
{

    private final AdministratorService service;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    private JWTService jwtService;

    public AdministratorController(AdministratorService service, RefreshTokenService refreshTokenService)
    {
        this.service = service;
        this.refreshTokenService = refreshTokenService;
    }

    // Create
    @PostMapping("registration")
    public ResponseEntity<AdminResponse> createAdmin(@Valid @RequestBody CreateAdminRequest req)
    {
        Administrator adm = new Administrator();
        adm.setEmail(req.getEmail());
        adm.setUsername(req.getUsername());
        adm.setFirstName(req.getFirstName());
        adm.setLastName(req.getLastName());
        adm.setMobileNumber(req.getMobileNumber());
        adm.setPassword(req.getPassword());

        Administrator saved = service.createAdmin(adm);
        return new ResponseEntity<>(toResponse(saved), HttpStatus.CREATED);
    }

    // Login - accepts identifier (email or username) + password
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginReq)
    {
        Optional<Administrator> opt = service.login(loginReq.getIdentifier(), loginReq.getPassword());
        if (opt.isPresent())
        {
            //return ResponseEntity.ok(toResponse(opt.get()));

            String token = jwtService.generateToken(opt.get().getEmail());
            String refreshToken = jwtService.generateRefreshToken(opt.get().getEmail());

            refreshTokenService.create(opt.get().getEmail(), refreshToken);

            return ResponseEntity.ok(new AuthResponse(token, refreshToken, "Bearer"));
        }
        else
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    // Update
    @PutMapping("/{email}")
    public ResponseEntity<AdminResponse> update(@PathVariable String email,
                                                @Valid @RequestBody UpdateAdminRequest req) {
        Administrator upd = new Administrator();
        // Only fields that can be changed:
        upd.setUsername(req.getUsername());
        upd.setFirstName(req.getFirstName());
        upd.setLastName(req.getLastName());
        upd.setMobileNumber(req.getMobileNumber());
        upd.setPassword(req.getPassword()); // optional

        Administrator saved = service.updateAdmin(email, upd);
        return ResponseEntity.ok(toResponse(saved));
    }

    //Change password
    @PostMapping("change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request)
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        service.changePassword(email, request.oldPassword(), request.newPassword());

        return ResponseEntity.ok("Password Changed successfully!!!");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request)
    {
        service.createResetToken(request.email());
        return ResponseEntity.ok("if Email Exists, reset link send to Email.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request)
    {
        service.resetPassword(request.token(), request.newPassword());
        return ResponseEntity.ok("Password changed Successfully");
    }

    // Read all
    @GetMapping
    public List<AdminResponse> getAll() {
        return service.getAllAdmins().stream().map(this::toResponse).collect(Collectors.toList());
    }

    // Read by email
    @GetMapping("/{email}")
    public ResponseEntity<AdminResponse> getByEmail(@PathVariable String email)
    {
        Optional<Administrator> opt = service.getAdminByEmail(email);
        return opt.map(a -> ResponseEntity.ok(toResponse(a)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    // Delete
    @DeleteMapping("/{email}")
    public ResponseEntity<Void> delete(@PathVariable String email)
    {
        service.deleteAdmin(email);
        return ResponseEntity.noContent().build();
    }



    private AdminResponse toResponse(Administrator a) {
        AdminResponse r = new AdminResponse();
        r.setEmail(a.getEmail());
        r.setUsername(a.getUsername());
        r.setFirstName(a.getFirstName());
        r.setLastName(a.getLastName());
        r.setMobileNumber(a.getMobileNumber());
        return r;
    }
}
