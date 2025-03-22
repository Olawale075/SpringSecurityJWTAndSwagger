package sms.com.sms.controller;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import sms.com.sms.config.JwtUtil;
import sms.com.sms.dto.AuthRequest;
import sms.com.sms.dto.AuthResponse;
import sms.com.sms.model.Users;
import sms.com.sms.service.OTPService;
import sms.com.sms.service.TwilioSMSService;
import sms.com.sms.service.UserServiceImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin("*")
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl service;
    private final TwilioSMSService twilioSMSService;
    private final OTPService otpService;
    private final AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtUtil jwtUtil;

    private final AtomicInteger validatedUsersCount = new AtomicInteger(0);
    private static final int MAX_VALIDATED_USERS = 20;

    /** Get all users */
    @GetMapping("/user/admin/")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public List<Users> getAllReceiverDetails() {
        return service.getAllDetails();
    }

    /** Get logged-in user details */
    @GetMapping("/user/admin//details")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserDetails> getUserDetails() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            return ResponseEntity.ok((UserDetails) principal);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /** Register a new user */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Users user) {
        String result = service.register(user);
        return ResponseEntity.ok(result);
    }
    /** Authenticate user and return JWT token */
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            System.out.println("🔹 Attempting login for: " + request.getPhoneNumber());
    
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getPhoneNumber(), request.getPassword())
            );
    
            System.out.println("✅ Authentication successful!");
    
            UserDetails userDetails = service.loadUserByUsername(request.getPhoneNumber());
            System.out.println("🔹 Loaded User: " + userDetails.getUsername());
    
            String token = jwtUtil.generateToken(userDetails.getUsername());
    
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (Exception e) {
            System.out.println("❌ Authentication failed: " + e.getMessage());
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
    

    /** Send fire detection alert */
    @PostMapping("/user/send-message-to-all-for-fireDetector")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> sendMessageToAllForFireDetector() {
        String message = "Dear Subscriber,\n\nA GAS hazard has been detected at Moremi Hall, Osun State Government Secretariat, Oke Pupa, Abere, Osogbo. \n" +
                         "This is an emergency situation, and your immediate action is required.\n" +
                         "Stay alert and take care.\n\nLocation: Moremi Hall, Osun State Government Secretariat, Oke Pupa, Abere, Osogbo\n" +
                         "[Your Organization/FireEye]";
        return ResponseEntity.ok(twilioSMSService.sendDefaultMessageToAllUsers(message));
    }

    /** Send gas detection alert */
    @PostMapping("/user/send-message-to-all-for-GasDetector")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> sendMessageToAllForGasDetector() {
        String message = "Dear Subscriber,\n\nA GAS hazard has been detected at Adegunwa Kitchen. \n" +
                         "This is an emergency situation, and your immediate action is required.\n" +
                         "Stay alert and take care.\n\nLocation: FOUNTAIN UNIVERSITY / Adegunwa Kitchen\n" +
                         "[Your Organization/ROBOTIC GROUP]";
        return ResponseEntity.ok(twilioSMSService.sendDefaultMessageToAllUsers(message));
    }

    /** Validate OTP and register user */
    @PostMapping("/validate-otp")
    public ResponseEntity<String> validateOtp(@RequestBody Users details) {
        String phoneNumber = details.getPhonenumber().trim();
        if (!phoneNumber.startsWith("+234")) {
            phoneNumber = "+234" + phoneNumber.replaceFirst("^0", "");
        }
        details.setPhonenumber(phoneNumber);

        if (details.getOtp() == null || details.getPhonenumber() == null) {
            return ResponseEntity.badRequest().body("OTP and phone number are required");
        }

        boolean isValid = otpService.validateOtp(details.getPhonenumber(), details.getOtp());
        if (isValid) {
            service.saveUser(details);
            return ResponseEntity.ok("User registered successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP.");
        }
    }

    /** Get user details by phone number */
    @GetMapping("user/{phonenumber}")
    
    public ResponseEntity<Users> getReceiverByPhoneNumber(@PathVariable String phonenumber) {
        Users details = service.getDetails(phonenumber);
        return ResponseEntity.ok(details);
    }

    /** Update user details */
    @PutMapping("/user/admin/{phonenumber}/update")
   
    public ResponseEntity<Users> updateReceiverDetails(@PathVariable String phonenumber, @RequestBody Users newDetails) {
        Users updatedDetails = service.updateProduct(phonenumber, newDetails); 
        return ResponseEntity.ok(updatedDetails);
    }

    /** Delete user by phone number */
    @DeleteMapping("/user/admin//{phonenumber}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteReceiver(@PathVariable String phonenumber) {
        service.deletes(phonenumber);
        return ResponseEntity.ok("Receiver deleted successfully.");
    }
}
