package uz.audio_book.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.audio_book.backend.entity.Book;

import java.util.UUID;

public interface BookRepo extends JpaRepository<Book, UUID> {

//    List<BookProjection> findAllProjections();
}