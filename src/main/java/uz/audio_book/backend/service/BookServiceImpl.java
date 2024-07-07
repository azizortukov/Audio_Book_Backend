package uz.audio_book.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import uz.audio_book.backend.dto.CommentDTO;
import uz.audio_book.backend.entity.Book;
import uz.audio_book.backend.entity.Category;
import uz.audio_book.backend.entity.Comment;
import uz.audio_book.backend.entity.User;
import uz.audio_book.backend.projection.BookProjection;
import uz.audio_book.backend.projection.CommentProjection;
import uz.audio_book.backend.projection.SelectedBookProjection;
import uz.audio_book.backend.repo.BookRepo;
import uz.audio_book.backend.repo.CategoryRepo;
import uz.audio_book.backend.repo.CommentRepo;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepo bookRepo;
    private final CategoryRepo categoryRepo;
    private final UserService userService;
    private final CommentRepo commentRepo;


    @Override
    public HttpEntity<?> getBooksProjection() {
        List<BookProjection> books = bookRepo.findAllProjections();
        return ResponseEntity.ok(books);
    }

    @Override
    public HttpEntity<?> getHomeData() {
        User user = userService.getUserFromContextHolder().get();
        List<UUID> ids = user.getPersonalCategories().stream().map(Category::getId).toList();

        List<BookProjection> newRelease = bookRepo.findNewRelease();
        List<BookProjection> trendingNow = bookRepo.findTrendingNow();
        List<BookProjection> bestSeller = bookRepo.findBestSeller();
        List<BookProjection> recommended = new ArrayList<>();
        if (ids.isEmpty()) {
            recommended = trendingNow;
        } else {
            recommended = bookRepo.findByPersonalCategories(ids);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("recommended", recommended);
        result.put("best-seller", bestSeller);
        result.put("new-release", newRelease);
        result.put("trending-now", trendingNow);
        return ResponseEntity.ok(result);
    }

    @Override
    public HttpEntity<?> getAdminProjection() {
        return ResponseEntity.ok(bookRepo.findAllAdminBookProjection());
    }

    @SneakyThrows
    @Override
    public HttpEntity<?> saveBook(String title, String author, String description, List<UUID> categoryIds, MultipartFile photo, MultipartFile audio, MultipartFile pdf) {
        List<Category> categories = categoryRepo.findAllById(categoryIds);
        Book book = Book.builder()
                .title(title)
                .author(author)
                .description(description)
                .photo(photo.getBytes())
                .pdf(pdf.getBytes())
                .audio(audio.getBytes())
                .categories(categories)
                .build();
        return ResponseEntity.ok(bookRepo.save(book));
    }

    @Override
    public void deleteById(UUID bookId) {
        bookRepo.deleteById(bookId);
    }

    @Override
    public HttpEntity<?> sendBookPicture(UUID bookId) {
        Optional<Book> bookById = bookRepo.findById(bookId);
        if (bookById.isEmpty() || bookById.get().getPhoto() == null) {
            return ResponseEntity.notFound().build();
        }
        Book book = bookById.get();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(book.getPhoto().length);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.IMAGE_JPEG)
                .body(book.getPhoto());
    }

    @Override
    public HttpEntity<?> sendBookPDF(UUID bookId) {
        Optional<Book> bookById = bookRepo.findById(bookId);
        if (bookById.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Book book = bookById.get();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentLength(book.getPdf().length);
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(book.getPdf());
    }

    @Override
    public HttpEntity<?> sendBookAudio(UUID bookId) {
        Optional<Book> bookById = bookRepo.findById(bookId);
        if (bookById.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Book book = bookById.get();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", book.getTitle() + ".mp3");
        headers.setContentLength(book.getAudio().length);
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(book.getAudio());
    }

    @Override
    public HttpEntity<?> getByAuthorOrTitle(String search) {
        String searchBy = "%" + search + "%";
        List<BookProjection> searchedBooks = bookRepo.findAllByAuthorOrTitle(searchBy);
        return ResponseEntity.ok(searchedBooks);
    }

    @Override
    public HttpEntity<?> getSelected(UUID id) {
        SelectedBookProjection selectedBook = bookRepo.findSelectedBookByDetails(id);
        List<CommentProjection> bookComments = commentRepo.findByBookId(id);
        List<Object> result = List.of(
                selectedBook,
                bookComments
        );
        return ResponseEntity.ok(result);
    }

    @Override
    public HttpEntity<?> saveComment(@RequestBody CommentDTO commentDTO) {
        Optional<User> userOpt = userService.getUserFromContextHolder();
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User user = userOpt.get();
        Comment comment = commentRepo.findByUserIdAndBookId(user.getId(), commentDTO.bookId());
        if (comment != null) {
            comment.setRating(commentDTO.rating());
            comment.setBody(commentDTO.body());
            commentRepo.save(comment);
        } else {
            Book book = findById(commentDTO.bookId());
            commentRepo.save(Comment.builder()
                    .user(user)
                    .book(book)
                    .rating(commentDTO.rating())
                    .body(commentDTO.body())
                    .build());
        }
        return ResponseEntity.status(201).body("success");
    }

    @Override
    public Book findById(UUID uuid) {
        return bookRepo.findById(uuid).orElse(null);
    }
}
