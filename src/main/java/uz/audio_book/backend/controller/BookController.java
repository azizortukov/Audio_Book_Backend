package uz.audio_book.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.audio_book.backend.service.BookService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/book")
@Tag(name = "Main page API", description = "(Sends four categories of books for each User)")
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

}
