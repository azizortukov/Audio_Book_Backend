package uz.audio_book.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.audio_book.backend.entity.Category;
import uz.audio_book.backend.entity.User;
import uz.audio_book.backend.exceptions.UserNotFoundException;
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
        if (categoryIds.isEmpty()) {
            throw new NullPointerException("Category IDs can't be empty");
        }
        List<Category> categories = categoryRepo.findAllById(categoryIds);
        if (user.isEmpty()) {
            throw new UserNotFoundException("Sorry, user's session is expired!");
        }
        user.get().getPersonalCategories().addAll(categories);
        userRepo.save(user.get());
        return ResponseEntity.ok("Saved successfully");
    }

    @Override
    public HttpEntity<?> customizeAllCategories() {
        Optional<User> user = userService.getUserFromContextHolder();
        if (user.isEmpty()) {
            throw new UserNotFoundException("Sorry, user's session is expired!");
        }
        user.get().getPersonalCategories().addAll(categoryRepo.findAll());
        userRepo.save(user.get());
        return ResponseEntity.ok("Saved successfully");
    }

    @Override
    public HttpEntity<?> getRecommendedCategories() {
        Optional<User> user = userService.getUserFromContextHolder();
        if (user.isEmpty()) {
            throw new UserNotFoundException("Sorry, user's session is expired!");
        }
        return ResponseEntity.ok().body(user.get().getPersonalCategories());
    }
}
