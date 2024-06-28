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
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String body;
    @Column(nullable = false)
    private int rating;
    @JoinColumn(nullable = false)
    @ManyToOne
    private Book book;
    @JoinColumn(nullable = false)
    @ManyToOne
    private User user;

}