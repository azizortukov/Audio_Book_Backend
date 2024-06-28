package uz.audio_book.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.audio_book.backend.service.CategoryService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/category")
public class CategoryController {


    private final CategoryService categoryService;

    @GetMapping
    public HttpEntity<?> getCategory() {
        return categoryService.getCategories();
    }
}
