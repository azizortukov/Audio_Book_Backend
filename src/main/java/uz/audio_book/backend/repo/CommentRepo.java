package uz.audio_book.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.audio_book.backend.entity.Comment;
import uz.audio_book.backend.model.projection.CommentProjection;

import java.util.List;
import java.util.UUID;

public interface CommentRepo extends JpaRepository<Comment, UUID> {

    @Query(value = """
            select c.id, c.body, c.rating,
                   u.display_name as display_name,
                   u.id as userId
            from comment c
                join users u on c.user_id = u.id
                join book b on c.book_id = b.id
            where b.id = :id""", nativeQuery = true)
    List<CommentProjection> findByBookId(UUID id);

    void deleteByBookId(UUID bookId);

    Comment findByUserIdAndBookId(UUID userId, UUID bookId);
}