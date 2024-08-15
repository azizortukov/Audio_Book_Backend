package uz.audio_book.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;
    @Email
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    private LocalDate birthDate;
    private String displayName;
    @Column(columnDefinition = "TEXT")
    private String profilePhotoUrl;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Category> personalCategories;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Book> myBooks;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles;

    @PrePersist
    protected void onCreate() {
        if (this.myBooks == null) {
            this.myBooks = new ArrayList<>();
        }
        if (this.personalCategories == null) {
            this.personalCategories = new ArrayList<>();
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}