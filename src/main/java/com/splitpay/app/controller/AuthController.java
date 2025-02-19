package com.splitpay.app.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.splitpay.app.model.PaymentChannel;
import com.splitpay.app.model.User;
import com.splitpay.app.repository.PaymentChannelRepository;
import com.splitpay.app.repository.UserRepository;
import com.splitpay.app.security.jwt.JwtUtils;
import com.splitpay.app.security.request.LoginRequest;
import com.splitpay.app.security.request.SignupRequest;
import com.splitpay.app.security.response.MessageResponse;
import com.splitpay.app.security.response.UserInfoResponse;
import com.splitpay.app.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // Inject the client-id from application.properties
    @Value("${oauth.google.client-id}")
    private static String clientId;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PaymentChannelRepository paymentChannelRepository;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody @Valid LoginRequest loginRequest) {

        // verify google user
        if(loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
            return new ResponseEntity<>(new MessageResponse("Invalid email found!"), HttpStatus.BAD_REQUEST);
        }

        // verify password
        if(loginRequest.getProfileId() == null || loginRequest.getProfileId().isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Unable to verify user!"), HttpStatus.BAD_REQUEST);
        }

        // Authenticate
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", false);
            return new ResponseEntity<Object>(map, HttpStatus.NOT_FOUND);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);


        UserInfoResponse response = new UserInfoResponse(
                userDetails.getId(),
                userDetails.getFullName(),
                userDetails.getPhotoUrl(),
                userDetails.getProfileId(),
                userDetails.getUsername(),
                jwtCookie.toString(),
                userDetails.getEmail());

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,
                        jwtCookie.toString())
                .body(response);

    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUserName(signUpRequest.getUsername())) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Username is already taken!");
            map.put("status", false);
            return new ResponseEntity<Object>(map, HttpStatus.BAD_REQUEST);
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Email is already registered!");
            map.put("status", false);
            return new ResponseEntity<Object>(map, HttpStatus.BAD_REQUEST);
        }
        // Create new user's account
        User user = new User(
                signUpRequest.getFullName(),
                signUpRequest.getPhotoUrl(),
                signUpRequest.getProfileId(),
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword()));
        User savedUser = userRepository.save(user);
        // Also create payment channels for the new user
        // 1. CASH
        // 2. UPI
        PaymentChannel upiPaymentChannel = new PaymentChannel();
        PaymentChannel cashPaymentChannel = new PaymentChannel();
        upiPaymentChannel.setUser(savedUser);
        cashPaymentChannel.setUser(savedUser);
        upiPaymentChannel.setPaymentChannelType("UPI");
        cashPaymentChannel.setPaymentChannelType("CASH");
        paymentChannelRepository.saveAll(Arrays.asList(upiPaymentChannel, cashPaymentChannel));
        Map<String, Object> map = new HashMap<>();
        map.put("message", "User registered successfully!");
        map.put("status", true);
        return new ResponseEntity<Object>(map, HttpStatus.CREATED);
    }

    @GetMapping("/username")
    public String currentUserName(Authentication authentication){
        if (authentication != null)
            return authentication.getName();
        else
            return "";
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyExistingUserByEmail(@RequestParam("email") String email) {
            Map<String, Object> map = new HashMap<>();
        if (userRepository.existsByEmail(email)) {
            User existingUser = userRepository.findByEmail(email).orElse(null);
            assert existingUser != null;
            String username = existingUser.getUserName();
            map.put("message", "User exist with this email!");
            map.put("existingUser", true);
            map.put("username", username);
        } else {
            map.put("message", "User doesn't exist with this email!");
            map.put("existingUser", false);
            map.put("username", null);
        }
        return new ResponseEntity<Object>(map, HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(Authentication authentication){
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UserInfoResponse response = new UserInfoResponse(
                userDetails.getId(),
                userDetails.getFullName(),
                userDetails.getPhotoUrl(),
                userDetails.getProfileId(),
                userDetails.getUsername(),
                userDetails.getEmail());
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signoutUser(){
        ResponseCookie responseCookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(new MessageResponse("Signed out successfully!"));
    }





}
