package uz.audio_book.backend.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailDTO(
        @Email(message = "Email format is not valid")
        @NotBlank(message = "Email cannot be blank")
        String email) {
}
