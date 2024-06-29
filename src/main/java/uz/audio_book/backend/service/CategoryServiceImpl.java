package uz.audio_book.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uz.audio_book.backend.entity.Category;
import uz.audio_book.backend.entity.User;
import uz.audio_book.backend.repo.CategoryRepo;
import uz.audio_book.backend.repo.UserRepo;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepo categoryRepo;
    private final UserRepo userRepo;

    @Override
    public HttpEntity<?> getCategories() {
        return ResponseEntity.ok(categoryRepo.findAll());
    }

    @Override
    public HttpEntity<?> customizeCategoryByIds(List<UUID> categoryIds) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal().toString();
        Optional<User> user = userRepo.findByEmail(userEmail);

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
        String userEmail = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal().toString();
        Optional<User> user = userRepo.findByEmail(userEmail);

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
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Optional<User> user = userRepo.findByEmail(userEmail);
        if (user.isPresent()) {
            return ResponseEntity.ok().body(user.get().getPersonalCategories());
        } else {
            return ResponseEntity.badRequest().body("User not found");
        }
    }
}
