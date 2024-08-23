package uz.audio_book.backend.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.audio_book.backend.entity.Book;
import uz.audio_book.backend.entity.User;
import uz.audio_book.backend.repo.BookRepo;
import uz.audio_book.backend.repo.UserRepo;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;
    private final BookRepo bookRepo;
    private final UserRepo userRepo;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("уyуу-MM-dd");

    @Value("${keys.bucket-name}")
    private String bucketName;

    @Async
    public void uploadPhoto(@NotNull MultipartFile file, @NotNull Book book) {
        try {
            deleteFile(book.getPhotoUrl());
            String photoUrl = uploadFile(file);
            book.setPhotoUrl(photoUrl);
            bookRepo.save(book);
        } catch (IOException e) {
            throw new RuntimeException("Error uploading file to S3", e);
        }
    }

    @Async
    public void uploadAudio(@NotNull MultipartFile file, @NotNull Book book) {
        try {
            deleteFile(book.getAudioUrl());
            String audioUrl = uploadFile(file);
            book.setAudioUrl(audioUrl);
            bookRepo.save(book);
        } catch (IOException e) {
            throw new RuntimeException("Error uploading file to S3", e);
        }
    }

    @Async
    public void uploadPDF(@NotNull MultipartFile file, @NotNull Book book) {
        try {
            deleteFile(book.getPdfUrl());
            String pdfUrl = uploadFile(file);
            book.setPdfUrl(pdfUrl);
            bookRepo.save(book);
        } catch (IOException e) {
            throw new RuntimeException("Error uploading file to S3", e);
        }
    }

    @Async
    public void uploadProfilePhoto(MultipartFile file, User user) {
        try {
            deleteFile(user.getProfilePhotoUrl());
            String profilePhotoUrl = uploadFile(file);
            user.setProfilePhotoUrl(profilePhotoUrl);
            userRepo.save(user);
        } catch (IOException e) {
            throw new RuntimeException("Error uploading file to S3", e);
        }
    }

    private String uploadFile(@NotNull MultipartFile file) throws IOException {
        String todayDate = dateTimeFormatter.format(LocalDate.now());
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        String fileName = todayDate + "/" + file.getOriginalFilename();
        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), objectMetadata));
        return amazonS3.getUrl(bucketName, fileName).toString();
    }

    private void deleteFile(String fileName) {
        if (fileName != null && !fileName.isEmpty() && amazonS3.doesObjectExist(bucketName, fileName)) {
            amazonS3.deleteObject(bucketName, fileName);
        }
    }
}
