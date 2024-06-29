package uz.audio_book.backend.component;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uz.audio_book.backend.entity.*;
import uz.audio_book.backend.entity.enums.RoleName;
import uz.audio_book.backend.repo.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class Runner implements CommandLineRunner {

    private final RoleRepo roleRepo;
    private final CategoryRepo categoryRepo;
    private final BookRepo bookRepo;
    private final CommentRepo commentRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddl;

    @Override
    public void run(String... args) {
        if (roleRepo.findAll().isEmpty()) {
            for (RoleName role : RoleName.values()) {
                roleRepo.save(Role.builder()
                        .roleName(role)
                        .build());
            }
        }
        if (ddl.equals("create")) {
            Random random = new Random();
            List<Role> roles = roleRepo.findAll();
            List<Category> categories = new ArrayList<>();
            List<Book> books = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                Category category = categoryRepo.save(Category.builder()
                        .name("test-category-" + i)
                        .build());
                categories.add(category);
            }
            List<User> users = List.of(
                    userRepo.save(User.builder()
                            .roles(List.of(
                                    roleRepo.findByRoleName(RoleName.ROLE_USER)
                            ))
                            .email("eshmat")
                            .password(passwordEncoder.encode("123"))
                            .personalCategories(List.of(
                                    categories.get(0),
                                    categories.get(1),
                                    categories.get(2)
                            ))
                            .build()),
                    userRepo.save(User.builder()
                            .roles(List.of(
                                    roleRepo.findByRoleName(RoleName.ROLE_ADMIN)
                            ))
                            .email("toshmat")
                            .password(passwordEncoder.encode("123"))
                            .personalCategories(List.of(
                                    categories.get(3),
                                    categories.get(4),
                                    categories.get(5)
                            ))
                            .build()));


            for (int i = 0; i < 50; i++) {
                Book book = bookRepo.save(Book.builder()
                        .title("asd")
                        .author("qwe")
                        .description("qwe")
                        .createdAt(LocalDateTime.now())
                        .categories(List.of(
                                categories.get(random.nextInt(0, 10)),
                                categories.get(random.nextInt(0, 10))
                        ))
                        .build());
                books.add(book);
            }
            for (int i = 0; i < 1000; i++) {
                commentRepo.save(Comment.builder()
                        .rating(random.nextInt(0,6))
                        .book(books.get(random.nextInt(0, 50)))
                        .user(users.get(random.nextInt(0, 2)))
                        .build());
            }
        }
    }
}
