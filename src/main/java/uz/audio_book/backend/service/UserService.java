package uz.audio_book.backend.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.audio_book.backend.entity.User;
import uz.audio_book.backend.model.dto.*;

@Service
public interface UserService {

    HttpEntity<?> cacheNewAccountDetails(@Valid SignUpDTO signUpDto);

    HttpEntity<?> verifyNewAccount(@Valid OTPVerifyDTO otpVerifyDTO);

    HttpEntity<?> checkLoginDetails(@Valid LoginDTO loginDto);

    HttpEntity<?> sendAccessLink(@Email(message = "Email format is not valid") @NotBlank(message = "Email cannot be blank") String email);

    HttpEntity<?> changeUserPassword(@Valid ChangePasswordDTO changePasswordDTO, String token);

    User getUserFromContextHolder();

    HttpEntity<?> getUserDetails();

    HttpEntity<?> updateUserDetails(UserDetailsDTO userDetailsDTO);

    HttpEntity<?> updateUserProfileImage(MultipartFile profileImage);
}
