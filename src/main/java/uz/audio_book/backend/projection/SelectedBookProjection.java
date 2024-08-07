package uz.audio_book.backend.projection;

import java.util.UUID;

public interface SelectedBookProjection {
    UUID getId();
    String getTitle();
    String getAuthor();
    String getPhotoUrl();
    String getAudioUrl();
    String getPdfUrl();
    String[] getCategoryNames();
    Double getRating();
    String getDescription();

}
