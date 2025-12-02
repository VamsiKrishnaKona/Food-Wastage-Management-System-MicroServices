package com.helpindia.Admin.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AdministratorLoginRequest
{
    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6, max = 72)
    private String password;

    public AdministratorLoginRequest() {}

}
