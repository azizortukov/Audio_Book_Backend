package uz.audio_book.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;
import uz.audio_book.backend.dto.CommentDTO;
import uz.audio_book.backend.service.BookService;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/book")
@Tag(name = "Book API", description = "(Sends four categories of books for each User)")
public class BookController {

    private final BookService bookService;

    @Operation(
            summary = "Getting all books Page API",
            description = """
                    This API returns all the books. The response are 200 (success)
                    , 403 (forbidden) or 401 (unauthorized) token is expired.""")
    @GetMapping
    public HttpEntity<?> getBooks() {
        return bookService.getBooksProjection();
    }

    @Operation(
            summary = "Getting home Page API",
            description = """
                    This API returns books for Home page of User. The response are 200 (success)
                    , 403 (forbidden) or 401 (unauthorized) token is expired.
                    """)
    @GetMapping("/home")
    public HttpEntity<?> home() {
        return bookService.getHomeData();
    }

    @Operation(
            summary = "Searching books API",
            description = """
                    This API returns books for searched result that matches with author
                    name or book title. The response are 200 (success), 403 (forbidden) or 401 (unauthorized) token is expired.
                    """)
    @GetMapping("/search/{search}")
    public HttpEntity<?> search(@PathVariable String search) {
        return bookService.getByAuthorOrTitle(search);
    }

    @Operation(
            summary = "Getting book details with comments by id",
            description = """
                    This API returns book's details with comments which will be matched. The responses are
                     200 (success), 403 (forbidden) or 401 (unauthorized) token is expired.
                    """)
    @GetMapping("{id}")
    public HttpEntity<?> getSelected(@PathVariable UUID id) {
        return bookService.getSelected(id);
    }

    @Operation(
            summary = "Leaving comment for a book",
            description = """
                    This API receives DTO of comment which has book id, rating that user left and comment boyd
                    if user left it. The responses are 200 (success), 403 (forbidden) or 401 (unauthorized) token is expired.
                    """)
    @PostMapping("comment")
    public HttpEntity<?> addComment(@RequestBody @Valid CommentDTO commentDTO) {
        return bookService.saveComment(commentDTO);
    }


}
