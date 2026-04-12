package com.devsense.model.entity;

import com.devsense.model.enums.Plan;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "users")        // maps to 'users' table in PostgreSQL
@Data                          // Lombok: generates getters, setters, toString, equals, hashCode
@Builder                       // Lombok: generates builder pattern — User.builder().email(...).build()
@NoArgsConstructor             // Lombok: generates empty constructor — required by JPA
@AllArgsConstructor            // Lombok: generates constructor with all fields
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    // JPA generates a UUID before INSERT — matches gen_random_uuid() in SQL
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;   // BCrypt hash — set in AuthService, never plain text

    private String fullName;

    @Enumerated(EnumType.STRING)
    // EnumType.STRING stores 'FREE' in DB, not 0
    // If you use EnumType.ORDINAL, renaming the enum breaks everything
    @Column(nullable = false)
    @Builder.Default
    private Plan plan = Plan.FREE;

    @Builder.Default
    private int reviewsUsed  = 0;

    @Builder.Default
    private int reviewsLimit = 5;

    @OneToMany(mappedBy = "user",
            cascade  = CascadeType.ALL,
            fetch    = FetchType.LAZY)
    // mappedBy = 'user' means: look at the 'user' field in Review class for the FK
    // FetchType.LAZY = don't load reviews unless explicitly accessed (prevents N+1)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;  // Hibernate sets this automatically on INSERT

    @UpdateTimestamp
    private LocalDateTime updatedAt;  // Hibernate updates this on every UPDATE
}

