package com.example.ludogoriesoft.lukeriaerpapi.models;

import com.example.ludogoriesoft.lukeriaerpapi.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_users")
public class User implements UserDetails {

    public static Object Role;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "The name should not be null!")
    private String firstname;

    @NotNull(message = "The name should not be null!")
    private String lastname;

    @Email(message = "Email should be a well-formatted email!")
    @NotNull(message = "The email should not be null!")
    @Column(unique = true)
    private String email;

    @NotNull(message = "The password should not be null!")
    private String password;

    @NotNull(message = "The address should not be null!")
    private String address;

    @NotNull(message = "The username should not be null!")
    @Column(name = "username")
    private String usernameField;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;


    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
//@Entity
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
//@Table(name = "user")
//public class User implements UserDetails {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    @NotNull(message = "Моля въведете потребителско име")
//    @Size(min = 4)
//    private String username;
//    @Column(name = "full_name")
//    @Size(min = 5)
//    @NotNull(message = "Моля въведете име и фамилия")
//    private String fullName;
//    @Email
//    private String email;
//    @NotNull(message = "Моля въведете парола")
//    private String password;
//    @Enumerated(EnumType.STRING)
//    private Role role;
//    @Column(name = "is_deleted")
//    private boolean deleted;
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return null;
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return false;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return false;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return false;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return false;
//    }
//}
