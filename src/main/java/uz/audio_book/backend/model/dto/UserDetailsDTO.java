package uz.audio_book.backend.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserDetailsDTO(
        @JsonProperty("display_name")
        String displayName,
        String email,
        @JsonProperty("birth_date")
        String birthDate) {
}
