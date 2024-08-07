package uz.audio_book.backend.projection;

import java.util.UUID;

public interface BookProjection {

    UUID getId();
    String getTitle();
    String getAuthor();
    String getPhotoUrl();
    String getAudioUrl();
    String getPdfUrl();
    Integer getRating();
    UUID[] getCategoryIds();

}
