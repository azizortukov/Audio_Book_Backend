package uz.audio_book.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.audio_book.backend.dto.SignUpDTO;
import uz.audio_book.backend.dto.UserDetailsDTO;
import uz.audio_book.backend.entity.User;
import uz.audio_book.backend.entity.enums.RoleName;
import uz.audio_book.backend.repo.UserRepo;
import uz.audio_book.backend.util.DateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final RoleServiceImpl roleServiceImpl;

    @Override
    public User saveUserFromDto(SignUpDTO dto) {
        return userRepo.save(User.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .roles(new ArrayList<>(List.of(roleServiceImpl.findByName(RoleName.ROLE_USER))))
                .birthDate(DateUtil.parse(dto.getBirthDate()))
                .build());
    }

    @Override
    public User save(User user) {
        return userRepo.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public HttpEntity<?> getUserDetails() {
        Optional<User> user = getUserFromContextHolder();
        if (user.isPresent()) {
            return ResponseEntity.ok(userRepo.findByIdProjection(user.get().getId()));
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    public HttpEntity<?> updateUserDetails(UserDetailsDTO userDetailsDTO) {
        if (!DateUtil.isValidFormat(userDetailsDTO.birthDate())) {
            return ResponseEntity.badRequest()
                    .body("Birth date is not a valid format");
        }
        Optional<User> contextUser = getUserFromContextHolder();
        if (contextUser.isPresent()) {
            User user = contextUser.get();
            user.setEmail(userDetailsDTO.email());
            user.setDisplayName(userDetailsDTO.displayName());
            user.setBirthDate(DateUtil.parse(userDetailsDTO.birthDate()));
            userRepo.save(user);
            return ResponseEntity.ok("User updated successfully");
        }
        return ResponseEntity.notFound().build();
    }

    @SneakyThrows
    @Override
    public HttpEntity<?> updateUserPhoto(MultipartFile file) {
        Optional<User> user = getUserFromContextHolder();
        if (user.isPresent()) {
            user.get().setProfilePhoto(file.getBytes());
            userRepo.save(user.get());
            return ResponseEntity.ok("User profile image updated successfully");
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    public HttpEntity<?> getUserPhoto() {
        Optional<User> user = getUserFromContextHolder();
        if (user.isPresent()) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_JPEG);
                headers.setContentLength(user.get().getProfilePhoto().length);
                return ResponseEntity.ok()
                        .headers(headers)
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(user.get().getProfilePhoto());
            } catch (NullPointerException e) {
                return ResponseEntity.noContent().build();
            }
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    public Optional<User> getUserFromContextHolder() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return findByEmail(userEmail);
    }

    @Override
    public User findById(UUID uuid) {
        return userRepo.findById(uuid).orElse(null);
    }

    @Override
    public boolean existsByEmail(String email) {
         return userRepo.findByEmail(email).isPresent();
    }
}
