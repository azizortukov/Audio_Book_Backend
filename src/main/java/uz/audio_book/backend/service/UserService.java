package uz.audio_book.backend.service;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.audio_book.backend.model.dto.SignUpDTO;
import uz.audio_book.backend.model.dto.UserDetailsDTO;
import uz.audio_book.backend.entity.User;

import java.util.Optional;

@Service
public interface UserService {

    User saveUserFromDto(SignUpDTO dtoFromToken);

    User save(User user);

    User getUserFromContextHolder();

    Optional<User> findByEmail(String email);

    HttpEntity<?> getUserDetails();

    HttpEntity<?> updateUserDetails(UserDetailsDTO userDetailsDTO);

    HttpEntity<?> updateUserPhoto(MultipartFile file);

    HttpEntity<?> getUserPhoto();

    boolean existsByEmail(String email);
}
