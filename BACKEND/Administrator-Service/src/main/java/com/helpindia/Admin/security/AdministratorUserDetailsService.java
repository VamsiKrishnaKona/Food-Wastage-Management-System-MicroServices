package com.helpindia.Admin.security;

import com.helpindia.Admin.model.Administrator;
import com.helpindia.Admin.service.AdministratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdministratorUserDetailsService implements UserDetailsService
{

    private final AdministratorService administratorService;

    @Autowired
    public AdministratorUserDetailsService(AdministratorService administratorService)
    {
        this.administratorService = administratorService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Administrator admin = administratorService.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found: " + username));

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));

        return org.springframework.security.core.userdetails.User.builder()
                .username(admin.getEmail())
                .password(admin.getPasswordHash())   // must be the stored (BCrypt) hash
                .authorities(authorities)
                .accountLocked(false)
                .disabled(false)
                .build();
    }
}
