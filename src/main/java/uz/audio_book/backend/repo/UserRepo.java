package uz.audio_book.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.audio_book.backend.entity.User;
import uz.audio_book.backend.model.projection.UserDetailsProjection;

import java.util.Optional;
import java.util.UUID;

public interface UserRepo extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    @Query(value = """
            SELECT u.display_name, u.email ,u.birth_date, u.profile_photo_url FROM users u
            WHERE u.id = :id LIMIT 1
            """, nativeQuery = true)
    UserDetailsProjection findByIdProjection(UUID id);

    boolean existsByEmail(String email);

}