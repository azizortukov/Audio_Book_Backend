package uz.audio_book.backend.service;

import org.springframework.stereotype.Service;
import uz.audio_book.backend.entity.Role;
import uz.audio_book.backend.entity.enums.RoleName;

@Service
public interface RoleService {

    Role findByName(RoleName name);

}
