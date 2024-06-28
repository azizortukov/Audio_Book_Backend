package uz.audio_book.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.audio_book.backend.entity.Book;
import uz.audio_book.backend.repo.BookRepo;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepo bookRepo;

    @Override
    public HttpEntity<?> getBooksProjection() {
        return ResponseEntity.ok(null);
    }

    @Override
    public HttpEntity<byte[]> sendBookPicture(UUID bookId) {
        Optional<Book> bookById = bookRepo.findById(bookId);
        if (bookById.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Book book = bookById.get();
        HttpHeaders headers = new HttpHeaders();
//        Response type for image
//        headers.setContentType(MediaType.IMAGE_JPEG);
//        Response type for pdf
//        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "sample.mp3");
        headers.setContentLength(book.getPhoto().length);

        return ResponseEntity.ok()
                .headers(headers)
//                Response type for image
//                .contentType(MediaType.IMAGE_JPEG
//                 Response type for pdf
//                .contentType(MediaType.APPLICATION_PDF)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(book.getPhoto());
    }
}
