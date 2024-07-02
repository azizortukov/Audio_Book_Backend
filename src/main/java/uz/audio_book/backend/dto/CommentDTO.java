package uz.audio_book.backend.dto;



import java.util.UUID;

public record CommentDTO(UUID bookId,  Integer rating, String body) {
}
