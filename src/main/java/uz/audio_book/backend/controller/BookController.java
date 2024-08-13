package uz.audio_book.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;
import uz.audio_book.backend.exceptions.ExceptionResponse;
import uz.audio_book.backend.model.dto.BookCommentDTO;
import uz.audio_book.backend.model.dto.BookHomeDTO;
import uz.audio_book.backend.model.dto.CommentDTO;
import uz.audio_book.backend.model.projection.BookProjection;
import uz.audio_book.backend.model.projection.SelectedBookProjection;
import uz.audio_book.backend.service.BookService;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/book")
@Tag(name = "Book API")
public class BookController {

    private final BookService bookService;

    @Operation(summary = "All books API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Books returned",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookProjection.class))),
            @ApiResponse(responseCode = "401", description = "Authorization token is invalid or expired",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping
    public HttpEntity<?> getBooks() {
        return bookService.getBooksProjection();
    }


    @Operation(summary = "Clients' book API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Books returned",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookHomeDTO.class))),
            @ApiResponse(responseCode = "401", description = "Authorization token is invalid or expired",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/home")
    public HttpEntity<?> home() {
        return bookService.getHomeData();
    }


    @Operation(summary = "Searching books API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found books",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SelectedBookProjection.class)))),
            @ApiResponse(responseCode = "401", description = "Authorization token is invalid or expired",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/search/{search}")
    public HttpEntity<?> search(@PathVariable String search) {
        return bookService.getByAuthorOrTitle(search);
    }


    @Operation(summary = "Getting book details by ID API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found books",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookCommentDTO.class))),
            @ApiResponse(responseCode = "400", description = "Provided ID's format is wrong",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authorization token is invalid or expired",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Book not found by provided ID"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/{id}")
    public HttpEntity<?> getSelected(@PathVariable UUID id) {
        return bookService.getSelected(id);
    }


    @Operation(summary = "Leaving comment for a book by ID API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Comment saved, no content returned"),
            @ApiResponse(responseCode = "400", description = "Param is not valid or not provided",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authorization token is invalid or expired",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping("/comment")
    public HttpEntity<?> addComment(@RequestBody @Valid CommentDTO commentDTO) {
        return bookService.saveComment(commentDTO);
    }


}
