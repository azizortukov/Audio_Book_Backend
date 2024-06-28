package uz.audio_book.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import uz.audio_book.backend.repo.CategoryRepo;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepo categoryRepo;

    @Override
    public HttpEntity<?> getCategories() {
        return new HttpEntity<>(categoryRepo.findAll());
    }


}
