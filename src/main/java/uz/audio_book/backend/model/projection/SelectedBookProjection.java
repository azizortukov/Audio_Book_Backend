package uz.audio_book.backend.model.projection;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public interface SelectedBookProjection {
    UUID getId();
    String getTitle();
    String getAuthor();
    @JsonProperty("photo_url")
    String getPhotoUrl();
    @JsonProperty("audio_url")
    String getAudioUrl();
    @JsonProperty("pdf_url")
    String getPdfUrl();
    @JsonProperty("category_names")
    String[] getCategoryNames();
    Double getRating();
    String getDescription();

}
