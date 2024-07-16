package uz.audio_book.backend.component;

import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uz.audio_book.backend.entity.Category;
import uz.audio_book.backend.entity.Role;
import uz.audio_book.backend.entity.User;
import uz.audio_book.backend.entity.enums.RoleName;
import uz.audio_book.backend.repo.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Runner implements CommandLineRunner {

    private final RoleRepo roleRepo;
    private final CategoryRepo categoryRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;
    private final Faker faker;

    @Override
    public void run(String... args) {
        if (roleRepo.findAll().isEmpty()) {
            for (RoleName role : RoleName.values()) {
                roleRepo.save(Role.builder()
                        .roleName(role)
                        .build());
            }
        }
        if (categoryRepo.findAll().isEmpty() && userRepo.findAll().isEmpty()) {
            List<Category> categories = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                Category category = categoryRepo.save(Category.builder()
                        .name(faker.book().genre())
                        .build());
                categories.add(category);
            }
            userRepo.save(User.builder()
                    .roles(List.of(
                            roleRepo.findByRoleName(RoleName.ROLE_USER)
                    ))
                    .birthDate(LocalDate.of(2003, 5, 22))
                    .email("eshmat@gmail.com")
                    .password(passwordEncoder.encode("123"))
                    .personalCategories(List.of(
                            categories.get(0),
                            categories.get(1),
                            categories.get(2)
                    ))
                    .build());
            userRepo.save(User.builder()
                    .roles(List.of(
                            roleRepo.findByRoleName(RoleName.ROLE_ADMIN)
                    ))
                    .birthDate(LocalDate.of(2000, 11, 29))
                    .email("toshmat@gmail.com")
                    .displayName("Toshmat Toshmatjonov")
                    .password(passwordEncoder.encode("123"))
                    .personalCategories(List.of(
                            categories.get(3),
                            categories.get(4),
                            categories.get(5)
                    ))
                    .build());

        }
    }
}
