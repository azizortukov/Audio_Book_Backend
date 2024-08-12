package uz.audio_book.backend.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginDTO(@Email(message = "Email format is not valid")
                       @NotBlank(message = "Email cannot be blank") String email,
                       @NotBlank(message = "Password cannot be blank") String password) {
}
