package uz.audio_book.backend.service;

import lombok.NonNull;
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
import uz.audio_book.backend.exceptions.ContentNotFound;
import uz.audio_book.backend.exceptions.NotFoundException;
import uz.audio_book.backend.exceptions.UserNotFoundException;
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
        List<BookProjection> recommended;
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
    public void deleteById(@NonNull UUID bookId) {
        bookRepo.deleteById(bookId);
    }

    @Override
    public HttpEntity<?> sendBookPicture(@NonNull UUID bookId) {
        Optional<Book> bookById = bookRepo.findById(bookId);
        if (bookById.isEmpty()) {
            throw new NotFoundException("Sorry, book is not found!");
        }
        if (bookById.get().getPhoto() == null) {
            throw new ContentNotFound("Sorry, book image is not found!");
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
    public HttpEntity<?> sendBookPDF(@NonNull UUID bookId) {
        Optional<Book> bookById = bookRepo.findById(bookId);
        if (bookById.isEmpty()) {
            throw new NotFoundException("Sorry, book is not found!");
        }
        if (bookById.get().getPdf() == null) {
            throw new ContentNotFound("Sorry, book pdf file is not found!");
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
    public HttpEntity<?> sendBookAudio(@NonNull UUID bookId) {
        Optional<Book> bookById = bookRepo.findById(bookId);
        if (bookById.isEmpty()) {
            throw new NotFoundException("Sorry, book is not found!");
        }
        if (bookById.get().getAudio() == null) {
            throw new ContentNotFound("Sorry, book audio file is not found!");
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
    public HttpEntity<?> getByAuthorOrTitle(@NonNull String search) {
        String searchBy = "%" + search + "%";
        List<SelectedBookProjection> searchedBooks = bookRepo.findAllByAuthorOrTitle(searchBy);
        return ResponseEntity.ok(searchedBooks);
    }

    @Override
    public HttpEntity<?> getSelected(@NonNull UUID id) {
        if (!bookRepo.existsById(id)) {
            throw new NotFoundException("Sorry, book is not found!");
        }
        SelectedBookProjection book = bookRepo.findSelectedBookByDetails(id);
        List<CommentProjection> bookComments = commentRepo.findByBookId(id);
        Map<Object, Object> result = new LinkedHashMap<>();
        result.put("book", book);
        result.put("comments", bookComments);
        return ResponseEntity.ok(result);
    }

    @Override
    public HttpEntity<?> saveComment(@RequestBody CommentDTO commentDTO) {
        Optional<User> userOpt = userService.getUserFromContextHolder();
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("Sorry, user's session is expired!");
        }
        User user = userOpt.get();
        // Checking if user left comment before, if yes then comment will be updated, if no then
        // new comment will be added
        Comment comment = commentRepo.findByUserIdAndBookId(user.getId(), commentDTO.bookId());
        if (comment != null) {
            comment.setRating(commentDTO.rating());
            comment.setBody(commentDTO.body());
            commentRepo.save(comment);
        } else {
            Optional<Book> book = bookRepo.findById(commentDTO.bookId());
            if (book.isEmpty()) {
                throw new NotFoundException("Sorry, book is not found!");
            }
            commentRepo.save(Comment.builder()
                    .user(user)
                    .book(book.get())
                    .rating(commentDTO.rating())
                    .body(commentDTO.body())
                    .build());
        }
        return ResponseEntity.ok("success");
    }
}
