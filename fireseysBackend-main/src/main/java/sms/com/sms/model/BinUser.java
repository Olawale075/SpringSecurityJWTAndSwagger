package sms.com.sms.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@DynamicUpdate
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity

public class BinUser {
   


    @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE)
   private Long id;
private String name;
   @Column(nullable = false)
   private String phonenumber;

   @Column(name = "get_otp", columnDefinition = "VARCHAR(6)") // Explicitly define the SQL type
   private String Otp;

   
    @CreationTimestamp
    private LocalDateTime createDateTime;
    @UpdateTimestamp
    private LocalDateTime updateDateTime;
 
  

  
  

} 

