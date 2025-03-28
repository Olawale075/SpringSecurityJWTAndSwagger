package sms.com.sms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import sms.com.sms.config.SecurityConfig;
import sms.com.sms.enums.UserRole;
import sms.com.sms.exception.ResourceNotFoundException;
import sms.com.sms.model.Users;
import sms.com.sms.repository.UsersRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


import org.springframework.stereotype.Service;
import java.util.Collections;
@Service
public class UserServiceImpl implements UserService {  // ✅ Implements UserService

    @Autowired
    private UsersRepository repository;
    @Autowired
    private SecurityConfig securityConfig; 
    private final Map<String, Users> tempUserStorage = new HashMap<>(); // Temporary user storage

    public boolean isPhoneNumberRegistered(String phonenumber) {
        return repository.existsByPhonenumber(phonenumber);
    }

    public void saveTempUser(Users user) {
        tempUserStorage.put(user.getPhonenumber(), user);
    }

    public Users findTempUser(String phonenumber) {
        return tempUserStorage.get(phonenumber);
    }

    public Users saveUser(Users user) {
        return repository.save(user);
    }

  
    public String register(Users details) {
        if (isPhoneNumberRegistered(details.getPhonenumber())) {
            return "Phone number already registered.";
        }
    
        // ✅ Assign default role if not provided
        if (details.getRole() == null) {
            details.setRole(UserRole.ROLE_USER); // Ensure UserRole.USER exists in your Enum
        }
    
        // ✅ Encode password before saving
        details.setPassword(securityConfig.passwordEncoder().encode(details.getPassword()));
    
        repository.save(details);
        return "User registered successfully.";
    }
    
   
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = repository.findByPhonenumber(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
    
        // ✅ Ensure the role has "ROLE_" prefix only once
        String role = user.getRole().name().startsWith("ROLE_") ? user.getRole().name() : "ROLE_" + user.getRole().name();
    
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getPhonenumber())
                .password(user.getPassword())
                .authorities(new SimpleGrantedAuthority(role)) // ✅ Use correct role
                .build();
    }
    
    
    public void deletes(String phonenumber) {
        Users details = repository.findByPhonenumber(phonenumber);
        if (details == null) {
            throw new ResourceNotFoundException("Number not found");
        }
        repository.delete(details);
    }

    public List<Users> getAllDetails() {
        return repository.findAll();
    }

    public Users getDetails(String phonenumber) {
        Users details = repository.findByPhonenumber(phonenumber);
        if (details == null) {
            throw new ResourceNotFoundException("Number not found");
        }
        return details;
    }

    public Users updateProduct(String phonenumber, Users newDetails) {
        Users existingDetails = repository.findByPhonenumber(phonenumber);

        existingDetails.setName(newDetails.getName());
        existingDetails.setPhonenumber(newDetails.getPhonenumber());

        return repository.save(existingDetails);
    }

    // ✅ Corrected `loadUserByUsername` method
   
}
