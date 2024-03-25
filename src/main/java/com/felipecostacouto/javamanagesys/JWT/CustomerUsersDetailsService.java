package com.felipecostacouto.javamanagesys.JWT;

import com.felipecostacouto.javamanagesys.dao.UserDAO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;

@Slf4j
@Service
public class CustomerUsersDetailsService implements UserDetailsService {

    private final UserDAO userDAO;


    @Getter
    private  com.felipecostacouto.javamanagesys.models.User userDetail; // Use your User class

    @Autowired //constructor injection (makes dependencies explicit)
    public CustomerUsersDetailsService(UserDAO userDAO) {

        this.userDAO = userDAO; //userDao as a parameter
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Inside loadUserByUsername {}",username);
        userDetail = userDAO.findByEmailId(username);
        if (!Objects.isNull(userDetail)) {
            return new org.springframework.security.core.userdetails.User(
                    userDetail.getEmail(), userDetail.getPassword(), new ArrayList<>()
            );
        } else {
            throw new UsernameNotFoundException("User Not Found.");
        }
    }

}
