package com.helpindia.Admin.DTOs;

public record ResetPasswordRequest(String token, String newPassword)
{
}
