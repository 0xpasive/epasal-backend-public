package epasal.com.userservice.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column()
    private String password;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private Set<Role> roles;

    @Column
    private Date createdAt;

    @Column
    private Date updatedAt;

    @Column
    private boolean verified;

    public enum Role {
        ROLE_USER,
        ROLE_ADMIN
    }
}
