package sms.com.sms.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sms.com.sms.enums.NotificationPreference;
import sms.com.sms.enums.UserRole;

@DynamicUpdate
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ReceiverDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String phonenumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role; // Enum for User Role (Admin, Regular User)

    @Column(nullable = false)
    private String password; // Hashed password

    @Enumerated(EnumType.STRING)
    private NotificationPreference notificationPreference; // Enum for Notification Preferences

    @Transient//t Prevents this field from being saved in the database
    private String otp;
    

    @CreationTimestamp
    private LocalDateTime createDateTime;

    @UpdateTimestamp
    private LocalDateTime updateDateTime;
}
