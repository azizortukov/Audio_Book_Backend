package uz.audio_book.backend.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AccessTokenDTO(@JsonProperty("access_token") String accessToken) {
}
