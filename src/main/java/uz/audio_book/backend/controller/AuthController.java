package uz.audio_book.backend.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.audio_book.backend.config.JwtUtil;
import uz.audio_book.backend.dto.LoginDto;
import uz.audio_book.backend.dto.SignUpDto;
import uz.audio_book.backend.dto.TokenDto;
import uz.audio_book.backend.entity.User;
import uz.audio_book.backend.service.UserServiceImpl;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserServiceImpl userServiceImpl;

    @PostMapping("/sign-up")
    public HttpEntity<?> login(@RequestBody SignUpDto signUpDto) {
        return ResponseEntity.ok(jwtUtil.generateVerificationCodeToken(signUpDto));
    }

    @PostMapping("/sign-up/verify")
    public HttpEntity<?> verify(@RequestBody String verificationCode, HttpServletRequest request) {
        String confirmation = request.getHeader("TempAuthorization");
        if (confirmation == null || !confirmation.startsWith("Confirmation")) {
            return ResponseEntity.badRequest().build();
        }
        String token = confirmation.substring(13);
        if (jwtUtil.checkVerificationCodeFromDto(verificationCode, token)) {
            User user = userServiceImpl.saveUserFromDto(jwtUtil.getDtoFromToken(token));
            return ResponseEntity.ok(new TokenDto(
                    jwtUtil.genToken(user),
                    jwtUtil.genRefreshToken(user)));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/sign-up/resend")
    public HttpEntity<?> verify(HttpServletRequest request) {
        String confirmation = request.getHeader("TempAuthorization");
        if (confirmation == null || !confirmation.startsWith("Confirmation")) {
            return ResponseEntity.badRequest().build();
        }
        String token = confirmation.substring(13);
        SignUpDto dto = jwtUtil.getDtoFromToken(token);
        return ResponseEntity.ok(
                jwtUtil.generateVerificationCodeToken(dto)
        );
    }

    @PostMapping("/login")
    public TokenDto login(@RequestBody LoginDto loginDto) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password()));
        return new TokenDto(
                jwtUtil.genToken((UserDetails)auth.getPrincipal()),
                jwtUtil.genRefreshToken((UserDetails)auth.getPrincipal()));
    }

    @PostMapping("/login/forgot-password")
    public HttpEntity<?> verify(@RequestBody String email) {
        var user = userServiceImpl.findByEmail(email);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(jwtUtil.generateCodeToken(email));
    }

    @PostMapping("/login/confirm")
    public HttpEntity<?> register(@RequestBody String verificationCode, HttpServletRequest request) {
        String confirmation = request.getHeader("TempAuthorization");
        if (confirmation == null || !confirmation.startsWith("Confirmation")) {
            return ResponseEntity.badRequest().build();
        }
        String token = confirmation.substring(13);
        if (jwtUtil.checkVerification(verificationCode, token)) {
            Optional<User> user = userServiceImpl.findByEmail(
                    jwtUtil.getEmailFromToken(token));
            return ResponseEntity.ok(new TokenDto(
                    jwtUtil.genToken(user.get()),
                    jwtUtil.genRefreshToken(user.get())
            ));
        }
        return ResponseEntity.badRequest().build();
    }

}
