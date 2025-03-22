package sms.com.sms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sms.com.sms.model.BinUser;


public interface BinRepositoty extends JpaRepository<BinUser,Long >{

    boolean existsByPhonenumber(String phonenumber);

    BinUser findByPhonenumber(String phonenumber);

}
