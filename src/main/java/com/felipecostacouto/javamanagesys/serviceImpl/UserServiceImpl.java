package com.felipecostacouto.javamanagesys.serviceImpl;

import com.felipecostacouto.javamanagesys.JWT.CustomerUsersDetailsService;
import com.felipecostacouto.javamanagesys.JWT.JwtUtil;
import com.felipecostacouto.javamanagesys.constants.ManagConstants;
import com.felipecostacouto.javamanagesys.dao.UserDAO;
import com.felipecostacouto.javamanagesys.models.User;
import com.felipecostacouto.javamanagesys.service.UserService;
import com.felipecostacouto.javamanagesys.utils.ManagUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserDAO userDao;

    private final AuthenticationManager authenticationManager;

    private final CustomerUsersDetailsService customerUsersDetailsService;

    private final JwtUtil jwtUtil;

    @Autowired
    public UserServiceImpl(UserDAO userDao, AuthenticationManager authenticationManager,
                           CustomerUsersDetailsService customerUsersDetailsService, JwtUtil jwtUtil ) {
        this.userDao = userDao;
        this.authenticationManager = authenticationManager;
        this.customerUsersDetailsService = customerUsersDetailsService;
        this.jwtUtil = jwtUtil;
    }

    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Inside signup {}",requestMap);
        try{
            if(validateSignUpMap(requestMap)){
                User user = userDao.findByEmailId(requestMap.get("email"));
                if(Objects.isNull(user)){
                    userDao.save(getUserFromMap(requestMap));
                    return ManagUtils.getResponseEntity("Sucessfully Registered.");
                }else{
                    return ManagUtils.getResponseEntity("Email already exists.");
                }
            }else{
                return ManagUtils.getResponseEntity(ManagConstants.INVALID_DATA);
            }
        }catch (Exception ex){
            log.error("Exception during sign-up:", ex);
        }
        return ManagUtils.getResponseEntity(ManagConstants.INVALID_DATA);
    }


    private boolean validateSignUpMap(Map<String,String> requestMap){
        return requestMap.containsKey("name") && requestMap.containsKey("contactNumber") &&
                requestMap.containsKey("email") && requestMap.containsKey("password");
    }

    private User getUserFromMap(Map<String,String> requestMap){
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setStatus("false");
        user.setRole("user");
        return user;
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Inside login");
        try{
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"),requestMap.get("password"))
            );
            if(auth.isAuthenticated()){

                if (customerUsersDetailsService.getuserDetail().getStatus() != null){
                    String status = customerUsersDetailsService.getuserDetail().getStatus();
                    if("true".equalsIgnoreCase(status)){
                        return new ResponseEntity<String>("{\"token\":\""
                                + jwtUtil.generateToken(customerUsersDetailsService.getUserDetail().getEmail(),
                                customerUsersDetailsService.getUserDetail().getRole()) + "\"}",
                                HttpStatus.OK);
                    }else{
                        return new ResponseEntity<String>("{\"message\":\""+ "Wait for admin approval." + "\"}",
                                HttpStatus.BAD_REQUEST);
                    }
                }else{
                    return new ResponseEntity<String>("{\"message\":\""+ "User details not found." + "\"}",
                            HttpStatus.BAD_REQUEST);
                }
            }
        }catch(Exception ex){
            log.error("Exception during login:", ex);
        }
        return new ResponseEntity<String>("{\"message\":\""+ "Bad Credentials test." + "\"}",
                HttpStatus.BAD_REQUEST);
    }

}
