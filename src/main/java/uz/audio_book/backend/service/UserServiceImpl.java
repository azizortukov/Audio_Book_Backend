package uz.audio_book.backend.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.audio_book.backend.entity.EmailCode;
import uz.audio_book.backend.entity.OTP;
import uz.audio_book.backend.entity.User;
import uz.audio_book.backend.entity.enums.RoleName;
import uz.audio_book.backend.exceptions.BadRequestException;
import uz.audio_book.backend.exceptions.NotFoundException;
import uz.audio_book.backend.model.dto.*;
import uz.audio_book.backend.repo.EmailCodeRepo;
import uz.audio_book.backend.repo.OTPRepo;
import uz.audio_book.backend.repo.UserRepo;
import uz.audio_book.backend.util.DateUtil;
import uz.audio_book.backend.util.JwtUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final S3Service s3Service;
    private final MailService mailService;
    private final OTPRepo otpRepo;
    private final EmailCodeRepo emailCodeRepo;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public HttpEntity<?> cacheNewAccountDetails(SignUpDTO signUpDto) {
        if (!DateUtil.isValidFormat(signUpDto.getBirthDate())) {
            throw new BadRequestException("Birth date format is incorrect! It should be like, 2000-05-27");
        }
        if (userRepo.existsByEmail(signUpDto.getEmail())) {
            throw new BadRequestException("User already exists!");
        }

        mailService.sendConfirmationCode(signUpDto.getEmail());
        OTP otp = new OTP(signUpDto.getEmail(), signUpDto.getBirthDate(), signUpDto.getPassword());
        otpRepo.save(otp);
        return ResponseEntity.ok(new EmailDTO(signUpDto.getEmail()));
    }

    @Override
    public HttpEntity<?> verifyNewAccount(@Valid OTPVerifyDTO verifyDTO) {
        String email = verifyDTO.email();
        String code = String.valueOf(verifyDTO.code());

        EmailCode emailCode = emailCodeRepo.findById(email)
                .orElseThrow(() -> new BadRequestException("The email code already expired"));

        if (emailCode.getLastSentTime().plusMinutes(5).isBefore(LocalDateTime.now())) {
            throw new BadRequestException("The email code already expired");
        }

        if (!emailCode.getCode().equals(code)) {
            throw new BadRequestException("Email code doesn't match");
        }

        OTP otpUser = otpRepo.findById(email)
                .orElseThrow(() -> new NotFoundException(String.format("User with email = %s not found", email)));

        User user = User.builder()
                .email(otpUser.getEmail())
                .password(passwordEncoder.encode(otpUser.getPassword()))
                .birthDate(DateUtil.parse(otpUser.getBirthDate()))
                .roles(List.of(roleService.findByName(RoleName.ROLE_USER)))
                .build();
        userRepo.save(user);
        otpRepo.deleteById(email);

        return ResponseEntity.ok(new TokenDTO(jwtUtil.genToken(user), jwtUtil.genRefreshToken(user)));
    }

    @Override
    public HttpEntity<?> checkLoginDetails(LoginDTO loginDto) {
        try {
            var auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password()));
            return ResponseEntity.ok(new TokenDTO(
                    jwtUtil.genToken((UserDetails) auth.getPrincipal()),
                    jwtUtil.genRefreshToken((UserDetails) auth.getPrincipal())));
        } catch (AuthenticationException e) {
            throw new BadRequestException("Email or password is incorrect. Please try again!");
        }
    }

    @Override
    public HttpEntity<?> sendAccessLink(String email) {
        var user = userRepo.findByEmail(email);
        if (user.isEmpty()) {
            throw new NotFoundException("User doesn't exist. Please try again!");
        }
        mailService.sendPasswordResetLink(email);
        return ResponseEntity.ok(new EmailDTO(email));
    }

    @Override
    public HttpEntity<?> changeUserPassword(ChangePasswordDTO changePasswordDTO, String token) {
        if (!changePasswordDTO.confirmPassword().equals(changePasswordDTO.newPassword())) {
            throw new BadRequestException("Passwords do not match");
        }
        if (token != null) {
            String username = jwtUtil.getUsername(token);
            Optional<User> userOptional = userRepo.findByEmail(username);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.setPassword(passwordEncoder.encode(changePasswordDTO.newPassword()));
                userRepo.save(user);
                return ResponseEntity.ok(new TokenDTO(
                        jwtUtil.genToken(user),
                        jwtUtil.genRefreshToken(user)));
            }
        }
        throw new BadRequestException("Entered token is invalid");
    }

    @Override
    public HttpEntity<?> getUserDetails() {
        User user = getUserFromContextHolder();
        return ResponseEntity.ok(userRepo.findByIdProjection(user.getId()));
    }

    @Override
    public HttpEntity<?> updateUserDetails(UserDetailsDTO userDetailsDTO) {
        if (!DateUtil.isValidFormat(userDetailsDTO.birthDate())) {
            throw new BadRequestException("Birth date format is incorrect! It should be like, 2000-05-27");
        }
        User user = getUserFromContextHolder();
        user.setEmail(userDetailsDTO.email());
        user.setDisplayName(userDetailsDTO.displayName());
        user.setBirthDate(DateUtil.parse(userDetailsDTO.birthDate()));
        userRepo.save(user);
        return ResponseEntity.ok(userRepo.findByIdProjection(user.getId()));
    }

    @Override
    public HttpEntity<?> updateUserProfileImage(MultipartFile profileImage) {
        User user = getUserFromContextHolder();
        s3Service.uploadProfilePhoto(profileImage, user);
        return ResponseEntity.noContent().build();
    }

    @Override
    public User getUserFromContextHolder() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return userRepo.findByEmail(userEmail).orElseThrow(() -> new BadRequestException("Authentication exception"));
    }

}
