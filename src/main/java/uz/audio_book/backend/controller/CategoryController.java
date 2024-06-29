package uz.audio_book.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.audio_book.backend.service.CategoryService;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/category")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@Tag(name = "Category API", description = "(Only for authorized users)")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(
            summary = "Category API",
            description = """
            This API returns list of all categories""")
    @GetMapping
    public HttpEntity<?> getCategory() {
        return categoryService.getCategories();
    }

    @Operation(
            summary = "Category Customization API",
            description = """
            This API receives list of category ids. Then, sets that categories
            to user preferred categories""")
    @PostMapping("/customize")
    public HttpEntity<?> customizeCategory(@RequestBody List<UUID> categoryIds) {
        return categoryService.customizeCategoryByIds(categoryIds);
    }

    @Operation(
            summary = "Categories Customization API",
            description = """
            This API sets that all categories to user preferred categories""")
    @PostMapping("/customize/all")
    public HttpEntity<?> customizeCategories() {
        return categoryService.customizeAllCategories();
    }

    @Operation(
            summary = "Categories Recommended categories",
            description = """
            This API returns recommended categories to current user""")
    @GetMapping("recommended")
    public HttpEntity<?> getRecommended(){
        return categoryService.getRecommendedCategories();
    }
}
