package uz.audio_book.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpDTO {

    private String email;
    private String password;
    private String birthDate;
    private String verificationCode;

}
