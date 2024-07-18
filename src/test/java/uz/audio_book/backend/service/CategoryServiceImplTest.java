package uz.audio_book.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uz.audio_book.backend.entity.Category;
import uz.audio_book.backend.entity.User;
import uz.audio_book.backend.exceptions.UserNotFoundException;
import uz.audio_book.backend.repo.CategoryRepo;
import uz.audio_book.backend.repo.UserRepo;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

class CategoryServiceImplTest {

    private CategoryServiceImpl categoryService;
    private CategoryRepo categoryRepo;
    private UserRepo userRepo;
    private UserService userService;


    @BeforeEach
    void setUp() {
        categoryRepo = Mockito.mock(CategoryRepo.class);
        userRepo = Mockito.mock(UserRepo.class);
        userService = Mockito.mock(UserService.class);
        categoryService = new CategoryServiceImpl(categoryRepo, userRepo, userService);
    }

    @Test
    void getCategories() {
        List<Category> categories = List.of(new Category());
        when(categoryRepo.findAll())
                .thenReturn(categories);

        var resp = (ResponseEntity<?>)categoryService.getCategories();
        assertEquals(categories, resp.getBody());
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void customizeCategoryByIdsEmptyList() {
        assertThrows(NullPointerException.class, () -> categoryService.customizeCategoryByIds(Collections.emptyList()));
    }

    @Test
    void customizeCategoryByIdsUserSessionExpired() {
        assertThrows(UserNotFoundException.class,
                ()->categoryService.customizeCategoryByIds(List.of(UUID.randomUUID())));
    }

    @Test
    void customizeCategoryByIds() {
        User user = User.builder()
                .personalCategories(new ArrayList<>(){{
                    add(new Category());
                }})
                .build();

        when(userService.getUserFromContextHolder())
                .thenReturn(Optional.of(user));
        when(categoryRepo.findAllById(anyList()))
                .thenReturn(List.of(new Category(), new Category()));
        var resp = (ResponseEntity<?>) categoryService.customizeCategoryByIds(List.of(UUID.randomUUID()));
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(3, user.getPersonalCategories().size());
    }

    @Test
    void customizeAllCategoriesUserSessionExpired() {
        assertThrows(UserNotFoundException.class, () ->categoryService.customizeAllCategories());
    }

    @Test
    void customizeAllCategories() {
        User user = User.builder()
                .personalCategories(new ArrayList<>(){{
                    add(new Category());
                }})
                .build();

        when(userService.getUserFromContextHolder())
                .thenReturn(Optional.of(user));

        when(categoryRepo.findAll())
                .thenReturn(List.of(new Category(), new Category()));

        var resp = (ResponseEntity<?>) categoryService.customizeAllCategories();
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(3, user.getPersonalCategories().size());
    }

    @Test
    void getRecommendedCategoriesUserSessionExpired() {
        assertThrows(UserNotFoundException.class, () -> categoryService.getRecommendedCategories());
    }

    @SuppressWarnings("unchecked")
    @Test
    void getRecommendedCategories() {
        User user = User.builder()
                .personalCategories(new ArrayList<>(){{
                    add(new Category());
                }})
                .build();

        when(userService.getUserFromContextHolder())
                .thenReturn(Optional.of(user));

        var resp = (ResponseEntity<?>)categoryService.getRecommendedCategories();
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        List<Category> body = (List<Category>) resp.getBody();
        assertNotNull(body);
        assertEquals(1, body.size());
    }
}