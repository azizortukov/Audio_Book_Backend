package uz.audio_book.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.audio_book.backend.exceptions.ExceptionResponse;
import uz.audio_book.backend.service.BookService;
import uz.audio_book.backend.service.UserService;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/file")
@Tag(name = "File API")
public class FileController {

    private final BookService bookService;
    private final UserService userService;


    @Operation(summary = "Book's image by ID API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book's image is returned", content = @Content(mediaType = "image/jpeg")),
            @ApiResponse(responseCode = "204", description = "Book image is not uploaded"),
            @ApiResponse(responseCode = "400", description = "Provided ID's format is wrong",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authorization token is invalid or expired",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Book is not found by provided ID"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/image/{bookId}")
    public HttpEntity<?> getImage(@PathVariable UUID bookId) {
        return bookService.sendBookPicture(bookId);
    }


    @Operation(summary = "Book's PDF by ID API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book's PDF is returned", content = @Content(mediaType = "application/pdf")),
            @ApiResponse(responseCode = "204", description = "Book PDF is not uploaded"),
            @ApiResponse(responseCode = "400", description = "Provided ID's format is wrong",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authorization token is invalid or expired",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Book is not found by provided ID"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/pdf/{bookId}")
    public HttpEntity<?> getPDF(@PathVariable UUID bookId) {
        return bookService.sendBookPDF(bookId);
    }


    @Operation(summary = "Book's audio by ID API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book's audio is returned", content = @Content(mediaType = "application/octet-stream")),
            @ApiResponse(responseCode = "204", description = "Book audio is not uploaded"),
            @ApiResponse(responseCode = "400", description = "Provided ID's format is wrong",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authorization token is invalid or expired",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Book is not found by provided ID"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/audio/{bookId}")
    public HttpEntity<?> getAudio(@PathVariable UUID bookId) {
        return bookService.sendBookAudio(bookId);
    }


    @Operation(summary = "User's profile photo API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User's profile image is returned", content = @Content(mediaType = "image/jpeg")),
            @ApiResponse(responseCode = "204", description = "User's profile image is not uploaded"),
            @ApiResponse(responseCode = "401", description = "Authorization token is invalid or expired",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/user")
    public HttpEntity<?> getImage() {
        return userService.getUserPhoto();
    }


    @Operation(summary = "Update user's profile photo API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User's profile image successfully saved"),
            @ApiResponse(responseCode = "401", description = "Authorization token is invalid or expired",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PutMapping(value = "/user", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public HttpEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        return userService.updateUserPhoto(file);
    }

}
