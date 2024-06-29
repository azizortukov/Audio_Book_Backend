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
import java.util.Optional;
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
        Optional<User> user = userService.getUserFromContextHolder();
        List<Category> categories = categoryRepo.findAllById(categoryIds);
        System.out.println("user: " + user);
        System.out.println("categories: " + categories);
        if (user.isPresent()) {
            user.get().getPersonalCategories().addAll(categories);
            userRepo.save(user.get());
            return ResponseEntity.ok("Saved successfully");
        } else {
            return ResponseEntity.badRequest().body("User not found");
        }
    }

    @Override
    public HttpEntity<?> customizeAllCategories() {
        Optional<User> user = userService.getUserFromContextHolder();

        if (user.isPresent()) {
            user.get().getPersonalCategories().addAll(categoryRepo.findAll());
            userRepo.save(user.get());
            return ResponseEntity.ok("Saved successfully");
        } else {
            return ResponseEntity.badRequest().body("User not found");
        }
    }

    @Override
    public HttpEntity<?> getRecommendedCategories() {
        Optional<User> user = userService.getUserFromContextHolder();
        if (user.isPresent()) {
            return ResponseEntity.ok().body(user.get().getPersonalCategories());
        } else {
            return ResponseEntity.badRequest().body("User not found");
        }
    }
}
