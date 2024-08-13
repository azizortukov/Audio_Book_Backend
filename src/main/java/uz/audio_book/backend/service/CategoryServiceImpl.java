package uz.audio_book.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.audio_book.backend.entity.Category;
import uz.audio_book.backend.entity.User;
import uz.audio_book.backend.repo.CategoryRepo;
import uz.audio_book.backend.repo.UserRepo;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepo categoryRepo;
    private final UserRepo userRepo;
    private final UserService userService;

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
}
