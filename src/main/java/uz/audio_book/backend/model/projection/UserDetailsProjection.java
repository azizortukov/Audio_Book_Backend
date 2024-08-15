package uz.audio_book.backend.model.projection;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public interface UserDetailsProjection {

    UUID getId();
    @JsonProperty("display_name")
    String getDisplayName();
    String getEmail();
    @JsonProperty("birth_date")
    String getBirthDate();
    @JsonProperty("profile_photo_url")
    String getProfilePhotoUrl();

}
