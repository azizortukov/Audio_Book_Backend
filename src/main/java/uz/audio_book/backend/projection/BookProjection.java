package uz.audio_book.backend.projection;

import uz.audio_book.backend.entity.Category;

import java.util.List;
import java.util.UUID;

public interface BookProjection {

    UUID getId();
    String getTitle();
    String getAuthor();
    String getDescription();
    double getRating();
    List<Category> getCategories();

}
