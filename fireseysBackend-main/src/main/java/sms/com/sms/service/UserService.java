package sms.com.sms.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import sms.com.sms.model.Users;
import java.util.List;

public interface UserService extends UserDetailsService {
    boolean isPhoneNumberRegistered(String phonenumber);
    void saveTempUser(Users user);
    Users findTempUser(String phonenumber);
    Users saveUser(Users user);
    String register(Users details);
    void deletes(String phonenumber);
    List<Users> getAllDetails();
    Users getDetails(String phonenumber);
    Users updateProduct(String phonenumber, Users newDetails);
}
