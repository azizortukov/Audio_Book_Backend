package uz.audio_book.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class MailServiceTest {

    private MailService mailService;
    private JavaMailSender mailSender;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        mailService = new MailService(mailSender);
    }

    @Test
    void sendConfirmationCode() {
        String email = "test@example.com";
        String code = "123456";

        mailService.sendConfirmationCode(email, code);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();

        assertEquals(email, Objects.requireNonNull(sentMessage.getTo())[0]);
        assertEquals("Verification Code", sentMessage.getSubject());
        assertEquals("Your verification code for Audio Book application is : " + code, sentMessage.getText());

    }
}