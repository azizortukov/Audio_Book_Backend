package uz.audio_book.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.audio_book.backend.entity.Category;
import uz.audio_book.backend.entity.User;
import uz.audio_book.backend.exceptions.NotFoundException;
import uz.audio_book.backend.repo.BookRepo;
import uz.audio_book.backend.repo.CategoryRepo;
import uz.audio_book.backend.repo.UserRepo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepo categoryRepo;
    private final UserRepo userRepo;
    private final UserService userService;
    private final BookRepo bookRepo;

    @Override
    public HttpEntity<?> getCategories() {
        return ResponseEntity.ok(categoryRepo.findAll());
    }

    @Override
    public HttpEntity<?> customizeCategoryByIds(List<UUID> categoryIds) {
        User user = userService.getUserFromContextHolder();
        if (categoryIds.isEmpty()) {
            throw new NullPointerException("Category IDs can't be empty");
        }
        List<Category> categories = categoryRepo.findAllById(categoryIds);
        user.getPersonalCategories().addAll(categories);
        userRepo.save(user);
        return ResponseEntity.noContent().build();
    }

    @Override
    public HttpEntity<?> customizeAllCategories() {
        User user = userService.getUserFromContextHolder();
        user.getPersonalCategories().addAll(categoryRepo.findAll());
        userRepo.save(user);
        return ResponseEntity.noContent().build();
    }

    @Override
    public HttpEntity<?> getRecommendedCategories() {
        User user = userService.getUserFromContextHolder();
        return ResponseEntity.ok().body(user.getPersonalCategories());
    }

    @Override
    public HttpEntity<?> updateCategory(Category category) {
        Optional<Category> categoryOptional = categoryRepo.findById(category.getId());
        if (categoryOptional.isEmpty()) {
            throw new NotFoundException("Category not found");
        }
        categoryOptional.get().setName(category.getName());
        categoryRepo.save(categoryOptional.get());
        return ResponseEntity.noContent().build();
    }

    @Override
    public void deleteById(UUID categoryId) {
        categoryRepo.findById(categoryId).orElseThrow(() -> new NotFoundException("Category not found"));
        bookRepo.deleteBookCategoryById(categoryId);
        bookRepo.deletePersonalCategoriesById(categoryId);
        categoryRepo.deleteById(categoryId);
    }
}
