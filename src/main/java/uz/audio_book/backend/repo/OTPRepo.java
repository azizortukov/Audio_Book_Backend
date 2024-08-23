package uz.audio_book.backend.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uz.audio_book.backend.entity.OTP;

@Repository
public interface OTPRepo extends CrudRepository<OTP, String> {
}
