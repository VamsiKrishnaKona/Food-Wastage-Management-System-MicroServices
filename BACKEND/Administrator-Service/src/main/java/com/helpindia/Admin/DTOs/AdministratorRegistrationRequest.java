package com.helpindia.Admin.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AdministratorRegistrationRequest
{
    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 2, max = 100)
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 100)
    private String lastName;

    @NotBlank
    @Pattern(regexp = "^[0-9]{7,15}$", message = "Invalid mobile number")
    private String mobileNumber;

    @NotBlank
    @Size(min = 8, max = 72)
    private String password;

    public AdministratorRegistrationRequest() {}

}
