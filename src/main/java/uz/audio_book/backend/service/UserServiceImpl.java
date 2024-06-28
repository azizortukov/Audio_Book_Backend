package uz.audio_book.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.audio_book.backend.dto.SignUpDTO;
import uz.audio_book.backend.entity.User;
import uz.audio_book.backend.entity.enums.RoleName;
import uz.audio_book.backend.repo.UserRepo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return userRepo.save(User.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .roles(new ArrayList<>(List.of(
                        roleServiceImpl.findByName(RoleName.ROLE_USER)
                )))
                .birthDate(LocalDate.parse(dto.getBirthDate(), formatter))
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
}
