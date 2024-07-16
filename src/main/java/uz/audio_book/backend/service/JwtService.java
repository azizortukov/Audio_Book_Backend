package uz.audio_book.backend.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import uz.audio_book.backend.dto.LoginDTO;
import uz.audio_book.backend.dto.SignUpDTO;
import uz.audio_book.backend.dto.TokenDTO;
import uz.audio_book.backend.entity.User;
import uz.audio_book.backend.exceptions.BadRequestException;
import uz.audio_book.backend.exceptions.HeaderException;
import uz.audio_book.backend.exceptions.NotFoundException;
import uz.audio_book.backend.exceptions.UserNotFoundException;
import uz.audio_book.backend.util.DateUtil;
import uz.audio_book.backend.util.JwtUtil;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class JwtService {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public HttpEntity<?> giveAccountDetailsToken(SignUpDTO signUpDto) {
        if (!DateUtil.isValidFormat(signUpDto.getBirthDate())) {
            throw new BadRequestException("Birth date format is incorrect");
        }
        if (userService.existsByEmail(signUpDto.getEmail())) {
            throw new BadRequestException("User already exists!");
        }
        return ResponseEntity.ok(jwtUtil.generateVerificationCodeToken(signUpDto));
    }

    public ResponseEntity<?> signUpVerifyCode(String verificationCode, HttpServletRequest request) {
        String confirmation = request.getHeader("TempAuthorization");
        if (confirmation == null || !confirmation.startsWith("Confirmation")) {
            throw new HeaderException("Expected TempAuthorization token in the header!");
        }
        String token = confirmation.substring(13);
        if (jwtUtil.checkVerificationCodeFromDto(verificationCode, token)) {
            User user = userService.saveUserFromDto(jwtUtil.getDtoFromToken(token));
            return ResponseEntity.ok(new TokenDTO(
                    jwtUtil.genToken(user),
                    jwtUtil.genRefreshToken(user)));
        } else {
            throw new BadRequestException("Entered code is wrong! Please, try again!");
        }
    }

    public ResponseEntity<?> resendSignUpVerificationCode(HttpServletRequest request) {
        String confirmation = request.getHeader("TempAuthorization");
        if (confirmation == null || !confirmation.startsWith("Confirmation")) {
            throw new BadRequestException("Session is expired! Please, return to sign up page!");
        }
        String token = confirmation.substring(13);
        SignUpDTO dto = jwtUtil.getDtoFromToken(token);
        return ResponseEntity.ok(
                jwtUtil.generateVerificationCodeToken(dto)
        );
    }

    public HttpEntity<?> checkLoginDetails(LoginDTO loginDto) {
       try {
           var auth = authenticationManager.authenticate(
                   new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password()));
           return ResponseEntity.ok(new TokenDTO(
                   jwtUtil.genToken((UserDetails) auth.getPrincipal()),
                   jwtUtil.genRefreshToken((UserDetails) auth.getPrincipal())));
       }catch (AuthenticationException e) {
           throw new UserNotFoundException("Email or password is incorrect. Please try again!");
       }
    }

    public ResponseEntity<?> sendAccessCode(String email) {
        var user = userService.findByEmail(email);
        if (user.isEmpty()) {
            throw new NotFoundException("User doesn't exist. Please try again!");
        }
        return ResponseEntity.ok(jwtUtil.generateCodeToken(email));
    }

    public ResponseEntity<?> checkVerificationCode(String verificationCode, HttpServletRequest request) {
        String confirmation = request.getHeader("TempAuthorization");
        if (confirmation != null && confirmation.startsWith("Confirmation")) {
            String token = confirmation.substring(13);
            if (jwtUtil.checkVerification(verificationCode, token)) {
                Optional<User> user = userService.findByEmail(
                        jwtUtil.getEmailFromToken(token));
                return ResponseEntity.ok(new TokenDTO(
                        jwtUtil.genToken(user.get()),
                        jwtUtil.genRefreshToken(user.get())
                ));
            }
            throw new BadRequestException("Entered code is wrong! Please, try again!");
        }
        throw new HeaderException("Expected TempAuthorization token in the header!");
    }

}
