package uz.audio_book.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class AudioBookBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AudioBookBackendApplication.class, args);
    }

}
