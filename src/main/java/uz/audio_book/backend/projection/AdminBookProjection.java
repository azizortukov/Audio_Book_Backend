package uz.audio_book.backend.projection;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AdminBookProjection {

    UUID getId();
    String getTitle();
    String getAuthor();
    String getDescription();
    List<String> getCategories();
    LocalDateTime getCreatedAt();

}