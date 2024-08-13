package uz.audio_book.backend.model.projection;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public interface BookProjection {

    UUID getId();
    String getTitle();
    String getAuthor();
    Integer getRating();
    @JsonProperty("category_ids")
    UUID[] getCategoryIds();

}
