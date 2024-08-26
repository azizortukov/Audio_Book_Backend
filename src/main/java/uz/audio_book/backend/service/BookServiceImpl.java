package uz.audio_book.backend.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import uz.audio_book.backend.entity.Book;
import uz.audio_book.backend.entity.Category;
import uz.audio_book.backend.entity.Comment;
import uz.audio_book.backend.entity.User;
import uz.audio_book.backend.exceptions.NotFoundException;
import uz.audio_book.backend.model.dto.BookCommentDTO;
import uz.audio_book.backend.model.dto.BookHomeDTO;
import uz.audio_book.backend.model.dto.CommentDTO;
import uz.audio_book.backend.model.projection.BookProjection;
import uz.audio_book.backend.model.projection.SelectedBookProjection;
import uz.audio_book.backend.repo.BookRepo;
import uz.audio_book.backend.repo.CategoryRepo;
import uz.audio_book.backend.repo.CommentRepo;
import uz.audio_book.backend.repo.UserRepo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepo bookRepo;
    private final CategoryRepo categoryRepo;
    private final UserService userService;
    private final CommentRepo commentRepo;
    private final S3Service s3Service;
    private final UserRepo userRepo;

    @Override
    public HttpEntity<?> getBooksProjection() {
        List<BookProjection> books = bookRepo.findAllProjections();
        return ResponseEntity.ok(books);
    }

    @Override
    public HttpEntity<?> getHomeData() {
        User user = userService.getUserFromContextHolder();
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
        return ResponseEntity.ok(new BookHomeDTO(newRelease, trendingNow, bestSeller, recommended));
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
                .categories(categories)
                .build();
        bookRepo.save(book);
        s3Service.uploadPhoto(photo, book);
        s3Service.uploadAudio(audio, book);
        s3Service.uploadPDF(pdf, book);
        return ResponseEntity.noContent().build();
    }

    @Override
    public void deleteById(@NonNull UUID bookId) {
        bookRepo.findById(bookId).orElseThrow(() -> new NotFoundException("Book not found"));
        commentRepo.deleteByBookId(bookId);
        userRepo.deleteMyBookById(bookId);
        bookRepo.deleteById(bookId);
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
        return ResponseEntity.ok(new BookCommentDTO(
                bookRepo.findSelectedBookByDetails(id),
                commentRepo.findByBookId(id))
        );
    }

    @Override
    public HttpEntity<?> saveComment(@RequestBody CommentDTO commentDTO) {
        User user = userService.getUserFromContextHolder();
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
        return ResponseEntity.noContent().build();
    }

    @Override
    public HttpEntity<?> updateBook(UUID bookId, String title, String author, String description, List<UUID> categoryIds, MultipartFile photo, MultipartFile audio, MultipartFile pdf) {
        Optional<Book> bookOptional = bookRepo.findById(bookId);
        if (bookOptional.isEmpty()) {
            throw new NotFoundException("Sorry, book is not found!");
        }
        Book book = bookOptional.get();
        book.setTitle(title != null ? title : book.getTitle());
        book.setAuthor(author != null ? author : book.getAuthor());
        book.setDescription(description != null ? description : book.getDescription());
        if (!categoryIds.isEmpty()) {
            List<Category> categories = categoryRepo.findAllById(categoryIds);
            book.setCategories(categories);
        }
        if (photo != null && !photo.isEmpty()) {
            s3Service.uploadPhoto(photo, book);
        }
        if (audio != null && !audio.isEmpty()) {
            s3Service.uploadAudio(audio, book);
        }
        if (pdf != null && !pdf.isEmpty()) {
            s3Service.uploadPDF(pdf, book);
        }
        bookRepo.save(book);
        return ResponseEntity.noContent().build();
    }
}
