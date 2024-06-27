package uz.audio_book.backend.service;

import org.springframework.stereotype.Service;
import uz.audio_book.backend.dto.SignUpDto;
import uz.audio_book.backend.entity.User;

import java.util.Optional;

@Service
public interface UserService {

    User saveUserFromDto(SignUpDto dtoFromToken);

    User save(User user);

    Optional<User> findByEmail(String email);
}
