package uz.audio_book.backend.service;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.audio_book.backend.model.dto.CommentDTO;

import java.util.List;
import java.util.UUID;

@Service
public interface BookService {

    HttpEntity<?> getBooksProjection();

    HttpEntity<?> sendBookPicture(UUID bookId);

    HttpEntity<?> getHomeData();

    HttpEntity<?> getAdminProjection();

    HttpEntity<?> saveBook(String title, String author, String description, List<UUID> categoryIds, MultipartFile photo, MultipartFile audio, MultipartFile pdf);

    void deleteById(UUID bookId);

    HttpEntity<?> sendBookPDF(UUID bookId);

    HttpEntity<?> sendBookAudio(UUID bookId);

    HttpEntity<?> getByAuthorOrTitle(String search);

    HttpEntity<?> getSelected(UUID id);

    HttpEntity<?> saveComment(CommentDTO commentDTO);
}
