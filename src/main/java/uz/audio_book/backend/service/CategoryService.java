package uz.audio_book.backend.service;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

@Service
public interface CategoryService {


    HttpEntity<?> getCategories();

}
