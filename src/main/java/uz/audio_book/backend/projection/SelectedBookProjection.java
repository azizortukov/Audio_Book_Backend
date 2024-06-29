package uz.audio_book.backend.projection;

import java.util.UUID;

public interface SelectedBookProjection {
    UUID getId();
    String getTitle();
    String getAuthor();
    String[] getCategoryNames();
    Double getRating();
    String getDescription();

}
