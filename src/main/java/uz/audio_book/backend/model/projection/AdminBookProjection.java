package uz.audio_book.backend.model.projection;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AdminBookProjection {

    UUID getId();
    String getTitle();
    String getAuthor();
    String getDescription();
    List<String> getCategories();
    @JsonProperty("created_at")
    LocalDateTime getCreatedAt();

}