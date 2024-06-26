package uz.audio_book.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.audio_book.backend.repo.UserRepo;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepo userRepo;

}
