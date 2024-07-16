package uz.audio_book.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

import java.util.UUID;

public record CommentDTO(@NonNull UUID bookId, @NotNull Integer rating, String body) {
}
