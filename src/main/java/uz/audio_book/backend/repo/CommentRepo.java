package uz.audio_book.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.audio_book.backend.entity.Comment;

import java.util.UUID;

public interface CommentRepo extends JpaRepository<Comment, UUID> {
}