package uz.audio_book.backend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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

    @GetMapping("/book")
    public HttpEntity<?> getBooks() {
        return bookService.getAdminProjection();
    }

    @PostMapping("/book")
    public HttpEntity<?> addBook(@RequestParam("title") String title,
                                 @RequestParam("author") String author,
                                 @RequestParam("description") String description,
                                 @RequestParam("categories") List<UUID> categoryIds,
                                 @RequestParam("photo") MultipartFile photo,
                                 @RequestParam("audio") MultipartFile audio,
                                 @RequestParam("pdf") MultipartFile pdf) {
        return bookService.saveBook(title, author, description, categoryIds, photo, audio, pdf);
    }

    @DeleteMapping("/book/{bookId}")
    public void deleteBook(@PathVariable UUID bookId) {
        bookService.deleteById(bookId);
    }

    @GetMapping("/category")
    public HttpEntity<?> getCategories() {
        return categoryService.getCategories();
    }


}