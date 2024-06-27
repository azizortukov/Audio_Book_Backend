package uz.audio_book.backend.component;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uz.audio_book.backend.entity.Role;
import uz.audio_book.backend.entity.enums.RoleName;
import uz.audio_book.backend.repo.RoleRepo;

@Component
@RequiredArgsConstructor
public class Runner implements CommandLineRunner {

    private final RoleRepo roleRepo;

    @Override
    public void run(String... args) {
        if (roleRepo.findAll().isEmpty()) {
            for (RoleName role : RoleName.values()) {
                roleRepo.save(Role.builder()
                        .roleName(role)
                        .build());
            }
        }
    }
}
