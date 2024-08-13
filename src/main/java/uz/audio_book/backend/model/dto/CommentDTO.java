package uz.audio_book.backend.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

import java.util.UUID;

public record CommentDTO(
        @NonNull
        @JsonProperty("book_id")
        UUID bookId,
        @NotNull
        Integer rating,
        String body) {
    public CommentDTO() {
        this(UUID.randomUUID(), 0, null);
    }
}
