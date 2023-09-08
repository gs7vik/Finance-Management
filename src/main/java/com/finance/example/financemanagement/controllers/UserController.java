package com.finance.example.financemanagement.controllers;

import com.finance.example.financemanagement.config.JwtHelper;
import com.finance.example.financemanagement.entities.JwtRequest;
import com.finance.example.financemanagement.entities.JwtResponse;
import com.finance.example.financemanagement.entities.User;
import com.finance.example.financemanagement.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager manager; //for authenticating

    @Autowired
    private JwtHelper helper; //for creating jwtToken

    @Autowired
    private UserService userService;

    private Logger logger= LoggerFactory.getLogger(UserController.class);


    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request){

        this.doAuthenticate(request.getEmail(),request.getPassword());

        UserDetails userDetails= userDetailsService.loadUserByUsername(request.getEmail());
        String token=this.helper.generateToken(userDetails);

        JwtResponse response=JwtResponse.builder()
                .jwtToken(token)
                .userName(userDetails.getUsername()).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private void doAuthenticate(String email,String password){

        UsernamePasswordAuthenticationToken authentication=new UsernamePasswordAuthenticationToken(email,password);

        try{
            manager.authenticate(authentication);

        }catch (BadCredentialsException e){
            throw new RuntimeException("Invalid username or password");
        }
    }

    @ExceptionHandler(BadCredentialsException.class)
    public String exceptionHandler(){
        return "Invalid Credentials";
    }

    @PostMapping("/create-user")
    public User createUser(@RequestBody User user){
        return userService.createUser(user);

    }

}
