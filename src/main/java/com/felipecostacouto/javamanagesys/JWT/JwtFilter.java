package com.felipecostacouto.javamanagesys.JWT;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private final CustomerUsersDetailsService service;

    @Autowired
    public JwtFilter(JwtUtil jwtUtil, CustomerUsersDetailsService service) {
        this.jwtUtil = jwtUtil;
        this.service = service;
    }

    Claims claims = null;
    private String userName = null;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletresponse,
                                    FilterChain filterChain) throws ServletException, IOException{

        if(httpServletRequest.getServletPath().matches("/user/login|/user/forgotPassword|/user/signup")){
            filterChain.doFilter(httpServletRequest,httpServletresponse);
        }else{
            String authorizationHeader = httpServletRequest.getHeader("Authorization");
            String token = null;

            if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
                token = authorizationHeader.substring(7);
                userName = jwtUtil.extractUsername(token);
                claims = jwtUtil.extractAllClaims(token);
            }

            if(userName != null && SecurityContextHolder.getContext().getAuthentication()==null){
                UserDetails userDetails = service.loadUserByUsername(userName);
                if(jwtUtil.validateToken(token,userDetails)){
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(httpServletRequest)
                    );
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
            filterChain.doFilter(httpServletRequest,httpServletresponse);
        }

    }


    public boolean isAdmin(){
        return "admin".equalsIgnoreCase((String) claims.get("role"));
    }

    public boolean isUser(){
        return "user".equalsIgnoreCase((String) claims.get("role"));
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if(authHeader == null){
            return null;
        }else {
            return authHeader.replace("Bearer ", "");
        }

    }

}
