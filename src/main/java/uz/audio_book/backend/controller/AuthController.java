package uz.audio_book.backend.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;
import uz.audio_book.backend.exceptions.ExceptionResponse;
import uz.audio_book.backend.model.dto.LoginDTO;
import uz.audio_book.backend.model.dto.SignUpDTO;
import uz.audio_book.backend.model.dto.TempAuthorizationDTO;
import uz.audio_book.backend.model.dto.TokenDTO;
import uz.audio_book.backend.service.JwtService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Authentication API", description = "(For Sign Up, Login, sending verification code and verification)")
public class AuthController {

    private final JwtService jwtService;


    @Operation(summary = "Sign up API", description = "This API receives user details and returns token which will be needed in code verification API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Confirmation code is sent to user's email",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TempAuthorizationDTO.class))),
            @ApiResponse(responseCode = "400", description = "Missing field, invalid data or user already exists",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping("/sign-up")
    public HttpEntity<?> signUp(@RequestBody @Valid SignUpDTO signUpDto) {
        return jwtService.giveAccountDetailsToken(signUpDto);
    }


    @Operation(summary = "Code verification API", description = "The temp_authorization token is expected")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Code is correct.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid token, entered code is wrong or code is not provided.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping("/sign-up/verify")
    public HttpEntity<?> signUpVerify(@RequestBody @Valid CodeDTO codeDTO, @RequestHeader("TempAuthorization") String tempAuthorization) {
        return jwtService.signUpVerifyCode(codeDTO.verificationCode(), tempAuthorization);
    }


    @Operation(summary = "Resend, code verification API", description = "The temp_authorization token is expected and new verification code will be sent.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New code is sent and temp_authorization is provided",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TempAuthorizationDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid token, entered code is wrong or code is not provided.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping("/sign-up/resend")
    public HttpEntity<?> signUpResend(@RequestHeader("TempAuthorization") String tempAuthorization) {
        return jwtService.resendSignUpVerificationCode(tempAuthorization);
    }


    @Operation(summary = "Login API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User details are right",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenDTO.class))),
            @ApiResponse(responseCode = "400", description = "Params are wrong or user details are wrong",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping("/login")
    public HttpEntity<?> login(@RequestBody @Valid LoginDTO loginDto) {
        return jwtService.checkLoginDetails(loginDto);
    }


    @Operation(summary = "Forgot password API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verification code is sent, temp_authorization token is given",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenDTO.class))),
            @ApiResponse(responseCode = "400", description = "Params are wrong",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "404", description = "User us not found by provided email"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping("/login/forgot-password")
    public HttpEntity<?> sendCode(@RequestBody @Valid EmailDTO emailDTO) {
        return jwtService.sendAccessCode(emailDTO.email());
    }


    @Operation(summary = "Code confirmation API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verification code is right, Tokens are provided",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenDTO.class))),
            @ApiResponse(responseCode = "400", description = "Either entered code, params are wrong",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping("/login/confirm")
    public HttpEntity<?> accountAccess(@RequestBody @Valid CodeDTO codeDTO, @RequestHeader("TempAuthorization") String tempAuthorization) {
        return jwtService.checkVerificationCode(codeDTO.verificationCode(), tempAuthorization);
    }
}

record CodeDTO(
        @NotBlank(message = "Verification code cannot be blank") @JsonProperty("verification_code") String verificationCode) {
}

record EmailDTO(
        @Email(message = "Email format is not valid") @NotBlank(message = "Email cannot be blank") String email) {
}