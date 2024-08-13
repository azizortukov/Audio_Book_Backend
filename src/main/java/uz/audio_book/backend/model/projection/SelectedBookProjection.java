package uz.audio_book.backend.model.projection;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public interface SelectedBookProjection {
    UUID getId();
    String getTitle();
    String getAuthor();
    @JsonProperty("category_names")
    String[] getCategoryNames();
    Double getRating();
    String getDescription();

}
