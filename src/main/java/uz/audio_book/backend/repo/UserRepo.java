package uz.audio_book.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.audio_book.backend.entity.User;

import java.util.UUID;

public interface UserRepo extends JpaRepository<User, UUID> {
}