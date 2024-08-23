package uz.audio_book.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;
import uz.audio_book.backend.exceptions.ExceptionResponse;
import uz.audio_book.backend.model.dto.*;
import uz.audio_book.backend.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Authentication API", description = "(For Sign Up, Login, sending verification code and verification)")
public class AuthController {

    private final UserService userService;

    @Operation(summary = "Sign up API", description = "This API receives user details and returns user's email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Confirmation code is sent to user's email",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmailDTO.class))),
            @ApiResponse(responseCode = "400", description = "Missing field, invalid data or user already exists",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping("/sign-up")
    public HttpEntity<?> signUp(@RequestBody @Valid SignUpDTO signUpDto) {
        return userService.cacheNewAccountDetails(signUpDto);
    }


    @Operation(summary = "Code verification API", description = "User's email and OTP expected")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Code is correct.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenDTO.class))),
            @ApiResponse(responseCode = "400", description = "Provided code is wrong, missing, expired or email invalid",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping("/sign-up/verify")
    public HttpEntity<?> signUpVerify(@RequestBody @Valid OTPVerifyDTO otpVerifyDTO) {
        return userService.verifyNewAccount(otpVerifyDTO);
    }


    @Operation(summary = "Login API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User details are right",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenDTO.class))),
            @ApiResponse(responseCode = "400", description = "Params or user details are wrong",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping("/login")
    public HttpEntity<?> login(@RequestBody @Valid LoginDTO loginDto) {
        return userService.checkLoginDetails(loginDto);
    }


    @Operation(summary = "Forgot password API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verification code is sent",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmailDTO.class))),
            @ApiResponse(responseCode = "400", description = "Params are wrong",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "404", description = "User us not found by provided email"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping("/login/forgot-password")
    public HttpEntity<?> sendCode(@RequestBody @Valid EmailDTO emailDTO) {
        return userService.sendAccessLink(emailDTO.email());
    }


    @Operation(summary = "User password change API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Access and refresh tokens are provided",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenDTO.class))),
            @ApiResponse(responseCode = "400", description = "Either entered passwords doesn't match, or token invalid",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping("/login/confirm")
    public HttpEntity<?> accountAccess(@RequestBody @Valid ChangePasswordDTO changePasswordDTO, @RequestHeader("Reset-Password-Token") String token) {
        return userService.changeUserPassword(changePasswordDTO, token);
    }
}