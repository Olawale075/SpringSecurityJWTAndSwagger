package sms.com.sms.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import sms.com.sms.config.TwilioConfig;
import sms.com.sms.model.Users;
import sms.com.sms.repository.UsersRepository;
import com.twilio.type.PhoneNumber;
@Service

@Component
public class TwilioSMSService {
@Autowired
    private  TwilioConfig twilioConfig;
    @Autowired
    private  OTPService otpService;
    @Autowired
    private UserServiceImpl userService;
    @Autowired // Added to handle user-related logic
    private  UsersRepository receiverRepository;

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;

//       public TwilioSMSService(TwilioConfig twilioConfig , OTPService otpService,SmsReceiverServiceImpl userService) {
// this.twilioConfig = twilioConfig;
// this.otpService = otpService;
// this.userService = userService;

// }
  
private void initializeTwilio() {

    Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());
} public String sendOtpForRegistrations(Users user) {
    initializeTwilio();
    // Check if the phone number is already registered
    if (userService.isPhoneNumberRegistered(user.getPhonenumber())) {
        return "Phone number already registered.";
    }

    // Generate a 6-digit OTP
    String otp = OtpGenerator.generateOtp(null);

    // Temporarily store user details and OTP for validation
    otpService.storeOtp(user.getPhonenumber(), otp);
    userService.saveTempUser(user); // Save the user temporarily

    // Send OTP using Twilio API
    try {
        Message message = Message.creator(
            new com.twilio.type.PhoneNumber(user.getPhonenumber()),
            new com.twilio.type.PhoneNumber(twilioConfig.getFromNumber()),
            "Welcome to our app! Your OTP is: " + otp
        ).create();
        return "OTP sent successfully to " + user.getPhonenumber() + ", SID: " + message.getSid();
    } catch (Exception e) {
        return "Failed to send OTP to " + user.getPhonenumber() + ": " + e.getMessage();
    }
}

    public String sendOtpForRegistration(Users user) {
        initializeTwilio();
        // Check if the phone number is already registered
        if (userService.isPhoneNumberRegistered(user.getPhonenumber())) {
            return "Phone number already registered.";
        }

        // Generate a 6-digit OTP
        String otp = OtpGenerator.generateOtp(null);

        // Temporarily store user details and OTP for validation
        otpService.storeOtp(user.getPhonenumber(), otp);
        userService.saveTempUser(user); // Save the user temporarily

        // Send OTP using Twilio API
        try {
            Message message = Message.creator(
                new com.twilio.type.PhoneNumber(user.getPhonenumber()),
                new com.twilio.type.PhoneNumber(twilioConfig.getFromNumber()),
                "Welcome to our app! Your OTP is: " + otp
            ).create();
            return "OTP sent successfully to " + user.getPhonenumber() + ", SID: " + message.getSid();
        } catch (com.twilio.exception.AuthenticationException e) {
            System.err.println("Authentication failed: " + e.getMessage());
            return "Failed to send OTP to " + user.getPhonenumber() + ": Authentication failed.";
        } catch (com.twilio.exception.ApiException e) {
            System.err.println("Twilio API error: " + e.getMessage());
            return "Failed to send OTP to " + user.getPhonenumber() + ": API error.";
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e);
            return "Failed to send OTP to " +" user.getPhonenumber()" + ": Unexpected error.";
        }
    }
    public String sendDefaultMessageToAllUsers(String messageBody) {
        // Initialize Twilio before sending messages
        initializeTwilio();

        // Fetch all users
        List<Users> users = receiverRepository.findAll();

        if (users.isEmpty()) {
            return "No users found to send messages.";
        }

        // Send message to each user
        for (Users user : users) {
            try {
                Message.creator(
                        new PhoneNumber(user.getPhonenumber()),  // User's phone number
                        new PhoneNumber(twilioPhoneNumber),      // Twilio's phone number
                        messageBody                              // Message content
                ).create();
            } catch (Exception e) {
                return "Failed to send message to " + user.getPhonenumber() + ": " + e.getMessage();
            }
        }

        return "Message sent to all users.";
    }
}