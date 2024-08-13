package uz.audio_book.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import uz.audio_book.backend.entity.User;
import uz.audio_book.backend.exceptions.BadRequestException;
import uz.audio_book.backend.exceptions.NotFoundException;
import uz.audio_book.backend.model.dto.LoginDTO;
import uz.audio_book.backend.model.dto.SignUpDTO;
import uz.audio_book.backend.model.dto.TempAuthorizationDTO;
import uz.audio_book.backend.model.dto.TokenDTO;
import uz.audio_book.backend.util.DateUtil;
import uz.audio_book.backend.util.JwtUtil;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class JwtService {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;


    public ResponseEntity<?> giveAccountDetailsToken(SignUpDTO signUpDto) {
        if (!DateUtil.isValidFormat(signUpDto.getBirthDate())) {
            throw new BadRequestException("Birth date format is incorrect! It should be like, 2000-05-27");
        }
        if (userService.existsByEmail(signUpDto.getEmail())) {
            throw new BadRequestException("User already exists!");
        }
        String token = jwtUtil.genVerificationCodeToken(signUpDto);
        return ResponseEntity.ok(new TempAuthorizationDTO(token));
    }

    public ResponseEntity<?> signUpVerifyCode(String verificationCode, String tempAuthorization) {
        if (!tempAuthorization.startsWith("Confirmation")) {
            throw new BadRequestException("Token is damaged. Please, try again");
        }
        String token = tempAuthorization.substring(13);
        if (!jwtUtil.checkVerificationCodeFromDto(verificationCode, token)) {
            throw new BadRequestException("Entered code is wrong! Please, try again!");
        }

        User user = userService.saveUserFromDto(jwtUtil.getDtoFromToken(token));
        return ResponseEntity.ok(new TokenDTO(jwtUtil.genToken(user), jwtUtil.genRefreshToken(user)));
    }

    public ResponseEntity<?> resendSignUpVerificationCode(String tempAuthorization) {
        if (!tempAuthorization.startsWith("Confirmation")) {
            throw new BadRequestException("Token is damaged. Please, try again!");
        }
        SignUpDTO dto = jwtUtil.getDtoFromToken(tempAuthorization.substring(13));
        String token = jwtUtil.genVerificationCodeToken(dto);
        return ResponseEntity.ok(new TempAuthorizationDTO(token));
    }

    public ResponseEntity<?> checkLoginDetails(LoginDTO loginDto) {
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

    public ResponseEntity<?> sendAccessCode(String email) {
        var user = userService.findByEmail(email);
        if (user.isEmpty()) {
            throw new NotFoundException("User doesn't exist. Please try again!");
        }
        String token = jwtUtil.generateCodeToken(email);
        return ResponseEntity.ok(new TempAuthorizationDTO(token));
    }

    public ResponseEntity<?> checkVerificationCode(String verificationCode, String tempAuthorization) {
        if (tempAuthorization != null && tempAuthorization.startsWith("Confirmation")) {
            String token = tempAuthorization.substring(13);
            if (jwtUtil.checkVerification(verificationCode, token)) {
                Optional<User> user = userService.findByEmail(
                        jwtUtil.getUsername(token));
                return ResponseEntity.ok(new TokenDTO(
                        jwtUtil.genToken(user.get()),
                        jwtUtil.genRefreshToken(user.get())
                ));
            }
        }
        throw new BadRequestException("Entered code is wrong! Please, try again!");
    }

}
