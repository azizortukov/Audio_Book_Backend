package uz.audio_book.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.audio_book.backend.entity.Role;
import uz.audio_book.backend.entity.enums.RoleName;

import java.util.UUID;

public interface RoleRepo extends JpaRepository<Role, UUID> {

    Role findByRoleName(RoleName roleName);

}