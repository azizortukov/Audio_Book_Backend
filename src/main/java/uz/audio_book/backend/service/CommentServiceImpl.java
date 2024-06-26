package uz.audio_book.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.audio_book.backend.repo.CommentRepo;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepo commentRepo;

}
