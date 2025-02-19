package com.splitpay.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")})
@Data
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @NotBlank
    private String fullName;

    private String profileId;

    private String photoUrl;

    @NotBlank
    @Size(min=3, max = 30)
    @Column(name = "username")
    private String userName;

    @Getter
    @Email
    @NotBlank
    @Size(min=5, max = 60)
    @Column(name = "email")
    private String email;

    @NotBlank
    @Size(min=8, max = 120)
    @Column(name = "password")
    @JsonIgnore
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<PaymentChannel> paymentChannels = new ArrayList<>();

    public User(String fullName, String photoUrl, String profileId, String userName, String email, String password) {
        this.fullName = fullName;
        this.photoUrl = photoUrl;
        this.profileId = profileId;
        this.email = email;
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }
}
