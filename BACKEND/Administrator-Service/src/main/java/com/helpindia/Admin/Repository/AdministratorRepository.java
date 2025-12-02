package com.helpindia.Admin.Repository;

import com.helpindia.Admin.model.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdministratorRepository extends JpaRepository<Administrator, String> {
    Optional<Administrator> findByEmail(String email);
}
