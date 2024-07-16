package uz.audio_book.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
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
@Tag(name = "Authentication API", description = "(For Sign Up, Login, sending verification code)")
public class AuthController {

    private final JwtService jwtService;

    @Operation(
            summary = "Sign up API",
            description = """
            This API receives user details json and verification code is not needed. This API always sends
            token, because its new user. Please save that token as 'TempAuthorization' because security will
            get token from you in other page in that name. Please check email and password for format before
            requesting this API. Response will be 200 (success), 400 (bad request) email or date of birth format
            is not valid""")
    @PostMapping("/sign-up")
    public HttpEntity<?> signUp(@RequestBody @Valid SignUpDTO signUpDto) {
        return jwtService.giveAccountDetailsToken(signUpDto);
    }

    @Operation(
            summary = "Code verification API",
            description = """
            This API requires TempAuthorization token, then the code that user has typed. The response will
            be 200(success) with two tokens which are access token and refresh token, 400 (bad request) entered
            code is wrong, 401 (unauthorized) expected TempAuth token is not received""")
    @PostMapping("/sign-up/verify")
    public HttpEntity<?> signUpVerify(@RequestBody @Valid CodeDTO codeDTO, HttpServletRequest request) {
        return jwtService.signUpVerifyCode(codeDTO.verificationCode(), request);
    }

    @Operation(
            summary = "Code verification API",
            description = """
            This API requires the TempAuthorization token. The new code will be sent. The responses are 200(success)
            with new TempAuthorization token, (401) token's been already expired""")
    @PostMapping("/sign-up/resend")
    public HttpEntity<?> signUpResend(HttpServletRequest request) {
        return jwtService.resendSignUpVerificationCode(request);
    }

    @Operation(
            summary = "Login API",
            description = """
            This API receives user details, please check for validity before requesting this API. The responses are
            200(success) with access & refresh tokens, 401(unauthorized) if entered user details are wrong
            """)
    @PostMapping("/login")
    public HttpEntity<?> login(@RequestBody @Valid LoginDTO loginDto) {
        return jwtService.checkLoginDetails(loginDto);
    }

    @Operation(
            summary = "Forgot password API",
            description = """
            Receives the email that user entered. The responses are 200 (success) with TempAuthorization token,
            404(not found) user is not found under entered email""")
    @PostMapping("/login/forgot-password")
    public HttpEntity<?> sendCode(@RequestBody @Valid EmailDTO emailDTO) {
        return jwtService.sendAccessCode(emailDTO.email());
    }

    @Operation(
            summary = "Code confirmation API",
            description = """
             This API receives TempAuthorization and the code that user has typed. The responses are 200(success) with
             access & refresh tokens, 401(unauthorized) expected TempAuth token is not received, 400(bad request) sent
             code is wrong""")
    @PostMapping("/login/confirm")
    public HttpEntity<?> accountAccess(@RequestBody @Valid CodeDTO codeDTO, HttpServletRequest request) {
        return jwtService.checkVerificationCode(codeDTO.verificationCode(), request);
    }
}

record CodeDTO(@NotBlank(message = "Verification code cannot be blank") String verificationCode) {}

record EmailDTO(@Email(message = "Email format is not valid") @NotBlank(message = "Email cannot be blank") String email) {}
