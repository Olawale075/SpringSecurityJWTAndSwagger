package sms.com.sms.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import sms.com.sms.model.ReceiverDetails;
@EnableJpaRepositories
public interface ReceiverRepository extends JpaRepository<ReceiverDetails, Long> {
  
  ReceiverDetails findByPhonenumber(String phonenumber);
  boolean existsByPhonenumber(String phonenumber);



}