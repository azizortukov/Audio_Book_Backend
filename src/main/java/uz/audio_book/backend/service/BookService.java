package uz.audio_book.backend.service;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface BookService {

    HttpEntity<?> getBooksProjection();

    HttpEntity<?> sendBookPicture(UUID bookId);
}
