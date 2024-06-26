package uz.audio_book.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.audio_book.backend.entity.Category;

import java.util.UUID;

public interface CategoryRepo extends JpaRepository<Category, UUID> {
}