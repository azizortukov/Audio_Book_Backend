package uz.audio_book.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.audio_book.backend.entity.User;
import uz.audio_book.backend.projection.UserDetailsProjection;

import java.util.Optional;
import java.util.UUID;

public interface UserRepo extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    @Query(value = """
            SELECT u.id, u.display_name, u.email ,u.birth_date FROM users u
            WHERE u.id = :id LIMIT 1
            """, nativeQuery = true)
    UserDetailsProjection findByIdProjection(UUID id);

}