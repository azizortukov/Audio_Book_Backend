package uz.audio_book.backend.model.projection;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public interface CommentProjection {
    UUID getId();
    String getBody();
    Integer getRating();
    @JsonProperty("display_name")
    String getDisplayName();
    @JsonProperty("user_id")
    UUID getUserId();
}
