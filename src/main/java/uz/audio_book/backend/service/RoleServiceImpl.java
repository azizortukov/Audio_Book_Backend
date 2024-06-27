package uz.audio_book.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.audio_book.backend.entity.Role;
import uz.audio_book.backend.entity.enums.RoleName;
import uz.audio_book.backend.repo.RoleRepo;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepo roleRepo;

    @Override
    public Role findByName(RoleName roleName) {
        return roleRepo.findByRoleName(roleName);
    }
}
