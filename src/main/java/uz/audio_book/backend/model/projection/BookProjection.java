package uz.audio_book.backend.model.projection;

import java.util.UUID;

public interface BookProjection {

    UUID getId();
    String getTitle();
    String getAuthor();
    Integer getRating();
    UUID[] getCategoryIds();

}
