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
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String author;
    private String description;
    private byte[] photo;
    private byte[] audio;
    private byte[] pdf;

}