package uz.audio_book.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "comment")
public class Comment {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
    private String body;
    @Column(nullable = false)
    private Double rating;
    @JoinColumn(nullable = false)
    @ManyToOne
    private Book book;
    @JoinColumn(nullable = false)
    @ManyToOne
    private User user;

}