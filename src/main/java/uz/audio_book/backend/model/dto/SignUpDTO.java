package uz.audio_book.backend.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpDTO {

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email format is not valid")
    private String email;
    @NotBlank(message = "Password cannot be blank")
    private String password;
    @JsonProperty("birth_date")
    @NotBlank(message = "Birth date cannot be blank")
    private String birthDate;

}
