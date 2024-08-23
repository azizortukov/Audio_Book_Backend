package uz.audio_book.backend.entity;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash( timeToLive = 300)
public class EmailCode
{
    @Id
    private String email;

    @Column( nullable = false )
    private String code;

    @Column( nullable = false )
    private LocalDateTime lastSentTime;

}
