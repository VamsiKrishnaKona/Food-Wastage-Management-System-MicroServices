package com.helpindia.Admin.Repository;

import com.helpindia.Admin.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long>
{
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByEmail(String username);
}
