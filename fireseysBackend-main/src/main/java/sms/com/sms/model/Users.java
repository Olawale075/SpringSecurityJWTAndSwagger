package sms.com.sms.model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import lombok.*;
import sms.com.sms.enums.NotificationPreference;
import sms.com.sms.enums.UserRole;

@DynamicUpdate
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Users implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ Use IDENTITY strategy for auto-increment
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String phonenumber; // ✅ Authentication uses phone number
    @Column(nullable = false)
    private Boolean isVerified = false; // Default value set to false
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role; // ✅ Enum for User Role (Admin, Regular User)

    @Column(nullable = false)
    private String password; // ✅ Hashed password

    @Enumerated(EnumType.STRING)
    private NotificationPreference notificationPreference; // ✅ Enum for Notification Preferences

    @Transient // ✅ Prevents this field from being saved in the database
    private String otp;

    @CreationTimestamp
    private LocalDateTime createDateTime;

    @UpdateTimestamp
    private LocalDateTime updateDateTime;

    // ✅ Return User Role as a List of Authorities
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // ✅ Convert Enum Role to GrantedAuthority for Spring Security
        return List.of(new SimpleGrantedAuthority(role.name())); 
    }

    @Override
    public String getUsername() {
        return phonenumber; // ✅ Uses phone number as username for authentication
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // ✅ Account is never expired
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // ✅ Account is never locked
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // ✅ Credentials never expire
    }

    @Override
    public boolean isEnabled() {
        return true; // ✅ Enable the account by default
    }
}
