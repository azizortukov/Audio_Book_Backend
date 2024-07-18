package uz.audio_book.backend.service;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import uz.audio_book.backend.dto.LoginDTO;
import uz.audio_book.backend.dto.SignUpDTO;
import uz.audio_book.backend.dto.TokenDTO;
import uz.audio_book.backend.entity.User;
import uz.audio_book.backend.exceptions.BadRequestException;
import uz.audio_book.backend.exceptions.HeaderException;
import uz.audio_book.backend.exceptions.NotFoundException;
import uz.audio_book.backend.exceptions.UserNotFoundException;
import uz.audio_book.backend.util.JwtUtil;

import java.util.Optional;


class JwtServiceTest {
    private JwtUtil jwtUtil;
    private JwtService jwtService;
    private UserService userService;
    private AuthenticationManager authenticationManager;


    @BeforeEach
    void beforeEach() {
        userService = Mockito.mock(UserService.class);
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        jwtUtil = Mockito.mock(JwtUtil.class);
        jwtService = new JwtService(jwtUtil, userService, authenticationManager);
    }

    // giveAccountDetailsToken test
    @Test
    void birthDateFormatInvalid() {
        SignUpDTO signUpDTO = new SignUpDTO("a@gmail.com", "root123", "123", "123");
        Assertions.assertThrows(
                BadRequestException.class,
                () -> {
                    jwtService.giveAccountDetailsToken(signUpDTO);
                }
        );
    }

    @Test
    void emailAlreadyExists() {
        SignUpDTO signUpDTO = new SignUpDTO("a@gmail.com", "root123", "1999-08-15", "123");
        Mockito.when(userService.existsByEmail(Mockito.anyString()))
                .thenReturn(true);
        Assertions.assertThrows(
                BadRequestException.class,
                () -> {
                    jwtService.giveAccountDetailsToken(signUpDTO);
                }
        );
    }

    @Test
    void giveAccountDetailsTokenSuccess() {
        SignUpDTO signUpDTO = new SignUpDTO("a@gmail.com", "root123", "1999-08-15", "123");
        Mockito.when(userService.existsByEmail(signUpDTO.getEmail())).thenReturn(false);
        Mockito.when(jwtUtil.generateVerificationCodeToken(signUpDTO)).thenReturn("token");

        ResponseEntity<?> response = jwtService.giveAccountDetailsToken(signUpDTO);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("token", response.getBody());
        Mockito.verify(userService).existsByEmail(signUpDTO.getEmail());
        Mockito.verify(jwtUtil).generateVerificationCodeToken(signUpDTO);
    }

    // signUpVerifyCode test
    @Test
    void signUpVerificationRequestHeaderInvalid() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader("TempAuthorization")).thenReturn("Starts With Not Confirmation");
        Assertions.assertThrows(
                HeaderException.class,
                () -> {
                    jwtService.signUpVerifyCode("", request);
                }
        );
    }

    @Test
    void signUpVerificationRequestHeaderNull() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader("TempAuthorization")).thenReturn(null);
        Assertions.assertThrows(
                HeaderException.class,
                () -> {
                    jwtService.signUpVerifyCode("", request);
                }
        );
    }

    @Test
    void signUpVerificationVerificationCodeInvalid() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader("TempAuthorization")).thenReturn("Confirmation token");
        String verificationCode = "123";
        String token = "token";
        Mockito.when(jwtUtil.checkVerificationCodeFromDto(verificationCode, token)).thenReturn(false);
        Assertions.assertThrows(
                BadRequestException.class,
                () -> {
                    jwtService.signUpVerifyCode(verificationCode, request);
                }
        );
    }

    @Test
    void signUpVerifyCodeSuccess() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        String verificationCode = "123";
        String token = "token";
        String tempAuthorizationHeader = "Confirmation " + token;

        SignUpDTO signUpDTO = new SignUpDTO(
                "a@gmail.com",
                "root123",
                "1999-08-15",
                "123");
        TokenDTO tokenDTO = new TokenDTO(
                "Bearer token",
                "Bearer token"
        );

        Mockito.when(request.getHeader("TempAuthorization"))
                .thenReturn(tempAuthorizationHeader);
        Mockito.when(jwtUtil.checkVerificationCodeFromDto(verificationCode, token))
                .thenReturn(true);
        Mockito.when(jwtUtil.getDtoFromToken(token))
                .thenReturn(signUpDTO);
        Mockito.when(userService.saveUserFromDto(signUpDTO))
                .thenReturn(new User());
        Mockito.when(jwtUtil.genToken(Mockito.any(User.class)))
                .thenReturn("Bearer token");
        Mockito.when(jwtUtil.genRefreshToken(Mockito.any(User.class)))
                .thenReturn("Bearer token");


        ResponseEntity<?> response = jwtService.signUpVerifyCode(verificationCode, request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(tokenDTO, response.getBody());

        // Verify the substring extraction
        Mockito.verify(request).getHeader("TempAuthorization");
        Mockito.verify(jwtUtil).checkVerificationCodeFromDto(verificationCode, token);
        Mockito.verify(jwtUtil).getDtoFromToken(token);
    }

    // resendSignUpVerificationCode test
    @Test
    void resendSignUpVerificationRequestHeaderInvalid() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader("TempAuthorization")).thenReturn("Starts With Not Confirmation");
        Assertions.assertThrows(
                HeaderException.class,
                () -> {
                    jwtService.resendSignUpVerificationCode(request);
                }
        );
    }

    @Test
    void resendSignUpVerificationRequestHeaderNull() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader("TempAuthorization")).thenReturn(null);
        Assertions.assertThrows(
                HeaderException.class,
                () -> {
                    jwtService.resendSignUpVerificationCode(request);
                }
        );
    }

    @Test
    void resendSignUpVerificationCodeSuccess() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        String token = "token";
        String tempAuthorizationHeader = "Confirmation " + token;

        SignUpDTO signUpDTO = new SignUpDTO(
                "a@gmail.com",
                "root123",
                "1999-08-15",
                "123");
        String confirmationToken = "Confirmation token";

        Mockito.when(request.getHeader("TempAuthorization"))
                .thenReturn(tempAuthorizationHeader);
        Mockito.when(jwtUtil.getDtoFromToken(token))
                .thenReturn(signUpDTO);
        Mockito.when(jwtUtil.generateVerificationCodeToken(signUpDTO))
                .thenReturn("Confirmation token");


        ResponseEntity<?> response = jwtService.resendSignUpVerificationCode(request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(confirmationToken, response.getBody());

        // Verify the substring extraction
        Mockito.verify(request).getHeader("TempAuthorization");
        Mockito.verify(jwtUtil).getDtoFromToken(token);
    }

    // checkLoginDetails test
    @Test
    void checkLoginDetailsSuccess() {
        LoginDTO loginDto = new LoginDTO("a@gmail.com", "root123");
        Authentication authentication = Mockito.mock(Authentication.class);
        UserDetails userDetails = Mockito.mock(UserDetails.class);

        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);
        Mockito.when(authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password())))
                .thenReturn(authentication);
        Mockito.when(jwtUtil.genToken(userDetails)).thenReturn("Bearer access-token");
        Mockito.when(jwtUtil.genRefreshToken(userDetails)).thenReturn("Bearer refresh-token");

        ResponseEntity<?> response = jwtService.checkLoginDetails(loginDto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        TokenDTO tokenDTO = (TokenDTO) response.getBody();
        Assertions.assertNotNull(tokenDTO);
        Assertions.assertEquals("Bearer access-token", tokenDTO.accessToken());
        Assertions.assertEquals("Bearer refresh-token", tokenDTO.refreshToken());

        Mockito.verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password()));
        Mockito.verify(jwtUtil).genToken(userDetails);
        Mockito.verify(jwtUtil).genRefreshToken(userDetails);
    }

    @Test
    void checkLoginDetailsFailure() {
        LoginDTO loginDto = new LoginDTO("a@gmail.com", "root123");
        Mockito.when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password())
        )).thenThrow(new UserNotFoundException("Authentication Failed"));

        Assertions.assertThrows(
                UserNotFoundException.class,
                () -> jwtService.checkLoginDetails(loginDto)
        );

        Mockito.verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password()));
    }

    // sendAccessCode test
    @Test
    void sendAccessCodeUserNotExist() {
        Mockito.when(userService.findByEmail(Mockito.anyString()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(
                NotFoundException.class,
                () -> jwtService.sendAccessCode(Mockito.anyString())
        );
    }

    @Test
    void sendAccessCodeSuccess() {
        String email = "a@gmail.com";
        String token = "Bearer access-token";
        User mockUser = Mockito.mock(User.class);

        Mockito.when(userService.findByEmail(email))
                .thenReturn(Optional.of(mockUser));
        Mockito.when(jwtUtil.generateCodeToken(email))
                .thenReturn("Bearer access-token");

        ResponseEntity<?> response = jwtService.sendAccessCode(email);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(token, response.getBody());

        Mockito.verify(userService).findByEmail(email);
        Mockito.verify(jwtUtil).generateCodeToken(Mockito.anyString());
    }

    // checkVerificationCode test
    @Test
    void checkVerificationCodeNull() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Assertions.assertThrows(HeaderException.class,
                () -> jwtService.checkVerificationCode(null, request)
        );
    }

    @Test
    void checkVerificationCodeHeaderInvalid() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader("TempAuthorization"))
                .thenReturn("Header doesn't starts with Confirmation");
        Assertions.assertThrows(HeaderException.class,
                () -> jwtService.checkVerificationCode("123", request)
        );
        Mockito.verify(request).getHeader("TempAuthorization");
    }

    @Test
    void checkVerificationCodeHeaderNotFound() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader("TempAuthorization")).thenReturn(null);

        Assertions.assertThrows(HeaderException.class, () -> {
            jwtService.checkVerificationCode("123", request);
        });

        Mockito.verify(request).getHeader("TempAuthorization");
    }

    @Test
    void checkVerificationCodeInvalid() {
        String verificationCode = "123";
        String token = "Confirmation token";
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

        Mockito.when(request.getHeader("TempAuthorization"))
                .thenReturn(token);
        Mockito.when(jwtUtil.checkVerification(verificationCode, token))
                .thenReturn(false);

        Assertions.assertThrows(
                BadRequestException.class,
                () -> jwtService.checkVerificationCode(verificationCode, request)
        );

        Mockito.verify(request).getHeader("TempAuthorization");
        Mockito.verify(jwtUtil).checkVerification(verificationCode, "token");
    }

    @Test
    void checkVerificationCodeSuccess() {
        String verificationCode = "123";
        String token = "Confirmation token";
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        User user = Mockito.mock(User.class);
        String email = "a@gmail.com";

        Mockito.when(request.getHeader("TempAuthorization"))
                .thenReturn(token);
        Mockito.when(jwtUtil.checkVerification(verificationCode, "token"))
                .thenReturn(true);
        Mockito.when(jwtUtil.getEmailFromToken("token"))
                .thenReturn(email);
        Mockito.when(userService.findByEmail(email))
                .thenReturn(Optional.of(user));
        Mockito.when(jwtUtil.genToken(user)).thenReturn("access-token");
        Mockito.when(jwtUtil.genRefreshToken(user)).thenReturn("refresh-token");
        Mockito.when(jwtUtil.checkVerification(verificationCode, token)).thenReturn(true);

        ResponseEntity<?> response = jwtService.checkVerificationCode(verificationCode, request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertInstanceOf(TokenDTO.class, response.getBody());
        TokenDTO tokenDTO = (TokenDTO) response.getBody();
        Assertions.assertEquals("access-token", tokenDTO.accessToken());
        Assertions.assertEquals("refresh-token", tokenDTO.refreshToken());

        Mockito.verify(request).getHeader("TempAuthorization");
        Mockito.verify(jwtUtil).checkVerification(verificationCode, "token");
        Mockito.verify(jwtUtil).getEmailFromToken("token");
        Mockito.verify(userService).findByEmail(Mockito.anyString());
        Mockito.verify(jwtUtil).genToken(user);
        Mockito.verify(jwtUtil).genRefreshToken(user);
    }
}