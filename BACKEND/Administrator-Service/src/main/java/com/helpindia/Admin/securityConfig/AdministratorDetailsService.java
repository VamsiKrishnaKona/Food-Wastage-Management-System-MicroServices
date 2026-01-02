package com.helpindia.Admin.securityConfig;

import com.helpindia.Admin.Repository.AdministratorRepository;
import com.helpindia.Admin.model.Administrator;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AdministratorDetailsService implements UserDetailsService
{
    private final AdministratorRepository administratorRepository;

    public AdministratorDetailsService(AdministratorRepository administratorRepository)
    {
        this.administratorRepository = administratorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException
    {
        Administrator admin = administratorRepository.findById(identifier)
                .or(() -> administratorRepository.findByUsername(identifier))
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found: " + identifier));

        //return new AdministratorDetails(admin);

        return User.builder()
                .username(admin.getEmail())
                .password(admin.getPassword())
                .roles("ADMIN")
                .build();
    }
}
