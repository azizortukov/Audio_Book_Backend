package uz.audio_book.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    private LocalDate birthDate;
    private String displayName;
    private byte[] profilePhoto;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Category> personalCategories;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Book> myBooks;

    @PrePersist
    protected void onCreate() {
        if (this.myBooks == null) {
            this.myBooks = new ArrayList<>();
        }
        if (this.personalCategories == null) {
            this.personalCategories = new ArrayList<>();
        }
    }

}