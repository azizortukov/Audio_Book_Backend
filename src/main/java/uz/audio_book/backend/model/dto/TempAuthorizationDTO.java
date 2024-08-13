package uz.audio_book.backend.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TempAuthorizationDTO(@JsonProperty("temp_authorization") String tempAuthorization) {
}
