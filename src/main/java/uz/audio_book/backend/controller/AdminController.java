package uz.audio_book.backend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.audio_book.backend.entity.Category;
import uz.audio_book.backend.repo.CategoryRepo;
import uz.audio_book.backend.service.BookService;
import uz.audio_book.backend.service.CategoryService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin API", description = "(Only for frontend! Cannot be used for flutter Development)")
public class AdminController {

    private final BookService bookService;
    private final CategoryService categoryService;
    private final CategoryRepo categoryRepo;

    @GetMapping("/book")
    public HttpEntity<?> getBooks() {
        return bookService.getAdminProjection();
    }

    @PostMapping(value = "/book", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public HttpEntity<?> addBook(
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam("description") String description,
            @RequestParam("categories") List<UUID> categoryIds,
            @RequestParam("photo") MultipartFile photo,
            @RequestParam("audio") MultipartFile audio,
            @RequestParam("pdf") MultipartFile pdf) {
        return bookService.saveBook(title, author, description, categoryIds, photo, audio, pdf);
    }

    @PutMapping(value = "/book", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public HttpEntity<?> updateBook(
            @RequestParam(value = "book_id") UUID bookId,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "author", required = false) String author,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "categories", required = false) List<UUID> categoryIds,
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            @RequestParam(value = "audio", required = false) MultipartFile audio,
            @RequestParam(value = "pdf", required = false) MultipartFile pdf) {
        return bookService.updateBook(bookId, title, author, description, categoryIds, photo, audio, pdf);
    }

    @DeleteMapping("/book/{bookId}")
    public void deleteBook(@PathVariable UUID bookId) {
        bookService.deleteById(bookId);
    }

    @GetMapping("/category")
    public HttpEntity<?> getCategories() {
        return categoryService.getCategories();
    }

    @PostMapping("/category")
    public HttpEntity<?> addCategory(@RequestParam("category_name") String categoryName) {
        categoryRepo.save(Category.builder().name(categoryName).build());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/category")
    public HttpEntity<?> updateCategory(@RequestBody Category category) {
        return categoryService.updateCategory(category);
    }

    @DeleteMapping("/category/{categoryId}")
    public void deleteCategory(@PathVariable UUID categoryId) {
        categoryService.deleteById(categoryId);
    }


}