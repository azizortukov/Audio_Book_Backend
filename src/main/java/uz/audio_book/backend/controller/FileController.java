package uz.audio_book.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.audio_book.backend.service.BookService;
import uz.audio_book.backend.service.UserService;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/file")
@Tag(name = "File API", description = "(Only for authorized users)")
public class FileController {

    private final BookService bookService;
    private final UserService userService;

    @Operation(
            summary = "Image API",
            description = """
                    This API receives only id of book. The responses are 200 (success)
                    , 404 (not found) when book by that id is not found, 204(no content)
                     book's image is not found""")
    @GetMapping("/image/{bookId}")
    public HttpEntity<?> getImage(@PathVariable UUID bookId) {
        return bookService.sendBookPicture(bookId);
    }

    @Operation(
            summary = "PDF API",
            description = """
                    This API receives only id of book. The responses are 200(success)
                    , 404 (not found) when book by that id is not found, 204(no content)
                     book's pdf file is not found""")
    @GetMapping("/pdf/{bookId}")
    public HttpEntity<?> getPDF(@PathVariable UUID bookId) {
        return bookService.sendBookPDF(bookId);
    }

    @Operation(
            summary = "Audio API",
            description = """
                    This API receives only id of book. Audio will be sent as attach for
                    storing the audio file. The responses are 200 (success), 404 (not found)
                     when book by that id is not found, 204(no content) book's audio file
                    is not found""")
    @GetMapping("/audio/{bookId}")
    public HttpEntity<?> getAudio(@PathVariable UUID bookId) {
        return bookService.sendBookAudio(bookId);
    }

    @Operation(
            summary = "Profile photo API",
            description = """
                    This API sends profile photo of logged in user. The responses are 200
                    (success) with profile photo, 404 (not found)
                    , 204 (no content) meaning user doesn't have profile image.""")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved", content = @Content(mediaType = "image/jpeg"))
    })
    @GetMapping("/user")
    public HttpEntity<?> getImage() {
        return userService.getUserPhoto();
    }

    @Operation(
            summary = "Update profile photo API",
            description = """
                    This API receives image named as file in form-data. Image will be uploaded for whom
                    logged in. The responses are 200 (success), 403 (unauthorized user) token has been expired""")
    @PutMapping(value = "/user/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public HttpEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        return userService.updateUserPhoto(file);
    }

}
