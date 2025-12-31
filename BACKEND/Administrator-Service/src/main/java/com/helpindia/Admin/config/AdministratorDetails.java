//package com.helpindia.Admin.config;
//
//import com.helpindia.Admin.model.Administrator;
//import org.jspecify.annotations.Nullable;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.util.Collection;
//import java.util.List;
//
//public class AdministratorDetails implements UserDetails
//{
//
//    private Administrator administrator;
//
//    public AdministratorDetails(Administrator administrator)
//    {
//        this.administrator = administrator;
//    }
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return List.of(new SimpleGrantedAuthority("ADMIN"));
//    }
//
//    @Override
//    public @Nullable String getPassword() {
//        return administrator.getPassword();
//    }
//
//    @Override
//    public String getUsername() {
//        return administrator.getEmail();
//    }
//}
