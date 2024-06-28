package uz.audio_book.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uz.audio_book.backend.entity.Book;
import uz.audio_book.backend.entity.Category;
import uz.audio_book.backend.entity.User;
import uz.audio_book.backend.projection.BookProjection;
import uz.audio_book.backend.repo.BookRepo;
import uz.audio_book.backend.repo.UserRepo;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final AuthenticationManager authenticationManager;
    private final BookRepo bookRepo;
    private final UserRepo userRepo;

    @Override
    public HttpEntity<?> getBooksProjection() {
        List<BookProjection> books = bookRepo.findAllProjections();
        return ResponseEntity.ok(books);
    }

    @Override
    public HttpEntity<?> getHomeData() {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        User user = userRepo.findByEmail(email).get();
        List<UUID> ids = user.getPersonalCategories().stream().map(Category::getId).toList();

        List<BookProjection> newRelease =  bookRepo.findNewRelease();
        List<BookProjection> trendingNow = bookRepo.findTrendingNow();
        BookProjection bestSeller = bookRepo.findBestSeller();
        List<BookProjection> recommended = new ArrayList<>();
        if (ids.isEmpty()) {
            recommended = bookRepo.findByPersonalCategories(ids);
        } else {
            recommended = trendingNow;
        }
        Map<String, Object> result = Map.of(
                "new-release", newRelease,
                "trending-now", trendingNow,
                "best-seller", bestSeller,
                "recommended", recommended
        );

        return ResponseEntity.ok(result);
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
