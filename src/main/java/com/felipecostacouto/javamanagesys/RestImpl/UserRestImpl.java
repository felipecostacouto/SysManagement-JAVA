package com.felipecostacouto.javamanagesys.RestImpl;

import com.felipecostacouto.javamanagesys.Rest.UserRest;
import com.felipecostacouto.javamanagesys.constants.ManagConstants;
import com.felipecostacouto.javamanagesys.service.UserService;
import com.felipecostacouto.javamanagesys.utils.ManagUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; // Correct import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
public class UserRestImpl implements UserRest {

    private static final Logger log = LoggerFactory.getLogger(UserRestImpl.class);
    private final UserService userService;

    @Autowired
    public  UserRestImpl(UserService userService){
        this.userService = userService;
    }

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Received sign-up request: {}", requestMap);
        try{
            return userService.signUp(requestMap);
        }catch (Exception ex){
            log.error("Exception during sign-up:", ex);
            return ManagUtils.getResponseEntity(ManagConstants.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        try{
            return userService.login(requestMap);
        }catch (Exception ex){
            log.error("Exception during login:", ex);
            return ManagUtils.getResponseEntity(ManagConstants.SOMETHING_WENT_WRONG);
        }
    }


}
