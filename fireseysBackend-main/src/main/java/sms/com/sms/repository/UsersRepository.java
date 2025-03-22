package sms.com.sms.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import sms.com.sms.model.Users;
@EnableJpaRepositories
public interface UsersRepository extends JpaRepository<Users, Long> {
  
  Users findByPhonenumber(String phonenumber);
  boolean existsByPhonenumber(String phonenumber);




}