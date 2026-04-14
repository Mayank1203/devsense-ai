package com.devsense.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginRequestDto {
    @NotNull @Email
    private String email;

    @NotNull
    private String password;
}
