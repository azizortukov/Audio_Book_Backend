package uz.audio_book.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.audio_book.backend.dto.LoginDTO;
import uz.audio_book.backend.dto.SignUpDTO;
import uz.audio_book.backend.service.JwtService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Authentication API", description = "(For Sign Up, Login, sending verification code")
public class AuthController {

    private final JwtService jwtService;

    @Operation(
            summary = "Sign up page API",
            description = """
            This API receives user details json and verification code is not needed. This API always sends
            token, because its new user. Please save that token as 'TempAuthorization' because security will
            get token from you in other page in that name. Please check email and password for format before
            requesting this API""")
    @PostMapping("/sign-up")
    public HttpEntity<?> signUp(@RequestBody SignUpDTO signUpDto) {
        return jwtService.giveAccountDetailsToken(signUpDto);
    }

    @Operation(
            summary = "Code verification page API",
            description = """
            This API requires TempAuthorization token, then the code that user has typed. The response will
            be either 200 with two tokens which are access token and refresh token or 400 (bad request) with
            its message in body""")
    @PostMapping("/sign-up/verify")
    public HttpEntity<?> signUpVerify(@RequestBody String verificationCode, HttpServletRequest request) {
        return jwtService.signUpVerifyCode(verificationCode, request);
    }

    @Operation(
            summary = "Code verification page API",
            description = """
            This API requires the TempAuthorization token. If the token is valid, the new verification code will
            be sent. If not, then 400 (bad request) with its message in body""")
    @PostMapping("/sign-up/resend")
    public HttpEntity<?> signUpResend(HttpServletRequest request) {
        return jwtService.resendSignUpVerificationCode(request);
    }

    @Operation(
            summary = "Login page API",
            description = """
            This API receives user details, please check for validity before requesting this API. If user details are correct
            two tokens will be send(refresh, access token). If not, then 400 error code with its message in body
            """)
    @PostMapping("/login")
    public HttpEntity<?> login(@RequestBody LoginDTO loginDto) {
        return jwtService.checkLoginDetails(loginDto);
    }

    @Operation(
            summary = "Forgot password page API",
            description = """
            Receives the email that user entered. Response will be TempAuthorization token. 400 error code with
            its message in body""")
    @PostMapping("/login/forgot-password")
    public HttpEntity<?> sendCode(@RequestBody String email) {
        return jwtService.sendAccessCode(email);
    }

    @Operation(
            summary = "Code confirmation page API",
            description = """
             This API receives TempAuthorization and the code that user has typed. 400 error code with its message in body""")
    @PostMapping("/login/confirm")
    public HttpEntity<?> accountAccess(@RequestBody String verificationCode, HttpServletRequest request) {
        return jwtService.checkVerificationCode(verificationCode, request);
    }


}
