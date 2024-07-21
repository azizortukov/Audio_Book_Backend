package uz.audio_book.backend.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uz.audio_book.backend.entity.Role;
import uz.audio_book.backend.entity.enums.RoleName;
import uz.audio_book.backend.repo.RoleRepo;

import java.util.UUID;

import static org.mockito.Mockito.*;

class RoleServiceImplTest {

    private RoleServiceImpl roleService;
    private RoleRepo roleRepo;

    @BeforeEach
    void setUp() {
        roleRepo = mock(RoleRepo.class);
        roleService = new RoleServiceImpl(roleRepo);
    }

    @Test
    void findByName() {
        UUID roleId = UUID.randomUUID();

        when(roleRepo.findByRoleName(any(RoleName.class)))
                .thenReturn(new Role(roleId, RoleName.ROLE_USER));

        Role role = roleService.findByName(RoleName.ROLE_USER);
        Assertions.assertEquals(roleId, role.getId());
    }
}