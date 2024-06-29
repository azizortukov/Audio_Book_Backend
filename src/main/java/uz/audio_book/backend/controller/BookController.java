package uz.audio_book.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;
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
                    This API returns books. The response will be: 200 (success),
                    400 (bad request), 401 (un-authorized or not logged in)
                    or 403 (forbidden or token is expired)""")
    @GetMapping
    public HttpEntity<?> getBooks() {
        return bookService.getBooksProjection();
    }

    @Operation(
            summary = "Getting home Page API",
            description = """
                    This API returns books for Home page of User. The response will be: 200 (success),
                    400 (bad request), 401 (un-authorized or not logged in)
                    or 403 (forbidden or token is expired) and response will be
                    """)
    @GetMapping("/home")
    public HttpEntity<?> home() {
        return bookService.getHomeData();
    }

    @Operation(
            summary = "Searching books API",
            description = """
                    This API returns books for searched result that matches with author
                    name or book title. The response will be: 200 (success), 400 (bad request),
                    401 (un-authorized or not logged in) or 403 (forbidden or token is expired).
                    """)
    @GetMapping("/search/{search}")
    public HttpEntity<?> search(@PathVariable String search) {
        return bookService.getByAuthorOrTitle(search);
    }

    @Operation(
            summary = "Getting single book details with comments",
            description = """
                    This API returns selected book details with comments. The response will be: 200 (success), 
                    400 (bad request), 401 (un-authorized or not logged in) or 403 (forbidden or token is expired).
                    """)
    @GetMapping("{id}")
    public HttpEntity<?> getSelected(@PathVariable UUID id) {
        return bookService.getSelected(id);
    }


}
