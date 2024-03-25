package com.felipecostacouto.javamanagesys.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public class ManagUtils {

    private ManagUtils(){

    }

    public static ResponseEntity<String> getResponseEntity(String responseMessage){
        return new ResponseEntity<String>("{\"message\":\"" + responseMessage + "\"}", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
