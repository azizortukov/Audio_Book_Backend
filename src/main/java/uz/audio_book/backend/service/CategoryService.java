package uz.audio_book.backend.service;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import uz.audio_book.backend.entity.Category;

import java.util.List;
import java.util.UUID;

@Service
public interface CategoryService {

    HttpEntity<?> getCategories();

    HttpEntity<?> customizeCategoryByIds(List<UUID> categoryIds);

    HttpEntity<?> customizeAllCategories();

    HttpEntity<?> getRecommendedCategories();

    HttpEntity<?> updateCategory(Category category);

    void deleteById(UUID categoryId);
}
