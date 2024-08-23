package uz.audio_book.backend.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record ChangePasswordDTO(
        @JsonProperty("new_password")
        @NotBlank
        String newPassword,
        @JsonProperty("confirm_password")
        @NotBlank
        String confirmPassword) {
}
