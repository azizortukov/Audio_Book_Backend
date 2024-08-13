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
        String photoUrl = s3Service.uploadFile(photo);
        String audioUrl = s3Service.uploadFile(audio);
        String pdfUrl = s3Service.uploadFile(pdf);
        book.setPhotoUrl(photoUrl);
        book.setAudioUrl(audioUrl);
        book.setPdfUrl(pdfUrl);
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
        if (bookById.get().getPhotoUrl() == null || bookById.get().getPhotoUrl().isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok()
                .body(bookById.get().getPhotoUrl());
    }

    @Override
    public HttpEntity<?> sendBookPDF(@NonNull UUID bookId) {
        Optional<Book> bookById = bookRepo.findById(bookId);
        if (bookById.isEmpty()) {
            throw new NotFoundException("Sorry, book is not found!");
        }
        if (bookById.get().getPdfUrl() == null || bookById.get().getPdfUrl().isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok()
                .body(bookById.get().getPdfUrl());
    }

    @Override
    public HttpEntity<?> sendBookAudio(@NonNull UUID bookId) {
        Optional<Book> bookById = bookRepo.findById(bookId);
        if (bookById.isEmpty()) {
            throw new NotFoundException("Sorry, book is not found!");
        }
        if (bookById.get().getAudioUrl() == null || bookById.get().getAudioUrl().isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().body(bookById.get().getAudioUrl());
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
}
