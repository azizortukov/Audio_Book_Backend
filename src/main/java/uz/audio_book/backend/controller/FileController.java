package uz.audio_book.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.audio_book.backend.service.BookService;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/file")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@Tag(name = "File API", description = "(Only for authorized users)")
public class FileController {

    private final BookService bookService;

    @Operation(
            summary = "Image API",
            description = """
                    This API receives only id of book. Response will be 200 (good status)
                    , 403 (unauthorized users) and 404 (not found) when book by that id is
                    not found""")
    @GetMapping("/image/{bookId}")
    public HttpEntity<?> getImage(@PathVariable UUID bookId) {
        return bookService.sendBookPicture(bookId);
    }

    @Operation(
            summary = "PDF API",
            description = """
                    This API receives only id of book. Response will be 200 (good status)
                    , 403 (unauthorized users) and 404 (not found) when book by that id is
                    not found""")
    @GetMapping("/pdf/{bookId}")
    public HttpEntity<?> getPDF(@PathVariable UUID bookId) {
        return bookService.sendBookPDF(bookId);
    }

    @Operation(
            summary = "Audio API",
            description = """
                    This API receives only id of book. Audio will be sent as attach for
                    storing the audio file. Response will be either 200 (good status)
                    , 403 (unauthorized users) and 404 (not found) when book by that id
                    is not found""")
    @GetMapping("/audio/{bookId}")
    public HttpEntity<?> getAudio(@PathVariable UUID bookId) {
        return bookService.sendBookAudio(bookId);
    }

}
