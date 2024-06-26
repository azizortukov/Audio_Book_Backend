package uz.audio_book.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.audio_book.backend.repo.BookRepo;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepo bookRepo;

}
