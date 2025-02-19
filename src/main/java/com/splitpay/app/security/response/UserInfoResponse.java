package com.splitpay.app.security.response;


import lombok.Data;

import java.util.List;

@Data
public class UserInfoResponse {

    private Long id;
    private String fullName;
    private String photoUrl;
    private String profileId;
    private String jwtToken;
    private String username;
    private String email;

//    private List<String> roles;

    public UserInfoResponse(Long id, String fullName, String photoUrl, String profileId, String username, String jwtToken, String email) {
        this.id = id;
        this.fullName = fullName;
        this.photoUrl = photoUrl;
        this.profileId = profileId;
        this.username = username;
        this.jwtToken = jwtToken;
        this.email = email;
    }

    public UserInfoResponse(Long id, String fullName, String photoUrl, String profileId, String username, String email) {
        this.id = id;
        this.fullName = fullName;
        this.photoUrl = photoUrl;
        this.profileId = profileId;
        this.username = username;
        this.jwtToken = jwtToken;
        this.email = email;
    }

}