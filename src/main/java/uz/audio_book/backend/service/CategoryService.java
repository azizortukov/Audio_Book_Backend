package uz.audio_book.backend.service;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface CategoryService {

    HttpEntity<?> getCategories();

    HttpEntity<?> customizeCategoryByIds(List<UUID> categoryIds);

    HttpEntity<?> customizeAllCategories();
}
