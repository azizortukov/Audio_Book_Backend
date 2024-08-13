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
import uz.audio_book.backend.entity.User;
import uz.audio_book.backend.entity.enums.RoleName;
import uz.audio_book.backend.exceptions.BadRequestException;
import uz.audio_book.backend.exceptions.NotFoundException;
import uz.audio_book.backend.model.dto.SignUpDTO;
import uz.audio_book.backend.model.dto.UserDetailsDTO;
import uz.audio_book.backend.repo.UserRepo;
import uz.audio_book.backend.util.DateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        User user = getUserFromContextHolder();
        return ResponseEntity.ok(userRepo.findByIdProjection(user.getId()));
    }

    @Override
    public HttpEntity<?> updateUserDetails(UserDetailsDTO userDetailsDTO) {
        if (!DateUtil.isValidFormat(userDetailsDTO.birthDate())) {
            throw new BadRequestException("Birth date format is incorrect! It should be like, 2000-05-27");
        }
        User user = getUserFromContextHolder();
        user.setEmail(userDetailsDTO.email());
        user.setDisplayName(userDetailsDTO.displayName());
        user.setBirthDate(DateUtil.parse(userDetailsDTO.birthDate()));
        userRepo.save(user);
        return ResponseEntity.noContent().build();
    }

    @SneakyThrows
    @Override
    public HttpEntity<?> updateUserPhoto(MultipartFile file) {
        User user = getUserFromContextHolder();
        user.setProfilePhoto(file.getBytes());
        userRepo.save(user);
        return ResponseEntity.noContent().build();
    }

    @SneakyThrows
    @Override
    public HttpEntity<?> getUserPhoto() {
        User user = getUserFromContextHolder();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setContentLength(user.getProfilePhoto().length);
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(user.getProfilePhoto());
        } catch (NullPointerException e) {
            return ResponseEntity.noContent().build();
        }

    }

    @Override
    public User getUserFromContextHolder() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return findByEmail(userEmail).orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepo.findByEmail(email).isPresent();
    }
}
