package uz.audio_book.backend.model.projection;

import java.util.UUID;

public interface CommentProjection {
    UUID getId();
    String getBody();
    Integer getRating();
    String getDisplayName();
    UUID getUserId();
}
