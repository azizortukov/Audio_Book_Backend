package uz.audio_book.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import uz.audio_book.backend.model.dto.CommentDTO;
import uz.audio_book.backend.entity.Book;
import uz.audio_book.backend.entity.Category;
import uz.audio_book.backend.entity.Comment;
import uz.audio_book.backend.entity.User;
import uz.audio_book.backend.exceptions.ContentNotFound;
import uz.audio_book.backend.exceptions.NotFoundException;
import uz.audio_book.backend.exceptions.UserNotFoundException;
import uz.audio_book.backend.model.projection.BookProjection;
import uz.audio_book.backend.repo.BookRepo;
import uz.audio_book.backend.repo.CategoryRepo;
import uz.audio_book.backend.repo.CommentRepo;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class BookServiceImplTest {

    private BookServiceImpl bookService;
    private BookRepo bookRepo;
    private CategoryRepo categoryRepo;
    private UserService userService;
    private CommentRepo commentRepo;
    private Book book;

    @BeforeEach
    void setUp() {
        bookRepo = mock(BookRepo.class);
        categoryRepo = mock(CategoryRepo.class);
        userService = mock(UserService.class);
        commentRepo = mock(CommentRepo.class);
        bookService = new BookServiceImpl(bookRepo, categoryRepo, userService, commentRepo);
        book = Book.builder()
                .title("Title")
                .author("Author")
                .description("Hello")
                .categories(new ArrayList<>())
                .photo(new byte[]{1, 23, 54})
                .audio(new byte[]{32, 43, 54})
                .pdf(new byte[]{32, 43, 54})
                .build();
    }

    @Test
    @SuppressWarnings("unchecked")
    void getBooksProjection() {
        when(bookRepo.findAllProjections())
                .thenReturn(Collections.EMPTY_LIST);
        ResponseEntity<?> resp = (ResponseEntity<?>) bookService.getBooksProjection();

        List<BookProjection> body = (List<BookProjection>) resp.getBody();

        assertNotNull(body);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    @SuppressWarnings("unchecked")
    void getHomeDataUserDoesntHaveCustomCategories() {
        when(bookRepo.findNewRelease())
                .thenReturn(Collections.EMPTY_LIST);
        when(bookRepo.findTrendingNow())
                .thenReturn(Collections.EMPTY_LIST);
        when(bookRepo.findBestSeller())
                .thenReturn(Collections.EMPTY_LIST);
        when(userService.getUserFromContextHolder())
                .thenReturn(Optional.of(User.builder()
                        .personalCategories(Collections.EMPTY_LIST)
                        .build()));

        ResponseEntity<?> resp = (ResponseEntity<?>) bookService.getHomeData();
        Map<String, Object> body = (Map<String, Object>) resp.getBody();

        assertNotNull(body);
        assertTrue(body.containsKey("recommended"));
        assertTrue(body.containsKey("best-seller"));
        assertTrue(body.containsKey("new-release"));
        assertTrue(body.containsKey("trending-now"));
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    @SuppressWarnings("unchecked")
    void getHomeDataUserHasCustomCategories() {
        when(bookRepo.findNewRelease())
                .thenReturn(Collections.EMPTY_LIST);
        when(bookRepo.findTrendingNow())
                .thenReturn(Collections.EMPTY_LIST);
        when(bookRepo.findBestSeller())
                .thenReturn(Collections.EMPTY_LIST);
        when(bookRepo.findByPersonalCategories(Collections.emptyList()))
                .thenReturn(Collections.EMPTY_LIST);
        when(userService.getUserFromContextHolder())
                .thenReturn(Optional.of(User.builder()
                        .personalCategories(List.of(new Category()))
                        .build()));

        ResponseEntity<?> resp = (ResponseEntity<?>) bookService.getHomeData();
        Map<String, Object> body = (Map<String, Object>) resp.getBody();

        assertNotNull(body);
        assertTrue(body.containsKey("recommended"));
        assertTrue(body.containsKey("best-seller"));
        assertTrue(body.containsKey("new-release"));
        assertTrue(body.containsKey("trending-now"));
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    @SuppressWarnings("unchecked")
    void getAdminProjection() {
        when(bookRepo.findAllAdminBookProjection())
                .thenReturn(Collections.EMPTY_LIST);

        ResponseEntity<?> resp = (ResponseEntity<?>) bookService.getAdminProjection();
        List<BookProjection> body = (List<BookProjection>) resp.getBody();

        assertNotNull(body);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void saveBook() {
        when(categoryRepo.findAllById(anyList())).thenReturn(new ArrayList<>());
        when(bookRepo.save(any(Book.class))).thenReturn(book);

        ResponseEntity<?> response = (ResponseEntity<?>) bookService.saveBook(
                "Title", "Author", "Hello", new ArrayList<>(),
                new MockMultipartFile("Photo", new byte[]{1, 23, 54}),
                new MockMultipartFile("Audio", new byte[]{32, 43, 54}),
                new MockMultipartFile("PDF", new byte[]{32, 43, 54})
        );

        Book body = (Book) response.getBody();
        assertNotNull(body, "Response body should not be null");
        assertEquals("Title", body.getTitle());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deleteById() {
        bookService.deleteById(UUID.randomUUID());
        assertTrue(true);
    }

    @Test
    void sendBookPictureBookNotFound() {
        assertThrows(NotFoundException.class, () -> bookService.sendBookPicture(UUID.randomUUID()));
    }

    @Test
    void sendBookPictureBookImageNotFound() {
        book.setPhoto(null);
        when(bookRepo.findById(any(UUID.class)))
                .thenReturn(Optional.of(book));
        assertThrows(ContentNotFound.class, () -> bookService.sendBookPicture(UUID.randomUUID()));
    }

    @Test
    void sendBookPicture() {
        when(bookRepo.findById(any(UUID.class)))
                .thenReturn(Optional.of(book));

        ResponseEntity<?> resp = (ResponseEntity<?>)bookService.sendBookPicture(UUID.randomUUID());
        HttpHeaders headers = resp.getHeaders();

        assertEquals(MediaType.IMAGE_JPEG, headers.getContentType());
        assertEquals(book.getPhoto().length, headers.getContentLength());
        assertEquals(resp.getBody(), book.getPhoto());
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void sendBookPDFBookNotFound() {
        assertThrows(NotFoundException.class, () -> bookService.sendBookPDF(UUID.randomUUID()));
    }

    @Test
    void sendBookPDFBookPDFNotFound() {
        book.setPdf(null);
        when(bookRepo.findById(any(UUID.class)))
                .thenReturn(Optional.of(book));
        assertThrows(ContentNotFound.class, () -> bookService.sendBookPDF(UUID.randomUUID()));
    }

    @Test
    void sendBookPDF() {
        when(bookRepo.findById(any(UUID.class)))
                .thenReturn(Optional.of(book));

        ResponseEntity<?> resp = (ResponseEntity<?>)bookService.sendBookPDF(UUID.randomUUID());
        HttpHeaders headers = resp.getHeaders();

        assertEquals(MediaType.APPLICATION_PDF, headers.getContentType());
        assertEquals(book.getPhoto().length, headers.getContentLength());
        assertEquals(resp.getBody(), book.getPdf());
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void sendBookAudioBookNotFound() {
        assertThrows(NotFoundException.class, () -> bookService.sendBookAudio(UUID.randomUUID()));
    }

    @Test
    void sendBookAudioBookAudioNotFound() {
        book.setAudio(null);
        when(bookRepo.findById(any(UUID.class)))
                .thenReturn(Optional.of(book));
        assertThrows(ContentNotFound.class, () -> bookService.sendBookAudio(UUID.randomUUID()));
    }

    @Test
    void sendBookAudio() {
        when(bookRepo.findById(any(UUID.class)))
                .thenReturn(Optional.of(book));

        ResponseEntity<?> resp = (ResponseEntity<?>)bookService.sendBookAudio(UUID.randomUUID());
        HttpHeaders headers = resp.getHeaders();

        assertEquals(MediaType.APPLICATION_OCTET_STREAM, headers.getContentType());
        assertEquals(book.getPhoto().length, headers.getContentLength());
        assertEquals(resp.getBody(), book.getAudio());
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void getByAuthorOrTitle() {
        String search = "Author";
        String searchBy = "%" + search + "%";

        when(bookRepo.findAllByAuthorOrTitle(searchBy))
                .thenReturn(Collections.EMPTY_LIST);

        ResponseEntity<?> response = (ResponseEntity<?>) bookService.getByAuthorOrTitle(search);

        assertEquals(Collections.EMPTY_LIST, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(bookRepo, times(1)).findAllByAuthorOrTitle(searchBy);
    }

    @Test
    void getSelectedDoesntExist () {
        when(bookRepo.existsById(any(UUID.class))).thenReturn(false);

        assertThrows(NotFoundException.class, () -> bookService.getSelected(UUID.randomUUID()));
        verify(bookRepo, times(1)).existsById(any(UUID.class));
    }

    @Test
    void getSelected() {
        when(bookRepo.existsById(any(UUID.class))).thenReturn(true);

        ResponseEntity<?> response = (ResponseEntity<?>) bookService.getSelected(UUID.randomUUID());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(bookRepo, times(1)).existsById(any(UUID.class));

    }

    @Test
    void saveCommentUserSessionExpired() {
        assertThrows(UserNotFoundException.class, ()->bookService.saveComment(new CommentDTO()));
        verify(userService, times(1)).getUserFromContextHolder();
    }

    @Test
    void saveCommentUserHasComment() {
        when(userService.getUserFromContextHolder())
                .thenReturn(Optional.of(User
                        .builder()
                        .id(UUID.randomUUID())
                        .build()));

        when(commentRepo.findByUserIdAndBookId(any(UUID.class), any(UUID.class)))
                .thenReturn(new Comment());

        ResponseEntity<?> resp = (ResponseEntity<?>) bookService
                .saveComment(new CommentDTO(UUID.randomUUID(), 3, "wassup"));

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(resp.getBody(), "success");
    }

    @Test
    void saveCommentUserHasNotCommentAndBookNotFound() {
        User user = User.builder()
                .personalCategories(new ArrayList<>(){{
                    add(new Category());
                }})
                .build();

        when(userService.getUserFromContextHolder())
                .thenReturn(Optional.of(user));
        when(commentRepo.findByUserIdAndBookId(any(UUID.class), any(UUID.class)))
                .thenReturn(null);
        when(bookRepo.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookService.saveComment(new CommentDTO(UUID.randomUUID(), 3, "wassup")));
    }

    @Test
    void saveCommentUserHasNotComment() {
        User user = User.builder()
                .personalCategories(new ArrayList<>(){{
                    add(new Category());
                }})
                .build();

        when(userService.getUserFromContextHolder())
                .thenReturn(Optional.of(user));
        when(commentRepo.findByUserIdAndBookId(any(UUID.class), any(UUID.class)))
                .thenReturn(null);
        when(bookRepo.findById(any(UUID.class)))
                .thenReturn(Optional.of(new Book()));
        var resp = (ResponseEntity<?>)bookService.saveComment(new CommentDTO(UUID.randomUUID(), 3, "wassup"));

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        verify(commentRepo, times(1)).save(any(Comment.class));

    }
}