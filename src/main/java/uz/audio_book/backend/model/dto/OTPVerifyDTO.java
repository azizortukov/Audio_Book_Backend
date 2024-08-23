package uz.audio_book.backend.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record OTPVerifyDTO(
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email format is wrong")
        String email,
        @NotBlank(message = "Code cannot be blank")
        String code) {}
