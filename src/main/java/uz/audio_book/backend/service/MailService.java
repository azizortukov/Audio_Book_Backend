package uz.audio_book.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uz.audio_book.backend.entity.EmailCode;
import uz.audio_book.backend.exceptions.BadRequestException;
import uz.audio_book.backend.repo.EmailCodeRepo;
import uz.audio_book.backend.util.JwtUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class MailService {

    private final JavaMailSender mailSender;
    private final EmailCodeRepo emailCodeRepo;
    private final String text = "Hi, someone tried to sign up for an Audio Book app account with %s . " +
                                "If it was you, enter this confirmation code in the app:" + System.lineSeparator() +
                                " %d";
    private final JwtUtil jwtUtil;
    Random random = new Random();

    @Async
    public void sendConfirmationCode(String email) {
        Optional<EmailCode> optionalEmailCode = emailCodeRepo.findById(email);
        if (optionalEmailCode.isEmpty()) {
            int code = genVerificationCode();

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Audio Book");
            message.setText(text.formatted(email, code));
            mailSender.send(message);

            EmailCode emailCode = new EmailCode(email, String.valueOf(code), LocalDateTime.now());
            emailCodeRepo.save(emailCode);
        } else {
            EmailCode emailCode = optionalEmailCode.get();

            if (!emailCode.getLastSentTime().plusMinutes(1).isBefore(LocalDateTime.now())) {
                Duration between = Duration.between(emailCode.getLastSentTime(), LocalDateTime.now());
                long diff = 60 - between.getSeconds();
                throw new BadRequestException("Please try after %d seconds".formatted(diff));
            }

            int code = genVerificationCode();

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Audio Book App");
            message.setText(text.formatted(email, code));
            mailSender.send(message);

            emailCode.setCode(String.valueOf(code));
            emailCode.setLastSentTime(LocalDateTime.now());
            emailCodeRepo.save(emailCode);
        }
    }


    private Integer genVerificationCode() {
        return random.nextInt(101010, 989898);
    }


    @Async
    public void sendPasswordResetLink(String email) {
        String token = jwtUtil.genResetPasswordToken(email);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Audio Book App");
        message.setText("""
                Hello. Your reset link is here: %s. If you didn't request for reset password
                , please do not pay attention""".formatted("https://audio_book.uz/api/auth/reset-password?token=" + token));
        mailSender.send(message);
    }
}

