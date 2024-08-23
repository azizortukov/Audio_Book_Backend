package uz.audio_book.backend.repo;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uz.audio_book.backend.entity.EmailCode;

import java.util.Optional;

@Repository
public interface EmailCodeRepo extends CrudRepository<EmailCode, String> {
    Optional<EmailCode> findEmailCodeByEmail(String email);
}
